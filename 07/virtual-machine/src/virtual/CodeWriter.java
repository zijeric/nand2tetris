package virtual;

import java.io.PrintWriter;

/**
 * ��VMָ����Hack�����룬��д����Ӧ��.asm����ļ�
 */
class CodeWriter {

    private PrintWriter writer;
//    ��ǰ�������.vm�ļ�
    private String fileName;
//    �߼���������������
    private int logicalIndex;

//    Ϊд������ļ���׼��
    CodeWriter(PrintWriter writer) {
        this.writer = writer;
        logicalIndex = 0;
    }

//    ��ʼ�����µ�.vm�ļ�
    void setFileName(String fileName) {
        this.fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
    }

    /**
     * ��������������������Ӧ�Ļ��д������ļ�
     * @param command ����ָ��
     */
    void writeArithmetic(String command) {
        switch (command){
            case "add":
                writeBinaryArithmetic();
                writer.println("M=M+D");
                break;
            case "sub":
                writeBinaryArithmetic();
                writer.println("M=M-D");
                break;
            case "neg":
                writeUnaryArithmetic();
                writer.println("M=-M");
                break;
            case "eq":
                writeLogical("JEQ");
                break;
            case "gt":
                writeLogical("JGT");
                break;
            case "lt":
                writeLogical("JLT");
                break;
            case "and":
                writeBinaryArithmetic();
                writer.println("M=M&D");
                break;
            case "or":
                writeBinaryArithmetic();
                writer.println("M=M|D");
                break;
            case "not":
                writeUnaryArithmetic();
                writer.println("M=!M");
                break;
        }
    }

    /**
     * д��(Push/Pop)ָ���Ӧ�Ļ��ָ��
     * segment(local��, argument��, this/that��)��RAM�д洢���Ƕ�Ӧ�εĻ���ַbase
     * (temp��, pointer��, static��)�����ε�ֱֵ��ӳ�䵽RAM
     * @param command ָ������(Push/Pop)
     * @param segment ָ���ĵ�һ��������segment(constant��, local��, argument��...)
     * @param index ��Ӧsegment�ε�����
     */
    void writePushAndPop(Parser.Command command, String segment, int index) {

        switch (segment) {
            case "constant":
                if (command == Parser.Command.C_PUSH) {
//                    @decimal
                    writer.println("@" + index);
//                    �洢��D�Ĵ���
                    writer.println("D=A");
//                    (PUSH)��D�Ĵ�����ֵ�洢��SP��ָ����ڴ浥Ԫ��SP++
                    PushD2StackAndInc();
                }
                break;
            case "local":
                if (command == Parser.Command.C_PUSH) {
                    writeBasePush("@LCL", index);
                } else if (command == Parser.Command.C_POP) {
                    writeBasePop("@LCL", index);
                }
                break;
            case "argument":
                if (command == Parser.Command.C_PUSH) {
                    writeBasePush("@ARG", index);
                } else if (command == Parser.Command.C_POP) {
                    writeBasePop("@ARG", index);
                }
                break;
            case "this":
                if (command == Parser.Command.C_PUSH) {
                    writeBasePush("@THIS", index);
                } else if (command == Parser.Command.C_POP) {
                    writeBasePop("@THIS", index);
                }
                break;
            case "that":
                if (command == Parser.Command.C_PUSH) {
                    writeBasePush("@THAT", index);
                } else if (command == Parser.Command.C_POP) {
                    writeBasePop("@THAT", index);
                }
                break;
            case "temp":
                if (command == Parser.Command.C_PUSH) {
                    int address = 5 + index;
                    String location = "@" + address;
                    writeMappedPush(location);
                } else if (command == Parser.Command.C_POP) {
                    int address = 5 + index;
                    String location = "@" + address;
                    writeMappedPop(location);
                }
                break;
            case "pointer":
//                TODO pointer & static implementation
                if (command == Parser.Command.C_PUSH) {
                    if (index == 0) {
                        writeMappedPush("@THIS");
                    } else if (index == 1) {
                        writeMappedPush("@THAT");
                    }
                } else if (command == Parser.Command.C_POP) {
                    if (index == 0) {
                        writeMappedPop("@THIS");
                    } else if (index == 1) {
                        writeMappedPop("@THAT");
                    }
                }
                break;
            case "static":
                if (command == Parser.Command.C_PUSH) {
                    writeMappedPush("@" + fileName + "." + index);
                } else if (command == Parser.Command.C_POP) {
                    writeMappedPop("@" + fileName + "." + index);
                }
                break;
        }
    }

