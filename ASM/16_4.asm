assume cs:code
code segment
start:
    ;储存程序到0:200h
    mov ax,0
    mov es,ax
    mov ax,cs
    mov ds,ax
    mov cx,offset setscreenend-offset setscreen
    mov di,200h
    mov si,offset setscreen
    cld
    rep movsb
    
    ;程序入口写入向量表
    mov word ptr es:[7ch*4],200h
    mov word ptr es:[7ch*4+2],0
    
    mov ax,4c00h
    int 21h
    
    org 200h
setscreen:
    jmp short set
    table dw sub1,sub2,sub3,sub4
set:
    push bx
    ;判断选择的功能是否越界
    cmp ah,4
    ja sret
    mov bl,ah
    mov bh,0
    add bx,bx
    
    call word ptr table[bx]
    
sret:
    pop bx
    iret

sub1:
    push es
    push bx
    push cx
    
    mov bx,0b800h
    mov es,bx
    mov bx,0
    mov cx,2000
sub1s:
    mov byte ptr es:[bx],' '
    add bx,2
    loop sub1s
    
    pop cx
    pop bx
    pop es
    iret
    
sub2:
    push bx
    push es
    push cx
    
    mov bx,0b800h
    mov es,bx
    mov bx,1
    mov cx,2000    
sub2s:
    ;设置指定颜色
    and byte ptr es:[bx],11111000b
    or es:[bx],al
    add bx,2
    loop sub2s
    
    pop cx
    pop es
    pop bx
    iret
    
sub3:
    push bx
    push cx
    push es
    
    ;因为al的范围在0~7，所以设置背景色时，需要右移4位
    mov cl,4
    shr al,cl
    mov bx,0b800h
    mov es,bx
    mov cx,2000
    mov bx,1
sub3s:
    and byte ptr es:[bx],10001111b
    or es:[bx],al
    add bx,2
    loop sub3s
    
    pop es
    pop cx
    pop bx
    iret
    
sub4:
    push bx
    push es
    push di
    push si
    push cx
    
    mov bx,0b800h
    mov es,bx
    mov ds,bx
    mov si,160
    mov di,0
    mov cx,24
sub4s:
    push cx
    mov cx,160
    cld
    rep movsb
    pop cx
    loop sub4s
    
    pop cx
    pop si
    pop di
    pop es
    pop bx
    iret
    
setscreenend:nop
code ends
end start