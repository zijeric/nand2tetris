assume cs:code,ds:data
data segment
    dw 128 dup (0)
data ends
code segment
start:
    mov ax,data
    mov ds,ax
    mov si,0
    mov dh,18
    mov dl,50
    call getstr
    
    mov ax,4c00h
    int 21h
    
getstr:
    push ax
    
getstrs:
    mov ah, 0
    int 16h
    cmp al, 20h
    jb nochar                        ; ASCII码小于20h, 说明不是字符

    ; 字符的处理分为两步，1:入栈,   2:显示栈中的字符
    mov ah, 0                        ; 1: 字符入栈
    call charstack
    mov ah, 2                        ; 2: 显示栈中的字符
    call charstack
    jmp getstrs
                    
nochar:    
    cmp ah, 0eh                    ; 退格键的扫描码
    je backspace
    cmp ah, 1ch                    ; Enter键的扫描码
    je enters
    jmp getstrs                    ; 其他控制键忽略
    
backspace:
    mov ah, 1
    call charstack                    ; 字符出栈
    mov ah, 2
    call charstack                    ; 字符显示
    jmp getstrs
    
enters:
    mov al, 0
    mov ah, 0
    call charstack                    ; 0入栈
    mov ah, 2
    call charstack                    ; 显示栈中的字符串
            
    pop ax
    ret

; 子程序: 字符栈的入栈、出栈和显示
; 参数说明：(ah)=功能号，0表示入栈，1表示出栈，2表示显示
; ds:si指向字符栈的空间
; 对于0号功能：(al)=入栈字符；
; 对于1号功能: (al)=返回的字符;
; 对于2号功能：(dh)、(dl)=字符串在屏幕上显示的行、列位置。
    
charstack:    jmp short charstart
    table     dw charpush, charpop, charshow
    top       dw 0;栈空间
        
charstart:
    push bx
    push dx
    push di
    push es
    
    ;判断选择是否越界
    cmp ah, 2
    ja sret
    ;选择序号对应到table中的地址
    mov bl, ah
    mov bh, 0
    add bx, bx
    jmp word ptr table[bx]

charpush:
    ;将入栈字符放入栈中，top是栈顶指针
    ;下面的[si][bx]，应该是ds:[si][bx],
    ;在我们主程序应该定义了一个data段，用于模拟栈空间，存储读取数据。
    ;top地址存储的数据是栈顶的位置
    mov bx, top
    mov [si][bx], al
    inc top
    jmp sret

charpop:
    ;栈顶位置为0，说明键盘缓冲区没有数据
    cmp top, 0
    je sret
    ;不为0，top减1，模拟出栈
    dec top
    ;将我们主程序定义的data段中的栈顶数据取出，赋值给al
    mov bx, top
    mov al, [si][bx]
    jmp sret
                
charshow:;(dh)、(dl)=字符串在屏幕上显示的行、列位置。    
    mov bx, 0b800h
    mov es, bx
    mov al, 160
    mov ah, 0
    mul dh
    mov di, ax
    add dl, dl
    mov dh, 0
    add di, dx

    mov bx, 0
    
charshows:
    ;使用bx在栈空间移动取值
    ;判断栈顶位置是否和bx相等，相等表示键盘缓冲区为空
    cmp bx, top
    jne noempty
    ;为空，在字符串后面加‘ ’
    mov byte ptr es:[di], ' '
    jmp sret

noempty:
    ;取出数据显示，以‘ ’间隔
    mov al, [si][bx]
    mov es:[di], al
    mov byte ptr es:[di+2], ' '
    inc bx
    add di, 2
    jmp charshows

sret:        
    pop es
    pop di
    pop dx
    pop bx
    ret
    
code ends
end start