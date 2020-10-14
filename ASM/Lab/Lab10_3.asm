assume cs:code, ds:data, ss:stack
data segment
    db 10 dup (0)
data ends

stack segment
    db 16 dup (0)
stack ends

code segment
start:
    mov ax,12666
    mov bx,data
    mov ds,bx
    mov si,0
    call dtoc

    mov dh,8
    mov dl,3
    mov cl,2
    call show_str

    mov ax,4c00H
    int 21H

dtoc:
    mov dx,0
    ; ds:[si] store string
    mov bx,10
    div bx
    ; ah=C
    mov cl, ah
    mov ah,0
    jcxz finish
    mov al, cl
    add al,30H
    push ax
    inc dx
    jmp short dtoc
finish:
    mov cx,dx
popDecimal:
    pop [si]
    loop popDecimal
    ret

show_str:
    mov ax,0b800H           ; 显存地址
    mov es,ax
    mov al,cl
    push ax
    
    sub dh, 2
    mov al, dh              ; ah:row
    mov bl, 0A0H
    mul bl
    push ax                 ; row(addr)

    sub dl, 2
    mov al, dl              ; al:colume
    mov bl, 2
    mul bl                  
    push ax                 ; col(addr)

    mov dl, cl              ; dl:color
    pop bp                  ; col
    pop bx                  ; row
    add bx,bp
    
show:
    mov cl,[si]
    mov ch,0
    jcxz ok             

    mov al, [si]
    mov ah, dl
    mov es:[bx], ax         ; 赋值data到显存
    ; mov es:[bx+bp+1],     ; 赋值color到显存
    inc si
    add bx, 2
    jmp short show

ok:
    ret

code ends
end start