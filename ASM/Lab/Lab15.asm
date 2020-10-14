assume cs:code
stacksg segment
    db 128 dup (0)
stacksg ends
code segment
start:
    mov ax,stacksg
    mov ss,ax
    mov sp,128
    ; 安装新的int9H中断例程
    ; 1. 将自定义的int9H中断例程粘贴进0:204H
    ; 2. 存储原来的int9H中断例程在0:200H~0:203H
    ; 3. 在IF=0的条件下，修改中断向量表
    ; 1. 粘贴，rep movsb: source,destination,length,direction
    ; source: cs:(offset int9)
    push cs
    pop ds
    mov si,offset int9

    ; destination
    mov ax,0
    mov es,ax
    mov di,204H
    ; length --> cx
    mov cx,offset int9end - offset int9
    ;direction
    cld
    rep movsb

    ; 利用栈的push/pop进行赋值，存储原来的int9H中断例程
    push es:[9*4]
    pop es:[200H]
    push es:[9*4+2]
    pop es:[202H]

    ; 修改中断向量表，高地址(段)，低地址(偏移)
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