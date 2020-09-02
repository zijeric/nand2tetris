// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

// pseudo code:
// sum = 0

// LOOP:
//     if RAM[1] ≤ 0 goto STOP
//     RAM[1] = RAM[1] - 1
//     sum = sum + RAM[0]
//     goto LOOP

// STOP:
//     RAM[2] = sum
    
// END:
//     goto END

// implement:
    @sum
    M=0
    @R2
    M=0

(LOOP)
    @R0
    D=M
    @STOP
    D;JLE  // jump less equal; if R[0] <= 0 goto STOP
    @R0
    M=M-1  // R0--

    @R1
    D=M
    @sum
    M=M+D  // D = sum + R[0]
    @LOOP
    0;JMP

(STOP)
    @sum
    D=M
    @R2
    M=D  // R[2] = sum

(END)
    @END
    0;JMP