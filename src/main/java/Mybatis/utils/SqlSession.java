package Mybatis.utils;

import java.lang.reflect.Proxy;

/**
 * @author shkstart
 * @create 2020-05-17 22:16
 */
public class SqlSession {

    // 加载Mapper接口
    public static <T> T getMapper(Class classz){
        return  (T) Proxy.newProxyInstance(classz.getClassLoader(),new Class[]{classz},
                new MyInvocationHandlerMbatis(classz));
    }
}
