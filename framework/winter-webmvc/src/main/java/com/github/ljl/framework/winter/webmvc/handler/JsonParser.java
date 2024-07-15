package com.github.ljl.framework.winter.webmvc.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 22:15
 **/

public class JsonParser implements ResponseParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void parseAndSend(HttpServletResponse response, Object result) throws IOException {
        Writer writer = response.getWriter();
        Object body = result;
        if (body instanceof String || body instanceof byte[]) {
            writer.write(String.valueOf(body));
        }
        // map或object 转json
        else {
            String json = body.toString();
            try {
                json = objectMapper.writeValueAsString(body);
                response.setContentType("application/json");
            } catch (Exception ignored) {
            }
            writer.write(json);
        }
        // write
        writer.flush();
    }
}
