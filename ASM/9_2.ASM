assume cs:code
code segment
start:
    mov ax,2000h
    mov ds,ax
    mov bx,0

    mov al,'b'
    mov ds:[bx],al
    mov al,'i'
    mov ds:[bx+1],al

s:  mov cl,ds:[bx]
    mov ch,0
    jcxz ok
    inc bx
    loop s

ok: mov dx,bx
    mov ax,4c00h
    int 21h

code ends
end
