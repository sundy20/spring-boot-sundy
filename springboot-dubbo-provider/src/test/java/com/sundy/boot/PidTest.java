package com.sundy.boot;

import com.sundy.boot.pid.BasePID;
import com.sundy.boot.pid.IncrementalPID;
import com.sundy.boot.pid.PIDParam;
import com.sundy.boot.pid.PositionalPID;

public class PidTest {
    public static void main(String[] args) {
        PIDParam param = new PIDParam(5, 1, 1, 80);

        System.out.println("--------------Positional--------------");

        BasePID pid = new PositionalPID(param);
        System.out.println("current:30,output:" + pid.collectAndCalculate(30));
        System.out.println("current:50,output:" + pid.collectAndCalculate(50));
        System.out.println("current:60,output:" + pid.collectAndCalculate(60));

        System.out.println("--------------------------------------\n");


        System.out.println("--------------Incremental-------------");

        pid = new IncrementalPID(param);
        System.out.println("current:30,output:" + pid.collectAndCalculate(30));
        System.out.println("current:50,output:" + pid.collectAndCalculate(50));
        System.out.println("current:60,output:" + pid.collectAndCalculate(60));

        System.out.println("--------------------------------------");
    }
}
