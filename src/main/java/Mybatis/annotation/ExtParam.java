package Mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author shkstart
 * @create 2020-05-17 22:09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ExtParam {
    String value();
}
