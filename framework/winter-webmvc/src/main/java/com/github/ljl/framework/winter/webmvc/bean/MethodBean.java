package com.github.ljl.framework.winter.webmvc.bean;

import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 22:32
 **/

@Data
public class MethodBean {
    private Class<?> clazz;
    private Object bean;
    private Method method;
    private Parameter[] parameters;
    private boolean isRestful;
    private String path;
    private LinkedHashMap<String, String> pathVariables;
    public void addPathVariableName(String name) {
        pathVariables.put(name, null);
    }

    /**
     * 先有key才允许set
     * @param name
     * @param value
     * @return
     */
    public boolean setPathVariable(String name, String value) {
        if (pathVariables.containsKey(name)) {
            pathVariables.put(name, value);
            return true;
        }
        return false;
    }
    public String getVariable(String name) {
        return pathVariables.get(name);
    }
}
