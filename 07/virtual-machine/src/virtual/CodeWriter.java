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
    private int logicalNum;

//    为写入输出文件作准备
    CodeWriter(PrintWriter writer) {
        this.writer = writer;
        logicalNum = 0;
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
//                TODO complete local segment
                break;
        }
    }

    /**
     *
     * @param operator write 分支
     */
    private void writeLogical(String operator) {
        decAndGetTopStack();
        writeUnaryArithmetic();
        writer.println("@RET_TRUE");
        writer.println("D;" + operator);
        writer.println("D=0");
        writer.println("@CONTINUE");
        writer.println("0;JMP");
        writer.println("(RET_TRUE)");
        writer.println("D=-1");
        writer.println("(CONTINUE)");
        getPointerVal();
        writeInc();
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
