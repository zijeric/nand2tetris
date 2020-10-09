assume cs:code
stack segment
    db 128 dup (0)
stack ends
code segment
start:
    ;栈段地址
    mov ax,stack
    mov ss,ax
    mov sp,128
    
    ;将新中断例程保存到0:204h
    push cs
    pop ds
    mov ax,0
    mov es,ax
    
    mov di,204h
    mov si,offset int9
    mov cx,offset int9end-offset int9
    cld
    rep movsb
    
    ;原int 9中断向量表中的入口地址保存到0:200h~203h
    push es:[9*4]
    pop es:[200h]
    push es:[9*4+2]
    pop es:[202h]
    
    ;设置新int9中断例程入口地址
    cli
    mov word ptr es:[9*4],204h
    mov word ptr es:[9*4+2],0
    sti
    
    mov ax,4c00h
    int 21h
    
    ;新中断例程
int9:
    push ax
    push bx
    push es
    push cx
    ;从60端口读出键盘的输入
    in al,60h
    
    pushf
    call dword ptr cs:[200h]

    cmp al,3ch;判断是否按下F2
    jne int9ret
    
    mov ax,0b800h
    mov es,ax
    mov bx,1
    mov cx,2000;一屏幕4000字节
s:
    inc byte ptr es:[bx]
    add bx,2
    loop s
    
int9ret:
    pop cx
    pop es
    pop bx
    pop ax
    iret
int9end:nop
    
code ends
end start