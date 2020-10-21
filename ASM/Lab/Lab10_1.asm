assume cs:code, ss:stacksg, ds:data
data segment
    db 'welcome to masm!',0
data ends

stacksg segment
    db 16 dup (0)
stacksg ends

code segment
start:
    mov dh,8            ; row
    mov dl,3            ; col
    mov cl,2            ; color
    mov ax,data         ; [si]: data[si]
    ; stack初始化，放进function
    mov ds,ax
    mov si,0
    mov ax,stacksg
    mov ss,ax
    mov sp,10H
    call show_str

    mov ax,4c00H
    int 21H

show_str:
    mov ax,0b800H           ; 显存地址
    mov es,ax
    mov al,cl
    push ax
    
    sub dh, 2
    mov al, dh              ; ah:row
    mov bl, 0A0H
    mul bl
    push ax                 ; row(addr)

    sub dl, 2
    mov al, dl              ; al:colume
    mov bl, 2
    mul bl                  
    push ax                 ; col(addr)

    mov dl, cl              ; dl:color
    pop bp                  ; col
    pop bx                  ; row
    add bx,bp
    
show:
    mov cl,[si]
    mov ch,0
    jcxz ok             

    mov al, [si]
    mov ah, dl
    mov es:[bx], ax         ; 赋值data到显存
    ; mov es:[bx+bp+1],     ; 赋值color到显存
    inc si
    add bx, 2
    jmp short show

ok:
    ret

code ends
end start