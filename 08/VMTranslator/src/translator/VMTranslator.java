package translator;
/*
 * Created by Nolva on 2020/9/21.
 */

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 将指定的VM文件或包含多个VM文件的目录转换为单个.asm文件
 * reference: jahnagoldman
 */

public class VMTranslator {
    public static void main(String[] args) {

//        输出文件名
        String outputFileName = null;
//        包含需要解析为.asm的.vm文件
        ArrayList<Parser> files2parse = new ArrayList<>();

        // 命令行参数的错误检查
        if (args.length != 1) {
            printCommandLineErrorAndExit();
        }

//        检查提供的命令行参数，以确定它是有效的文件还是目录
        File file = new File(args[0]);
//        命令行参数路径的3种情况: 1.不存在，2.文件，3.目录
//        文件是否存在
        boolean exists = file.exists();
//        是否为常规的文件
        boolean isFile = file.isFile();
//        是否为目录
        boolean isDirectory = file.isDirectory();

//        1.不存在
        if (!exists) {
            System.err.println(args[0] + " is not a valid file or path");
            System.exit(1);

//        2.单个.vm文件
        } else if (isFile && args[0].endsWith(".vm")) {
            Parser parser = getParser(file);
            String fileName = args[0].substring(0, args[0].indexOf(".vm"));
            parser.setFileName(fileName);
            files2parse.add(parser);
            outputFileName = fileName + ".asm";

//        3.目录，扫描目录下所有的.vm文件
        } else if (isDirectory) {
//            将路径下的所有文件实例化为File对象并放入File数组
            File[] files = file.listFiles();
            assert files != null;

//            遍历所有.vm文件，初始化Parser对象fileName参数(无后缀)，并加入到Parser的数组链表
            for (File f : files) {
                if (f.getName().endsWith(".vm")) {
                    Parser parser = getParser(f);
                    String fileName = f.getName().substring(0, f.getName().indexOf(".vm"));
                    parser.setFileName(fileName);
                    files2parse.add(parser);
                }
            }

//            如果目录不包含.vm文件，则抛出错误并退出
            if (files2parse.size() == 0) {
                System.err.println("No .vm files to parse in " + args[0]);
                System.exit(1);
            }

            outputFileName = file.getAbsolutePath() + "/" + file.getName() + ".asm"; // output fileName is dir name + .asm

//        4. 命令行输入错误
        } else {
            printCommandLineErrorAndExit();
        }

//        实例化CodeWriter对象
        PrintWriter printWriter = null;
        try {
            assert outputFileName != null;
            printWriter = new PrintWriter(outputFileName);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        CodeWriter codeWriter = new CodeWriter(printWriter);

//        initialized指示.asm文件是否已写入引导程序代码
        boolean initialized = false;

//        遍历Parser(每个.vm文件)，CodeWriter写入
        for (Parser fileToParse : files2parse) {

//            在文件开头只编写一次引导代码
            if (!initialized) {
                codeWriter.writerInit();
                initialized = true;
            }
            
//            设置当前正在解析的文件的文件名
            codeWriter.setFileName(fileToParse.getFileName());

            while (fileToParse.hasMoreCommand()) {
                fileToParse.advance();
                fileToParse.skipBlanks();
                if (fileToParse.Length() == 0) continue;

                Parser.Command command = fileToParse.CommandType();

                switch (command) {
                    case C_POP:
                    case C_PUSH:
                        codeWriter.writePushAndPop(fileToParse.CommandType(), fileToParse.arg1(), fileToParse.arg2());
                        break;
                    case C_ARITHMETIC:
                        codeWriter.writeArithmetic(fileToParse.command());
                        break;
                    case C_LABEL:
                        codeWriter.writeLabel(fileToParse.arg1());
                        break;
                    case C_GOTO:
                        codeWriter.writeGoto(fileToParse.arg1());
                        break;
                    case C_IF:
                        codeWriter.writeIf(fileToParse.arg1());
                        break;
                    case C_FUNCTION:
                        codeWriter.writeFunction(fileToParse.arg1(), fileToParse.arg2());
                        break;
                    case C_RETURN:
                        codeWriter.writeReturn();
                        break;
                    case C_CALL:
                        codeWriter.writeCall(fileToParse.arg1(), fileToParse.arg2());
                        break;
                    default:
                        break;
                }

            }
            fileToParse.close();
        }
        codeWriter.close();
    }

    private static Parser getParser(File file) {
        Parser parser = null;
        try {
            parser = new Parser(new Scanner(new FileReader(file)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return parser;
    }

    private static void printCommandLineErrorAndExit() {
        System.err.println("usage: java translator/VMTranslator <fileName.vm>");
        System.err.println("OR");
        System.err.println("java translator/VMTranslator <directory>");
        System.exit(1);
    }
}
