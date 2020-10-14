assume cs:codesg,ds:datasg,ss:stacksg

datasg segment
    db 'welcome to masm!'
    db 02h,24h,71h
datasg ends

stacksg segment
    db 16 dup (0)
stacksg ends

codesg segment

start:
    mov ax,datasg
    mov ds,ax 

    mov ax,stacksg
    mov ss,ax
    mov sp,10H

    mov ax, 0b800h
    mov es,ax

    ; be ready for s
    mov bx,0F00H+64         ; 表示第12行
    mov cx,3
    mov di,10h          ; 指向颜色参数

s:
    mov ah,ds:[di]      ; 将data segment的字符属性赋值到ax的高位
    push cx
    push di

    mov cx, 16
    mov si,0            ; 指向SCREEN映射区的字符
    mov di,0            ; 指向data内存区的字符
    ; data内存区的字符间隔为2，SCREEN的字符间隔为1，需设置不同参数

s1:
    mov al, ds:[di]
    mov es:[bx+si], ax
    inc di
    add si,2
    loop s1

    pop di
    pop cx
    inc di
    add bx,0a0h
    loop s

    mov ax,4c00h
    int 21h

codesg ends

end start
