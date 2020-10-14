assume cs:code,ds:stack
stack segment
    db 128 dup (0)
stack ends
code segment
start:
    mov ax,stack
    mov ss,ax
    mov sp,128

    push cs
    pop ds
    mov ax,0
    mov es,ax

    mov si,offset int7ch
    mov di,204H
    mov cx,offset int7ch_end-offset int7ch
    cld
    rep movsb

    ; 保存原7ch中断例程
    push es:[7ch*4]
    pop es:[200H]
    push es:[7ch*4+2]
    pop es:[202H]

    cli
    mov word ptr es:[7ch*4],204H
    mov word ptr es:[7ch*4+2],0
    sti

    mov ax,4C00H
    int 21H
; 参数说明
; 用ax传递功能号:0代表读，1代表写
; dx代表要读写的扇区的逻辑扇区号
; es:bx指向存储读写数据的内存区
    org 200H
int7ch:
    jmp short main
    table dw read,write
main:
    mov al,ah
    mov ah,0
    mov si,ax
    add si,si
    call word ptr table[si]

    mov ax,4C00H
    int 21H
read:
    push bx

    ; 扇区号
    mov ax,dx
    mov bl,18
    div bl
    inc ah
    mov cl,ah
    
    ;磁道号
    mov ah,0
    mov bl,80
    div bl
    mov ch,ah
    
    ;面号
    mov dh,al
    
    ;驱动器号
    mov dl,0
    
    ;读取
    mov ah,2
    
    ;读取的扇区数
    mov al,1
    
    pop bx
    int 13
    
    ret
    
write:
    push bx
    
    ;扇区号
    mov ax,dx
    mov bl,18
    div bl
    inc ah
    mov cl,ah
    
    ;磁道号
    mov ah,0
    mov bl,80
    div bl
    mov ch,ah
    
    ;面号
    mov dh,al

    ;驱动器号
    mov dl,0

    ;写入
    mov ah,3
    
    ;写入的扇区数
    mov al,1
    
    pop bx
    int 13h


int7ch_end:
    nop
code ends
end start
