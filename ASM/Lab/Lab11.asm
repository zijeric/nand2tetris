assume cs:code, ds:data
data segment
    db "Beginer's All-purpose Symbolic Instruction Code.",0
data ends

code segment
start:
    mov ax,data
    mov ds,ax
    mov si,0
    call letterc

    mov ax,4C00H
    int 21H

letterc:
    ; 遇0结束函数
    mov cl,[si]
    mov ch,0
    jcxz ok
    ; 判断是否小写字母
    cmp cl,61H
    jb upCase
    cmp cl,7AH
    ja upCase
    ; 将小写字母改大写
    and cl, 11011111B
    mov [si],cl

upCase:
    ; advance
    inc si
    loop letterc

ok: ret
code ends
end start