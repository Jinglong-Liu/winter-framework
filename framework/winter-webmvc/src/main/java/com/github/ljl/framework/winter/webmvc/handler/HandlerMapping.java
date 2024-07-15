package com.github.ljl.framework.winter.webmvc.handler;

import com.github.ljl.framework.winter.webmvc.bean.MethodBean;
import com.github.ljl.framework.winter.webmvc.exception.RegisterRepeatPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;


/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 22:31
 **/

public class HandlerMapping {
    private static Logger logger = LoggerFactory.getLogger(HandlerMapping.class);
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";

    private final Map<String, Map<String, MethodBean>> methodPathMap = new HashMap<>();

    private static final String[] SUPPORT_HTTP_METHOD = {
            METHOD_GET,
            METHOD_POST,
    };

    public HandlerMapping() {
        for (String method: SUPPORT_HTTP_METHOD) {
            methodPathMap.put(method, new HashMap<>());
        }
    }

    public void register(String requestMethod, String path, Object bean, Method method) {
        Map<String, MethodBean> map = methodPathMap.get(requestMethod);
        if (Objects.isNull(map)) {
            logger.error("request method not allowed:" + requestMethod);
        }
        if (map.containsKey(path)) {
            throw new RegisterRepeatPathException("Register Repeat Path:" + path);
        }
        MethodBean methodBean = new MethodBean();
        methodBean.setMethod(method);
        methodBean.setBean(bean);
        methodBean.setPath(path);
        methodBean.setParameters(method.getParameters());
        methodBean.setPathVariables(new LinkedHashMap<>());

        String[] parts = path.split("/");
        for (String part: parts) {
            part = part.trim();
            if (part.startsWith("{") && part.endsWith("}")) {
                methodBean.addPathVariableName(part.substring(1, part.length() - 1));
            }
        }
        map.put(path, methodBean);
    }
    public MethodBean getMethod(String requestMethod, String url) {
        Map<String, MethodBean> map = methodPathMap.get(requestMethod);
        if (Objects.isNull(map)) {
            logger.error("method {} is not support", requestMethod);
            return null;
        }
        MethodBean methodBean = matchMethodBean(map, url);
        if (Objects.nonNull(methodBean)) {
            return methodBean;
        }
        logger.error("url path {} not match!", url);
        return null;
    }
    // TODO: 重构
    MethodBean matchMethodBean(Map<String, MethodBean> map, String url)
    {   // /users/{userId}/profile
        // /user/asdasd/profile
        Set<String> patterns = map.keySet();
        if (patterns.contains(url)) {
            return map.get(url);
        }

        // 要求前面匹配前缀，但只要出现了依次{}，后面需要完全一致或者{}
        // 那么，找到第一个{}，分两段，前面前缀匹配，后面完全匹配，降低难度
        // 暂时不匹配**
        // root/user/{id}/profile/{pid}
        // root/3/profile/5
        //
        //PathVariable i, 需要set到MethodBean里面
        String[] pathArray = url.split("/");
        for (String pattern: patterns) {
            MethodBean methodBean = map.get(pattern);
            String[] patternArray = pattern.split("/");

            // pattern中，{}的数量
            int count = (int) Arrays.stream(patternArray)
                    .filter(part -> part.startsWith("{") && part.endsWith("}"))
                    .count();
            if (count == 0) {
                continue;
            }
            int firstIndex = 0;
            for (; firstIndex < patternArray.length; firstIndex++) {
                if (patternArray[firstIndex].startsWith("{") && patternArray[firstIndex].endsWith("}")) {
                    break;
                }
            }

            int pathIndex = 0;
            int patternIndex = 0;
            boolean notEqual = false;
            while(patternIndex < firstIndex && pathIndex < pathArray.length) {
                if (!patternArray[patternIndex].equals(pathArray[pathIndex])) {
                    notEqual = true;
                    break;
                }
                patternIndex++;
                pathIndex++;
            }
            if (notEqual) {
                continue;
            }
            // user/name/{id}
            // user/name/
            // 不匹配
            if (pathIndex == pathArray.length) {
                continue;
            }
            // user/name/{id}
            // user/3
            // 匹配，id = 3
            if (patternIndex <= firstIndex) {
                patternIndex = firstIndex;
            }
            // {name} -> name
            String varName = patternArray[patternIndex].substring(1, patternArray[patternIndex].length() - 1);
            if (!methodBean.setPathVariable(varName, pathArray[pathIndex])) {
                continue;
            }

            pathIndex++;
            patternIndex++;

            if (pathArray.length - pathIndex != patternArray.length - patternIndex) {
                continue;
            }

            while(pathIndex < pathArray.length) {
                String patternPart = patternArray[patternIndex];
                if (patternPart.startsWith("{") && patternPart.endsWith("}")) {
                    varName = patternPart.substring(1, patternPart.length() - 1);
                    if (!methodBean.setPathVariable(varName, pathArray[pathIndex])) {
                        break;
                    }
                    pathIndex++;
                    patternIndex++;
                } else if (patternPart.equals(pathArray[pathIndex])) {
                    pathIndex++;
                    patternIndex++;
                } else {
                    break;
                }
            }
            // success
            if (pathIndex == pathArray.length) {
                logger.info("pattern {} match path {}",pattern, url);
                return methodBean;
            }
        }

        // 前缀
        for (String pattern: patterns) {
            if (url.startsWith(pattern)) {
                return map.get(pattern);
            }
        }

        return null;
    }
}
