package xyz.imxqd.feedback;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by imxqd on 2016/5/12.
 * Feedback主类
 */
public class Feedback {
    public static final String SERVER_URL = "http://imxqd.xyz/feedback/index.php?c=api&a=upload";

    private String mDescription, mUserEmail, mUserQQ, mAppTitle, mAppPackage, mAppVersion, mAppAttachment
            , mDeviceImei, mDeviceModel, mSystemVersion;
    private int mLevel = 2;

    public Feedback(Context context, boolean imei){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            this.mDescription = "";
            this.mUserEmail = "";
            this.mUserQQ = "";
            this.mAppTitle = (String) pm.getApplicationLabel(ai);
            this.mAppPackage = ai.packageName;
            this.mAppVersion = info.versionName + ",code:" + info.versionCode;
            this.mAppAttachment = "";
            this.mDeviceModel = Build.MODEL;
            this.mSystemVersion = Build.VERSION.RELEASE + ", API" + Build.VERSION.SDK_INT;
            if(imei)
            {
                TelephonyManager telephonyManager=(TelephonyManager)
                        context.getSystemService(Context.TELEPHONY_SERVICE);

                this.mDeviceImei = telephonyManager.getDeviceId();
            }else {
                this.mDeviceImei = "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Feedback(String mDescription, String mUserEmail, String mUserQQ, String mAppTitle,
                    String mAppPackage, String mAppVersion, String mAppAttachment, String mDeviceImei,
                    String mDeviceModel, String mSystemVersion) {
        this.mDescription = mDescription;
        this.mUserEmail = mUserEmail;
        this.mUserQQ = mUserQQ;
        this.mAppTitle = mAppTitle;
        this.mAppPackage = mAppPackage;
        this.mAppVersion = mAppVersion;
        this.mAppAttachment = mAppAttachment;
        this.mDeviceImei = mDeviceImei;
        this.mDeviceModel = mDeviceModel;
        this.mSystemVersion = mSystemVersion;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setUserEmail(String mUserEmail) {
        this.mUserEmail = mUserEmail;
    }

    public void setUserQQ(String mUserQQ) {
        this.mUserQQ = mUserQQ;
    }

    public String getAppPackage() {
        return mAppPackage;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    public String getAppAttachment() {
        return mAppAttachment;
    }

    public void setAppAttachment(String mAppAttachment) {
        this.mAppAttachment = mAppAttachment;
    }

    public String getDeviceImei() {
        return mDeviceImei;
    }

    public void setDeviceImei(String mDeviceImei) {
        this.mDeviceImei = mDeviceImei;
    }

    public String getDeviceModel() {
        return mDeviceModel;
    }


    public String getSystemVersion() {
        return mSystemVersion;
    }

    public void submit(SubmitCallBack callBack)
    {
        FeedbackTask task = new FeedbackTask(callBack);
        task.execute();
    }

    private boolean submit() throws IOException {
        Connection connection = Jsoup.connect(SERVER_URL)
                .ignoreContentType(true)
                .data("description", mDescription)
                .data("user_email", mUserEmail)
                .data("user_qq", mUserQQ)
                .data("level", String.valueOf(mLevel))
                .data("app_title", mAppTitle)
                .data("app_package", mAppPackage)
                .data("app_version", mAppVersion)
                .data("device_imei", mDeviceImei)
                .data("device_model", mDeviceModel)
                .data("system_version", mSystemVersion);

        if(!mAppAttachment.equals(""))
        {
            File file = new File(mAppAttachment);
            connection.data("app_attachment", file.getName(), new FileInputStream(file));
        }
        String json = connection.post().text();
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getBoolean("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "mDescription='" + mDescription + '\'' +
                ", mUserEmail='" + mUserEmail + '\'' +
                ", mUserQQ='" + mUserQQ + '\'' +
                ", mAppTitle='" + mAppTitle + '\'' +
                ", mAppPackage='" + mAppPackage + '\'' +
                ", mAppVersion='" + mAppVersion + '\'' +
                ", mAppAttachment='" + mAppAttachment + '\'' +
                ", mDeviceImei='" + mDeviceImei + '\'' +
                ", mDeviceModel='" + mDeviceModel + '\'' +
                ", mSystemVersion='" + mSystemVersion + '\'' +
                ", mLevel=" + mLevel +
                '}';
    }

    public interface SubmitCallBack{
        boolean onPreSubmit();
        void onSubmitted(boolean result);
    }

    private class FeedbackTask extends AsyncTask<Void, Void, Boolean>
    {
        private SubmitCallBack callBack;
        public FeedbackTask(SubmitCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected void onPreExecute() {
            System.out.println(Feedback.this);
            if(!callBack.onPreSubmit())
            {
                this.cancel(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return submit();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            callBack.onSubmitted(aBoolean);
        }
    }
}
