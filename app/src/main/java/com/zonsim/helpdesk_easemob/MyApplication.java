package com.zonsim.helpdesk_easemob;

import android.app.Application;
import android.content.Context;

import com.easemob.chat.EMChat;
import com.zonsim.helpdesk_easemob.utils.HelpDeskPreferenceUtils;

/**
 * 全局的Application
 * Created by tang-jw on 5/9.
 */
public class MyApplication extends Application {
    
    public static Context appContext;
    private static MyApplication instance;
    
    public static MyApplication getInstance() {
        return instance;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        instance = this;
        
        //代码中设置环信IM的Appkey
        String appkey = HelpDeskPreferenceUtils.getInstance(this).getSettingCustomerAppkey();
        EMChat.getInstance().setAppkey(appkey);
        
        //init demo helper
        DemoHelper.getInstance().init(appContext);
    }
}
