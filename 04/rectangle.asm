// for (i=0; i<n; i++){
// 		draw 16 block pixels at the beginning of row i
// }

    // addr = SCREEN
    // n = RAM[0]
    // i = 0

    // LOOP:
        // if i > n goto END
        // RAM[addr] = -1  // 1111 1111 1111 1111(= 16 black pixels)
        //// advances to the next row
        // addr = addr + 32  // 我们用内存映射的前32行代表一整行512(= 32*16)个像素，诸如此类
        // i = i + 1
        // goto LOOP

    // END:
        // goto END

   @SCREEN
   D=A
   @addr
   M=D  // addr = 16384(base address)

   @R0
   D=M
   @n
   M=D  // n = RAM[0]

   @i
   M=0

(LOOP)
    @i
    D=M
    @n
    D=D-M
    @END
    D;JGT  // if i > n goto END

    @addr
    A=M
    M=-1  // RAM[addr] = 1111 1111 1111 1111

    @i
    M=M+1  // i = i + 1
    @1
    D=A  // D = RAM[32]
    @addr
    M=M+D  // addr = addr + 32
    @LOOP
    0;JMP  // goto END

(END)
    @END  // program's end
    0;JMP  // infinite loop
