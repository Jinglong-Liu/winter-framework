package com.github.ljl.framework.winter.webmvc.bean;

import com.github.ljl.framework.winter.webmvc.handler.MultiValueMap;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 17:23
 **/

@Data
public class ResponseEntity<T> {

    private T body;

    private int statusCode = 200;

    private MultiValueMap<String, String> headers = new MultiValueMap<>();
    public boolean hasBody() {
        return Objects.nonNull(body);
    }

    public T getBody() {
        return body;
    }

    public static <T> ResponseEntity<T> ok(Class<T> clazz) {
        ResponseEntity<T> entity = new ResponseEntity<>();
        entity.statusCode = 200;
        return entity;
    }
    public static ResponseEntity ok() {
        ResponseEntity entity = new ResponseEntity<>();
        entity.statusCode = 200;
        return entity;
    }

    public ResponseEntity<T> status(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseEntity<T> header(Map<String, List<String>> header) {
        headers.putAll(header);
        return this;
    }
    public ResponseEntity<T> header(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public ResponseEntity<T> body(T body) {
        this.body = body;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        if (this.body != null) {
            builder.append(this.body);
            builder.append(',');
        }
        builder.append(">");
        builder.append(headers);
        return builder.toString();
    }
}
