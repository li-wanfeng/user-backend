package com.lixinghao.user.controller;

import com.lixinghao.user.common.BaseResponse;
import com.lixinghao.user.common.ErrorCode;
import com.lixinghao.user.entity.User;
import com.lixinghao.user.exception.BusInessException;
import com.lixinghao.user.model.vo.UserLoginVO;
import com.lixinghao.user.model.vo.UserRegisterVO;
import com.lixinghao.user.service.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.lixinghao.user.constant.UserConstant.ADMIN_USER;
import static com.lixinghao.user.constant.UserConstant.USER_LOGIN_STATE;


/**
 * @author 风车下跑
 * @create 2023-05-18
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Resource
    private UserService userServicel;

    /**
     * 用户登录接口
     * @param userLoginVO
     * @param request
     * @return
     */
    @PostMapping("login")
    public BaseResponse<User> userlogin(@RequestBody UserLoginVO userLoginVO, HttpServletRequest request){
        if (userLoginVO == null){
//            return BaseResponse.error(ErrorCode.PARAM_ERROR);
            throw new BusInessException(ErrorCode.PARAM_ERROR,"");
        }
        String userAccount = userLoginVO.getUserAccount();
        String userPassword = userLoginVO.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return BaseResponse.error(ErrorCode.PARAM_ERROR);
        }
        User user = userServicel.userLogin(userAccount, userPassword, request);
        if (user == null){
            return BaseResponse.error("账号或密码错误 请重试");
        }
        return BaseResponse.succsess(user);
    }
    @PostMapping("logout")
    public BaseResponse<Integer> userlogout(HttpServletRequest request){
        User attributeUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (ObjectUtils.isEmpty(attributeUser)){
            return BaseResponse.error("注销失败，请重试");
        }
        int i = userServicel.userLogout(request);
        return BaseResponse.succsess(i,"注销成功");
    }
    /**
     * 用户注册接口
     * @param userRegisterVO
     * @return
     */
    @PostMapping("register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterVO userRegisterVO){
        if (userRegisterVO == null){
            return BaseResponse.error(ErrorCode.PARAM_ERROR);
        }
        String userAccount = userRegisterVO.getUserAccount();
        String userPassword = userRegisterVO.getUserPassword();
        String checkPassword = userRegisterVO.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return BaseResponse.error(ErrorCode.PARAM_ERROR);
        }
        long userId = userServicel.userRegister(userAccount, userPassword, checkPassword);
        if (userId <= 0){
            return BaseResponse.error(ErrorCode.ACCOUNT_EXITS);
        }
        return BaseResponse.succsess(userId);
    }

    /**
     * 根据昵称查询用户信息接口
     * @param userName
     * @return
     */
    @GetMapping("/searchUsers")
    public BaseResponse<List<User>> searchUsers(@RequestParam(value = "userName",required = false) String userName,HttpServletRequest request){
        boolean admin = isAdmin(request);
        if (!admin){
            throw new BusInessException(ErrorCode.NOT_ADMIN);
        }
        List<User> userList  = userServicel.searchByUserName(userName);
        return BaseResponse.succsess(userList);
    }

    /**
     * 删除用户操作
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteUser(@PathVariable("id") Long id){
        if (id == null) {
            BaseResponse.error(ErrorCode.PARAM_ERROR);
        }
        boolean isdel = userServicel.deleteByUserId(id);
        if (isdel) {
            return BaseResponse.error(ErrorCode.DELETE_ERROR);
        }
        return BaseResponse.succsess(isdel,"删除用户成功");
    }
    protected boolean isAdmin(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (ObjectUtils.isEmpty(user)){
            throw new BusInessException(ErrorCode.USER_NOT_LOGIN);
        }
        if (user.getUserRole() != ADMIN_USER){
            throw new BusInessException(ErrorCode.NOT_ADMIN);
        }
        return true;
    }

    /**
     * 获取当前登录的用户态
     * @param request
     * @return
     */
    @GetMapping("/currentUser")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User attributeUser = (User) attribute;// 这里在进行类型强转的时候，null也会强转成功，强转之后的user也为null
        if (ObjectUtils.isEmpty(attributeUser)){
            return BaseResponse.error(ErrorCode.USER_NOT_LOGIN);
        }
        //如果不为空，由于user信息可能会发生变化，需要在查一遍数据库获取最新的信息
        User user = userServicel.searchByUserId(attributeUser.getId());
        return BaseResponse.succsess(user);
    }
}