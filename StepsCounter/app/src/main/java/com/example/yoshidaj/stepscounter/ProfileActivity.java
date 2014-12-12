package com.example.yoshidaj.stepscounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ProfileActivity extends Activity {


    @InjectView(R.id.textView6)
    TextView mTextView6;
    @InjectView(R.id.getDataButton)
    Button mGetDataButton;

    @InjectView(R.id.totalView)
    TextView mTotalView;
    @InjectView(R.id.dayView)
    TextView mDayView;
    @InjectView(R.id.costView)
    TextView mCostView;
    @InjectView(R.id.textView7)
    TextView mTextView7;
    @InjectView(R.id.editText)
    EditText mEditText;
    @InjectView(R.id.textView8)
    TextView mTextView8;
    @InjectView(R.id.editText2)
    EditText mEditText2;
    @InjectView(R.id.textView9)
    TextView mTextView9;
    @InjectView(R.id.editText3)
    EditText mEditText3;
    @InjectView(R.id.button)
    Button mButton;

    private MobileServiceClient mClient;

    Intent intent;
    Calendar calendar;
    String today;
    Date date;
    DateFormat df;

    int nowTabacco;
    int pastTabacco;
    int futureTabacco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);
        ButterKnife.inject(this);
        ButterKnife.inject(this);
        ButterKnife.inject(this);

        mTextView6.setText("Profile");
        intent = getIntent();
        String str = intent.getStringExtra("KeyWord");

        //calendar = Calendar.getInstance();
        //today = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        try {
            mClient = new MobileServiceClient("https://mycounter.azure-mobile.net/", "RiIzLgCjTIunvogUaAVZbgMiCRnpaJ39", this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.getDataButton)
    public void getData(View view) {
        MobileServiceTable<Item> items = mClient.getTable(Item.class);

        items.where().field("Text").eq("すばらしいアイテム").select("Steps").execute(new TableQueryCallback<Item>() {
            @Override
            public void onCompleted(List<Item> result, int count, Exception exception, ServiceFilterResponse response) {
                int total = 0;
                for (Item i : result) {
                    total += i.Steps;
                }
                final int finalTotal = total;
                ProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(MainActivity.this, "Steps: " + finalTotal, Toast.LENGTH_LONG).show();
                        mTotalView.setText("Total Steps : " + Integer.toString(finalTotal) + " 歩");
                        mCostView.setText("Total Cost : " + (finalTotal * 21) + "円");
                    }
                });
            }
        });

        items.where().field("Date").eq(today).select("Steps").execute(new TableQueryCallback<Item>() {
            @Override
            public void onCompleted(List<Item> result, int count, Exception exception, ServiceFilterResponse response) {
                int totalToday = 0;
                for (Item i : result) {
                    totalToday += i.Steps;
                }
                final int finalTotal = totalToday;
                ProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(MainActivity.this, "Steps: " + finalTotal, Toast.LENGTH_LONG).show();
                        mDayView.setText("今日の歩数 : " + Integer.toString(finalTotal) + " 歩");
                    }
                });
            }
        });
    }

    @OnClick(R.id.button)
    public void submit(View view){
        nowTabacco = Integer.parseInt(mEditText.getText().toString());
        pastTabacco = Integer.parseInt(mEditText2.getText().toString());
        futureTabacco = Integer.parseInt(mEditText3.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = new Intent();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_main) {
            //Toast.makeText(this, "Main Page selected", Toast.LENGTH_LONG).show();
            intent.setClassName("com.example.yoshidaj.stepscounter", "com.example.yoshidaj.stepscounter.MainActivity");
            //intent.putExtra();
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profile) {
            //Toast.makeText(this, "profile selected", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_ranking) {
            Toast.makeText(this, "ranking selected", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_facebook) {
            Toast.makeText(this, "facebook selected", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
