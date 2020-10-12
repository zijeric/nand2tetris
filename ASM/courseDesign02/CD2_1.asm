assume cs:code, ss:stacksg
stacksg segment
    db 128 dup (0)
stacksg ends

code segment
start:
    mov ax,stacksg
    mov ss,ax
    mov sp,128

    call copy_boot

    mov ax,0
    push ax
    mov ax,7E00H
    push ax
    retf

    mov ax,4C00H
    int 21H

boot:
    ;mov ax,1000H
    jmp boot_start

boot_start:
    call init_register
    call clear_screen

    mov ax,4C00H
    int 21H

clear_screen:
    mov ax,0
    mov bx,0700H
    mov cx,2000


init_register:
    mov ax,0B800H
    mov es,ax

    mov ax,0
    mov ds,ax

    ret

boot_end:
    nop

copy_boot:
    mov bx,cs
    mov ds,bx
    mov si,offset boot

    mov bx,0
    mov es,bx
    mov di,7E00H

    mov cx,offset boot_end - offset boot
    cld
    rep movsb
    ret

code ends
end start