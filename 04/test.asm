    @x  // 16
    M=0
    @sum  // 17
    M=0
    @numbers  // [18,27]

(LOOP)
    @x
    D=M  // D=x
    @10
    D=D-M  // D=(x-10)
    @END
    D;JGE  // 与伪代码符号完全相反：‘＞’ => ‘≤’, if (x>=10) goto END

    @numbers  // array
    A=M
    D=M  // D = *numbers
    @sum
    M=D+M  // D = *numbers + sum
    @numbers
    M=M+1  // numbers++, get the next one in array
    @x
    M=M+1
    @LOOP
    0;JMP

(END)
    @END
    0;JMP