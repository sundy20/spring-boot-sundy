package com.sundy.boot.mongodb.bson;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * Created on 2017/12/11
 *
 * @author plus.wang
 * @description 发送短信mongodb日志记录模型
 */
@Document(collection = "smslog")
public class SmsLog implements Serializable {

    private static final long serialVersionUID = -5550073264177594236L;

    @Id
    private String id;

    //手机号
    private String phone;

    //发短信内容
    private String content;

    //模板号
    private String templateNo;

    //来自哪个平台
    private String sendFrom;

    //发送结果
    private String result;

    //失败异常信息
    private String errorMessage;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date sendDate;

    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
