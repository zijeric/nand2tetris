// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

// pseudo code:
// sum = 0
// i = RAM[0]

// LOOP:
//     // This is assembly, may be variable 'i' isnt so necessary like C program, can use RAM[0] directly.
//     if i ≤ 0 goto STOP  
//     i = i - 1
//     sum = sum + RAM[1]
//     goto LOOP

// STOP:
//     RAM[2] = sum
    
// END:
//     goto END

// implement:
    @sum
    M=0
    @R0
    D=M
    @i
    M=D
    @R2
    M=0

(LOOP)
    @i
    D=M
    @STOP
    D;JLE  // jump less equal; if R[0] <= 0 goto STOP
    @i
    M=M-1  // i--

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
