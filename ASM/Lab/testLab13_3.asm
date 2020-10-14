assume cs:code
data segment
    db 'conversation',0
data ends
code segment
start:
    mov ax,data
    mov ds,ax
    mov si,0
    mov ax,0B800H
    mov es,ax
    mov di,13*160
    mov bx,offset flag-offset flagend
flag:
    cmp byte ptr ds:[si],0
    je ok
    mov al,ds:[si]
    mov byte ptr es:[di],al
    mov byte ptr es:[di+2],2
    inc si
    add di,2
    int 7CH
flagend:
    nop
ok:
    mov ax,4C00H
    int 21H
code ends
end start