package com.sundy.boot.utils;

import org.springframework.util.StringUtils;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;

/**
 * Created on 2017/12/14
 *
 * @author plus.wang
 * @description 公式计算
 */
public class ParsiiExprUtil {

    public static final String FOREIGN_EXP = "( f + b + s ) * y * ( 1 + t )";

    public static final String FOREIGN_EXP_NOY = "( f + b + s ) * ( 1 + t )";

    public static final String INLAND_EXP = "( i + p ) / 1.16";

    public static final String INLAND_EXP_NOT = "i + p";

    public static final String PARITY_EXP = "i / f";

    public static double evaluateExpr(String exprStr, String f, String i, String b, String s, String y, String t,
                                      String p) throws Exception {

        Scope scope = new Scope();

        if (StringUtils.isEmpty(exprStr)) {

            throw new Exception("unexpected expr : " + exprStr);
        }

        if (exprStr.contains("f") && !StringUtils.isEmpty(f)) {

            Variable variablef = scope.getVariable("f");

            variablef.setValue(Double.parseDouble(f));
        }

        if (exprStr.contains("i") && !StringUtils.isEmpty(i)) {

            Variable variablei = scope.getVariable("i");

            variablei.setValue(Double.parseDouble(i));
        }

        if (exprStr.contains("b") && !StringUtils.isEmpty(b)) {

            Variable variableb = scope.getVariable("b");

            variableb.setValue(Double.parseDouble(b));
        }

        if (exprStr.contains("s") && !StringUtils.isEmpty(s)) {

            Variable variables = scope.getVariable("s");

            variables.setValue(Double.parseDouble(s));
        }

        if (exprStr.contains("y") && !StringUtils.isEmpty(y)) {

            Variable variabley = scope.getVariable("y");

            variabley.setValue(Double.parseDouble(y));
        }

        if (exprStr.contains("t") && !StringUtils.isEmpty(t)) {

            Variable variablet = scope.getVariable("t");

            variablet.setValue(Double.parseDouble(t));
        }

        if (exprStr.contains("p") && !StringUtils.isEmpty(p)) {

            Variable variablep = scope.getVariable("p");

            variablep.setValue(Double.parseDouble(p));
        }

        Expression expr = Parser.parse(exprStr, scope);

        return expr.evaluate();
    }
}
