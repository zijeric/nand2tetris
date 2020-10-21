assume cs:codesg,ds:datasg,ss:stacksg

datasg segment
    db 'welcome to masm!'
    db 02h,24h,71h;三种颜色
datasg ends

stacksg segment
    db 16 dup (0)
stacksg ends

codesg segment
        ;datasg段地址
start:    
        mov ax,datasg
        mov ds,ax
    
        ;stacksg段地址
        mov ax, stacksg
        mov ss,ax
        mov sp,10h
        
        ;目标地址
        mov ax,0b800h
        mov es,ax
        
        mov cx,3         ; 填充3行的字符串
        mov di,10h       ; 在datasg中的偏移量
        mov bx,6E0h      ; 表示第12行
        ; 第一层循环，3种颜色
s:        
        mov si,0         ; 在显示缓冲区中的偏移地址
        mov ah,ds:[di]
        push cx          ; 保存第一层循环的次数    ++
        push di          ; 保存第一层循环的偏移量  递增下一行
        
        mov di,0         ; 开始位偏移为0
        mov cx,16        ; 第二层循环16个字符
        ; 第二层循环，字符串
s0:        
        mov al,ds:[di]
        mov es:[bx+si+64],al;低位存字符
        mov es:[bx+si+64+1],ah;高位存属性
        inc di            ; data段的偏移++
        add si,2          ; 屏幕显示地址+=2
        loop s0
        
        pop di            ; 获取第一层循环的参数，并迭代
        pop cx
        inc di
        add bx,0a0h       ; 屏幕显示映射区的下一行
        loop s
        
        mov ax,4c00h
        int 21h
codesg ends
end start