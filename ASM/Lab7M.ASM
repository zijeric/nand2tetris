assume cs:code,ds:data,es:table

data segment
db '1975','1976','1977','1978','1979','1980','1981','1982','1983'
db '1984','1985','1986','1987','1988','1989','1990','1991','1992'
db '1993','1994','1995'
;以上是表示21年的字符串 4 * 21 = 84

dd 16,22,382,1356,2390,8000,16000,24486,50065,97479,140417,197514
dd 345980,590827,803530,1183000,1843000,2759000,3753000,4649000,5937000
;以上是表示21年公司总收入的dword型数据 4 * 21 = 84

dw 3,7,9,13,28,38,130,220,476,778,1001,1442,2258,2793,4037,5635,8226
dw 11542,14430,15257,17800
;以上是表示21年公司雇员人数的21个word型数据 2 * 21 = 42
data ends

table segment
    db 21 dup ('year summ ne ?? ') ; 'year summ ne ?? ' 刚好16个字节
table ends

code segment
start:  mov ax,data
        mov ds,ax
        mov ax,table
        mov es,ax

        mov cx,21    ;外层循环每一年的情况
        mov bx,0    ;表示第几年的情况
        mov di,0    ;存储年份地址
        
        mov bp,0    ;1.获取到data段中雇员数数据 2.保存bx的值
s0:     push cx    
        mov cx,4
        mov si,0    ;表示这一年中的第几个
        
        ;年份
s:      mov al,ds:[di]        ;一位一位保存字符
        mov es:[bx+si],al
        
        ;收入
        mov al,ds:[di+54h]    
        mov es:[bx+si+5h],al
        
        inc si
        inc di
        loop s
        
        ;雇员数
        mov ax,ds:[bp+0a8h]    ;单独保存数据
        mov es:[bx+0ah],ax
        add bp,2    ;到段的下一个字的数据
        
        ;人均收入
        mov dx,es:[bx+7h]    ;高16位被除数
        mov ax,es:[bx+5h]    ;低16位被除数
        push bx    ;存放除数
        push bp    ;存放bx地址
        mov bp,bx
        mov bx,es:[bp+0ah]
        div bx
        mov es:[bp+0dh],ax        ;余数在dx中，只保存了商
        pop bp
        pop bx
        
        pop cx
        add bx,10h
        loop s0
        
        mov ax,4c00h
        int 21h

code ends
end start