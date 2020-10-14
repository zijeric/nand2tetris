assume cs:code
code segment
start:
    mov ah,0
    mov dx,36
    mov bx,0b800h
    mov es,bx
    mov bx,160*13+40*2
    
    int 7ch
    
    mov ax,4c00h
    int 21h
code ends
end start