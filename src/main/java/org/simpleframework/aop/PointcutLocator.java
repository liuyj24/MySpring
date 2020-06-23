package org.simpleframework.aop;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * 解析Aspect表达式并且定位被织入的目标
 */
public class PointcutLocator {
    /**
     * Pointcut解析器，直接给它赋值上AspectJ的所有表达式，以便支持对众多表达式的解析
     */
    private PointcutParser pointcutParser = PointcutParser.
            getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(
                    PointcutParser.getAllSupportedPointcutPrimitives()
            );
    /**
     * 表达式解析器
     */
    private PointcutExpression pointcutExpression;

    public PointcutLocator(String expression){
        this.pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }

    /**
     * 判断传入的Class对象是否是Aspect的目标代理类，即匹配Pointcut表达式（初筛）
     * @param targetClass
     * @return
     */
    public boolean roughMatches(Class<?> targetClass){
        //couldMatchJoinPointsInType比较坑，只能校验within
        //不能校验（execution，call，get，set），面对无法校验的表达式，直接返回true
        return pointcutExpression.couldMatchJoinPointsInType(targetClass);
    }

    /**
     * 判断传入的Methodd对象是否是Aspect的目标代理方法，即匹配Pointcut表达式（精筛）
     * @param method
     * @return
     */
    boolean accurateMatches(Method method){
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        return shadowMatch.alwaysMatches() ? true : false;
    }
}
