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

    /**
     * 国外价格计算公式 乘了汇率
     */
    public static final String FOREIGN_EXP = "( f + b + s ) * y * ( 1 + t )";

    /**
     * 国外价格计算公式 不换算汇率
     */
    public static final String FOREIGN_EXP_NOY = "( f + b + s ) * ( 1 + t )";

    /**
     * 白银国外价格计算公式 乘了汇率
     */
    public static final String AG_FOREIGN_EXP = "f * y * ( 1 + t ) / 31.1035 * 1000";

    /**
     * 国内现货价格计算公式 不含税
     */
    public static final String INLAND_EXP = "( i + p ) / 1.16";

    /**
     * 国内现货价格计算公式 含税
     */
    public static final String INLAND_EXP_NOT = "i + p";

    /**
     * 比价计算公式
     */
    public static final String PARITY_EXP = "i / f";

    /**
     * @param exprStr: 公式 f：国外合约价格 i：国内合约价格 b：保税区仓单 s：调水 y：cny即期/cnh即期 t：关税 p：升贴水
     */
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
