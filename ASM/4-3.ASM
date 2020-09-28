assume cs:code
code segment

    mov ax,cs   ; cs/code
    mov ds,ax
    mov ax,0020h
    mov es,ax           
    mov bx,0
    mov cx,23   ; debug 程序时查看cx寄存器(存储程序的字节长度)的值，(cx-5)

s:  mov al,[bx]
    mov es:[bx],al
    inc bx
    loop s

    mov ax,4c00h
    int 21h

code ends
end