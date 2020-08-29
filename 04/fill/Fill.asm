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

    screen = SCREEN  // 16384
    kbd = KBD  // 24576
    max = 8191

    i = 0

STOP:
    if kbd != 0 goto LOOP

    LOOP:
        if i > max goto STOP
        RAM[screen] = -1  // 1111 1111 1111 1111
        // advances to next
        screen = screen + 1
        i = i + 1
        goto LOOP

