package com.github.ljl.framework.winter.webmvc.handler;

import com.github.ljl.framework.winter.webmvc.bean.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ResponseParser {
    void parseAndSend(HttpServletResponse response, Object result) throws IOException;
}
