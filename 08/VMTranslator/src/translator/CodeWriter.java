package translator;

import java.io.PrintWriter;

/**
 * 将VM指令翻译成Hack汇编代码，并写入相应的.asm输出文件
 */
class CodeWriter {

    private PrintWriter writer;
//    当前被翻译的.vm文件
    private String fileName;
//    伪指令: 区分分支标签的数字
    private int branchIndex;
//    伪指令: 区分返回地址的标签数字
    private int returnIndex;

    CodeWriter(PrintWriter writer) {
        this.writer = writer;
        branchIndex = 0;
        returnIndex = 0;
    }

//    设置翻译出来的的.vm文件的文件名(提取出文件名)
    void setFileName(String fileName) {
        this.fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
    }

    void writerInit() {
//        SP=256
        writer.println("@256");
        writer.println("D=A");
        writer.println("@SP");
        writer.println("M=D");
//        call Sys.init
        writeCall("Sys.init", 0);
    }

    /**
     * 编写执行Label命令的汇编指令
     * @param label 标签
     */
    void writeLabel(String label) {
        writer.println("(" + label + ")");
    }

    /**
     * 编写goto命令的汇编指令
     * @param label goto label中的标签
     */
    void writeGoto(String label) {
        writer.println("@" + label);
        writer.println("0;JMP");
    }

    /**
     * 编写if-goto命令的汇编指令
     * @param label @label
     */
    void writeIf(String label) {
        decAndPopTopStack();
        writer.println("@" + label);
        writer.println("D;JNE");
    }

    /**
     * call f n
     * @param functionName 函数名
     * @param numArgs 函数参数个数
     */
    void writeCall(String functionName, int numArgs) {
        String returnAdd = uniqueReturnAdd();

//        push return-address
        writer.println("@" + returnAdd);
        writer.println("D=A");
        PushD2StackAndInc();

//        push LCL, ARG, THIS, THAT
        writeMappedPush("@LCL");
        writeMappedPush("@ARG");
        writeMappedPush("@THIS");
        writeMappedPush("@THAT");

//        reset ARG: ARG = (SP-n-5)
        writer.println("@SP");
        writer.println("D=M");
        writer.println("@" + numArgs);
        writer.println("D=D-A");
        writer.println("@5");
        writer.println("D=D-A");
        writer.println("@ARG");
        writer.println("M=D");

//        reset LCL: LCL = SP
        writer.println("@SP");
        writer.println("D=M");
        writer.println("@LCL");
        writer.println("M=D");
//        goto F
        writeGoto(functionName);

//        为返回地址声明一个标签
        writer.println("(" + returnAdd + ")");
    }

    void writeReturn() {
//        FRAME = LCL
        writer.println("@LCL");
        writer.println("D=M");
        writer.println("@FRAME");
        writer.println("M=D");
        writer.println("@5");
        writer.println("A=D-A");
        writer.println("D=M");
        writer.println("@RET");
        writer.println("M=D");
        writePushAndPop(Parser.Command.C_POP, "argument", 0);
//        SP = ARG + 1
        writer.println("@ARG");
        writer.println("D=M");
        writer.println("@SP");
        writer.println("M=D+1");
//        THAT = *(FRAME - 1)  恢复caller的THAT段指针
        restoreCallerPointer("@THAT");
//        THIS = *(FRAME - 2) 恢复caller的THIS的指针
        restoreCallerPointer("@THIS");
//        ARG = *(FRAME - 3)
        restoreCallerPointer("@ARG");
//        LCL = *(FRAME - 4)
        restoreCallerPointer("@LCL");
        writer.println("@RET");
        writer.println("A=M");
        writer.println("0;JMP");
    }

    /**
     * function f k
     * @param functionName 函数名f
     * @param numLocals 局部变量的个数k
     */
    void writeFunction(String functionName, int numLocals) {
//        declare label
        writer.println("(" + functionName + ")");
//        set aside locations for local vars
        for (int i = 0; i < numLocals; i++) {
            writePushAndPop(Parser.Command.C_PUSH, "constant", 0);
        }
    }

