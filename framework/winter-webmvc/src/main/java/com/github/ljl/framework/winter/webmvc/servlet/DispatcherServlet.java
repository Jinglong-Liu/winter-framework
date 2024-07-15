package com.github.ljl.framework.winter.webmvc.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ljl.framework.winter.context.beans.BeanDefinition;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.context.context.ConfigurableApplicationContext;
import com.github.ljl.framework.winter.context.utils.DataConvertUtils;
import com.github.ljl.framework.winter.webmvc.annotation.*;
import com.github.ljl.framework.winter.webmvc.bean.MethodBean;
import com.github.ljl.framework.winter.webmvc.exception.InvalidParameterTypeException;
import com.github.ljl.framework.winter.webmvc.exception.MissingPathVariableException;
import com.github.ljl.framework.winter.webmvc.exception.MissingRequestHeaderException;
import com.github.ljl.framework.winter.webmvc.exception.MissingServletRequestParameterException;
import com.github.ljl.framework.winter.webmvc.handler.HandlerMapping;
import com.github.ljl.framework.winter.webmvc.handler.ResponseBodyMethodProcessor;
import com.github.ljl.framework.winter.webmvc.handler.ResponseParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 22:26
 **/

public class DispatcherServlet extends HttpServlet {

    private final HandlerMapping handlerMapping = new HandlerMapping();

    private final ConfigurableApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    private final Map<Class<? extends Annotation>, Function<ParameterContext, Object>> parameterAnnotationConvertMap = new HashMap<>();

    private final Map<Class<?>, Function<ParameterContext, Object>> parameterTypeConvertMap = new HashMap<>();

    @SneakyThrows
    public DispatcherServlet(ApplicationContext applicationContext) {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;

        parameterAnnotationConvertMap.put(PathVariable.class, this::parsePathVariable);
        parameterAnnotationConvertMap.put(RequestBody.class, this::parseRequestBody);
        parameterAnnotationConvertMap.put(RequestParam.class, this::parseRequestParam);
        // untested
        parameterAnnotationConvertMap.put(RequestHeader.class, this::parseRequestHeader);
        parameterAnnotationConvertMap.put(RequestAttribute.class, this::parseRequestAttribute);
        parameterAnnotationConvertMap.put(SessionAttribute.class, this::parseSessionAttribute);
        //
        parameterTypeConvertMap.put(HttpServletRequest.class, ParameterContext::getRequest);
        parameterTypeConvertMap.put(HttpServletResponse.class, ParameterContext::getResponse);
        parameterTypeConvertMap.put(HttpSession.class, context -> context.getRequest().getSession());
        //
        parameterTypeConvertMap.put(Reader.class, this::getReaderFromRequest);
        parameterTypeConvertMap.put(InputStream.class, this::getInputStreamFromRequest);
        parameterTypeConvertMap.put(Writer.class, this::getWriterFromResponse);
        parameterTypeConvertMap.put(OutputStream.class, this::getOutputStreamFromResponse);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doDispatch(request, response);
    }

