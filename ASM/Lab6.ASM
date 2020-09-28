assume cs:codesg, ss:stacksg, ds:datasg

stacksg segment
    dw 0,0,0,0,0,0,0,0
stacksg ends

datasg segment
    db '1. display      '
    db '2. brows        '
    db '3. replace      '
    db '4. modify       '
datasg ends

codesg segment
start:
    mov ax,datasg
    mov ds,ax
    mov ax,stacksg
    ; 暂存cx的循环次数
    mov ss, ax
    mov sp, 16
    mov bx, 0
    mov cx, 4

s0:
    push cx  ; 暂存外层循环次数到stack栈区
    mov si,0
    mov cx,1  ; 设置内层循环次数

s:
    mov al,[bx+si+3]
    and al,11011111b  ; 置0, 字母大写
    mov [bx+si+3],al
    inc si
    loop s

    add bx,16
    pop cx
    loop s0

    mov ax,4c00h
    int 21h

codesg ends

end start