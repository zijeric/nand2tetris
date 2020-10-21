assume cs:code
stacksg segment
    db 128 dup (0)
stacksg ends
code segment

start:
    mov ax,stacksg
    mov ss,ax
    mov sp,128

    ; source
    push cs
    pop ds
    mov si,offset int9

    ; destination 0:204H
    mov ax,0H
    mov es,ax
    mov di,204H

    ; length
    mov cx,offset int9end - offset int9
    cld
    rep movsb

    ; store old int9
    push es:[9*4]
    pop es:[200H]
    push es:[9*4+2]
    pop es:[202H]

    ; modify the table
    cli
    mov word ptr es:[9*4], 204H
    mov word ptr es:[9*4+2], 0H
    sti

    mov ax,4C00H
    int 21H

int9:
    push ax
    push bx
    push cx
    push es

    in al, 60H

    ; fake int9
    pushf
    call dword ptr cs:[200H]

    cmp al,9EH
    jne int9ret

    mov ax,0B800H
    mov es,ax
    mov bx,0
    mov cx,2000
s:
    mov byte ptr es:[bx], 'A'
    add bx,2
    loop s

int9ret:
    pop es
    pop cx
    pop bx
    pop ax
    iret

int9end:
    nop
    
code ends
end start