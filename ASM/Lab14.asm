assume cs:code
code segment
    data: db '00/00/00 00:00:00'
    ;        '20/10/08 19:30:55'
    dataend: nop
    num: db 9,8,7,4,2,0
start:
    mov si,offset num
    mov ax,cs
    mov ds,ax
    mov di,offset data
    mov cx,6

get:
    push cx
    mov al,ds:[si]
    out 70H,al
    in al,71H
    mov ah,al
    mov cl,4
    shr al,cl
    and ah,00001111B
    add ah,30H
    add al,30H
    mov ds:[di],ax
    inc si
    add di,3
    pop cx
    loop get

    ; 把cs:data的数据转移到0B800:[160*13(BE0)+40*2]
    mov bx,cs
    mov ds,bx
    mov si,offset data
    mov bx,0b800H
    mov es,bx
    mov di,0BE0H+40*2
    mov cx, 17

display:
    mov bx,ds:[si]
    mov es:[di],bx
    
    inc si
    add di,2
    loop display
    ; 不可以用rep movsb, 容易把字符串的属性覆盖
    ;cld
    ;rep movsb
    

    mov ax,4C00H
    int 21H
code ends
end start