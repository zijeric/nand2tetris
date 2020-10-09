assume cs:code
stack segment
    db 128 dup (0)
stack ends
data segment
    dw 0,0
data ends
code segment
start:
    ;栈段地址
    mov ax,stack
    mov ss,ax
    mov sp,128
    
    ;数据段地址
    mov ax,data
    mov ds,ax
    
    ;原9号中断向量表中的入口地址保存到ds:[0],ds:[2]
    mov ax,0
    mov es,ax
    push es:[9*4]
    pop ds:[0]
    push es:[9*4+2]
    pop ds:[2]
    
    ;设置新int9中断例程入口地址
    mov word ptr es:[9*4],offset int9
    mov es:[9*4+2],cs
    
    ;设置显示缓冲区
    mov ax,0b800h
    mov es,ax
    mov di,160*12+40*2
    mov ah,'a'
s:
    ;显示字符
    mov es:[di],ah
    call delay
    inc ah
    cmp ah,'z'
    jna s
    
    ;恢复原来int9中断例程入口地址
    mov ax,0
    mov es,ax
    push ds:[0]
    pop es:[9*4]
    push ds:[2]
    pop es:[9*4+2]
    
    mov ax,4c00h
    int 21h

    ;延时
delay:
    push dx
    push ax
    ;10 0000h写入高地址和低地址
    mov dx,10h
    mov ax,0
s1:
    ;只能使用sub,不能dec
    sub ax,1
    sbb dx,0
    cmp dx,0
    jne s1
    cmp ax,0
    jne s1
    
    pop ax
    pop dx
    ret
    
    ;新中断例程
int9:
    push ax
    push bx
    push es
    ;从60h端口读出键盘的输入
    in al,60h
    
    pushf
    pushf
    ;取出标志寄存器
    pop bx
    ;IF=0，TF=0
    and bh,11111100b
    ;保存标志寄存器
    push bx
    popf
    
    call dword ptr ds:[0];对int指令进行模拟，调用原来的int9中断例程，处理其他硬件细节

    cmp al,1;判断是否按下ESC
    jne int9ret
    
    mov ax,0b800h
    mov es,ax
    inc byte ptr es:[di+1];属性值+1
int9ret:
    pop es
    pop bx
    pop ax
    iret
    
code ends
end start
