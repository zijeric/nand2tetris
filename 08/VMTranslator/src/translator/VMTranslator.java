package translator;
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

//        包含需要解析为.asm的.vm文件，为空进行错误处理
        ArrayList<Parser> files2parse = new ArrayList<>();

//        判断命令行是否正确
        if (args.length != 1) {
            printCommandLineErrorAndExit();
        }

//        检查提供的命令行参数，以确定它是有效的文件还是目录
//        以命令行参数新建File对象
        File file = new File(args[0]);
//        检查文件是否存在
        boolean exists = file.exists();
//        检查是否为目录
        boolean isDirectory = file.isDirectory();
//        检查是否为常规的文件
        boolean isFile = file.isFile();

//        文件不存在
        if (!exists) {
            System.err.println(args[0] + "is not a valid file or path !");
            System.exit(1);

//        单个的.vm文件
        } else if (isFile && args[0].endsWith(".vm")) {
//            获取Parser对象并初始化文件名，获取指令参数(路径+文件名)
            Parser parser = getParser(file);
            String fileName = args[0].substring(0, args[0].indexOf(".vm"));
            parser.setFileName(fileName);

            String outputFile = fileName + ".asm";
//            单个.vm文件直接翻译成.asm文件
            writeVM2ASM(outputFile, parser);

//        一个目录，扫描当中的所有.vm文件
        } else if (isDirectory) {
//            将指令参数arg[0]映射路径下的所有文件转换成File对象，并用File[]数组存储
            File[] files = file.listFiles();
            assert files != null;
//            遍历所有文件，找出.vm文件并进行解析(Parser)
            for (File f : files) {
                if (f.getName().endsWith(".vm")) {
                    Parser parser = getParser(f);
                    String fileName = f.getName().substring(0, f.getName().indexOf(".vm"));
                    parser.setFileName(fileName);
                    String outputFile = file.getAbsolutePath() + "/" + fileName + ".asm";
                    writeVM2ASM(outputFile, parser);
                    files2parse.add(parser);
                }
            }

//            如果提供的目录不包含.vm文件，则抛出错误并退出
            if (files2parse.size() == 0) {
                System.err.println("No .vm files to parse in " + args[0]);
                System.exit(1);
            }

        } else {
            printCommandLineErrorAndExit();
        }
    }

    /**
     * 封装写入对应Hack汇编代码到输出文件的操作，便于独立的Parser和单一的codeWriter处理输出
     * @param output 单个.vm文件
     */
    private static void writeVM2ASM(String output, Parser parser) {
        CodeWriter codeWriter = null;
        try (PrintWriter writer = new PrintWriter(output)) {
            codeWriter = new CodeWriter(writer);
            codeWriter.setFileName(parser.getFileName());
            while (parser.hasMoreCommand()) {
                parser.advance();
                parser.skipBlanks();
                if (parser.Length() == 0) continue;

                if (parser.CommandType() == Parser.Command.C_PUSH || parser.CommandType() == Parser.Command.C_POP) {
                    codeWriter.writePushAndPop(parser.CommandType(), parser.arg1(), parser.arg2());
                } else if (parser.CommandType() == Parser.Command.C_ARITHMETIC) {
                    codeWriter.writeArithmetic(parser.command());
                }
                writer.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } finally {
            parser.close();
            assert codeWriter != null;
            codeWriter.close();
        }

    }

    /**
     * 根据File文件对象创建Parser对象
     * @param file 根据文件名构建的File对象
     * @return Parser对象
     */
    private static Parser getParser(File file) {
        Parser parser = null;
        try {
            parser = new Parser(new Scanner(new FileReader(file)));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return parser;
    }

    /**
     * 输出命令行的用法并退出
     */
    private static void printCommandLineErrorAndExit() {
        System.err.println("usage: java VMTranslator/VMTranslator <filename.vm>");
        System.err.println("OR");
        System.err.println("java VMTranslator/VMTranslator <directory>");
        System.exit(1);
    }
}
