package com.test;
/*
 * Created by Nolva on 2020/9/8.
 */

public class testSplit {
    public static void main(String[] args) {
        String current = "dest=comp;jump";
        System.out.println("输出命令：" + current.split(";")[1]);
    }
}
