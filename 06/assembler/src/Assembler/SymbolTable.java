package Assembler;

import java.util.Hashtable;

/**
 * Created by Nolva on 2020/9/10.
 * @author Nolva
 */


public class SymbolTable {

    private Hashtable<String, Integer> symbolTable;
    private int currAddress = 16;

    SymbolTable(Hashtable<String, Integer> symbolTable) {
        this.symbolTable = symbolTable;
//        predefined symbols
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        symbolTable.put("R0", 0);
        symbolTable.put("R1", 1);
        symbolTable.put("R2", 2);
        symbolTable.put("R3", 3);
        symbolTable.put("R4", 4);
        symbolTable.put("R5", 5);
        symbolTable.put("R6", 6);
        symbolTable.put("R7", 7);
        symbolTable.put("R8", 8);
        symbolTable.put("R9", 9);
        symbolTable.put("R10", 10);
        symbolTable.put("R11", 11);
        symbolTable.put("R12", 12);
        symbolTable.put("R13", 13);
        symbolTable.put("R14", 14);
        symbolTable.put("R15", 15);
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
    }

    void addEntry(String symbol, int address) {
        symbolTable.put(symbol, address);
    }

    boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    int getAddress(String symbol) {
        return symbolTable.get(symbol);
    }
}
