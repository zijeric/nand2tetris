package virtual;

import java.io.PrintWriter;

/**
 * 将VM指令翻译成Hack汇编代码，并写入相应的.asm输出文件
 */
class CodeWriter {

    private PrintWriter writer;
//    当前被翻译的.vm文件
    private String fileName;
//    逻辑运算符后面的数字
    private int logicalIndex;

//    为写入输出文件作准备
    CodeWriter(PrintWriter writer) {
        this.writer = writer;
        logicalIndex = 0;
    }

//    开始翻译新的.vm文件
    void setFileName(String fileName) {
        this.fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
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
                    write5Push(location);
                } else if (command == Parser.Command.C_POP) {
                    int address = 5 + index;
                    String location = "@" + address;
                    write5Pop(location);
                }
                break;
            case "pointer":
//                TODO pointer & static implementation
                break;
            case "static":
                break;
        }
    }

    /**
     * temp: stored in RAM locations 5 to 12 (8个)
     * addr = (5+i), SP--, *addr = *SP
     * @param location @(5+index)
     */
    private void write5Pop(String location) {
        decAndPopTopStack();
        writer.println(location);
        writer.println("M=D");
    }

    /**
     * temp: stored in RAM locations 5 to 12 (8个)
     * (TEMP段无指针映射)直接将值存储在D寄存器，并PushD2StackAndInc
     * addr = (5+i), *SP = *addr, SP++
     * @param location @(5+index)
     */
    private void write5Push(String location) {
        writer.println(location);
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
        writer.println("@" + fileName + "_TRUE" + logicalIndex);
        writer.println("D;" + operator);
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=0");
        writer.println("@" + fileName + "_CONTINUE" + logicalIndex);
        writer.println("0;JMP");
        writer.println("(" + fileName + "_TRUE" + logicalIndex + ")");
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=-1");
        writer.println("(" + fileName + "_CONTINUE" + logicalIndex + ")");
        logicalIndex++;
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
        writeUnaryArithmetic();
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
