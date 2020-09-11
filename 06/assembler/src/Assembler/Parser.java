package Assembler;
/*
 * Created by Nolva on 2020/9/6.
 */

import java.util.Scanner;

/**
 * Parser 封装对输入代码的访问操作。
 *    实现功能：
 *      1.读取汇编语言命令并对其进行解析;
 *      (*)2.提供“方便访问汇编命令成分(域和符号)”的方法;
 *      3.去掉所有的空格和注释.
 *
 * 用于Assembler.java
 */
class Parser {

//    存储输入文件/输入流，为语法解析作准备
    private Scanner scanner;
//    存储当前命令行
    private String current;

    /**
     * 构造函数，将 输入文件/输入流 存储在Scanner对象
     * @param scanner
     */
    Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 判断输入当中是否还有下一行
     * @return  有:true ; 没有:false
     */
    boolean hasMoreCommands() {
        return scanner.hasNextLine();
    }

    /**
     * 从输入中读取下一条(行)命令，将其当作"当前命令"，
     * 仅当hasNextCommands()为true时，才能调用此方法。
     *
     * × next(): 读取第一个有效字符后，遇到空格就结束。若要完整读取需要利用循环
     * nextLine(): 读取一整行的字符串，遇到换行符就结束。(可得到空白)
     */
    void advance() {
        this.current = scanner.nextLine();
    }

    /**
     * 跳过所有的注释和空白
     */
    void skipSpacesAndComments() {
//        如果是注释
        if (current.contains("//")) {
//            丢弃 "//"注释后面的所有内容，删除注释
            current = current.substring(0, current.indexOf("//"));
        }
//        删除空格
        current = current.replace(" ", "");
//        删除Tab
        current = current.replace("\t", "");
    }

    /**
     * 返回当前指令的长度
     * @return
     */
    int currentLength() {
        return current.length();
    }

    /**
     * 指令类型
     */
    public enum commandType {
        A_COMMAND,  // 当@Xxx中的Xxx是符号或十进制数字时
        C_COMMAND,  // 用于 dest=comp;jump
        L_COMMAND   // (伪命令), 当(Xxx)中的Xxx是符号时
    }

    /**
     * 根据当前指令的开始字符，判断当前指令的类型
     * @return commandType: A/C/L_COMMAND
     */
    commandType CommandType() {

//        当@Xxx中的Xxx是符号或十进制数字时, 为A-指令.
        if (current.startsWith("@")) {
            return commandType.A_COMMAND;

//        当(Xxx)中的Xxx是符号时, 为伪指令.   eg:(END)
        } else if (current.startsWith("(")) {
            return commandType.L_COMMAND;

//        dest=comp;jump, C-指令.
        } else {
            return commandType.C_COMMAND;
        }
    }

    /**
     * 若指令包含"="，说明指令含有dest域和comp域   eg:dest=comp(;jump)
     * 由"="分割为两个字符串，读取[0](第一个)
     * @return C-指令的dest域 或 null
     */
    String dest() {
        if (current.contains("=")) {
            return current.split("=")[0];
        } else {
            return "";
        }
    }

    /**
     * 在C-指令中comp域必定存在
     *     eg:comp;jump, dest=comp, dest=comp;jump
     *
     * 若指令包含"="，用"="和";"分割为三(两)个字符串，读取[1](第二个)
     * 若指令不包含"="，用";"分割为两个字符串，读取[0](第一个)
     * @return C-指令的comp域
     */
    String comp() {
        if (current.contains("=")) {
            return current.split("[=;]")[1];
        } else {
            return current.split(";")[0];
        }
    }

    /**
     * 若C-指令包含";"，说明指令含有jump域   eg:dest=comp;jump, comp;jump
     * 用";"分割为两个字符串，读取[1](第二个)
     * @return C-指令的jump域 或 null
     */
    String jump(){
        if (current.contains(";")){
            return current.split(";")[1];
        } else {
            return "";
        }
    }

    /**
     * 根据"@Value", "@Variable", "(Symbol)"语法拆分字符串
     * @return 返回Value、Variable、Symbol 或 null
     */
    String symbol() {
        if (current.startsWith("@")) {
//            Value | Variable
            return current.substring(1);
        } else if (current.startsWith("(")) {
//            Symbol
            return current.substring(1, current.indexOf(")"));
        } else {
            return null;
        }
    }

    /**
     * 释放资源
     */
    void close() {
        if (scanner != null)
            scanner.close();
    }

}
