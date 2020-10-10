assume cs:code
code segment
start:
    mov ax,cs                   ; source address
    mov ds,ax
    mov si,offset funcName
    mov ax,0                    ; destination address
    mov es,ax
    mov di,200H
    mov cx,offset funcEnd-offset funcName

    cld                         ; 正向
    rep movsb                   ; 将源地址的程序转移到目标地址
    mov ax, 0
    mov es,ax
    mov word ptr es:[7CH*4],200H
    mov word ptr es:[7CH*4+2],0
    mov ax,4C00H
    int 21H

funcName:
    push ax
    push ss
    push bp
    push dx
    mov ax,0B800H
    mov ss,ax

    mov ax,0B800H
    mov ss,ax

    mov al,160
    mul dh
    mov bp,ax

    dec dl
    mov dh,0
    add dx,dx                   ; dx*=2
    add bp,dx

show:
    cmp byte ptr [si],0
    je ok

    mov al,[si]
    mov byte ptr [bp],al
    mov [bp+2],cl
    inc si
    add bp,2
    jmp short show

ok:
    pop dx
    pop bp
    pop ss
    pop ax
    iret
funcEnd:
    nop
code ends
end start