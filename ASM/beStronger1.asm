assume cs:code,ss:stacksg
stacksg segment
    db 128 dup (0)
stacksg ends
code segment
start:
    mov ax,stacksg
    mov ss,ax
    mov sp, 128

    ;mov al,00100000B
    ;mov ah,2
    ;call setscreen

    ; source
    push cs
    pop ds
    mov si,offset int7ch

    ; destination
    mov ax,0
    mov es,ax
    mov di,204H

    ; length 
    mov cx,offset int7ch_end - offset int7ch
    ; dirction 
    cld
    rep movsb

    ; store old int7ch
    push es:[7ch*4]
    pop es:[200H]
    push es:[7ch*4+2]
    pop es:[202H]

    ; modify the table
    cli
    mov word ptr es:[7ch*4],204H
    mov word ptr es:[7ch*4+2],0
    sti

    mov ax,4C00H
    int 21H

;setscreen:
int7ch:
    jmp short set
tablesg dw clear,setfront,setbackground,rollaline

set:
    push bx         ; need modify bx
    
    cmp ah,3        ; N*2>6, jmp to ret
    ja sret
    mov bl,ah
    mov bh,0
    add bx,bx       ; bx*=2

    call word ptr tablesg[bx]


sret:
    pop bx
    ret             ; pop IP

clear:
    push bx
    push cx
    push es
    mov bx,0B800H
    mov es,bx
    mov bx,0
    mov cx,2000
clears:
    mov byte ptr es:[bx], ' '
    add bx,2
    loop clears
    pop es
    pop cx
    pop bx
    ret

setfront:
    push bx
    push cx
    push es

    mov bx,0B800H
    mov es,bx
    mov bx,1                ; 属性位
    mov cx,2000
setfronts:
    ; 改写位：通过and操作使该字节对应位为0，再与对应长度的寄存器进行or操作
    and byte ptr es:[bx],11111000B      ; 设置属性位的012位为0，为下一指令or作准备
    or es:[bx],al                       ; 设置属性位为al所存储的，其他不变
    add bx,2
    loop setfronts

    pop es
    pop cx
    pop bx
    ret

setbackground:
    push bx
    push cx
    push es

    mov bx,0B800H
    mov es,bx
    mov bx,1
    mov cx,2000

setbackgrounds:
    and byte ptr es:[bx],10001111B
    or es:[bx],al
    add bx,2
    loop setbackgrounds
    pop es
    pop cx
    pop bx
    ret

rollaline:
    push cx
    push si
    push di
    push es
    push ds

    ; es,ds:B800H  si:160
    mov si,0B800H
    mov es,si
    mov ds,si
    mov si,160
    mov di,0
    cld
    mov cx,24                   ; 前24行
rollalines:
    push cx                     ; 保存行数循环
    mov cx,160                  ; 160列
    rep movsb                   ; 粘贴
    pop cx
    loop rollalines

    mov cx,80                   ; 最后一列，仅处理字符，因而160/2 = 80
    mov si,0

rollalines1:
    mov byte ptr [160*24+si], ' '
    add si,2
    loop rollalines1

    pop ds
    pop es
    pop di
    pop si
    pop cx
    ret
int7ch_end:
    nop
code ends
end start