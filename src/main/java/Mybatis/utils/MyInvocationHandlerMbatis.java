package Mybatis.utils;


import Mybatis.annotation.ExtInsert;
import Mybatis.annotation.ExtParam;
import Mybatis.annotation.ExtSelect;
import com.mchange.v2.sql.SqlUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shkstart
 * @create 2020-05-17 22:13
 */
public class MyInvocationHandlerMbatis implements InvocationHandler {

    private Object object;

    public MyInvocationHandlerMbatis(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        System.out.println("使用动态代理技术拦截接口方法开始");
        // 使用白话问翻译,@ExtInsert封装过程
        // 1. 判断方法上是否存在@ExtInsert
        ExtInsert extInsert = method.getDeclaredAnnotation(ExtInsert.class);
        if (extInsert != null){
            return extInsert(extInsert,method,objects);
        }
        // 2.查询的思路
        // 1. 判断方法上是否存 在注解
        ExtSelect extSelect = method.getDeclaredAnnotation(ExtSelect.class);
        if (extSelect != null){
            // 2. 获取注解上查询的SQL语句
            String selectSQL = extSelect.value();
            // 3. 获取方法上的参数,绑定在一起
            ConcurrentHashMap<Object,Object> parameterMap = paramsMap(object, method, objects);
            // 4. 参数替换？传递方式
            List<String> sqlSelectParameter = SQLUtils.sqlSelectParameter(selectSQL);
            // 5.传递参数
            List<Object> sqlParams = new ArrayList<>();
            for (String parameterName:sqlSelectParameter){
                Object parameterValue = parameterMap.get(parameterName);
                sqlParams.add(parameterValue);
            }
            // 6.将sql语句替换成?
            // 思路:
            // 1.使用反射机制获取方法的类型
            // 2.判断是否有结果集,如果有结果集，在进行初始化
            // 3.使用反射机制,给对象赋值
            String newSql = SQLUtils.parameQuestion(selectSQL, sqlSelectParameter);
            System.out.println("newSQL:" + newSql + ",sqlParams:" + sqlParams.toString());
            ResultSet res = JDBCUtils.query(newSql,sqlParams);
            // 判断是否存在值
            if (!res.next()){
                return null;
            }
            // 下标往上移动一位
            res.previous();
            // 使用反射机制获取方法的类型
            Class<?> returnType = method.getReturnType();
            Object objectReturnType = returnType.newInstance();
            while (res.next()){
                Field[] declaredFields = returnType.getDeclaredFields();
                for (Field field:declaredFields){
                    String fieldName = field.getName();
                    Object fieldValue = res.getObject(fieldName);
                    field.setAccessible(true);
                    field.set(objectReturnType, fieldValue);
                }
                // for (String parameteName : sqlSelectParameter) {
                // // 获取参数值
                // Object resultValue = res.getObject(parameteName);
                // // 使用java的反射值赋值
                // Field field = returnType.getDeclaredField(parameteName);
                // // 私有方法允许访问
                // field.setAccessible(true);
                // field.set(object, resultValue);
                // }
            }
            return objectReturnType;
        }

        return  null;
    }

    private Object extInsert(ExtInsert extInsert,Method method, Object[] objects){
        // 方法上存在@ExtInsert,获取他的SQL语句
        // 2. 获取SQL语句,获取注解Insert语句
        String insertSql = extInsert.value();
//             System.out.println("insertSql:" + insertSql);
        // 3. 获取方法的参数和SQL参数进行匹配
        // 定一个一个Map集合 KEY为@ExtParamValue,Value 结果为参数值
        // 存放sql执行的参数---参数绑定过程
        ConcurrentHashMap<Object,Object> parameterMap = paramsMap(object, method, objects);
        // 存放sql执行的参数---参数绑定过程
        String[] sqlInsertParameter = SQLUtils.sqlInsertParameter(insertSql);
        List<Object> sqlParams = sqlParams(sqlInsertParameter,parameterMap);
        // 4. 根据参数替换参数变为?
        String newSQL = SQLUtils.parameQuestion(insertSql, sqlInsertParameter);
        System.out.println("newSQL:" + newSQL + ",sqlParams:" + sqlParams.toString());
        // 5. 调用jdbc底层代码执行语句
        return JDBCUtils.insert(newSQL,false,sqlParams);
    }


    private List<Object> sqlParams(String[] sqlInsertParameter,ConcurrentHashMap<Object,Object> parameterMap){
        List<Object> sqlParams = new ArrayList<>();
        for (String paramName:sqlInsertParameter){
            Object paramValue = parameterMap.get(paramName);
            sqlParams.add(paramValue);
        }
        return sqlParams;
    }

    //获取sql语句上的参数
    private ConcurrentHashMap<Object,Object> paramsMap(Object object, Method method, Object[] objects){
        ConcurrentHashMap<Object,Object> parameterMap = new ConcurrentHashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i=0;i<parameters.length;i++){
            Parameter parameter = parameters[i];
            ExtParam extParam = parameter.getDeclaredAnnotation(ExtParam.class);
            if (extParam!=null){
                // 参数名称
                String paramName = extParam.value();
                Object paramValue = objects[i];
//                    System.out.println(paramName + "," + paramValue);
                parameterMap.put(paramName,paramValue);
            }
        }
        return parameterMap;
    }
}
