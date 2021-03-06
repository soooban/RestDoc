package cn.willingxyz.restdoc.core.parse;

import java.lang.reflect.Method;

public interface IMethodFilter {
    /**
     * 给定一个Method，判断该Method是否是HTTP处理接口
     */
    boolean isSupport(Method method);
}
