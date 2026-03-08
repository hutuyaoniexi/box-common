package com.box.common.log.support;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * SpEL 表达式解析器。
 */
public class LogExpressionEvaluator {

    private static final ParserContext TEMPLATE_PARSER_CONTEXT = ParserContext.TEMPLATE_EXPRESSION;

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public String evaluate(String expression, Method method, Object target, Object[] args, Object result, Throwable error) {
        if (expression == null || expression.isBlank()) {
            return null;
        }
        StandardEvaluationContext context = new MethodBasedEvaluationContext(target, method, args, parameterNameDiscoverer);
        context.setVariable("result", result);
        context.setVariable("error", error);
        String actualExpression = expression.trim();
        if (actualExpression.startsWith("#{") && actualExpression.endsWith("}")) {
            actualExpression = actualExpression.substring(2, actualExpression.length() - 1);
        }
        Object value = expression.contains("#{")
                ? expressionParser.parseExpression(expression, TEMPLATE_PARSER_CONTEXT).getValue(context)
                : expressionParser.parseExpression(actualExpression).getValue(context);
        return value == null ? null : String.valueOf(value);
    }
}
