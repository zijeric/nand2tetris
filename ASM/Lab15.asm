assume cs:code
stack segment
    db 128 dup (0)
stack ends
code segment
start:
    mov ax,stack
    mov ss,ax
    mov sp,128
    ; 将我们自定义的int9中断例程放在0:204H
    push cs
    pop ds
    mov ax,0
    mov es,ax

    mov si,offset int9
    mov di,204H
    mov cx,offset int9end-offset int9
    cld
    rep movsb

    ; 存储中断向量表中 原int9中断例程的地址
    push es:[9*4]
    pop es:[200H]
    push es:[9*4+2]
    pop es:[202H]

    ; *important* 修改中断向量表，真正使我们自定义的int9成为中断例程
    cli
    mov word ptr es:[9*4], 204H
    mov word ptr es:[9*4+2], 0
    sti

    mov ax,4C00H
    int 21H

int9:
    push ax
    push bx
    push cx
    push es

    ; TODO
    in al,60H

    ; 仿制原int 9
    pushf
    call dword ptr cs:[200H]

    cmp al,9EH
    jne int9ret

    mov ax,0B800H
    mov es,ax
    mov bx,0
    ; 屏幕有400bits
    mov cx,2000
chageAttr:
    mov byte ptr es:[bx],'A'
    add bx,2
    loop chageAttr

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