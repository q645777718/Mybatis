package Mybatis;

import Mybatis.entity.User;
import Mybatis.mapper.UserMapper;
import Mybatis.utils.SqlSession;


/**
 * @author shkstart
 * @create 2020-05-17 22:14
 */
public class Test0003 {
    public static void main(String[] args) {
        UserMapper userMapper = SqlSession.getMapper(UserMapper.class);
        User selectUser = userMapper.selectUser("张三", 644064);
        System.out.println(
                "结果:" + selectUser.getName() + "," + selectUser.getAge() + ",id:" + selectUser.getId());

        // // 先走拦截invoke
//         int insertUserResult = userMapper.insertUser("张三", 644064);
//         System.out.println("insertUserResult:" + insertUserResult);
    }
}
