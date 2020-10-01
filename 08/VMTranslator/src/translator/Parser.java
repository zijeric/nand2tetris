package translator;
/*
 * Created by Nolva on 2020/9/14.
 */

import java.util.Scanner;

/**
 * 分析.vm文件，封装对输入代码的访问操作
 * 读取vm指令并解析，然后为它各个部分提供方便的访问入口
 * 移除代码中所有空格和注释
 */
class Parser {

//    文件扫描
    private Scanner scanner;
//    当前命令
    private String current;
//    正在被解析的.vm文件名称
    private String fileName;

//    算术指令
    private final String[] ARITHMETIC_CMD = {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};

    Parser(Scanner scanner) {
        this.scanner = scanner;
    }

//    是否还有指令
    boolean hasMoreCommand() {
        return scanner.hasNextLine();
    }

//    向前
    void advance() {
        current = scanner.nextLine();
    }

    /**
     * vm command type
     */
    enum Command {
        C_ARITHMETIC,  // 所有算术命令
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL
    }

//    跳过所有的空白
    void skipBlanks() {
        if (current.contains("//")){
            current = current.substring(0, current.indexOf("//"));
        }
//        不能跳过空格，eg: push constant 7处理后, pushconstant7--无法分割指令，报出数组越界的错误
//        current = current.replace(" ", "");
        current = current.replace("\n", "");
        current = current.replace("\t", "");
    }

//    当前指令长度
    int Length() {
        return current.length();
    }

    /**
     * 区分出9种指令？
     * chap7: 区分"push", "pop", arithmetic cmd
     * @return 指令类型commandType
     */
    Command CommandType() {
        if (current.startsWith("push")) {
            return Command.C_PUSH;
        } else if (current.startsWith("pop")) {
            return Command.C_POP;
        } else if (isArithmeticCmd()){
            return Command.C_ARITHMETIC;
        } else if (current.startsWith("label")) {
            return Command.C_LABEL;
        } else if (current.startsWith("goto")) {
            return Command.C_GOTO;
        } else if (current.startsWith("if-goto")) {
            return Command.C_IF;
        } else if (current.startsWith("function")) {
            return Command.C_FUNCTION;
        } else if (current.startsWith("return")) {
            return Command.C_RETURN;
        } else if (current.startsWith("call")){
            return Command.C_CALL;
        }else {
            return null;
        }
    }

    /**
     * 专由算术指令C_ARITHMETIC调用
     * @return 完整的指令
     */
    String command() {
        return current.split("\\s+")[0];
    }

    /**
     * forbid C_RETURN
     * @return 返回当前指令第一个参数segment
     */
    String arg1() {
        return current.split("\\s+")[1];
    }

    /**
     * only for C_PUSH, C_POP, C_FUNCTION, C_CALL
     * @return 返回当前指令的第二个参数, segment索引
     */
    int arg2() {
        if (CommandType() == Command.C_PUSH ||
                CommandType() == Command.C_POP ||
                CommandType() == Command.C_FUNCTION ||
                CommandType() == Command.C_CALL){
            return Integer.parseInt(current.split("\\s+")[2]);
        } else return -1;
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }

    String getFileName() {
        return fileName;
    }

    void close() {
        scanner.close();
    }

    /**
     * 遍历算术指令ARITHMETIC_CMD，如果当前指令的开始等于其中一个就返回true
     * @return 是否为算术指令
     */
    private boolean isArithmeticCmd() {
        for (String cmd : ARITHMETIC_CMD) {
            if (current.startsWith(cmd)) return true;
        }
        return false;
    }
}
