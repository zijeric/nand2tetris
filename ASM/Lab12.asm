assume cs:code
code segment
start:
    mov ax,cs
    mov ds,ax
    mov si,offset do0               ; 设置ds:si指向源地址(cs:do0)
    mov ax,0
    mov es,ax
    mov di,200H                     ; 设置es:di指向目标地址(0:200H)
    mov cx,offset do0end-offset do0 ; 设置cx为传输长度
    cld                             ; 设置rep movsb传输方向为 正向
    rep movsb                       ; 循环传输完毕
    ; 设置中断向量表
    mov ax,0
    mov es,ax
    mov word ptr es:[0*4],200H      ; 低地址存储偏移地址200H
    mov word ptr es:[0*4+2],0       ; 高地址存储段地址0

    int 0H                          ; 制造0中断
    mov ax,4C00H
    int 21H

do0:
    jmp short do0start              ; 跳过字符串
    db 'overflow!'                  ; 字符串存放在code段不会被改写(放在其他段程序结束后可能被改写，但是程序的内存段不变，因而显示其他garbage)

do0start:
    mov ax,cs
    mov ds,ax
    mov si,202H                     ; 设置ds:si指向字符串

    mov ax,0B800H
    mov es,ax
    mov di,13*160+36*2              ; 设置es：di指向显存的中间位置

    mov cx,9                        ; 设置cx为字符串长度
s:
    mov al,[si]
    mov es:[di],al
    mov byte ptr es:[di+2],4        ; attr: color red
    inc si
    add di,2
    loop s

    mov ax,4C00H
    int 21H
            
do0end:
    nop

code ends
end start
    