assume cs:codesg, ds:datasg, ss:stacksg
datasg segment
    db 'welcome to masm!'
    db 02h,24h,71h
datasg ends

stacksg segment
    db 16 dup (0)
stacksg ends

codesg segment
start:
    mov ax,datasg
    mov ds,ax
    mov ax,stacksg
    mov ss,ax
    mov sp,16

    mov ax,0b800h
    mov es,ax

    mov di,16               ; attribute
    mov cx,3                ; loop attribute times
    mov bx,8c0h+64           ; location of SCREEN
    
AttributeLOOP:  
    mov ah,ds:[di]
    push cx
    push di

    mov cx,16
    mov di,0                ; data段字符指针
    mov si,0                ; SCREEN段字符指针

StringLOOP:
    mov al,ds:[di]
    mov es:[bx+si],ax

    inc di
    add si,2
    loop StringLOOP

    pop di
    pop cx
    inc di
    add bx,160
    loop AttributeLOOP

    mov ax,4c00h
    int 21h
codesg ends
end start