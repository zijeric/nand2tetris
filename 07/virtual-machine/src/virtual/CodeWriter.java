package virtual;

import java.io.PrintWriter;

/**
 * Created by Nolva on 2020/9/14.
 */

public class CodeWriter {
    private PrintWriter writer;
    private String fileName;
    private int logicalFlag;

    CodeWriter(PrintWriter writer) {
        this.writer = writer;
        logicalFlag = 0;
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 将给定的算术操作所对应的汇编写到输出文件
     * @param command 算术指令
     */
    void writeArithmetic(String command) {
//        switch (command){
//            case "add":
//
//        }
    }
}
