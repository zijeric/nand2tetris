package virtual;
/*
 * Created by Nolva on 2020/9/21.
 */

import java.io.File;
import java.util.ArrayList;

public class VMTranslator {
    public static void main(String[] args) {

        String outputFile = null;
        ArrayList<Parser> files2parse = new ArrayList<>();

        if (args.length != 1) {
            printCommandLineErrorAndExit();
        }

        File file = new File(args[0]);
        boolean exists = file.exists();
        boolean isDirectory = file.isDirectory();
        boolean isFile = file.isFile();

//        if (!exists) {
//
//        }
    }

    private static void printCommandLineErrorAndExit() {
        System.err.println("usage: java VMTranslator/VMTranslator <filename.vm>");
        System.err.println("OR");
        System.err.println("java VMTranslator/VMTranslator <directory>");
        System.exit(1);
    }
}
