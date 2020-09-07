package com.nolva;
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
 */
public class Parser {

//    打开输入文件/输入流，为语法解析作准备
    private Scanner scanner;
    private String current;

    private Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public enum commandType {
        A_COMMAND,  // 当@Xxx中的Xxx是符号或十进制数字时
        C_COMMAND,  // 用于 dest=comp;jump
        L_COMMAND   // (伪命令), 当(Xxx)中的Xxx是符号时
    }

    /**
     * 判断输入当中是否还有命令
     * @return  有:true ; 没有:false
     */
    private boolean hasMoreCommands() {
        return scanner.hasNextLine();
    }

    /**
     * 从输入中读取下一条命令，将其当作"当前命令"，
     * 仅当hasNextCommands()为true时，才能调用此方法。
     */
    public void advance() {
        this.current = scanner.nextLine();
    }

//    跳过所有的空白和注释
    public void skipSpacesAndComments() {
        if (current.contains("//")) {
            current = current.substring(0, current.indexOf("//"));
        }
        current = current.replace(" ", "");
        current = current.replace("\t", "");
    }

    public int currentLength() {
        return current.length();
    }

//    根据指令的开始字符，判断当前指令的类型
    public commandType CommandType() {

        if (current.startsWith("@")) {
            return commandType.A_COMMAND;
        } else if (current.startsWith("(")) {
            return commandType.L_COMMAND;
        } else {
            return commandType.C_COMMAND;
        }
    }

    public String dest() {
        if (current.contains("=")) {
            return current.split("=")[0];
        } else {
            return "";
        }
    }

    public String comp() {
        if (current.contains("=")) {
            return current.split("[=;]")[1];
        } else {
            return current.split(";")[0];
        }
    }

    public String jump(){
        if (current.contains(";")) {
            return current.split(";")[1];
        } else {
            return "";
        }
    }

    public void close() {
        if (scanner != null)
            scanner.close();
    }

}
