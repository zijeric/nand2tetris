assume cs:code,es:data,ds:table

data segment
    db '1975','1976','1977','1978','1979','1980','1981','1982','1983'
    db '1984','1985','1986','1987','1988','1989','1990','1991','1992'
    db '1993','1994','1995'
    ; 以上是表示21年的字符串 4 * 21 = 84

    dd 16,22,382,1356,2390,8000,16000,24486,50065,97479,140417,197514
    dd 345980,590827,803530,1183000,1843000,2759000,3753000,4649000,5937000
    ; 以上是表示21年公司总收入的dword型数据 4 * 21 = 84

    dw 3,7,9,13,28,38,130,220,476,778,1001,1442,2258,2793,4037,5635,8226
    dw 11542,14430,15257,17800
    ; 以上是表示21年公司雇员人数的21个word型数据 2 * 21 = 42
data ends

table segment
    db 21 dup ('year summ ne ?? ') ; 'year summ ne ?? ' 刚好16个字节
table ends

code segment
start:  
    mov ax,data
    mov es,ax
    mov ax,table
    mov ds,ax
    mov bx,0
    mov cx,21
    mov si, 0

s0:  
    ; 年份
    mov ax,es:[si]
    mov [bx].0[si], ax
    add si,2                ; si=2
    mov ax,es:[si]
    mov [bx].0[si],ax
    sub si,2                ; si=0
    ; 收入
    mov ax,es:[si].54h      ; 54h:84为收入数据段
    mov [bx].05H[si],ax
    add si,2                ; si=2
    mov ax,es:[si].54h
    mov [bx].05H[si],ax
    sub si,2                ; si=0

    ; 下一次循环，bx到下一行，si+=4到下一条数据
    add bx,12
    add si,4
    loop s0
    ; 重置循环条件
    mov bx,0
    mov cx,21
    mov si,0

s1: 
    ; 公司总人数
    mov ax,es:[si].0a8h      ; 0a8h:168为人数数据段
    mov [bx].0AH[si],ax
    ; 计算人均收入
    mov ax,[bx].05H[si]
    mov dx,[bx].07H[si]
    div word ptr [bx].0AH[si]
    mov [bx].0DH[si],ax

    add bx,14               ; 下一行
    add si,2                ; data段下一个数据
    loop s1
    mov ax,4c00h
    int 21h

code ends
end start