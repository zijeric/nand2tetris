package virtual;

import java.io.PrintWriter;

/**
 * 将VM指令翻译成Hack汇编代码，并写入相应的.asm输出文件
 */
public class CodeWriter {

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

//    通知代码编写者：新的.vm文件的翻译已开始
    void setFileName(String fileName) {
        this.fileName = fileName;
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

    private void writeLogical(String operator) {

    }

    private void writeUnaryArithmetic() {
        writer.println("@SP");
        writer.println("A=M-1");
    }

    private void writeBinaryArithmetic() {
        writeStoreTopStackAndDec();
    }

    private void writeStoreTopStackAndDec() {
        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
    }
}
