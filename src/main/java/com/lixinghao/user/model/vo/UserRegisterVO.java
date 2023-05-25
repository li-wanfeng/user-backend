package com.lixinghao.user.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author 风车下跑
 * @create 2023-05-18
 */
@Getter
@Setter
public class UserRegisterVO implements Serializable {

    private static final long serialVersionUID = -3233564571715572554L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;


}
