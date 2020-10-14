assume cs:code, ss:stacksg

stacksg segment
    dw 8 dup (0)
stacksg ends

code segment
start:
    mov ax,stacksg
    mov ss,ax
    mov sp,10H
    mov ax,4240H            ; L
    mov dx,000FH            ; H
    mov cx,0AH              ; N
    call divdw

    mov ax,4c00H
    int 21H

divdw:
    ;push cx
    ;push dx
    push ax
    ; H/N
    ;mov ax,0                ; L=0
    mov ax,dx
    mov dx,0
    div cx
    ; ax = int(H/N)，dx = rem(H/N)
    ; ax为H，dx亦为H
    ;mov bx, ax              ; bx=int(H/N)
    pop ax                  ; ax=L
    div cx
    ; ax=int(rem../N), dx=rem...
    push bx                 ; H
    ;push ax                ; L
    push dx                 ; C
    pop cx
    ;pop ax
    pop dx
    ret

code ends
end start