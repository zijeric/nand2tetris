assume cs:code
code segment
start:
    int 16H

    mov ax,4C00H
    int 21H

code ends

end start