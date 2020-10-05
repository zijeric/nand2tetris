assume cs:code
data segment
    dw 123,12666,1,8,3,38
data ends
code segment
start:    mov ax,data
        mov ds,ax
        mov ax,0b800h
        mov es,ax
        
        mov si,0;表示数据段中的偏移地址
        mov di,0;将16进制数存储为10进制字符的偏移地址
        mov cx,6
        
        ;循环取出数据段中的数据
s:        mov ax,ds:[si]
        call show_str
        add si,2
        loop s
        
        mov ax,4c00h
        int 21h
        
show_str:    push cx
            mov bp,0
            
            ;方法：将该数除以10，得到的余数就是该数的一位数
            ;注意，我们判断数字转换结束的条件是商为0，但是loop是先减cx，再判断，所以需要inc cx
            ;处理一个16进制数，将每一位转换为10进制字符，压入栈中
            ;压栈原因：因为得到的数据是逆序的，所以需要入栈，再出栈，得到顺序的数
s0:            mov dx,0
            mov bx,10
            div bx
            ; ax=shang, dx=C
            mov cx,ax
            inc cx
            add dx,30h;数字转字符
            push dx;转换的数据入栈
            inc bp
            loop s0
            
            ;将栈中的数据取出，存储到显示缓冲区
            mov cx,bp
            ;mov bx,0
store_str:    pop dx
            mov byte ptr es:[di+500h+4],dl;低位数据，500h表示显示器第8行，4表示第3列
            mov byte ptr es:[di+500h+4+1],2;高位属性，2就是00000010，绿色
            add di,2
            loop store_str

           ; pop cx
            ;add di,2;方便查看
            ret
            
code ends
end start