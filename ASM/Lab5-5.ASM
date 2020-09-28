assume cs:code

a segment 
    db 1,2,3,4,5,6,7,8     ; db: 1bit, dw:2bits
a ends

b segment
    db 1,2,3,4,5,6,7,8
b ends

c segment
    db 0,0,0,0,0,0,0,0
c ends

code segment
start:
    mov ax,a
    mov ds,ax
    mov ax,b
    mov es,ax
    mov bx,0
    mov cx,8
    mov dl,0
    mov ax,c
    mov ss,ax

s:  mov dl,ds:[bx]
    add dl,es:[bx]
    mov ss:[bx],dl          ; ss可单纯当作段寄存器，ss:sp才是stack
    inc bx
    loop s
    mov ax,4c00h
    int 21h
code ends

end start