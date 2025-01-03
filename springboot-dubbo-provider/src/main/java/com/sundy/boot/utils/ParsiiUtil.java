package com.sundy.boot.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.tokenizer.ParseException;

import java.util.Map;

/**
 * Parsii数学表达式解析工具
 */
public class ParsiiUtil {

    /**
     * 数学表达式解析执行
     *
     * @param expression
     * @param params
     * @return
     */
    public static Double evaluate(String expression, Map<String, Double> params) throws ParseException {

        if (StringUtils.isBlank(expression) || CollectionUtils.isEmpty(params)) {
            return null;
        }

        Scope scope = new Scope();
        params.forEach((k, v) -> scope.getVariable(k).setValue(v));

        Expression expr = Parser.parse(expression, scope);
        return expr.evaluate();

    }
}
