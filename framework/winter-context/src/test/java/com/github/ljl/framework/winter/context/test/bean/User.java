package com.github.ljl.framework.winter.context.test.bean;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.annotation.Value;
import com.github.ljl.framework.winter.context.beans.BeanPostProcessor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 14:35
 **/

@Component
@Data
public class User {
    @Value("user_id")
    String id;
    @Value("user_name")
    String name;
    String email;
    @Value("20")
    String age;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
