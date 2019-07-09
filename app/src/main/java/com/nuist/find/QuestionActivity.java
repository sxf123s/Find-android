package com.nuist.find;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class QuestionActivity extends AppCompatActivity {
    private EditText as_text1,as_text2;
    private Button as_answer,as_remend;
    private TextView as_text3,as_text4,as_text5;
    private ImageButton imageButton,topback2;
    private MyHandler myhandler = new MyHandler(this);
    public static final String TAG="QuestionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_question);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String data = bundle.getString("text");
        as_text1 = (EditText)findViewById(R.id.as_text1);
        as_text1.setText(data);
        InitView();
        imageButton = (ImageButton) findViewById(R.id.as_search);
        as_answer = (Button) findViewById(R.id.as_answer);
        as_remend = (Button) findViewById(R.id.as_remend);
        as_text4 = (TextView) findViewById(R.id.as_text4);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitView();
            }
        });

        as_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                as_text4.setText("");
            }
        });

        as_remend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Insert();
                Toast.makeText(QuestionActivity.this, "sucess", Toast.LENGTH_SHORT).show();
            }
        });

        topback2 = (ImageButton) findViewById(R.id.topback2);
        topback2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(QuestionActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private static class MyHandler extends Handler {
        private final WeakReference<QuestionActivity> sActivity;

        public MyHandler(QuestionActivity activity) {
            sActivity = new WeakReference<QuestionActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (sActivity.get() == null) {
                return;
            }
            sActivity.get().updateUIThread(msg);
        }
    }

    //配合子线程更新UI线程
    private void updateUIThread(Message msg){
        as_text3 = (TextView) findViewById(R.id.as_text3);
        as_text4 = (TextView) findViewById(R.id.as_text4);
        as_text5 = (TextView) findViewById(R.id.as_text5);
        Bundle bundle = new Bundle();
        bundle = msg.getData();
        String result = bundle.getString("result");
        String[] b = result.split("\\|");
        as_text3.setText(b[0]);
        as_text5.setText(b[1]);
        as_text4.setText(b[2]);
//        Toast.makeText(QuestionActivity.this, result, Toast.LENGTH_SHORT).show();
    }
    void InitView(){
        as_text1 = (EditText) findViewById(R.id.as_text1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = HttpAnswer.AnswerQuestion(as_text1.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putString("result",str);
                Message msg = new Message();
                msg.setData(bundle);
                myhandler.sendMessage(msg);
            }
        }).start();
    }
    void Insert(){
        as_text2 = (EditText) findViewById(R.id.as_text2);
        as_text5 = (TextView) findViewById(R.id.as_text5);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = HttpAnswer.InsertAnswer(as_text5.getText().toString(),as_text2.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putString("result",str);
            }
        }).start();
    }
}
