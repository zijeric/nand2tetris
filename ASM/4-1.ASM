assume cs:code
code segment
    ; ds:bx, cx times, 
    mov ax, 0020
    mov ds, ax

    mov bx, 0
    mov cx, 3FH
s:  mov [bx], bx
    inc bx
    loop s

    mov ax, 4c00h
    int 21h

code ends
end