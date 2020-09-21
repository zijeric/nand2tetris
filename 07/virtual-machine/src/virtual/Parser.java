package virtual;
/*
 * Created by Nolva on 2020/9/14.
 */

import java.util.Scanner;

/**
 * 分析.vm文件，封装对输入代码的访问操作
 * 读取vm指令并解析，然后为它各个部分提供方便的访问入口
 * 移除代码中所有空格和注释
 */
public class Parser {

    private Scanner scanner;
    private String current;
//    正在被解析的.vm文件
    private String fileName;

    private final String[] ARITHMETIC_CMD = {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};

    Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    boolean hasMoreCommand() {
        return scanner.hasNextLine();
    }

    void advance() {
        current = scanner.nextLine();
    }

    /**
     * vm command type
     */
    enum commandType {
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

    void skipBlanks() {
        if (current.contains("//")){
            current = current.substring(0, current.indexOf("//"));
        }
        current = current.replace(" ", "");
        current = current.replace("/t", "");
    }

    int Length() {
        return current.length();
    }

    /**
     * 区分出9种指令？
     * @return 指令类型commandType
     */
    commandType CommandType() {
        if (current.contains("push")) {
            return commandType.C_PUSH;
        } else if (current.contains("pop")) {
            return commandType.C_POP;
        } else if (current.contains("label")) {
            return commandType.C_LABEL;
        } else if (current.contains("goto")) {
            return commandType.C_GOTO;
        } else if (current.contains("if")) {
            return commandType.C_IF;
        } else if (current.contains("fun")) {
            return commandType.C_FUNCTION;
        } else if (current.contains("return")) {
            return commandType.C_RETURN;
        } else if (current.contains("call")) {
            return commandType.C_CALL;
        } else {
            return commandType.C_ARITHMETIC;
        }
    }

    /**
     * @return 完整的指令
     */
    String command() {
        return current.split("\\s+")[0];
    }

    /**
     * forbid C_RETURN
     * @return 返回当前指令第一个参数
     */
    String arg1() {
        if (CommandType() == commandType.C_ARITHMETIC){
            return current;
        } else {
            return current.split("\\s+")[1];
        }
    }

    /**
     * only for C_PUSH, C_POP, C_FUNCTION, C_CALL
     * @return 返回当前指令的第二个参数segment
     */
    int Arg2() {
        if (CommandType() == commandType.C_PUSH ||
                CommandType() == commandType.C_POP ||
                CommandType() == commandType.C_FUNCTION ||
                CommandType() == commandType.C_CALL){
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
     * 遍历算术指令ARITHMETIC_CMD，如果当前指令等于其中一个就返回true
     * @return 是否为算术指令
     */
    boolean isArithmeticCmd() {
        for (String cmd : ARITHMETIC_CMD) {
            if (current.equals(cmd)) return true;
        }
        return false;
    }
}
