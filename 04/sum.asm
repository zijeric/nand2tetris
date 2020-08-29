// Computes RAM[1] = 1+2+ ... +n
// Usage: put a number (n) in RAM[0]

    @R0
    D=M
    @n
    M=D  // n = R0
    @i
    M=1  // i = 1
    @sum
    M=0  // sum = 0

(LOOP)
    @i
    D=M
    @n
    D=D-M  // i - n
    @STOP
    D;JGT  // jump (if) greater than; if i > n goto STOP

    @sum
    D=M
    @i
    D=D+M
    @sum
    M=D
    @i
    M=M+1
    @LOOP
    0;JMP

(STOP)
    @sum
    D=M
    @R1
    M=D  // R[1] = sum

(END)
    @END
    0;JMP