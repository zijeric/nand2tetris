package Assembler;
/*
 * Created by Nolva on 2020/9/8.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Compilation: javac Assembler/*.java
 * Execution: java packageName/className path/filename.asm
 * Dependencies: SymbolTable.java, Code.java, Parser.java
 *
 * Takes a Hack assembly program via a file of .asm format,
 * and produces a text file of .hack format, containing the
 * translated Hack machine code.
 */
public class Assembler {

    public static void main(String[] args) {

        String filename = null;

//        解析命令行参数
        if (args.length != 1 || !isValidFileName(args[0])) {
            System.err.println("usage: java packageName/className path/fileName.asm");
            System.exit(1);
        } else {
            filename = args[0];
        }

//        实例化Parser对象
        Parser parser = null;
        parser = getParser(filename, parser);

//        第一次遍历: 构建符号表
        SymbolTable symbolTable = new SymbolTable();
//        当前命令将被加载到的地址
        int currentRomAddress = -1;

        while (parser.hasMoreCommands()) {
            parser.advance();
            parser.skipSpacesAndComments();
            if (parser.currentLength() == 0) continue;

//            第一次遍历只处理L_COMMAND，添加到symbol table
            Parser.commandType commandType = parser.CommandType();
            if (commandType == Parser.commandType.L_COMMAND) {
                symbolTable.addEntry(parser.symbol(), currentRomAddress + 1);
            } else if (commandType == Parser.commandType.A_COMMAND || commandType == Parser.commandType.C_COMMAND){
                currentRomAddress++;
            }
        }
        parser.close();

//        实例化Parser对象
        parser = getParser(filename, parser);
//        创建输出流(.hack文件)
        String outputFile = filename.substring(0, filename.indexOf(".asm")) + ".hack";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        while (parser.hasMoreCommands()) {
            parser.advance();
            parser.skipSpacesAndComments();
            if (parser.currentLength() == 0) continue;

            Parser.commandType commandType = parser.CommandType();

            switch (commandType) {
                case C_COMMAND:
                    String comp = Code.comp(parser.comp());
                    String dest = Code.dest(parser.dest());
                    String jump = Code.jump(parser.jump());
                    assert writer != null;
                    writer.print("111" + comp + dest + jump);
                    break;
                case L_COMMAND:
                    continue;
                case A_COMMAND:
                    String binary = Code.binary(getInt(parser.symbol(), symbolTable));
                    assert writer != null;
                    writer.print("0" + binary);
                    break;
            }
//            在新行写下一个命令的二进制代码
            if (parser.hasMoreCommands())
                writer.println("");
        }
//        关闭资源
        if (writer != null){
            writer.close();
        }
        parser.close();
    }

    private static Parser getParser(String filename, Parser parser) {

        try {
            parser = new Parser(new Scanner(new FileReader(new File(filename))));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return parser;
    }

//    检查输入流文件名是不是可接受的输入，即.asm文件
    private static boolean isValidFileName(String filename) {
        return filename.endsWith(".asm");
    }

//    将A_COMMAND的符号化常数转换为int类型
    private static int getInt(String input, SymbolTable symbolTable){
        try {
//            当input是符号化常数时，转换为int并返回
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {  // 当input非常数
//            当符号存在于符号表，根据input返回对应的地址
            if (symbolTable.contains(input)) {
                return symbolTable.getAddress(input);
            } else {
//                输入是第一次声明的变量，将其加入到符号表
                int address = symbolTable.getNextAddAndIncrement();
                symbolTable.addEntry(input, address);
                return address;
            }
        }
    }

}
