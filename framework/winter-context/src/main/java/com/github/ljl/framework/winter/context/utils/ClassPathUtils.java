package com.github.ljl.framework.winter.context.utils;

import com.github.ljl.framework.winter.context.io.InputStreamCallback;

import java.io.*;
import java.util.Objects;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 13:52
 **/

public class ClassPathUtils {
    public static <T> T readInputStream(String path, InputStreamCallback<T> inputStreamCallback) throws FileNotFoundException {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        try (InputStream input = getContextClassLoader().getResourceAsStream(path)) {
            if (Objects.isNull(input)) {
                throw new FileNotFoundException("File not found in classpath: " + path);
            }
            return inputStreamCallback.doWithInputStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    static ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (Objects.isNull(cl == null)) {
            cl = ClassPathUtils.class.getClassLoader();
        }
        return cl;
    }
}
