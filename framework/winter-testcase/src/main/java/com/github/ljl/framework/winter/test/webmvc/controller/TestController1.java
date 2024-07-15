package com.github.ljl.framework.winter.test.webmvc.controller;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.test.webmvc.bean.LoginBean;
import com.github.ljl.framework.winter.test.webmvc.bean.User;
import com.github.ljl.framework.winter.test.webmvc.service.IUserService;
import com.github.ljl.framework.winter.webmvc.annotation.*;
import com.github.ljl.framework.winter.webmvc.bean.ResponseEntity;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 11:18
 **/

@RestController("/test1")
public class TestController1 {

    @Resource
    private IUserService userService;

    @Autowired
    private IUserService userService1;

    @GetMapping("/login-error/{user}")
    public String loginError(@PathVariable("user") User user) {
        return "login-error";
    }

    @PostMapping("/login-body")
    public User login(@RequestBody LoginBean loginBean) {;
        return new User(loginBean.getUsername(), loginBean.getPassword(), "abc@123.com", "Desc1");
    }
    @PostMapping("/login-param")
    public User loginParams(@RequestParam("username") String username,
                            @RequestParam("passwd") String password) {
        return new User(username, password,null,null);
    }
    @PostMapping("/login-map")
    public Map<String, String> loginMap(@RequestBody Map<String, String> loginMap) {
        return loginMap;
    }

    @PostMapping("/login/abc/{username}/{pswd}/profile")
    public ResponseEntity<User> loginPartPathVariable(@PathVariable("username") String username, @PathVariable("pswd") String password) {
        User user = new User(username + "2333", password, null, "@PathVariable");
        return ResponseEntity.ok(User.class).body(user);
    }

    @PostMapping("/login/{username}/{pswd}/profile")
    public ResponseEntity loginPathVariable(@PathVariable("username") String username, @PathVariable("pswd") String password) {
        User user = new User(username + "9999", password, null, "@PathVariable");
        return ResponseEntity.ok().body(user);
    }


    @GetMapping("/login/unAccessible")
    String unAccessible() {
        assert false;
        return "unAccessible() is called";
    }

    @PostMapping("/login/servlet/io/404")
    public void loginServet(HttpServletRequest request, HttpServletResponse response, Reader reader, Writer writer) throws IOException {
        assert reader == request.getReader();
        assert writer == response.getWriter();

        assert userService != null;
        assert userService == userService1;

        response.setStatus(404);
        String result = userService.test();
        writer.write(result);
    }
}