    // init(ServletConfig)
    @Override
    public void init() throws ServletException {
        initBean();
    }
    protected void initBean() {
        final List<BeanDefinition> beanDefinitions = applicationContext.findBeanDefinitions(Object.class);
        beanDefinitions.forEach(beanDefinition -> {
            Class<?> clazz = beanDefinition.getBeanClass();
            final RestController restController = clazz.getAnnotation(RestController.class);
            if (Objects.nonNull(restController)) {
                handleRestController(clazz, beanDefinition.getRequiredInstance());
            }
        });
    }
    private void handleRestController(Class<?> clazz, Object bean) {
        String prefix = clazz.getAnnotation(RestController.class).value();

        Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            if (Objects.nonNull(getMapping)) {
                String methodPath = getMapping.value();
                String path = trimPath(prefix + methodPath);
                handlerMapping.register("GET", path, bean, method);
            }
            if (Objects.nonNull(postMapping)) {
                String methodPath = postMapping.value();
                String path = trimPath(prefix + methodPath);
                handlerMapping.register("POST", path, bean, method);
            }
        });
    }

    private String trimPath(String inputPath) {
        String path = inputPath.trim().replaceAll("//", "/");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
    private String toPath(String url) {
        return url;
    }


    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, InvalidParameterTypeException {
        String requestMethod = request.getMethod();
        String uri = request.getRequestURI();
        String path = toPath(uri);
        MethodBean methodBean = handlerMapping.getMethod(requestMethod, path);
        if (Objects.isNull(methodBean)) {
            String message = "method not match!\r\npath=" + path + ", method=" + requestMethod;
            logger.error(message);
            sendError(response, message, 500);
            return;
        }
        Method method = methodBean.getMethod();

        List<Object> argList = new ArrayList<>();

        // 方法参数逐个按顺序解析并放进argList,
        /**
         * 目前支持的注解参数类型
         * @RequestParam(value=name): 去匹配request.getParameter(name)
         * @ResponseBody: 通过getReader() 获得body，解析成对应类型(Object, map)
         * @HttpServletRequest: request
         * @HttpServletResponse: response
         * @HttpSession: request.getSession()
         * @PathVariable : /user/{id}/profile/{pid}
         *
         * other: throw exception
         * @throws: InvalidParameterTypeException
         */
        try {
            for (Parameter parameter: methodBean.getParameters()) {
                final ParameterContext parameterContext = new ParameterContext(request, response, parameter, methodBean);
                final Class<?> parameterType = parameter.getType();
                // @RequestBody, @RequestParam, @PathVariable
                Optional<Annotation> matchingAnnotation =
                        Arrays.stream(parameter.getAnnotations())
                        .filter(annotation -> parameterAnnotationConvertMap.containsKey(annotation.annotationType()))
                        .findFirst();

                if (matchingAnnotation.isPresent()) {
                    argList.add(parameterAnnotationConvertMap.get(matchingAnnotation.get().annotationType()).apply(parameterContext));
                    continue;
                }

                // request, response, session
                if (parameterTypeConvertMap.containsKey(parameterType)) {
                    argList.add(parameterTypeConvertMap.get(parameterType).apply(parameterContext));
                    continue;
                }
                // other: invalid
                throw new InvalidParameterTypeException("Parameter Type in controller not supported, " +
                        "maybe add annotation @RequestBody, @RequestParam, @PathVariable" +
                        "Or using HttpServletRequest, HttpServletResponse and HttpSession");
            }
        } catch (InvalidParameterTypeException e) {
            sendError(response, "invalid parameter", 500);
            return;
        }
        Object result = null;
        // 必须是数组形式，也就是Object... args
        Object[] args = argList.toArray(new Object[0]);

        // TODO: other parameter annotations
        try {
            if (!Modifier.isPublic(method.getModifiers())) {
                logger.error("method '{}' is not accessible, make it public", method.getName());
                sendError(response,"Method is not accessible", 500);
                throw new IllegalStateException("Method is not accessible, make it public:" + method.getName());
            }
            result = method.invoke(methodBean.getBean(), args);
        } catch (IllegalAccessException
                | InvocationTargetException
                | IllegalArgumentException e) {
            e.printStackTrace();
            sendError(response, "server meet error when invoke method", 500);
            return;
        }
        ResponseParser processor = new ResponseBodyMethodProcessor();
        try {
            processor.parseAndSend(response, result);
        } catch (Exception e) {
            sendError(response, "error when parse result", 500);
            e.printStackTrace();
        }
    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        Writer writer = response.getWriter();
        writer.write(message);
        writer.flush();
    }

    @AllArgsConstructor
    @Data
    private static class ParameterContext {
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final Parameter parameter;
        private final MethodBean methodBean;
    }


    private Object parseRequestBody(ParameterContext context) throws IllegalArgumentException {
        HttpServletRequest request = context.getRequest();
        Class<?>parameterType = context.getParameter().getType();
        // body
        Object value = null;
        StringBuilder bodyBuilder = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();

            String line;
            while((line = reader.readLine()) != null) {
                bodyBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String body = bodyBuilder.toString();
        String contentType = request.getContentType();

        /**
         * application允许json
         */
        if (contentType.contains("json") || contentType.contains("/*")) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 尝试json转object 或map
                // 参数是Map类型
                if (parameterType.isAssignableFrom(Map.class)) {
                    value = objectMapper.readValue(body.trim(), Map.class);
                } else {
                    // Object
                    value = objectMapper.readValue(body.trim(), parameterType);
                }
            } catch (Exception ignored) {
                logger.warn("parse json request body to map or object error");
            }
        }
        if (Objects.isNull(value)) {
            value = DataConvertUtils.convertToType(body, parameterType);
        }
        return value;
    }

    private Object parseRequestParam(ParameterContext context) {
        RequestParam requestParam = context.getParameter().getAnnotation(RequestParam.class);
        HttpServletRequest request = context.getRequest();
        Class<?> parameterType = context.getParameter().getType();
        String name = requestParam.value();
        String defaultValue = requestParam.defaultValue();
        // 去匹配request url中parameter, 不需要按顺序
        String value = request.getParameter(name);
        if (Objects.isNull(value)) {
            value = defaultValue;
        }
        return DataConvertUtils.convertToType(value, parameterType);
    }

    private Object parsePathVariable(ParameterContext context) {
        PathVariable pathVariable = context.getParameter().getAnnotation(PathVariable.class);
        String name = pathVariable.value();
        String value = context.getMethodBean().getVariable(name);
        if (Objects.isNull(value)) {
            logger.error("Parse PathVariable(value={}) failed, no matched name in Path Pattern", name);
            throw new MissingPathVariableException("Parse PathVariable error");
        }
        return value;
    }

    private Object parseRequestHeader(ParameterContext context) {
        RequestHeader requestHeader = context.getParameter().getAnnotation(RequestHeader.class);
        String name = requestHeader.value();

        String value = context.getRequest().getParameter(name);

        if (Objects.isNull(value)) {
            logger.error("Parse request header(value={}) failed, no matched name in Request Header", name);
            throw new MissingRequestHeaderException("Parse Request Header error");
        }
        return value;
    }

    private Object parseRequestAttribute(ParameterContext context) {
        RequestAttribute requestAttribute = context.getParameter().getAnnotation(RequestAttribute.class);
        String name = requestAttribute.value();

        Object value = context.getRequest().getAttribute(name);

        if (Objects.isNull(value)) {
            logger.error("Parse request attribute(value={}) failed, no matched attribute name", name);
            throw new MissingRequestHeaderException("Parse Request Attribute Error");
        }
        return value;
    }

    private Object parseCookieValue(ParameterContext context) {
        CookieValue cookieValue = context.getParameter().getAnnotation(CookieValue.class);
        String name = cookieValue.value();

        Optional<String> value =
                Arrays.stream(context.getRequest().getCookies())
                        .filter(cookie -> cookie.getName().equals(name))
                        .map(Cookie::getValue)
                        .findFirst();

        if (!value.isPresent()) {
            logger.error("Parse Cookie Value(value={}) failed, no matched name in Cookie", name);
            throw new MissingRequestHeaderException("Parse Cookie Value error");
        }
        return value;
    }

    private Object parseSessionAttribute(ParameterContext context) {
        SessionAttribute sessionAttribute = context.getParameter().getAnnotation(SessionAttribute.class);
        String name = sessionAttribute.value();

        HttpSession httpSession = context.getRequest().getSession(false);
        if (Objects.isNull(httpSession) || Objects.isNull(httpSession.getAttribute(name))) {
            if (sessionAttribute.required()) {
                logger.error("Parse Session Attribute(value={}) failed, no session or attribute", name);
                throw new IllegalStateException("no session found to get sessionAttribute");
            } else {
                return null;
            }
        }
        return httpSession.getAttribute(name);
    }

    @SneakyThrows
    private InputStream getInputStreamFromRequest(ParameterContext context) {
        return context.getRequest().getInputStream();
    }

    @SneakyThrows
    private Object getReaderFromRequest(ParameterContext context) {
        return context.getRequest().getReader();
    }

    @SneakyThrows
    private Object getOutputStreamFromResponse(ParameterContext context) {
        return context.getResponse().getOutputStream();
    }

    @SneakyThrows
    private Object getWriterFromResponse(ParameterContext context) {
        return context.getResponse().getWriter();
    }
}
