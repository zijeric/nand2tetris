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
                    writer.println("@" + index);
                    writer.println("D=A");
                    getPointerVal();
                    writeInc();
                }
                break;
            case "local":
//                TODO complete local segment and other commands
                break;
        }
    }

    /**
     * @param operator write 分支
     */
    private void writeLogical(String operator) {
        decAndGetTopStack();
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
     * 一元的逻辑指令
     */
    private void writeUnaryArithmetic() {
        writer.println("@SP");
        writer.println("A=M-1");
    }

    /**
     * 二元的逻辑指令
     */
    private void writeBinaryArithmetic() {
        decAndGetTopStack();
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
    private void decAndGetTopStack() {
        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
    }

    /**
     * 获取SP指针的值
     */
    private void getPointerVal() {
        writer.println("@SP");
        writer.println("A=M");
        writer.println("M=D");
    }

    void close() {
        writer.close();
    }
}
