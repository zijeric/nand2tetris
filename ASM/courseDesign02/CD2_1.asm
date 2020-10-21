assume cs:code,ss:stacksg
stacksg segment
    db 128 dup (0)
stacksg ends
code segment
start:
    mov ax,stacksg
    mov ss,ax
    mov sp,128

    ; 将程序粘贴到0:7E00H:(0面0道2扇区:7C00H+512)
    ; 其实只要在实模式1M内存内即可，当然最好是在第一个扇区512之后
    call COPY_BOOT

    ; 设置CS:IP为0:7E00H，进而让程序进入BOOT执行
    mov ax,0                            ; CS
    push ax
    mov ax,7E00H                        ; IP
    push ax
    retf                                ; pop IP and CS

    mov ax,4C00H
    int 21H

; 复制BOOT引导程序的内容到0:7E00H
COPY_BOOT:
    mov ax,cs
    mov ds,ax
    mov si,offset BOOT

    mov ax,0
    mov es,ax
    mov di,7E00H                        ; 0面0道2扇区

    mov cx,offset BOOT_END - offset BOOT
    cld
    rep movsb
    ret

BOOT:
    jmp BOOT_START
FUNC_0          db  'https://github.com/Nolva reference Hk_Mayfly',0
FUNC_1          db  '1) reset PC',0
FUNC_2          db  '2) start system',0
FUNC_3          db  '3) clock',0
FUNC_4          db  '4) set clock',0
; FUNC_ADDR: 绝对位置
; 相减得到的是FUNC_*的相对位置，+7E00H得到绝对位置
FUNC_ADDR       dw offset FUNC_0 - offset BOOT + 7E00H      ; FUNC_ADDR[0]
                dw offset FUNC_1 - offset BOOT + 7E00H      ; FUNC_ADDR[2]
                dw offset FUNC_2 - offset BOOT + 7E00H
                dw offset FUNC_3 - offset BOOT + 7E00H
                dw offset FUNC_4 - offset BOOT + 7E00H

BOOT_START:
    call INIT_BOOT
    call CLEAR_SCREEN
    call SHOW_MENU

    jmp CHOOSE_FUNC
    
    mov ax,4C00H
    int 21H

CHOOSE_FUNC:
    ; TODO  vedio 9:05
    jmp CHOOSE_FUNC

SHOW_MENU:
    ; 在第10行，第30列显示菜单
    mov di,160*10+30*2
    ; 保存在直接定址表的绝对位置
    mov bx,offset FUNC_ADDR - offset BOOT + 7E00H
    ; 菜单有5行
    mov cx,5

SHOW_FUNC:
    ; 这里相当于外循环，每次一行
    ; 获取FUNC_ADDR中每行的保存位置的偏移地址
    mov si,ds:[bx]
    ; 调用内循环函数，输出一行的每个字符
    call SHOW_LINES
    ; 下一行偏移地址
    add bx,2
    ; 下一行显示
    add di,160
    loop SHOW_FUNC
    ret

SHOW_LINES:
    push ax
    push di
    push si
SHOW_LINE:
    ; 获取这一行的第si个字符
    mov al,ds:[si]
    ; 判断是否到达行尾0
    cmp al,0
    je SHOW_LINE_RET
    ; 保存字符到显示缓冲区
    mov es:[di],al
    add di,2
    inc si
    jmp SHOW_LINE

SHOW_LINE_RET:
    pop si
    pop di
    pop ax
    ret

CLEAR_SCREEN:
    mov bx,0
    mov cx,2000
    mov dl,' '
    mov dh,2;字体为绿色，不设置的话，在我们显示菜单时，字体和背景颜色相同
CLEAR:
    mov es:[bx],dx
    add bx,2
    loop CLEAR

    ret

INIT_BOOT:
    mov ax,0B800H
    mov es,ax

    mov ax,0
    mov ds,ax
    ret

BOOT_END:
    nop


code ends
end start