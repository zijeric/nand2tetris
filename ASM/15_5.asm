assume cs:code

stack segment 
    db 128 dup (0)
stack ends

code segment 
start:
    mov ax,stack
    mov ss,ax
    mov sp,128

    ; 将中断例程int9逐bit写入到0:204H 
    ; 不直接将int9的地址传入中断向量表的原因
    ; (防止程序执行完，自定义int9内存被改写，导致异常)
    push cs
    pop ds
    mov ax,0
    mov es,ax

    mov si,offset int9                  ; ds:[si] source
    mov di,204h                         ; es:[si] destination
    mov cx, offset int9end-offset int9  ; length
    cld                                 ; direction
    rep movsb

    ; 将原int9中断例程保存到0:200H(偏移)，0:202H(段地址)
    push es:[9*4]                       ; 0:4N -> 0:200H
    pop es:[200H]                       
    push es:[9*4+2]                     ; 0:4N+2 -> 0:202H
    pop es:[202H]

    ; 屏蔽中断
    cli                                 ; IF = 0

    ; 将中断向量表的int9向量 更换成 自定义的int9
    mov word ptr es:[9*4],204H
    mov word ptr es:[9*4+2],0
    sti                                 ; IF = 1

    mov ax,4C00H
    int 21H

int9:
    ; 此程序用到的寄存器都要push
    push ax
    push bx
    push cx
    push es

    ; 键盘输入到达60端口就会引发9号中断
    ; 从端口60H读出键盘的的输入
    in al, 60H

    ; 1.取中断类型码   (键盘输入固定9号中断，不处理)
    ; 2.标志寄存器入栈           pushf
    ; 3.IF=0, TF=0              进入中断例程后，TF、IF已经置零
    ; 4.CS、IP入栈              call dword ptr ds:[0]   
    ; 5.(IP)=(n*4), (CS)=(n*4+2)
    pushf
    call dword ptr cs:[200H]            ; int9执行时CS=0。 CS、IP入栈，IP=cs:[200H], CS=cs:[202H]

    ; 如果按下了F2，继续执行改变屏幕各个字符串的属性的值
    cmp al,3CH                          
    jne int9ret

    ; es:[bx]指向屏幕
    mov ax,0B800H
    mov es,ax
    mov bx,2
    ; 一屏幕4000字节
    mov cx,2000
s:  
    ; 循环改属性
    inc byte ptr es:[bx]
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