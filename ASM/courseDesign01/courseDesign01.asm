assume cs:code,es:data,ds:table,ss:stack
data segment ;按照书本定义数据段
	db '1975','1976','1977','1978','1979','1980','1981'
	db '1982','1983','1984','1985','1986','1987','1988'
	db '1989','1990','1991','1992','1993','1994','1995'
	
	dd 16,22,382,1356,2390,8000,16000
	dd 24486,50065,97479,140417,197514,345980,590827
	dd 803530,1183000,1843000,2759000,3753000,4649000,5937000
	
	dw 3,7,9,13,28,38,130
	dw 220,476,778,1001,1442,2258,2793
	dw 4037,5635,8226,11542,14430,15257,17800
data ends
 
table segment
	db 'year      su        n         ?        ',0 
table ends
 
stack segment
	db 64 dup (0)
stack ends
 
code segment
start:  
	mov ax, data
	mov es, ax
	mov ax, table
	mov ds, ax               ;使用ds和es寄存器作为段寄存器
	mov ax, stack
	mov ss, ax
	mov sp, 30h   
	mov si, 0
	mov di, 0
	mov bp, 0                ;初始偏移地址置0
	mov cx, 21               ;21行数据故loop21次   
s:  
	push cx
	mov bx, 0   
	mov cx, es:[bp]          ;使用寄存器相对寻址方式
	mov ds:[bx], cx
	mov cx, es:[bp+2]
	mov ds:[bx+2], cx   
		   
	mov ax, es:[bp+84]       ;年份数据与对应总收入偏移地址相差84字节（54h）
	mov dx, es:[bp+84+2]
	push ax
	push dx
	mov si, 10
	call dtoc
		   
	mov ax, es:[di+168]
	mov dx, 0
	push ax
	mov si, 20
	call dtoc
 
	pop cx
	pop dx
	pop ax
	call divdw
	mov si, 30
	call dtoc
	
	mov ax, di
	mov bl, 2
	div bl
	inc ax
	mov dh,al                ;行号
	mov dl,1                 ;列号
	mov cl,2                 ;颜色
	call show_str
	
	add bp,4                 ;年份与总收入每个数据占4字节
	add di,2                 ;雇员人数占2字节
	pop cx
loop s     	   
mov ah,4ch
int 21h
	  
dtoc:
	push cx
	push di
	mov di, 0
d:	
	mov cx, 10
	call divdw               ;余数存在cx
	add cx, 30h              ;十进制数字加30h为其ASCII码
	push cx
	inc di
	mov cx, ax               ;若ax为0则结束
	jcxz over
	jmp short d
over:                        ;使用栈翻转数字顺序
	pop ds:[si]
	inc si
	dec di
	mov cx, di
	jcxz got
	jmp short over
got:
	mov al, ' '              ;show_str遇0结束，故将数字串最后的0变为空格
	mov ds:[si], al
	pop di
	pop cx
ret
 
 
show_str: 
	push es
	push di
	push si
	push cx
	mov ax,0b800h
	mov es,ax                ;使用ds和es寄存器作为段寄存器
	mov si, 0
	
	mov al,160
	mul dh
	mov dh,0
	dec dx
	add ax,dx
	add ax,dx
	mov di,ax                ;将计算出的第一个字符的偏移地址存在di中
	mov al, cl
	   
sh: mov bl, ds:[si]                  
	mov es:[di], bl          ;存入字符
	mov es:[di+1], al        ;设置属性字节
	
	inc si
	add di,2                 ;每个字符占2个字节
	mov cx, ds:[si]
	jcxz ok                  ;遇0结束
    jmp short sh 
ok: pop cx
	pop si
	pop di
	pop es
ret
 
 
divdw:
	push bp
	push bx
	mov bp, ax               ;将被除数的低16位暂存于bp
	mov ax, dx           
	mov dx, 0            
	div cx                   ;H/N，结果为(DX)=rem(H/N),(AX)=int(H/N)
	mov bx, ax               ;将int(H/N)暂存至bx
	mov ax, bp               ;rem(H/N)*65536+L，结果为32位，高位存于DX，低位存于AX
	div cx                   ;[rem(H/N)*65536+L]/N，结果商存于AX，余数存于DX
	mov cx, dx               ;将余数移至CX
	mov dx, bx               ;int(H/N)*65536后高位存于DX，低位为0
	pop bx
	pop bp
ret
		   	   
code ends
end start