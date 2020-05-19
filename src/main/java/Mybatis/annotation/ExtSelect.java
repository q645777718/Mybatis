package Mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author shkstart
 * @create 2020-05-17 22:10
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExtSelect {
    String value();
}
