package com.nuist.find;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity {
    private Button button;
    private EditText edit1,edit2;
    private TextView textView1;
    private MyHandler myhandler = new MyHandler(this);
    public static final String TAG="LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_login);
        InitView();
        /*
         * 用户注册文字按钮
         */
        textView1=(TextView)findViewById(R.id.textView_reg);
        String text1="新用户注册";
        SpannableString spannableString1=new SpannableString(text1);
        spannableString1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        }, 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#0099EE"));
        spannableString1.setSpan(colorSpan, 0, spannableString1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView1.setText(spannableString1);
        textView1.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private static class MyHandler extends Handler {//弱引用，防止内存泄漏
        private final WeakReference<LoginActivity> mActivity;

        public MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().updateUIThread(msg);
        }
    }

    //配合子线程更新UI线程
    private void updateUIThread(Message msg){
        Bundle bundle = new Bundle();
        bundle = msg.getData();
        String result = bundle.getString("result");
        if("success".equals(result)){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
    }
    void InitView(){
        button = (Button) findViewById(R.id.login_btn);
        edit1 = (EditText) findViewById(R.id.editText_name);
        edit2 = (EditText) findViewById(R.id.editText_pwd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edit1.getText())||TextUtils.isEmpty(edit2.getText())) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                else{
                    //开启访问数据库线程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String str = HttpLogin.LoginByPost(edit1.getText().toString(),edit2.getText().toString());
                            Bundle bundle = new Bundle();
                            bundle.putString("result",str);
                            Message msg = new Message();
                            msg.setData(bundle);
                            myhandler.sendMessage(msg);
                        }
                    }).start();
                }

            }
        });
    }

}
