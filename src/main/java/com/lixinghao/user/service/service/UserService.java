package com.lixinghao.user.service.service;

import com.lixinghao.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户层业务逻辑
 * @author chengguo
*/
public interface UserService extends IService<User> {

    long userRegister(String UserAccount, String UserPassword, String CheckPassword);

    User userLogin(String UserAccount, String UserPassword, HttpServletRequest request);

    /**
     * 用户退出逻辑
     * @param request
     * @return　
     */
    int userLogout(HttpServletRequest request);

    /**
     * 返回脱敏后的用户信息
     * @param getUser
     * @return
     */
    User getSafetyUser(User getUser);

    List<User> searchByUserName(String userName);

    /**
     * 根据用户id返回脱敏后的用户信息
     * @param userId
     * @return
     */
    User searchByUserId(long userId);
    boolean deleteByUserId(long id);
}
