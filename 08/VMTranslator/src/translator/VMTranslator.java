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
 * Translates a specified VM file, or a directory containing multiple VM files, into a single .asm file.
 */

public class VMTranslator {
    public static void main(String[] args) {

        String outFilename = null; // name of output file
        ArrayList<Parser> filesToParse = new ArrayList<>(); // contains the .vm files that need to be parsed into .asm

        // parse command line arguments
        if (args.length != 1) {
            printCommandLineErrorAndExit();
        }

        // examine the supplied command line argument to determine if it's a valid file or directory
        File file = new File(args[0]);
        boolean exists = file.exists(); // Check if file exists
        boolean isDirectory = file.isDirectory(); // Check if it's a directory
        boolean isFile = file.isFile(); // Check if it's a regular file

        if (!exists) {
            System.err.println(args[0] + " is not a valid file or path");
            System.exit(1);
        } else if (isFile && args[0].endsWith(".vm")) { // single .vm file supplied
            Parser parser = getParser(file);
            String filename = args[0].substring(0, args[0].indexOf(".vm"));
            parser.setFileName(filename);
            filesToParse.add(parser);
            outFilename = filename + ".asm";
        } else if (isDirectory) { // directory supplied, scan it for all .vm files
            File[] files = file.listFiles();
            assert files != null;
            for (File f : files) {
                if (f.getName().endsWith(".vm")) {
                    Parser parser = getParser(f);
                    String filename = f.getName().substring(0, f.getName().indexOf(".vm"));
                    parser.setFileName(filename);
                    filesToParse.add(parser);
                }
            }

            // throw error and exit if directory supplied contains no .vm files
            if (filesToParse.size() == 0) {
                System.err.println("No .vm files to parse in " + args[0]);
                System.exit(1);
            }

            outFilename = file.getAbsolutePath() + "/" + file.getName() + ".asm"; // output filename is dir name + .asm
        } else {
            printCommandLineErrorAndExit();
        }

        // instantiate a single codeWriter
        PrintWriter printWriter = null;
        try {
            assert outFilename != null;
            printWriter = new PrintWriter(outFilename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        CodeWriter codeWriter = new CodeWriter(printWriter);

        boolean initialized = false; // flag indicates if bootstrap code has been written

        // iterate through the parsers: one for each .vm file
        for (Parser fileToParse : filesToParse) {

            if (!initialized) { // write bootstrap code just once, at beginning of file
                codeWriter.writerInit();
                initialized = true;
            }

            codeWriter.setFileName(fileToParse.getFileName()); // set the file name of the file currently being parsed

            while (fileToParse.hasMoreCommand()) {
                fileToParse.advance(); // go to next line

                fileToParse.skipBlanks(); // strip comments
                if (fileToParse.Length() == 0) continue; // don't process if no valid command

                Parser.Command cmd = fileToParse.CommandType();

                switch (cmd) {
                    case C_POP:
                    case C_PUSH:
                        codeWriter.writePushAndPop(fileToParse.CommandType(), fileToParse.arg1(),
                                fileToParse.arg2());
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
        // close resources
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
        System.err.println("usage: java VMTranslator/VMTranslator <filename.vm>");
        System.err.println("OR");
        System.err.println("java VMTranslator/VMTranslator <directory>");
        System.exit(1);
    }
}