    private String uniqueReturnAdd() {
        return "return-address" + "_" + (returnIndex++);
    }

    /**
     * 将给定的算术操作所对应的汇编写到输出文件
     * @param command 算术指令
     */
    void writeArithmetic(String command) {
        switch (command){
            case "add":
                writeBinaryArithmetic();
                writer.println("M=M+D");
                break;
            case "sub":
                writeBinaryArithmetic();
                writer.println("M=M-D");
                break;
            case "neg":
                writeUnaryArithmetic();
                writer.println("M=-M");
                break;
            case "eq":
                writeLogical("JEQ");
                break;
            case "gt":
                writeLogical("JGT");
                break;
            case "lt":
                writeLogical("JLT");
                break;
            case "and":
                writeBinaryArithmetic();
                writer.println("M=M&D");
                break;
            case "or":
                writeBinaryArithmetic();
                writer.println("M=M|D");
                break;
            case "not":
                writeUnaryArithmetic();
                writer.println("M=!M");
                break;
        }
    }

    /**
     * 写入(Push/Pop)指令对应的汇编指令
     * segment(local段, argument段, this/that段)：RAM中存储的是对应段的基地址base
     * (temp段, pointer段, static段)：将段的值直接映射到RAM
     * @param command 指令类型(Push/Pop)
     * @param segment 指令后的第一个参数，segment(constant段, local段, argument段...)
     * @param index 对应segment段的索引
     */
    void writePushAndPop(Parser.Command command, String segment, int index) {

        switch (segment) {
            case "constant":
                if (command == Parser.Command.C_PUSH) {
//                    @decimal
                    writer.println("@" + index);
//                    存储在D寄存器
                    writer.println("D=A");
//                    (PUSH)将D寄存器的值存储在SP所指向的内存单元，SP++
                    PushD2StackAndInc();
                }
                break;
            case "local":
                if (command == Parser.Command.C_PUSH) {
                    writeBasePush("@LCL", index);
                } else if (command == Parser.Command.C_POP) {
                    writeBasePop("@LCL", index);
                }
                break;
            case "argument":
                if (command == Parser.Command.C_PUSH) {
                    writeBasePush("@ARG", index);
                } else if (command == Parser.Command.C_POP) {
                    writeBasePop("@ARG", index);
                }
                break;
            case "this":
                if (command == Parser.Command.C_PUSH) {
                    writeBasePush("@THIS", index);
                } else if (command == Parser.Command.C_POP) {
                    writeBasePop("@THIS", index);
                }
                break;
            case "that":
                if (command == Parser.Command.C_PUSH) {
                    writeBasePush("@THAT", index);
                } else if (command == Parser.Command.C_POP) {
                    writeBasePop("@THAT", index);
                }
                break;
            case "temp":
                if (command == Parser.Command.C_PUSH) {
                    int address = 5 + index;
                    String location = "@" + address;
                    writeMappedPush(location);
                } else if (command == Parser.Command.C_POP) {
                    int address = 5 + index;
                    String location = "@" + address;
                    writeMappedPop(location);
                }
                break;
            case "pointer":
                if (command == Parser.Command.C_PUSH) {
                    if (index == 0) {
                        writeMappedPush("@THIS");
                    } else if (index == 1) {
                        writeMappedPush("@THAT");
                    }
                } else if (command == Parser.Command.C_POP) {
                    if (index == 0) {
                        writeMappedPop("@THIS");
                    } else if (index == 1) {
                        writeMappedPop("@THAT");
                    }
                }
                break;
            case "static":
                String location = "@" + fileName + "_" + index;
                if (command == Parser.Command.C_PUSH) {
                    writeMappedPush(location);
                } else if (command == Parser.Command.C_POP) {
                    writeMappedPop(location);
                }
                break;
        }
    }

    /**
     * temp: stored in RAM locations 5 to 12 (8个)
     * addr = (5+i), SP--, *addr = *SP
     * @param location 直接映射在RAM的段地址
     */
    private void writeMappedPop(String location) {
        decAndPopTopStack();
        writer.println(location);
        writer.println("M=D");
    }

