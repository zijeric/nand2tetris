package virtual;
/*
 * Created by Nolva on 2020/9/21.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class VMTranslator {
    public static void main(String[] args) {

        String outputFile = null;
//        包含需要解析为.asm的.vm文件
        ArrayList<Parser> files2parse = new ArrayList<>();

//        解析命令行参数
        if (args.length != 1) {
            printCommandLineErrorAndExit();
        }

//        检查提供的命令行参数，以确定它是有效的文件还是目录
        File file = new File(args[0]);
//        检查文件是否存在
        boolean exists = file.exists();
//        检查是否为目录
        boolean isDirectory = file.isDirectory();
//        检查是否为常规的文件
        boolean isFile = file.isFile();

        if (!exists) {
            System.err.println(args[0] + "is not a valid file or path !");
            System.exit(1);
        } else if (isFile && args[0].endsWith(".vm")) {  // 单个.vm文件
            final Parser parser = getParser(file);
            final String fileName = args[0].substring(0, args[0].indexOf(".vm"));
            parser.setFileName(fileName);
            files2parse.add(parser);
            outputFile = fileName + ".asm";
        } else if (isDirectory) {  // 目录，扫描当中的所有.vm文件
            final File[] files = file.listFiles();
            assert files != null;
            for (final File f : files) {
                if (f.getName().endsWith(".vm")) {
                    final Parser parser = getParser(f);
                    final String fileName = f.getName().substring(0, f.getName().indexOf(".vm"));
                    parser.setFileName(fileName);
                    files2parse.add(parser);
                }
            }

//            如果提供的目录不包含.vm文件，则抛出错误并退出
            if (files2parse.size() == 0) {
                System.err.println("No .vm files to parse in " + args[0]);
                System.exit(1);
            }
            outputFile = file.getAbsolutePath() + "/" + file.getName() + ".asm";

        } else {
            printCommandLineErrorAndExit();
        }
//        实例化一个codeWriter
        PrintWriter printWriter = null;
        try {
            assert outputFile != null;
            printWriter = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        final CodeWriter codeWriter = new CodeWriter(printWriter);

//        遍历Parser对象：每个.vm文件一次
        for (final Parser file2parse : files2parse) {
//            设置当前正在解析的文件的文件名
            codeWriter.setFileName(file2parse.getFileName());
            while (file2parse.hasMoreCommand()) {
                file2parse.advance();
                file2parse.skipBlanks();
                if (file2parse.Length() == 0)
                    continue;

                if (file2parse.CommandType() == Parser.commandType.C_PUSH
                        || file2parse.CommandType() == Parser.commandType.C_POP) {
                    
                }
            }
        }
    }

    private static Parser getParser(File file) {
        Parser parser = null;
        try {
            parser = new Parser(new Scanner(new FileReader(file)));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return parser;
    }

    private static void printCommandLineErrorAndExit() {
        System.err.println("usage: java VMTranslator/VMTranslator <filename.vm>");
        System.err.println("OR");
        System.err.println("java VMTranslator/VMTranslator <directory>");
        System.exit(1);
    }
}
