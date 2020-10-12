assume cs:code,ds:data
data segment
    dw 128 dup (0)
data ends
code segment
start:
    mov ax,0B800H
    mov es,ax
    mov bx,0

    mov al,8  ; 写入的扇区数
    mov ch,0  ; 磁道号
    mov cl,1  ; 扇区号
    mov dl,0  ; 驱动器号
    mov dh,0  ; 磁头号(面)
    
    mov ah,2  ; 2是读，3是写
    int 13h

    mov ax,4C00H
    int 21H

code ends
end start