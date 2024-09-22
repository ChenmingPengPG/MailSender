package com.pcm.automailsender.model.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
    private MailSender(){}
    private static final class MailSenderHolder {
        private static final MailSender INSTANCE = new MailSender();
    }
    public static MailSender getInstance() {
        return MailSenderHolder.INSTANCE;
    }

    public boolean sendTextMail(final MailSenderInfo mailInfo) {

        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);

//    Session sendMailSession = Session.getInstance(pro, new Authenticator() {
//      @Override
//      protected PasswordAuthentication getPasswordAuthentication() {
//        return new PasswordAuthentication(mailInfo.getUserName(),mailInfo.getPassword());
//      }
//    });

        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());

            // 设置邮件消息的主要内容
            String mailContent = mailInfo.getContent();
            mailMessage.setText(mailContent);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static class MailSenderInfo {
        // 发送邮件的服务器的IP和端口
        private String mailServerHost;
        // 发送邮件服务器端口,有些邮箱可能不是25
        private String mailServerPort = "25";
        // 邮件发送者的地址
        private String fromAddress;
        // 邮件接收者的地址
        private String toAddress;
        // 登陆邮件发送服务器的用户名和密码
        private String userName;
        private String password;
        // 是否需要身份验证
        private boolean validate = false;
        // 邮件主题
        private String subject;
        // 邮件的文本内容
        private String content;
        // 邮件附件的文件名
        private String[] attachFileNames;


        /**
         * 获得邮件会话属性
         */
        public Properties getProperties() {
            Properties p = new Properties();
            p.put("mail.smtp.host", this.mailServerHost);
            p.put("mail.smtp.port", this.mailServerPort);
            p.put("mail.smtp.auth", validate ? "true" : "false");
            p.put("mail.smtp.starttls.enable", "true");
            return p;
        }

        public MailSenderInfo() {
            this.mailServerHost = "smtp.qq.com"; //qq邮件的服务器地址
            this.mailServerPort = "25";
            this.validate = true;
            //注意需要开启邮箱的SMTP/POP3/IMAP 的服务
            this.password = "......."; //这里密码应设置为qq邮箱的第三方授权码
            this.userName = "....@qq.com";//qq邮箱账户名
            this.toAddress = "....@qq.com";//发到的qq邮箱账户
            this.fromAddress = "@qq.com";//发送邮件的qq邮箱地址
            this.subject = "test";
            this.content = "This is a test of mailsender.\n If you get this email, please return \"succeeded\" to" +
                    "your father!";
        }

        public String getMailServerHost() {
            return mailServerHost;
        }

        public void setMailServerHost(String mailServerHost) {
            this.mailServerHost = mailServerHost;
        }

        public String getMailServerPort() {
            return mailServerPort;
        }

        public void setMailServerPort(String mailServerPort) {
            this.mailServerPort = mailServerPort;
        }

        public boolean isValidate() {
            return validate;
        }

        public void setValidate(boolean validate) {
            this.validate = validate;
        }

        public String[] getAttachFileNames() {
            return attachFileNames;
        }

        public void setAttachFileNames(String[] fileNames) {
            this.attachFileNames = fileNames;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
            if (userName.endsWith("@outlook.com")) {
                this.mailServerHost = "smtp-mail.outlook.com";
                this.mailServerPort= "587";
            } else if (userName.endsWith("@qq.com")) {
                this.mailServerHost = "smtp.qq.com"; //qq邮件的服务器地址
                this.mailServerPort = "25";
            }
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String textContent) {
            this.content = textContent;
        }
    }

    public static class MyAuthenticator extends Authenticator {
        String userName = null;
        String password = null;

        public MyAuthenticator() {
        }

        public MyAuthenticator(String username, String password) {
            this.userName = username;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }
}
