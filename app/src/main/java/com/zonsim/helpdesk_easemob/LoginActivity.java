package com.zonsim.helpdesk_easemob;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.zonsim.helpdesk_easemob.ui.ChatActivity;
import com.zonsim.helpdesk_easemob.utils.CommonUtils;

/**
 * 登陆到环信
 * Created by tang-jw on 5/9.
 */
public class LoginActivity extends Activity {
    
    private boolean progressShow;
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
        if (EMChat.getInstance().isLoggedIn()) {
            progressDialog = getProgressDialog();
            progressDialog.setMessage("正在联系客服中...");
            progressDialog.show();
            
            new Thread(){
                @Override
                public void run() {
                    try {
                        //加载本地数据库中的消息到内存中
                        EMChatManager.getInstance().loadAllConversations();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toChatActivity();
                }
            }.start();
            
            
        } else {
            // 随机创建一个用户并登录到环信服务器
            createRandomAccountAndLoginChatServer();
        }
    }
    
    /**
     * 自动生成账号并登陆
     */
    private void createRandomAccountAndLoginChatServer() {
        //自动生成账号
        final String randomAccount = CommonUtils.getRandomAccount();
        final String userPwd = Constant.DEFAULT_ACCOUNT_PWD;
        progressDialog = getProgressDialog();
        progressDialog.setMessage("正在自动生成账号...");
        progressDialog.show();
        
        createAccountToServer(randomAccount, userPwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //登陆环信服务器
                        loginHuanxinServer(randomAccount, userPwd);
                        
                    }
                });
            }
            
            @Override
            public void onError(int i, String s) {
                
            }
            
            @Override
            public void onProgress(int i, String s) {
                
            }
        });
        
    }
    
    /**
     * 登陆到环信服务器
     *
     * @param userName
     * @param userPwd
     */
    private void loginHuanxinServer(final String userName, final String userPwd) {
        progressShow = true;
        progressDialog = getProgressDialog();
        progressDialog.setMessage("正在联系客服...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        
        //登陆到环信服务器
        EMChatManager.getInstance().login(userName, userPwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (!progressShow) {
                    return;
                }
                DemoHelper.getInstance().setCurrentUserName(userName);
                DemoHelper.getInstance().setCurrentPassword(userPwd);
                try {
                    EMChatManager.getInstance().loadAllConversations();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                toChatActivity();
            }
            
            @Override
            public void onError(int i, String s) {
                
            }
            
            @Override
            public void onProgress(final int i, final String s) {
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "联系客服失败!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
        
    }
    
    /**
     * 进入聊天界面
     */
    private void toChatActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!LoginActivity.this.isFinishing()) {
                    progressDialog.dismiss();
                    //进入主页面
                    Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                    startActivity(intent);
                    finish();
                }
        
            }
        });
        
    }
    
    /**
     * 注册用户
     */
    private void createAccountToServer(final String name, final String pwd, final EMCallBack callback) {
        new Thread() {
            @Override
            public void run() {
                try {
                    EMChatManager.getInstance().createAccountOnServer(name, pwd);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (EaseMobException e) {
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.getMessage());
                    }
                }
            }
        }.start();
    }
    
    //创建一个进度条
    private ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
        }
        
        return progressDialog;
    }
    
    
}
