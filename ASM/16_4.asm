assume cs:code
code segment
start:
    push cs
    pop ds
    mov ax,0
    mov es,ax

    mov si,offset int7ch
    mov di,200H
    mov cx,offset int7chend-offset int7ch
    cld
    rep movsb

    cli
    mov word ptr es:[7ch*4],200H
    mov word ptr es:[7ch*4+2],0
    sti
    
    mov ax,4c00h
    int 21h
    
int7ch:
    jmp short set
    table dw sub1,sub2,sub3,sub4
set:
    push bx
    ; 判断选择的功能是否越界 (n>3)
    cmp ah,3
    ja sret
    mov bl,ah
    mov bh,0
    add bx,bx
    
    call word ptr table[bx]
    
sret:
    pop bx
    ret

sub1:
    push bx
    push cx
    push es
    
    mov bx,0b800h
    mov es,bx
    mov bx,0
    mov cx,2000
sub1s:
    mov byte ptr es:[bx],' '
    add bx,2
    loop sub1s
    
    pop es
    pop cx
    pop bx
    ret
    
sub2:
    push bx
    push cx
    push es
    
    mov bx,0b800h
    mov es,bx
    mov bx,1
    mov cx,2000    
sub2s:
    ; 设置指定颜色
    and byte ptr es:[bx], 11111000B
    or es:[bx],al
    add bx,2
    loop sub2s
    
    pop es
    pop cx
    pop bx
    ret
    
sub3:
    push bx
    push cx
    push es
    
    ; 因为al的范围在0~7，所以设置背景色时，需要左移4位
    mov cl,4
    shl al,cl
    mov bx,0b800h
    mov es,bx
    mov bx,1
    mov cx,2000
sub3s:
    and byte ptr es:[bx],10001111B
    or es:[bx],al
    add bx,2
    loop sub3s
    
    pop es
    pop cx
    pop bx
    ret
    
sub4:
    push cx
    push si
    push di
    push es
    push ds
    
    mov si,0b800h
    mov es,si
    mov ds,si
    mov si,160
    mov di,0
    cld
    mov cx,24
sub4s:
    push cx
    mov cx,160
    rep movsb
    pop cx
    loop sub4s

    mov cx,80
    mov si,0

sub4s1:
    mov byte ptr [160*24+si],' '  ; 清空最后一行
    add si,2
    loop sub4s1
    
    pop ds
    pop es
    pop di
    pop si
    pop cx
    ret

int7chend:
    nop
    
code ends
end start