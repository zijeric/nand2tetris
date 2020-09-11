package Assembler;
/**
 * Created by Nolva on 2020/9/10.
 * @author Nolva
 */

import java.util.Scanner;

class Parser {

    private Scanner scanner;
    private String currentCommand;

    Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    boolean hasMoreCommands() {
        return scanner.hasNextLine();
    }

    void advance() {
        this.currentCommand = scanner.nextLine();
    }

    public enum commandType {
        C_COMMAND,
        A_COMMAND,
        L_COMMAND
    }

    void skipSpacesAndComments() {
        if (currentCommand.contains("//")) {
            currentCommand = currentCommand.substring(0, currentCommand.indexOf("//"));
        }
        currentCommand = currentCommand.replace(" ", "");
        currentCommand = currentCommand.replace("\t", "");
    }

    int commandLength(){
        return currentCommand.length();
    }

    commandType CommandType() {
        if (currentCommand.startsWith("@")) {
            return commandType.A_COMMAND;
        } else if (currentCommand.startsWith("(")) {
            return commandType.L_COMMAND;
        } else {
            return commandType.C_COMMAND;
        }
    }

    /**
     * C_Command: dest=comp;jump, dest is optional
     * @return (dest region)
     */
    String dest() {
        if (currentCommand.contains("=")) {
            return currentCommand.split("=")[0];
        } else {
            return "";
        }
    }

    /**
     * C_Command: dest=comp;jump, comp is indispensable
     * @return (dest region)
     */
    String comp() {
        if (currentCommand.contains("=")) {
            return currentCommand.split("[=;]")[1];
        } else {
            return currentCommand.split(";")[0];
        }
    }

    /**
     * C_Command: dest=comp;jump, jump is optional
     * @return (jump region)
     */
    String jump() {
        if (currentCommand.contains(";")) {
            return currentCommand.split(";")[1];
        } else {
            return "";
        }
    }

    /**
     * A_Command: only @decimal
     * @return (decimal)
     */
    String symbol() {
        return currentCommand.substring(1);
    }

    void close() {
        if (scanner != null)
            scanner.close();
    }

}
