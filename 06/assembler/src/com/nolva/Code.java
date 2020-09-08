package com.nolva;
/*
 * Created by Nolva on 2020/9/8.
 */

import java.util.HashMap;

/**
 * 将Hack汇编语言助记符翻译成二进制码
 * 用于Assembler.java
 */
public class Code {

   /**
    * 指令comp域助记符及其二进制形式的转换表
    */
    private static final HashMap<String, String> compRegion = new HashMap<>(){{
        put("0",   "0101010");
        put("1",   "0111111");
        put("-1",  "0111010");
        put("D",   "0001100");
        put("A",   "0110000");
        put("M",   "1110000");
        put("!D",  "0001101");
        put("!A",  "0110011");
        put("!M",  "1110011");
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

   /**
    * 以二进制形式返回comp助记符(7bits)
    * @param symbol
    * @return
    */
   public static String comp(String symbol) {
       if (!compRegion.containsKey(symbol)) {
          throw new IllegalArgumentException("Invalid comp mnemonic: " + symbol);
       } else {
          return compRegion.get(symbol);
       }
   }

   /**
    * 根据 是否 需要存储到当前寄存器往binary(指令对应的二进制形式)填充(1/0)
    * @param symbol
    * @param binary
    * @param register
    */
    private static void checkDest(String symbol, StringBuilder binary, String register) {
//      判断输入是否需要存储到当前寄存器
        if (symbol.contains(register)) {
           binary.append("1");
        } else {
           binary.append("0");
        }
    }

   /**
    * 以二进制形式返回dest助记符(3bits)
    * @param symbol
    * @return
    */
    public static String dest(String symbol) {
        StringBuilder binary = new StringBuilder();

        checkDest(symbol, binary, "A");
        checkDest(symbol, binary, "D");
        checkDest(symbol, binary, "M");

        return binary.toString();
    }

    /**
     * 以二进制形式返回jump助记符(3bits)
     * @param symbol
     * @return
     */
    public static String jump(String symbol) {
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
     * 返回15位 以二进制形式表示的 十进制非负常数
     * @param decimal
     * @return
     */
    public static String binary(int decimal) {

//        常数大于2^15，二进制形式大于15位 不合规范
        if (decimal > 65535) {
            throw new IllegalArgumentException("Number too big to load into A-register");
        }

        StringBuilder binary = new StringBuilder();

//        将十进制转换为二进制，并存储在StringBuilder对象
        while (decimal > 0){
            binary.append(decimal % 2);
            decimal /= 2;
        }

//        用0填充，直至二进制码为15位
        while (binary.length() < 15) {
            binary.append("0");
        }

        binary.reverse();

        return binary.toString();
    }

}
