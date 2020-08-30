// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

    // max = 24575, store as a address
    @24575
    D=A
    @max
    M=D

    // current = SCREEN, store as a address
    @SCREEN
    D=A
    @current
    M=D

(LOOP)
    // if(Memory[KBD] > 0){  // 键盘有按键
    //     FILL();
    // }
    @KBD
    D=M
    @FILL
    D;JGT

    // CLEAR();  // no conditional
    @CLEAR
    0;JMP

(FILL)
    @current
    D=M
    @max
    D=D-M  // current - max
    @LOOP
    D;JGT

    @current
    A=M
    M=-1
    @current
    M=M+1
    @LOOP
    0;JMP

(CLEAR)
    @SCREEN
    D=A  // get SCREEN address
    @current
    D=D-M  // SCREEN - current
    @LOOP
    D;JGT

    @current
    A=M
    M=0

    @current
    M=M-1

    @LOOP
    0;JMP