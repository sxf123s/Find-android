package com.nuist.find;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class SearchActivity extends AppCompatActivity {

    private ImageButton back;
    private TextView orcTextview,searchTextview;
    private MyHandler myhandler = new MyHandler(this);
    public static final String TAG="SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_search);
        orcTextview = (TextView) findViewById(R.id.orc_text);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String data = bundle.getString("maple");
        orcTextview.setText(data);
        InitView();
        back = (ImageButton)findViewById(R.id.topback1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private static class MyHandler extends Handler {
        private final WeakReference<SearchActivity> sActivity;

        public MyHandler(SearchActivity activity) {
            sActivity = new WeakReference<SearchActivity>(activity);
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
        searchTextview = (TextView) findViewById(R.id.search_text);
        Bundle bundle = new Bundle();
        bundle = msg.getData();
        String result = bundle.getString("result");
        searchTextview.setText(result);
       Toast.makeText(SearchActivity.this, result, Toast.LENGTH_SHORT).show();
    }
    void InitView(){
        orcTextview = (TextView) findViewById(R.id.orc_text);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = HttpSearch.SearchByPost(orcTextview.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putString("result",str);
                Message msg = new Message();
                msg.setData(bundle);
                myhandler.sendMessage(msg);
            }
        }).start();
    }
}
