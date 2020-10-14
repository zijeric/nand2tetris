assume cs:code
code segment
start:
    ;将程序移入指定空闲区域
    mov ax,cs
    mov ds,ax
    mov si,offset do7c
    mov ax,0
    mov es,ax
    mov di,200h
    mov cx,offset do7cend-offset do7c
    cld
    rep movsb
    ;设置中断向量
    mov ax,0
    mov es,ax
    mov word ptr es:[7ch*4+2],0
    mov word ptr es:[7ch*4],200h
    
    mov ax,4c00h
    int 21h
    
do7c:
    ;设置IP位置
    push bp
    mov bp,sp
    add ss:[bp+2],bx
    pop bp
    iret
do7cend:nop
    
code ends
end start