package com.sundy.boot.validator;

import com.sundy.boot.validator.group.UserLoginGroup;
import com.sundy.boot.validator.group.UserRegistGroup;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 注册用户时的输入参数
 */
public class UserVO {

    @NotBlank(groups = {UserRegistGroup.class, UserLoginGroup.class}, message = "账号不能为空")
    private String phone;

    @Length(groups = {UserRegistGroup.class}, max = 30, min = 2, message = "姓名的长度为:{min}-{max}个字符")
    @NotBlank(groups = {UserRegistGroup.class}, message = "姓名不能为空")
    private String nickName;

    @NotBlank(groups = {UserRegistGroup.class, UserLoginGroup.class}, message = "密码不能为空")
    @Length(groups = {UserRegistGroup.class, UserLoginGroup.class}, min = 6, message = "密码长度至少{min}位")
    private String password;

    private String signData;

    private String code;

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
