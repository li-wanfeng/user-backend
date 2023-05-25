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
public class UserLoginVO implements Serializable {
    private static final long serialVersionUID = -8465714512945012595L;
    private String userAccount;
    private String userPassword;
}
