package com.pcm.automailsender.model.email;

import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class EmailSendModel {
    public static final String TAG = "EmailSendModel";
    private EmailSendModel() {

    }
    private static final class EmailSendModelHolder {
        private static final EmailSendModel INSTANCE = new EmailSendModel();
    }
    public static EmailSendModel getInstance() {
        return EmailSendModelHolder.INSTANCE;
    }

    private Map<String, String> emailData = null;

    public void setEmailData(Map<String, String> data) {
        this.emailData = data;
    }

    public void sendEmails() {
        if (emailData == null || emailData.isEmpty()) {
            Log.i(TAG, "email data is empty");
            return;
        }
        Log.i(TAG, "send start");
        new SendTask().execute(emailData);
    }

    private static MailSender.MailSenderInfo buildBaseSendEmailInfo(String toAddress, String subject, String content) {
        MailSender.MailSenderInfo senderInfo = new MailSender.MailSenderInfo();
        senderInfo.setUserName("pcmpcmpcm@outlook.com");
        senderInfo.setFromAddress("pcmpcmpcm@outlook.com");
        senderInfo.setPassword("pcm307069.");


        senderInfo.setToAddress("pcmpcmpcm@qq.com");
        senderInfo.setSubject(subject);
        senderInfo.setContent(content);
        return senderInfo;
    }


    public static class SendTask extends AsyncTask<Map<String, String>, Integer, Integer> {

        @Override
        protected Integer doInBackground(Map<String, String> ... datas) {
            Map<String, String> emailData = datas[0];
            Map<String, String> failEmails = new HashMap<>();
            Map<String, String> finalFailEmails = new HashMap<>();
            int index = 0, size = emailData.size();
            for (Map.Entry<String, String> entry : emailData.entrySet()) {
                MailSender.MailSenderInfo senderInfo = buildBaseSendEmailInfo(entry.getKey(), "成绩测试" + index, entry.getValue());
                boolean isSuc = MailSender.getInstance().sendTextMail(senderInfo);
                if (!isSuc) {
                    failEmails.put(entry.getKey(), entry.getValue());
                } else {
                    publishProgress((int)((float) (++index)/ size * 100));
                }
            }
            // 失败重试
            for (Map.Entry<String, String> entry : failEmails.entrySet()) {
                MailSender.MailSenderInfo senderInfo = buildBaseSendEmailInfo(entry.getKey(), "成绩测试" + index, entry.getValue());
                boolean isSuc = MailSender.getInstance().sendTextMail(senderInfo);
                if (!isSuc) {
                    finalFailEmails.put(entry.getKey(), entry.getValue());
                } else {
                    publishProgress((int)((float) (++index)/ size * 100));
                }
            }
            for (Map.Entry<String, String> entry : finalFailEmails.entrySet()) {
                Log.i(TAG, "FAIL! " + entry.getValue());
            }
            return 0;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.i(TAG, "send mail:" + progress[0]);
        }

        protected void onPostExecute(Long result) {
            Log.i(TAG, "send finish:" + result);
        }
    }

}
