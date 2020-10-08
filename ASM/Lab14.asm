assume cs:code
code segment
    data: db '0000/0000/0000 0000:0000:0000'
    dataend: nop
    num: db 9,8,7,4,2,0
start:
    mov si,offset num
    mov ax,cs
    mov ds,ax
    mov di,offset data
    mov cx,6

get:
    mov al,ds:[si]
    out 70H,al
    in al,71H
    mov ah,al
    mov cl,4
    shr ah,cl
    and al,00001111B
    add ah,30H
    add al,30H
    mov ds:[di],ax
    inc si
    add di,3
    loop get

    ; 把cs:data的数据转移到0B800:[160*13+40*2]
    ;mov si,offset data
    ;mov bx,0B800H+160*13+40*2               
    ;mov es,bx
    ;mov di,0
    ;mov cx, offset dataend-offset data
    ;cld
    ;rep movsb

    mov ax,4C00H
    int 21H
code ends
end start