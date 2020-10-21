assume cs:code,ds:data
data segment
    dw 12345
data ends
stacksg segment
    db 128 dup (0)
stacksg ends
code segment
start:
    mov ax,stacksg
    mov ss,ax
    mov sp,128
    mov ax,data
    mov ds,ax
    mov si,0

    mov bx,0B800H
    mov es,bx
    mov di, 160*18+64*2

    ; 被除数: ax存放高16位，dx存放低16位
    mov ax,ds:[si]                  ; ax=商，dx=余数
    mov dx,0

    call short_div

    mov ax,4C00H
    int 21H

short_div:
    mov cx,10
    div cx
    add dl,30H
    mov es:[di],dl
    mov byte ptr es:[di+1],00000010B
    ; 被除数为0则跳出并返回
    mov cx,ax
    jcxz short_div_ret
    ; 存放了除法结果的余数，在下一次除法前置零
    mov dx,0
    ; di -= 2，往前循环输出，好想法
    sub di,2
    jmp short_div

    ret

short_div_ret:
    ret

code ends
end start