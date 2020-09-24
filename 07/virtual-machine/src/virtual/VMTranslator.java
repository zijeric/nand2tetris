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

//        输出文件
        String outputFile = null;
//        包含需要解析为.asm的.vm文件
        ArrayList<Parser> files2parse = new ArrayList<>();
        ArrayList<CodeWriter> codeWriters = new ArrayList<>();

//        判断命令行是否正确
        if (args.length != 1) {
            printCommandLineErrorAndExit();
        }

//        检查提供的命令行参数，以确定它是有效的文件还是目录
//        以命令行参数新建File对象
        final File file = new File(args[0]);
//        检查文件是否存在
        final boolean exists = file.exists();
//        检查是否为目录
        final boolean isDirectory = file.isDirectory();
//        检查是否为常规的文件
        final boolean isFile = file.isFile();

//        文件不存在
        if (!exists) {
            System.err.println(args[0] + "is not a valid file or path !");
            System.exit(1);

//        单个的.vm文件
        } else if (isFile && args[0].endsWith(".vm")) {
//            获取Parser对象并初始化文件名，获取指令参数(路径+文件名)
            final Parser parser = getParser(file);
            final String fileName = args[0].substring(0, args[0].indexOf(".vm"));
            parser.setFileName(fileName);

//            将Parser对象存储在ArrayList<Parser>数组
            files2parse.add(parser);
            outputFile = fileName + ".asm";

//        一个目录，扫描当中的所有.vm文件
        } else if (isDirectory) {
//            files存储了指令参数arg[0]映射路径下的所有文件
            final File[] files = file.listFiles();
            assert files != null;

//            遍历所有文件，找出.vm文件并进行解析(Parser)
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
//            输出文件的路径+名称，此处只能将目录中所有.vm文件翻译到一个.asm文件
            outputFile = file.getAbsolutePath() + "/" + file.getName() + ".asm";

        } else {
            printCommandLineErrorAndExit();
        }
//        实例化codeWriter对象，对输出文件写入对应的Hack汇编指令
        PrintWriter printWriter = null;
        try {
            assert outputFile != null;
            printWriter = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        final CodeWriter codeWriter = new CodeWriter(printWriter);

//        遍历Parser对象数组：每个.vm文件
        for (final Parser file2parse : files2parse) {
//            设置当前正在解析的文件的文件名
            codeWriter.setFileName(file2parse.getFileName());

//            常规逐行扫描、跳过空白
            while (file2parse.hasMoreCommand()) {
                file2parse.advance();
                file2parse.skipBlanks();
                if (file2parse.Length() == 0)
                    continue;

//                如果是"push" OR "pop"指令
                if (file2parse.CommandType() == Parser.Command.C_PUSH
                        || file2parse.CommandType() == Parser.Command.C_POP) {
//                    写入"push" OR "pop"指令对应的汇编指令，传入 (指令类型，指令后接的第一个参数，第二个参数)
                    codeWriter.writePushAndPop(file2parse.CommandType(), file2parse.arg1(),
                            file2parse.arg2());

//                    如果是算术指令
                } else if (file2parse.CommandType() == Parser.Command.C_ARITHMETIC) {
//                    传入完整的指令
                    codeWriter.writeArithmetic(file2parse.command());
                }
            }
            file2parse.close();
        }
        codeWriter.close();
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
