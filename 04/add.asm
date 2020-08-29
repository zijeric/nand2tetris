// Adds up two numbers
// RAM[2] = RAM[0] + RAM[1]
// Usage: put the value that you wish to add
//        in RAM[0] and RAM[1]

    @R0
    D=M  // D = RAM[0]

    @R1
    D=D+M  // D = D + RAM[0]

    @R2
    M=D  // RAM[2] = D

    @6
    0;JMP  // 以无限循环结束程序