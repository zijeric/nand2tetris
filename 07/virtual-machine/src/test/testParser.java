package test;
/*
 * Created by Nolva on 2020/9/15.
 */

public class testParser {
    public static void main(String[] args) {
        String cmd = "pop pointer 1";
        String str = cmd.split("\\s+")[0];  // [1], [2]...
        System.out.println(str);
    }
}
