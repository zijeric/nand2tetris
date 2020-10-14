assume cs:code
code segment
start:
    mov ax,cs
    mov ds,ax
    mov si,offset lp
    mov ax,0
    mov es,ax
    mov di,200H
    mov cx, offset lp-offset ok

    cld
    rep movsb

    mov ax,0
    mov es,ax
    mov word ptr es:[7CH*4],200H
    mov word ptr es:[7CH*4+2],0
    mov ax,4C00H
    int 21H

lp: 
    push bp
    mov bp,sp
    dec cx
    jcxz ok
    add [bp+2],bx

ok:
    pop bp
    iret

    mov ax,4C00H
    int 21H

code ends
end start