    /**
     * temp: stored in RAM locations 5 to 12 (8个)
     * 压入堆栈，将段直接映射到RAM
     * 直接将值存储在D寄存器，并PushD2StackAndInc
     * addr = (5+i), *SP = *addr, SP++
     * @param location @(5+index)
     */
    private void writeMappedPush(String location) {
        writer.println(location);
//        D=M, 映射地址M存储到D寄存器
        writer.println("D=M");
        PushD2StackAndInc();
    }

    /**
     * 写入由基地址和索引(base+i)构建的Pop指令
     * addr = segmentPointer + i, SP--, *addr = *SP
     * @param segmentPointer segment段的指针
     * @param index segment索引
     */
    private void writeBasePop(String segmentPointer, int index) {
//        addr = segmentPointer + i，获取addr，存储在R13
        writer.println(segmentPointer);
        writer.println("D=M");
        writer.println("@" + index);
        writer.println("D=D+A");
        writer.println("@R13");
        writer.println("M=D");
//        SP--, *addr = *SP
        decAndPopTopStack();
        writer.println("@R13");
        writer.println("A=M");
        writer.println("M=D");
    }

    /**
     * 压入堆栈以查找指向虚拟段基地址的符号
     * 写入由基地址和索引(base+i)构建的Push指令
     * addr = segmentPointer + i, *SP = *addr, SP++
     * @param segmentPointer segment段的指针
     * @param index segment索引
     */
    private void writeBasePush(String segmentPointer, int index) {
//        将(segmentPointer+i)所指向的内存单元的值(*addr)存储在D寄存器
        writer.println(segmentPointer);
        writer.println("D=M");
        writer.println("@" + index);
        writer.println("A=D+A");
        writer.println("D=M");

        PushD2StackAndInc();
    }

    /**
     * @param operator write 分支
     */
    private void writeLogical(String operator) {
        decAndPopTopStack();
        writer.println("A=A-1");
        writer.println("D=M-D");
        writer.println("@" + fileName + "_TRUE" + branchIndex);
        writer.println("D;" + operator);
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=0");
        writer.println("@" + fileName + "_CONTINUE" + branchIndex);
        writer.println("0;JMP");
        writer.println("(" + fileName + "_TRUE" + branchIndex + ")");
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=-1");
        writer.println("(" + fileName + "_CONTINUE" + branchIndex + ")");
        branchIndex++;
    }

    /**
     * (PUSH)将D寄存器的值存储在SP所指向的内存单元，SP++
     * 先赋值，再自增
     */
    private void PushD2StackAndInc() {
        writePointerVal();
        writeInc();
    }

    /**
     * 一元的逻辑指令：M寄存器存储了(SP-1)所指向的内存单元的值
     * SP-1: A=M-1，并未保存在M寄存器
     */
    private void writeUnaryArithmetic() {
        writer.println("@SP");
        writer.println("A=M-1");
    }

    /**
     * 二元的逻辑指令：D寄存器存储了(SP--)所指向的内存单元的值，M寄存器存储了(SP-1)所指向的内存单元的值
     * 即，D获取了SP--指向的值，M获取了SP-1指向的值，等待进一步的算术指令
     * SP--: M=M-1，SP自减
     */
    private void writeBinaryArithmetic() {
        decAndPopTopStack();
//        writeUnaryArithmetic();
        writer.println("A=A-1");
    }

    /**
     * SP自增
     */
    private void writeInc() {
        writer.println("@SP");
        writer.println("M=M+1");
    }

    /**
     * SP自减并获取栈顶元素
     */
    private void decAndPopTopStack() {
        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
    }

    private void restoreCallerPointer(String dest) {
        writer.println("@FRAME");
        writer.println("AM=M-1");
//        writer.println("D=M");
//        writer.println("A=D-" + index);
        writer.println("D=M");
        writer.println(dest);
        writer.println("M=D");
    }

    /**
     * 给SP所指向的内存单元赋值(D寄存器的值)
     */
    private void writePointerVal() {
        writer.println("@SP");
        writer.println("A=M");
        writer.println("M=D");
    }

    void close() {
        writer.close();
    }
}
