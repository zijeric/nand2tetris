assume cs:codesg
codesg segment

    mov ax,4c00h
    int 21h

start:
    mov ax,0
s:
    nop
    nop

    ; 这里实际上是将，标号s2处的指令移动到了s处，
    ; 而jmp跳转地址是根据偏移地址改变的，在s处被替换的指令偏移位置，
    ; 是s2处的偏移量。
    mov di,offset s
    mov si,offset s2
    mov ax,cs:[si]
    mov cs:[di],ax

s0:
    jmp short s

s1:
    mov ax,0
    int 21h
    mov ax,0

s2:
    jmp short s1
    nop

; s2处偏移量为18H(nop)-22H(int 21H)=-AH，因为jmp指令占两个内存单元空间，
; 所以s处两个nop变成一条jmp指令，也就是从076A:000AH处，向上移动AH，     
; 也就是0，所以在跳转到s处，会显示jmp 0000，进而执行返回指令
codesg ends
end start