assume cs:code,ds:stack
stack segment
    dw 128 dup (0)
stack ends
code segment
start:
    mov ax,stack
    mov ds,ax
    mov si,0
    mov dh,18
    mov dl,60
    call getstr

    mov ax,4C00H
    int 21H

getstr:
    push ax
getstrs:
    mov ah,0                    ; 功能号：入栈
    int 16h
    cmp al,20h                  ; ASCII码小于20h，说明不是字符
    jb notchar
    mov ah,0                    ; 否则字符入栈
    call charstack
    mov ah,2                    ; 并显示
    call charstack
    jmp getstrs                 ; 循环检测

notchar:
    cmp ah,0EH                  ; 退格键的扫描符
    je backspace
    cmp ah,1CH                  ; Enter建的扫描符
    je enter
    jmp getstrs
backspace:
    mov ah,1
    call charstack              ; 字符出栈
    mov ah,2
    call charstack              ; 显示栈中的字符
    jmp getstrs

enter:
    mov al,0
    mov ah,0
    call charstack              ; 0号：将0入栈
    mov ah,2
    call charstack              ; 2号：重新显示
    pop ax
    ret

charstack:
    jmp short charstart
    table dw charpush,charpop,charshow
    top   dw 0
charstart:
    push bx
    push dx
    push di
    push es

    cmp ah,2                    ; 功能号判错
    ja sret
    mov bl,ah
    mov bh,0
    add bx,bx
    jmp word ptr table[bx]
charpush:
    mov bx,top
    mov [si][bx],al
    inc top
    jmp sret

charpop:
    cmp top,0                   ; 栈空，无法出栈，直接下一轮
    je sret
    dec top
    mov bx,top
    mov al,[si][bx]
    jmp sret
charshow:
    mov bx,0B800H
    mov es,bx
    mov al,160
    mov ah,0
    mul dh                      ; 用户的行号，计算真实的屏幕偏移位置，结果在ax
    mov di,ax
    add dl,dl
    mov dh,0
    add di,dx                   ; di基于屏幕基址0B800H的偏移

    mov bx,0
charshows:
    cmp bx,top                  ; top=0时，此时栈为空，直接显示空格
    jne notempty                ; 不为空，跳转并显示字符
    mov byte ptr es:[di],' '
    jmp sret
notempty:
    mov al,[si][bx]
    mov es:[di],al
    mov byte ptr es:[di+2],' '
    inc bx
    add di,2
    jmp charshows
sret:
    pop es
    pop di
    pop dx
    pop bx
    ret
code ends
end start