package Assembler;

import java.util.HashMap;

/**
 * Created by Nolva on 2020/9/10.
 * @author Nolva
 */

class Code {

//    comp region
    private static HashMap<String, String> compRegion = new HashMap<>(){{
    put("0",   "0101010");
    put("1",   "0111111");
    put("-1",  "0111010");
    put("D",   "0001100");
    put("A",   "0110000");
    put("M",   "1110000");
    put("!D",  "0001101");
    put("!A",  "0110011");
    put("!M",  "1110001");
    put("-D",  "0001111");
    put("-A",  "0110011");
    put("-M",  "1110011");
    put("D+1", "0011111");
    put("A+1", "0110111");
    put("M+1", "1110111");
    put("D-1", "0001110");
    put("A-1", "0110010");
    put("M-1", "1110010");
    put("D+A", "0000010");
    put("D+M", "1000010");
    put("D-A", "0010011");
    put("D-M", "1010011");
    put("A-D", "0000111");
    put("M-D", "1000111");
    put("D&A", "0000000");
    put("D&M", "1000000");
    put("D|A", "0010101");
    put("D|M", "1010101");
    }};

    static String comp(String symbol) {
        if (!compRegion.containsKey(symbol)) {
            throw new IllegalArgumentException("Invalid comp mnemonic: " + symbol);
        } else {
            return compRegion.get(symbol);
        }
    }

//    dest region
    private static void checkDest(String symbol, StringBuilder binary, String register) {
        if (symbol.contains(register)) {
            binary.append("1");
        } else {
            binary.append("0");
        }
    }

    static String dest(String symbol) {
        StringBuilder binary = new StringBuilder();

        checkDest(symbol, binary, "A");
        checkDest(symbol, binary, "M");
        checkDest(symbol, binary, "D");
        return binary.toString();
    }

//    jump region
    static String jump(String symbol) {
        switch (symbol) {
            case "":
                return "000";
            case "JGT":
                return "001";
            case "JEQ":
                return "010";
            case "JGE":
                return "011";
            case "JLT":
                return "100";
            case "JNE":
                return "101";
            case "JLE":
                return "110";
            default:
                return "111";
        }
    }

    /**
     * A_Command: @decimal
     * @return binary(decimal)
     */
    static String binary(int decimal) {

        if (decimal > 65535) {
            throw new IllegalArgumentException("Number is too big to load into A_Command");
        }

        StringBuilder binary = new StringBuilder();

        while (decimal > 0) {
            binary.append(decimal % 2);
            decimal /= 2;
        }

        while (binary.length() < 15) {
            binary.append("0");
        }

        binary.reverse();
        return binary.toString();

    }
}