    /**
     * temp: stored in RAM locations 5 to 12 (8��)
     * addr = (5+i), SP--, *addr = *SP
     * @param location ֱ��ӳ����RAM�Ķε�ַ
     */
    private void writeMappedPop(String location) {
        decAndPopTopStack();
        writer.println(location);
        writer.println("M=D");
    }

    /**
     * temp: stored in RAM locations 5 to 12 (8��)
     * ѹ���ջ������ֱ��ӳ�䵽RAM
     * ֱ�ӽ�ֵ�洢��D�Ĵ�������PushD2StackAndInc
     * addr = (5+i), *SP = *addr, SP++
     * @param location @(5+index)
     */
    private void writeMappedPush(String location) {
        writer.println(location);
//        D=M, ӳ���ַM�洢��D�Ĵ���
        writer.println("D=M");
        PushD2StackAndInc();
    }

    /**
     * д���ɻ���ַ������(base+i)������Popָ��
     * addr = segmentPointer + i, SP--, *addr = *SP
     * @param segmentPointer segment�ε�ָ��
     * @param index segment����
     */
    private void writeBasePop(String segmentPointer, int index) {
//        addr = segmentPointer + i����ȡaddr���洢��R13
        writer.println(segmentPointer);
        writer.println("D=M");
        writer.println("@" + index);
        writer.println("D=D+A");
        writer.println("@R13");
        writer.println("M=D");
//        SP--, *addr = *SP
        decAndPopTopStack();
        writer.println("@R13");
        writer.println("A=M");
        writer.println("M=D");
    }

    /**
     * ѹ���ջ�Բ���ָ������λ���ַ�ķ���
     * д���ɻ���ַ������(base+i)������Pushָ��
     * addr = segmentPointer + i, *SP = *addr, SP++
     * @param segmentPointer segment�ε�ָ��
     * @param index segment����
     */
    private void writeBasePush(String segmentPointer, int index) {
//        ��(segmentPointer+i)��ָ����ڴ浥Ԫ��ֵ(*addr)�洢��D�Ĵ���
        writer.println(segmentPointer);
        writer.println("D=M");
        writer.println("@" + index);
        writer.println("A=D+A");
        writer.println("D=M");

        PushD2StackAndInc();
    }

    /**
     * @param operator write ��֧
     */
    private void writeLogical(String operator) {
        decAndPopTopStack();
        writer.println("A=A-1");
        writer.println("D=M-D");
        writer.println("@" + fileName + "_TRUE" + logicalIndex);
        writer.println("D;" + operator);
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=0");
        writer.println("@" + fileName + "_CONTINUE" + logicalIndex);
        writer.println("0;JMP");
        writer.println("(" + fileName + "_TRUE" + logicalIndex + ")");
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=-1");
        writer.println("(" + fileName + "_CONTINUE" + logicalIndex + ")");
        logicalIndex++;
    }

    /**
     * (PUSH)��D�Ĵ�����ֵ�洢��SP��ָ����ڴ浥Ԫ��SP++
     * �ȸ�ֵ��������
     */
    private void PushD2StackAndInc() {
        writePointerVal();
        writeInc();
    }

    /**
     * һԪ���߼�ָ�M�Ĵ����洢��(SP-1)��ָ����ڴ浥Ԫ��ֵ
     * SP-1: A=M-1����δ������M�Ĵ���
     */
    private void writeUnaryArithmetic() {
        writer.println("@SP");
        writer.println("A=M-1");
    }

    /**
     * ��Ԫ���߼�ָ�D�Ĵ����洢��(SP--)��ָ����ڴ浥Ԫ��ֵ��M�Ĵ����洢��(SP-1)��ָ����ڴ浥Ԫ��ֵ
     * ����D��ȡ��SP--ָ���ֵ��M��ȡ��SP-1ָ���ֵ���ȴ���һ��������ָ��
     * SP--: M=M-1��SP�Լ�
     */
    private void writeBinaryArithmetic() {
        decAndPopTopStack();
        writeUnaryArithmetic();
    }

    /**
     * SP����
     */
    private void writeInc() {
        writer.println("@SP");
        writer.println("M=M+1");
    }

    /**
     * SP�Լ�����ȡջ��Ԫ��
     */
    private void decAndPopTopStack() {
        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
    }

    /**
     * ��SP��ָ����ڴ浥Ԫ��ֵ(D�Ĵ�����ֵ)
     */
    private void writePointerVal() {
        writer.println("@SP");
        writer.println("A=M");
        writer.println("M=D");
    }

    void close() {
        writer.close();
    }
}