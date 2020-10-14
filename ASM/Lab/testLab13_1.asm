assume cs:code
data segment
    db 'Welcome to masm!BIU',0
data ends
code segment
start:
    mov dh,12
    mov dl,30
    mov cl,2
    mov ax,data
    mov ds,ax
    mov si,0
    int 7ch
    
    mov ax,4c00h
    int 21h
code ends
end start