package com.shankong.admin.utils;

import com.shankong.admin.common.exception.BusinessException;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 邮件工具类
 */
@Component
public class EmailUtil {
    @Resource
    private JavaMailSender javaMailSender;

    /**
     *  发送注册验证码邮件
     * @param to      接收邮件的邮箱
     * @param code    注册验证码
     */
    public void seedEmail(String to, String code){
        try{
            // 创建MimeMessage对象
            // MimeMessage对象用于发送HTML格式的邮件
            // SimpleMailMessage对象用于发送文本格式的邮件
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom("2197902558@qq.com");
            helper.setTo(to);
            helper.setSubject("业主管理系统 - 注册验证码");
            helper.setText(
                    "<p>您好，您的注册验证码是：</p>" +
                            "<p style='font-size:24px;font-weight:bold'>" + code + "</p>" +
                            "<p>验证码 5 分钟内有效，请勿泄露给他人。</p>",
                    true // true开启HTML格式
            );
            javaMailSender.send(message);
        }catch(Exception e){
            throw new BusinessException("500", "发送邮件失败");
        }
    }
}
