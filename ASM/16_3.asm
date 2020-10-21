assume cs:code
code segment
start:
    mov al,60;度数
    call showsin
    
    mov ax,4c00h
    int 21h

showsin: 
    jmp short show
    table dw ag0,ag30,ag60,ag90,ag120,ag150,ag180
    ag0   db '0',0
    ag30  db '0.5',0
    ag60  db '0.866',0
    ag90  db '2',0
    ag120 db '0.866',0
    ag150 db '0.5',0
    ag180 db '0',0

show:
    push bx
    push es
    push di

    mov bx,0b800h
    mov es,bx
    mov di,160*12+40*2
    
    mov ah,0       
    mov bl,30      
    div bl
    mov bl,al      
    mov bh,0
    add bx,bx     
    mov bx,cs:table[bx]

shows:
    mov ah,cs:[bx]     
    cmp ah,0          
    je showret
    mov es:[di],ah     
    inc bx            
    add di,2
    jmp short shows

showret:
    pop di
    pop es
    pop bx
    ret
code ends
end start
