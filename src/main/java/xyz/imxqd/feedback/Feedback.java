package xyz.imxqd.feedback;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    private String description, user_email, user_qq, app_title, app_package, app_version, app_attachment
            , device_imei, device_model, system_version;
    private int level = 2;

    public Feedback(Context context, boolean imei){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            this.description = "";
            this.user_email = "";
            this.user_qq = "";
            this.app_title = context.getApplicationInfo().name;
            this.app_package = context.getApplicationInfo().packageName;
            this.app_version = info.versionName + ",code:" + info.versionCode;
            this.app_attachment = "";
            this.device_model = Build.MODEL;
            this.system_version = Build.VERSION.RELEASE + ", API" + Build.VERSION.SDK_INT;
            if(imei)
            {
                TelephonyManager telephonyManager=(TelephonyManager)
                        context.getSystemService(Context.TELEPHONY_SERVICE);

                this.device_imei = telephonyManager.getDeviceId();
            }else {
                this.device_imei = "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Feedback(String description, String user_email, String user_qq, String app_title,
                    String app_package, String app_version, String app_attachment, String device_imei,
                    String device_model, String system_version) {
        this.description = description;
        this.user_email = user_email;
        this.user_qq = user_qq;
        this.app_title = app_title;
        this.app_package = app_package;
        this.app_version = app_version;
        this.app_attachment = app_attachment;
        this.device_imei = device_imei;
        this.device_model = device_model;
        this.system_version = system_version;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_qq(String user_qq) {
        this.user_qq = user_qq;
    }

    public void setApp_title(String app_title) {
        this.app_title = app_title;
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {

        this.app_package = app_package;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getApp_attachment() {
        return app_attachment;
    }

    public void setApp_attachment(String app_attachment) {
        this.app_attachment = app_attachment;
    }

    public String getDevice_imei() {
        return device_imei;
    }

    public void setDevice_imei(String device_imei) {
        this.device_imei = device_imei;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getSystem_version() {
        return system_version;
    }

    public void setSystem_version(String system_version) {
        this.system_version = system_version;
    }

    public boolean submit() throws IOException {
        Connection connection = Jsoup.connect(SERVER_URL)
                .data("description", description)
                .data("user_email", user_email)
                .data("user_qq", user_qq)
                .data("level", String.valueOf(level))
                .data("app_title", app_title)
                .data("app_package", app_package)
                .data("app_version", app_version)
                .data("device_imei", device_imei)
                .data("device_model", device_model)
                .data("system_version", device_imei);

        if(app_attachment != null)
        {
            File file = new File(app_attachment);
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
}
