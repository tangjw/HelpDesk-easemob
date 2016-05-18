package com.zonsim.helpdesk_easemob;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.easemob.chat.EMChat;

/**
 * 登陆到环信
 * Created by tang-jw on 5/9.
 */
public class LoginActivity extends AppCompatActivity {
    
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        
        //可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
        if (EMChat.getInstance().isLoggedIn()) {
            getProgressDialog();
        }
    }
    
    //创建一个进度条
    private void getProgressDialog() {
        if (progressDialog == null) {
    
            progressDialog = new ProgressDialog(LoginActivity.this);
        }
    
    }
    
    
}
