package com.lixinghao.user.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lixinghao.user.common.ErrorCode;
import com.lixinghao.user.entity.User;
import com.lixinghao.user.exception.BusInessException;
import com.lixinghao.user.service.service.UserService;
import com.lixinghao.user.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lixinghao.user.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户层面业务逻辑具体实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;

    private static final String salt = "chengguo";
    /**
     * 用户注册功能实现
     * @param UserAccount 用户昵称
     * @param UserPassword 用户密码
     * @param CheckPassword 校验密码
     * @return 新用户id
     */
    @Override
    public long userRegister(String UserAccount, String UserPassword, String CheckPassword) {
        //1.校验
        // 非空校验
        if (StringUtils.isAnyBlank(UserAccount,UserPassword,CheckPassword)) {
            throw new BusInessException(ErrorCode.PARAM_ERROR,"参数不能为空");
        }
        //账户不能低于4位
        if (UserAccount.length() < 4){
            throw new BusInessException(ErrorCode.PARAM_ERROR,"账户不能低于4位");
        }
        //密码不能低于8位
        if (UserPassword.length() < 8 ){
            throw new BusInessException(ErrorCode.PARAM_ERROR,"密码不能低于8位");
        }

        //两次密码校验
        if (!UserPassword.equals(CheckPassword)){
            throw new BusInessException(ErrorCode.PARAM_ERROR,"两次密码校验不相同");
        }
        //账户特殊校验 字母开头 6-16位，只允许字母数字
        String validPattern = "^[a-zA-Z][a-zA-Z0-9]{5,15}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(UserAccount);
        if (!matcher.find()){
            throw new BusInessException(ErrorCode.PARAM_ERROR,"字母开头 6-16位，只允许字母数字");
        }
        //账户不能重复
        long count = userMapper.selectCount(new QueryWrapper<User>().eq("UserAccount", UserAccount));
        if (count >0){
            throw new BusInessException(ErrorCode.ACCOUNT_EXITS);
        }
        //2.密码加密，进行简单的加密

        String encryptPassword = DigestUtils.md5DigestAsHex((salt + UserPassword).getBytes(StandardCharsets.UTF_8));
        //插入数据
        User user = new User();
        user.setUserAccount(UserAccount);
        user.setUserPassword(encryptPassword);
        int insert = userMapper.insert(user);
        if (insert <=0){
            throw new BusInessException(ErrorCode.INSERT_ERROR,"注册失败");
        }
        return user.getId();
    }

    /**
     * 用户登录逻辑实现
     * @param UserAccount 用户名
     * @param UserPassword 密码
     * @param request 请求转发，将数据转发到前端
     * @return 脱敏后的user对象
     */
    @Override
    public User userLogin(String UserAccount, String UserPassword, HttpServletRequest request) {
        //1.校验
        // 非空校验
        if (StringUtils.isAnyBlank(UserAccount,UserPassword)) {
            return null;
        }
        //账户不能低于4位
        if (UserAccount.length() < 4){
            return null;
        }
        //密码不能低于8位
        if (UserPassword.length() < 8 ){
            return null;
        }
        //账户特殊校验 字母开头 6-16位，只允许字母数字
        String validPattern = "^[a-zA-Z][a-zA-Z0-9]{5,15}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(UserAccount);
        if (!matcher.find()){
            return null;
        }
        //查询用户名是否存在，存在的话查询密码是否正确
        User getUser = userMapper.selectOne(new QueryWrapper<User>().eq("UserAccount", UserAccount));
        if (ObjectUtils.isEmpty(getUser)){
            //用户名不存在
            return null;
        }else{
            //密码加密，进行简单的加密
            String encryptPassword = DigestUtils.md5DigestAsHex((salt + UserPassword).getBytes(StandardCharsets.UTF_8));
            if (!encryptPassword.equals(getUser.getUserPassword())){
                return null;
            }
        }
        User safetyUser = getSafetyUser(getUser);
        //记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户退出逻辑
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态即可
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
    /**
     * 返回脱敏后的用户信息
     * @param getUser
     * @return
     */
    @Override
    public User getSafetyUser(User getUser) {
        if (ObjectUtils.isEmpty(getUser)){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(getUser.getId());
        safetyUser.setUserAccount(getUser.getUserAccount());
        safetyUser.setUserName(getUser.getUserName());
        safetyUser.setAvatarUrl(getUser.getAvatarUrl());
        safetyUser.setGender(getUser.getGender());
        safetyUser.setPhone(getUser.getPhone());
        safetyUser.setEmail(getUser.getEmail());
        safetyUser.setUserRole(getUser.getUserRole());
        safetyUser.setUserStatus(getUser.getUserStatus());
        safetyUser.setCreateTime(getUser.getCreateTime());
        return safetyUser;
    }

    @Override
    public List<User> searchByUserName(String userName) {
        //查询用户信息，如果前端返回的参数中userName为空，则代表查询所有用户，但是不包括当前登录用户的个人信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (userName != null && !StringUtils.isBlank(userName)) {
            userQueryWrapper.eq("userName",userName);
        }
        List<User> userList = userMapper.selectList(userQueryWrapper);
        return userList.stream().map(user->getSafetyUser(user)).collect(Collectors.toList());
    }

    @Override
    public User searchByUserId(long userId) {
        User user = userMapper.selectById(userId);
        if (ObjectUtils.isEmpty(user)){
            return null;
        }
        User safetyUser = getSafetyUser(user);
        return safetyUser;
    }

    @Override
    public boolean deleteByUserId(long id) {
        if (id <= 0){
            return false;
        }
        int i = userMapper.deleteById(id);
        if (i <=0){
            return false;
        }
        return true;
    }
}




