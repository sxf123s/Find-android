package com.nuist.find;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class RegisterActivity extends AppCompatActivity {
    private Button reg_btn;
    private ImageButton back_btn;
    private EditText edit_name,edit_pwd,edit_gender,edit_email;
    private MyHandler myhandler1 = new MyHandler(this);
    public static final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_register);
        InitView();
        back_btn = (ImageButton) findViewById(R.id.imbtn_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<RegisterActivity> nActivity;

        public MyHandler(RegisterActivity activity) {
            nActivity = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (nActivity.get() == null) {
                return;
            }
            nActivity.get().updateUIThread(msg);
        }
    }

    //配合子线程更新UI线程
    private void updateUIThread(Message msg){
        Bundle bundle = new Bundle();
        bundle = msg.getData();
        String result = bundle.getString("result");
        if("success".equals(result)){
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(RegisterActivity.this, "注册成功,请登录", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
    }
    void InitView(){
        reg_btn = (Button) findViewById(R.id.register_btn);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_pwd = (EditText) findViewById(R.id.edit_pwd);
        edit_gender = (EditText) findViewById(R.id.edit_gender);
        edit_email = (EditText) findViewById(R.id.edit_email);
        // Toast.makeText(MainActivity.this, "str111", Toast.LENGTH_SHORT).show();
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edit_name.getText())||TextUtils.isEmpty(edit_pwd.getText())||TextUtils.isEmpty(edit_gender.getText())||TextUtils.isEmpty(edit_email.getText())) {
                    Toast.makeText(RegisterActivity.this, "必要信息不能为空", Toast.LENGTH_SHORT).show();
                }
                else{
                    //开启访问数据库线程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String str = HttpRegister.RegisterByPost(edit_name.getText().toString(),edit_pwd.getText().toString(),edit_gender.getText().toString(),edit_email.getText().toString());
                            Bundle bundle = new Bundle();
                            bundle.putString("result",str);
                            Message msg = new Message();
                            msg.setData(bundle);
                            myhandler1.sendMessage(msg);
                        }
                    }).start();
                }

            }
        });
    }
}
