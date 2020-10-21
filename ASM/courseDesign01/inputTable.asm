assume cs:code,ds:data,ss:stacksg,es:table
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

tablesg segment
    db 21 dup ('year summ ne ?? ') ; 'year summ ne ?? ' 刚好16个字节
tablesg ends
stacksg segment
    db 128 dup (0)
stacksg ends
code segment
start:
    mov ax,stacksg
    mov ss,ax
    mov sp,128
    
    call input_table

    mov ax,4C00H
    int 21H
;===================================
input_table:
    ; source
    mov ax,data
    mov ds,ax
    mov si,0
    ; destination
    mov bx,table
    mov es,bx
    mov di,0

    mov bx,21*4*2
    mov cx,21

fill_table:
    push ds:[si]
    pop es:[di]
    push ds:[si+2]
    pop es:[si+2]

    mov ax,ds:[si+21*4]
    mov dx,ds:[si
;db 21 dup ('year summ ne ?? ') ; 'year summ ne ?? ' 刚好16个字节

    

    ret
code ends
end start