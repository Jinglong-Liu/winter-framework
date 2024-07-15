package com.github.ljl.framework.winter.webmvc.handler;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 21:44
 **/

import com.github.ljl.framework.winter.webmvc.bean.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * parse ResponseEntity
 */
public class ResponseEntityParser implements ResponseParser {

    private static final JsonParser jsonParser = new JsonParser();

    @Override
    public void parseAndSend(HttpServletResponse response, Object result) throws IOException {
        ResponseEntity entity = (ResponseEntity) result;
        // status
        response.setStatus(entity.getStatusCode());
        // headers
        entity.getHeaders().forEachKV((key, value) -> {
            response.addHeader((String) key, (String) value);
        });
        // body
        Object body = entity.getBody();
        jsonParser.parseAndSend(response, body);
    }
}
