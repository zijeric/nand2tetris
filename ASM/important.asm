    MOV AX,076A
    MOV DS,AX
    MOV SS,AX
    MOV SP,0080
    CALL 0012 ;调用f函数
    INT 21
    PUSH BP ;0012
    MOV BP,SP
    MOV BX,0200
    MOV ES,BX
    XOR BX,BX
    ES:
    MOV WORD PTR [BX],0000 ;设置Buffer=0，即将0200:0000处的内存单元改为0
    MOV BX,0200
    MOV ES,BX
    XOR BX,BX
    ES:
    MOV BX,[BX] ;将Buffer解引用，即将0200:0000处的内存字单元存入BX中
    MOV BYTE PTR [BX+0A],00 ;设置Buffer[10]=0 即将DS:000A处的内存单元改为0
    JMP 006D ;while 循环条件判断
    MOV BX,0200 ;0031
    MOV ES,BX
    XOR BX,BX
    ES:
    MOV BX,[BX] ;将Buffer解引用，即将0200:0000处的内存字单元存入BX中
    MOV AL,[BX+0A] ;AL等于Buffer[10]，即将DS:000A内存字节单元的内容存入AL中
    ADD AL,61 ;为AL加上'a'
    MOV BX,0200
    MOV ES,BX
    XOR BX,BX
    ES:
    MOV BX,[BX] ;将Buffer解引用，即将0200:0000处的内存字单元存入BX中
    PUSH AX
    PUSH BX
    MOV BX,0200
    MOV ES,BX
    XOR BX,BX
    ES:
    MOV BX,[BX] ;将Buffer解引用，即将0200:0000处的内存字单元存入BX中
    MOV AL,[BX+0A] ;将Buffer[10]的值传入AL，即将DS:000A处的内存字节单元存入AL中
    CBW ;将AL扩展至16位
    POP BX ;取回BX
    ADD BX,AX ;BX现在值为Buffer[10]
    POP AX ;取回AX
    MOV [BX],AL ;将所要设置的数值'a'+Buffer[10]存入Buffer[Buffer[10]]中
    MOV BX,0200
    MOV ES,BX
    XOR BX,BX
    ES:
    MOV BX,[BX] ;将Buffer解引用，即将0200:0000处的内存字单元存入BX中
    INC BYTE PTR [BX+0A] ;将Buffer[10]自增
    MOV BX,0200 ;006D
    MOV ES,BX
    XOR BX,BX
    ES:
    MOV BX,[BX]
    CMP BYTE PTR [BX+0A],08 ;判断Buffer[10]是否等于8
    JNZ 0031 ;不等于则继续
    POP BP ;等于则返回
    RET