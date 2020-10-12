assume cs:code,ss:stack
stack segment
    db 128 dup (0)
stack ends
code segment
start:
    mov ax,stack
    mov ss,ax
    mov sp,128
    
    call copy_boot
    
    ;设置CS:IP为0:7e00h
    mov ax,0
    push ax
    mov ax,7e00h
    push ax
    retf
    
    mov ax,4c00h
    int 21h
;org 7e00h
;引导程序
boot:
    jmp boot_begin
    func0    db 'Hk_Mayfly----XIUXIUXIU~',0
    func1    db '1) reset pc',0
    func2    db '2) start system',0
    func3    db '3) clock',0
    func4    db '4) set clock',0
    ;相减得到的是标号的相对位置，+7e00h得到的绝对位置
    func_pos    dw offset func0-offset boot+7e00h
                dw offset func1-offset boot+7e00h
                dw offset func2-offset boot+7e00h
                dw offset func3-offset boot+7e00h
                dw offset func4-offset boot+7e00h
    time    db 'YY/MM/DD hh:mm:ss',0
    cmos    db 9,8,7,4,2,0
    clock1    db 'F1----change the color        ESC----return menu',0
    clock2    db 'Please input Date and Time,(YY MM DD hh mm ss):',0
    change    db 12 dup (0),0

boot_begin:
    call init_boot
    call cls_screen
    call show_menu 
    jmp choose
    mov ax,4c00h
    int 21h

choose:
    call clear_kb_buffer
    ;获取我们输入的操作，跳转到对于函数
    mov ah,0
    int 16h
    cmp al,'1'
    je choose_func1
    cmp al,'2'
    je choose_func2
    cmp al,'3'
    je choose_func3
    cmp al,'4'
    je choose_func4
    
    jmp choose

;在题中提到了，开机后进入到ffff:0处执行指令
;那我们也可以把重启理解为，跳转到ffff:0执行指令
;所以我们利用jmp dword跳转到ffff:0地址，模拟重启
choose_func1:
    mov bx,0ffffh
    push bx
    mov bx,0
    push bx
    retf
    
    jmp choose

;题中对引导现有的操作系统的描述是调用int 19，这里为了方便就直接写成函数了
choose_func2:
    mov bx,0
    mov es,bx
    mov bx,7c00h
    
    mov al,1;扇区数
    mov ch,0
    mov cl,1;扇区
    mov dl,80h
    mov dh,0
    mov ah,2;读取
    int 13h
    
    mov bx,0
    push bx
    mov bx,7c00h
    push bx
    retf
    
    jmp choose

;获取时间
choose_func3:
    call show_time
    
    jmp choose

show_time:
    call init_boot
    call cls_screen
    ;显示按键信息
    mov si,offset clock1-offset boot+7e00h
    mov di,160*14+10*2;在14行10列显示
    call show_line
show_time_start:
    ;获取时间信息，并显示（将time中的未知字符替换为当前时间）
    call get_time_info
    mov di,160*10+30*2;屏幕显示的偏移地址
    mov si,offset time-offset boot+7e00h;time标号的偏移地址
    call show_line
    
    ;获取键盘缓存区的数据
    mov ah,1
    int 16h
    ;没有数据就跳回show_time_start
    jz show_time_start
    ;判断是否按下F1
    cmp ah,3bh
    je change_color
    ;判断是否按下ESC
    cmp ah,1
    je Return_Main
    ;有数据，但是是无用的键盘中断，清除
    cmp al,0
    jne clear_kb_buffer2
    ;返回开始，重复之前的操作，达到刷新时间的效果。
    jmp show_time_start

change_color:
    call change_color_show
clear_kb_buffer2:
    call clear_kb_buffer
    jmp show_time_start
Return_Main:
    ;返回到开始，重新打印菜单
    jmp boot_begin
    ret

choose_func4:
    call set_time
    jmp boot_begin
    
set_time:
    call init_boot
    call cls_screen
    call clear_stack
    
    ;设置提示信息显示位置
    mov di,160*10+13*2
    mov si,offset clock2-offset boot+7e00h
    call show_line
    ;显示修改后change中的内容
    mov di,160*12+26*2
    mov si,offset change-offset boot+7e00h
    call show_line
    
    call get_string

get_string:
    mov si,offset change - offset boot + 07e00H
    mov bx,0
getstring:
    ;获取键盘输入的时间信息
    mov ah,0
    int 16h
    
    ;输入的时间为数字0~9
    cmp al,'0'
    jb error_input
    cmp al,'9'
    ja error_input
    ;将我们输入的时间字符入栈
    call char_push
    ;不能超过输入的数量
    cmp bx,12
    ja press_ENTER
    mov di,160*12+26*2
    call show_line
    jmp getstring
