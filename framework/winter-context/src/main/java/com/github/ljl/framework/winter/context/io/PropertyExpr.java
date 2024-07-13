package com.github.ljl.framework.winter.context.io;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 10:57
 **/

@Data
@AllArgsConstructor
public class PropertyExpr {
    String key;
    String defaultValue;
}
