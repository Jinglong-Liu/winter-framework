package com.github.ljl.framework.winter.test.webmvc.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 12:43
 **/

/**
 * 作为@RequestBody的参数，必须有无参构造方法
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginBean {
    String username;
    String password;
}
