package Assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by Nolva on 2020/9/10.
 * @author Nolva
 */

public class Assembler {

    public static void main(String[] args) {
//        To .class command: javac path/*.java
//        To .hack command: java package/class path/Program.asm
        String fileName = null;
        if (args.length != 1 || !isValidFileName(args[0])) {
            System.err.println("usage: java package/class path/Program.asm");
            System.exit(1);
        } else {
            fileName = args[0];
        }

        Parser parser = null;
        parser = getParser(fileName, parser);

        String file = fileName.substring(0, fileName.indexOf(".asm")) + ".hack";
        PrintWriter outputFile = null;
        try {
            outputFile = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        while (parser.hasMoreCommands()) {
            parser.advance();
            parser.skipSpacesAndComments();
            if (parser.commandLength() == 0) continue;

//            Get current command type
            Parser.commandType commandType = parser.CommandType();
            switch (commandType) {
                case C_COMMAND:
                    String comp = Code.comp(parser.comp());
                    String dest = Code.dest(parser.dest());
                    String jump = Code.jump(parser.jump());
                    assert outputFile != null;
                    outputFile.print("111" + dest + comp + jump);
                    break;

                case L_COMMAND:
                    continue;

                case A_COMMAND:
                    String binary = Code.binary(getInt(parser.symbol()));
                    assert outputFile != null;
                    outputFile.print("0" + binary);
                    break;

            }
            if (parser.hasMoreCommands())
                outputFile.println("");
        }
        assert outputFile != null;
        outputFile.close();
        parser.close();
    }

    private static Parser getParser(String fileName, Parser parser) {
        try {
            parser = new Parser(new Scanner(new FileReader(new File(fileName))));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return parser;
    }

    private static boolean isValidFileName(String fileName) {
        return fileName.endsWith(".asm");
    }

    private static int getInt(String decimalStr) {
        return Integer.parseInt(decimalStr);
    }





}
