package com.github.ljl.framework.winter.context.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 13:53
 **/

@FunctionalInterface
public interface InputStreamCallback<T> {
    T doWithInputStream(InputStream stream) throws IOException;
}
