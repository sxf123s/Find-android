package com.nuist.find;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private float ratio = 0.5f; //取景框高宽比
    private ImageButton imageButton,refresh,qs_search;
    private EditText qs_text;
    private TextView tv1,tv2;//item_layout.xml里的TextView
    private ListView lv;//activity_main.xml里的ListView
    private BaseAdapter adapter;//适配器
    private List<Question> QuestionList = new ArrayList<Question>();//实体类;
    private MyHandler myhandler = new  MyHandler(this);
    public static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        //动画效果
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);//动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_spin);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        imageView.startAnimation(animation);

        initViews();//显示拍照按钮模块
        InitItem();//显示ListView
        //刷新ListView
        lv = (ListView) findViewById(R.id.listview_question);
        refresh = (ImageButton) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InitItem();
            }
        });
        qs_text = (EditText) findViewById(R.id.main_qs);
        qs_search = (ImageButton) findViewById(R.id.main_search);
        //qs_search点击跳转
        qs_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = qs_text.getText().toString();
                Intent intent = new Intent(MainActivity.this,QuestionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("text",data);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        //item点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv1 = (TextView) view.findViewById(R.id.text_title);//找到Textviewname
                String data = tv1.getText().toString();//得到数据
                Intent intent = new Intent(MainActivity.this,QuestionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("text",data);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }
        });
    }
    private void initViews() {
        imageButton = (ImageButton) findViewById(R.id.imagebtn_take);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecognizeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> Activity;

        public MyHandler(MainActivity activity) {
            Activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (Activity.get() == null) {
                return;
            }
            Activity.get().updateUIThread(msg);
        }
    }

    //配合子线程更新UI线程
    private void updateUIThread(Message msg){
        lv = (ListView) findViewById(R.id.listview_question);
        Bundle bundle = new Bundle();
        bundle = msg.getData();
        String result = bundle.getString("result");
        String[] b = result.split("\\|");  //注意这里用两个 \\，而不是一个\
        for (int i = 0; i < 5; i++) {
            Question qs = new Question();//给实体类赋值
            qs.setTitle(b[i]);
            qs.setMsg("查看详情");
            QuestionList.add(qs);
        }
//        for (int i = 0; i < 5; i++) {
//            Question qs = new Question();//给实体类赋值
//            qs.setTitle("小米米米米"+i);
//            qs.setMsg("查看详情");
//            QuestionList.add(qs);
//        }

        //adapter适配器
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return QuestionList.size();//数目
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View view;
                if (convertView==null) {
                    //因为getView()返回的对象，adapter会自动赋给ListView
                    view = inflater.inflate(R.layout.item_layout, null);
                }else{
                    view=convertView;
                }
                tv1 = (TextView) view.findViewById(R.id.text_title);//找到text_title
                tv1.setText(QuestionList.get(position).getTitle());//设置参数
                tv2 = (TextView) view.findViewById(R.id.text_msg);//找到text_msg
                tv2.setText(QuestionList.get(position).getMsg());//设置参数
                return view;
            }
            @Override
            public long getItemId(int position) {//取在列表中与指定索引对应的行id
                return 0;
            }
            @Override
            public Object getItem(int position) {//获取数据集中与指定索引对应的数据项
                return null;
            }
        };
        lv.setAdapter(adapter);
    }

    void InitItem(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = HttpQuestion.Question("1");
                Bundle bundle = new Bundle();
                bundle.putString("result",str);
                Message msg = new Message();
                msg.setData(bundle);
                myhandler.sendMessage(msg);
            }
        }).start();
    }
}
