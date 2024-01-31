package com.sundy.boot;

import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;
import parsii.tokenizer.ParseException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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

        double d = 123.496782;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        System.out.println(decimalFormat.format(d));

        BigDecimal bigDecimal = new BigDecimal("123.496782");
        System.out.println(bigDecimal.setScale(2, RoundingMode.HALF_UP));

        DecimalFormat df = new DecimalFormat("#0.00");
        String s = df.format(bigDecimal.doubleValue());
        System.out.println(s);
    }
}
