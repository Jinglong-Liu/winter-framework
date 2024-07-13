package com.github.ljl.framework.winter.context.io;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 10:59
 **/

@Data
@AllArgsConstructor
public class Resource {
    private String path;
    private String name;
}
