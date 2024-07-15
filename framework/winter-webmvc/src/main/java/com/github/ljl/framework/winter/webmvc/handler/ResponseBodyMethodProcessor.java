package com.github.ljl.framework.winter.webmvc.handler;

import com.github.ljl.framework.winter.webmvc.bean.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 21:20
 **/

public class ResponseBodyMethodProcessor implements ResponseParser {
    private static final ResponseEntityParser responseEntityParser = new ResponseEntityParser();
    private static final JsonParser jsonParser = new JsonParser();
    @Override
    public void parseAndSend (HttpServletResponse responseObject, Object result) throws IOException {
        if(Objects.isNull(result)) {
            responseObject.flushBuffer();
        }
        else if (result instanceof ResponseEntity) {
            responseEntityParser.parseAndSend(responseObject, result);
        } else {
            jsonParser.parseAndSend(responseObject, result);
        }
    }
}
