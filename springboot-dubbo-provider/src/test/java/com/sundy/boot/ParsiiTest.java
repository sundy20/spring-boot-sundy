package com.sundy.boot;

import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;
import parsii.tokenizer.ParseException;

/**
 * Created on 2017/11/23
 *
 * @author plus.wang
 * @description
 */
public class ParsiiTest {

    public static void main(String[] args) throws ParseException {

        Scope scope = new Scope();

        Variable a = scope.getVariable("a");

        Variable b = scope.getVariable("b");

        a.setValue(5);

        b.setValue(7);

        Expression expr = Parser.parse("3 * a + b / 4", scope);

        System.out.println(expr.evaluate());

        String volume = "1000000";

        volume = volume.length() > 6 ? volume.substring(0, volume.length() - 4) + "万" : volume;

        System.out.println(volume);
    }
}
