package Mybatis.mapper;

import Mybatis.annotation.ExtInsert;
import Mybatis.annotation.ExtParam;
import Mybatis.annotation.ExtSelect;
import Mybatis.entity.User;


/**
 * @author shkstart
 * @create 2020-05-17 22:08
 */
public interface UserMapper {

    @ExtSelect("select * from t_users where name=#{name} and age=#{age}")
    User selectUser(@ExtParam("name")String name, @ExtParam("age")Integer age);

    @ExtInsert("insert into t_users(name,age) values(#{name},#{age})")
    public int insertUser(@ExtParam("name") String name, @ExtParam("age") Integer age);
}