error_input:
    ;判断是不是按下退格或回车键
    cmp ah,0eh
    je press_BS
    cmp ah,1ch
    je press_ENTER

    jmp getstring
;按下回车
press_BS:
    call char_pop
    mov di,160*12+26*2
    call show_line
    jmp getstring
;按下enter就退出
press_ENTER:
    ret

char_push:
    ;只能最多输入12个梳子
    cmp bx,12
    ja char_push_end
    ;将数值移动到对应位置
    mov ds:[si+bx],al
    inc bx;表示我们输入了多少个字符
char_push_end:
    ret

char_pop:
    ;判断是否输入了设置时间的数值，没有就相当于删完了
    cmp bx,0
    je char_pop_end
    ;否则用星号替换，相当于删除
    dec bx
    mov byte ptr ds:[si+bx],'*'
char_pop_end:
    ret

clear_stack:
    push bx
    push cx
    
    mov bx,offset change-offset boot+7e00h
    mov cx,12
cls_stack:
    ;替换change段中内容
    mov byte ptr ds:[bx],'*'
    inc bx
    loop cls_stack
    
    pop cx
    pop bx
    ret
    

;获取时间
get_time_info:
    ;从cmos ram获取年月日，时分秒6个数据
    mov cx,6
    ;获取存放单元地址
    mov bx,offset cmos - offset boot + 7e00H
    ;通过替换来显示
    mov si,offset time - offset boot + 7e00H
next_point:   
    push cx
    ;获取单元号
    mov al,ds:[bx]
    ;向70h端口写入要访问的单元地址，并从71h端口读取数据
    out 70H,al
    in al,71H
    ;右移4位获取十位
    mov ah,al
    mov cl,4
    shr al,cl
    and ah,00001111b
    ;将BCD码转换为ASCII码
    add ax,3030H
     ;写入time中
    mov word ptr ds:[si],ax
    ;下一单元号
    inc bx
    ;每个数据之间距离都是3
    add si,3
    pop cx
    loop next_point
    ret

;改变颜色
change_color_show:
    push bx
    push cx
 
    mov cx,2000
    mov bx,1
next:
    ;属性值+1，改变颜色
    add byte ptr es:[bx],1
    ;当超出字体颜色的数值(0~111h)时，将数值重置
    cmp byte ptr es:[bx],00001000b
    jne change_end
    ;因为背景是黑色，所以文字颜色就不设置成黑色了
    mov byte ptr es:[bx],1
change_end:
    add bx,2
    loop next
 
    pop cx
    pop bx
    ret

clear_kb_buffer:
    ;1号程序，用来检测键盘缓冲区是否有数据
    ;如果有的话ZF!=0，没有，ZF=0
    mov ah,1
    int 16h
    ;通过ZF判断减缓缓冲区是否有数据，没有就跳出
    jz clear_kb_bf_end
    mov ah,0
    int 16h
    jmp clear_kb_buffer
clear_kb_bf_end:
    ret

init_boot:
    ; 基本设置，注意：程序的直接定址表默认段地址是CS
    ; 当程序转移到7c00h时，代码中CS值未发生改变，
    ; 所以需要我们指明段地址
    mov bx,0b800h
    mov es,bx
    mov bx,0
    mov ds,bx
    ret
    
;清屏
cls_screen:
    mov bx,0
    mov cx,2000
    mov dl,' '
    mov dh,2;字体为绿色，不设置的话，在我们显示菜单时，字体和背景颜色相同
s:    mov es:[bx],dx
    add bx,2
    loop s
sret:
    ret

;展示界面
show_menu:
    ;在10行，30列显示菜单
    mov di,160*10+30*2
    ;保存在直接定址表的绝对位置
    mov bx,offset func_pos-offset boot+7e00h
    ;菜单有5行
    mov cx,5
s1:
    ;这里相当于外循环，每次一行
    ;获取func_pos中每行的保存位置的偏移地址
    mov si,ds:[bx]
    ;调用内循环函数，输出一行的每个字符
    call show_line
    ;下一行偏移地址
    add bx,2
    ;下一行显示
    add di,160
    loop s1
    ret
    
show_line:
    push ax
    push di
    push si
show_line_start:
    ;获取这一行的第si+1个字符
    mov al,ds:[si]
    ;判断是否到末尾
    cmp al,0
    je show_line_end
    ;保存字符到显示缓冲区
    mov es:[di],al
    add di,2
    inc si
    jmp show_line_start
show_line_end:
    pop si
    pop di
    pop ax
    ret

boot_end:nop

;转存引导程序
copy_boot:
    ;将引导程序储存到指定位置
    mov ax,0
    mov es,ax
    mov di,7e00h
    
    mov ax,cs
    mov ds,ax
    mov si,offset boot
    mov cx,offset boot_end-offset boot
    cld
    rep movsb
    
    ret

code ends
end start