package com.example.yoshidaj.stepscounter;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.microsoft.windowsazure.mobileservices.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{
    private EditText editText;
    private Button button;

    private MobileServiceClient mClient;

    private TextView textView2;
    private Button incrementButton;

    private TextView textView3;
    private Button submitButton;

    int num = 0;
    int incrementNum = 0;

    Calendar calendar;
    String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        textView2 = (TextView) findViewById(R.id.textView2);
        incrementButton = (Button) findViewById(R.id.incrementButton);
        textView3 = (TextView) findViewById(R.id.textView3);
        submitButton = (Button) findViewById(R.id.submitButton);

        button.setOnClickListener(this);
        incrementButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        textView2.setText(Integer.toString(incrementNum));

        calendar = Calendar.getInstance();
        today = calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);

        try {
            mClient = new MobileServiceClient("https://mycounter.azure-mobile.net/", "RiIzLgCjTIunvogUaAVZbgMiCRnpaJ39", this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.getSteps();
    }

    public void getSteps(){
        MobileServiceTable<Item> items = mClient.getTable(Item.class);
        //items.where().field("Text").eq("すばらしいアイテム").select("Steps")

        items.where().field("Text").eq(today).select("Steps").execute(new TableQueryCallback<Item>() {
            @Override
            public void onCompleted(List<Item> result, int count, Exception exception, ServiceFilterResponse response) {
                int total = 0;
                for (Item i : result) {
                    total += i.Steps;
                }

                final int finalTotal = total;
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(MainActivity.this, "Steps: " + finalTotal, Toast.LENGTH_LONG).show();
                        textView3.setText("Total Steps : "+Integer.toString(finalTotal)+" 歩");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button:
                num = Integer.parseInt(String.valueOf(editText.getText()));
                Toast.makeText(this, num+" AddButtonPushed", Toast.LENGTH_LONG).show();
                break;
            case R.id.incrementButton:
                textView2.setText(Integer.toString(++incrementNum));
                //Toast.makeText(this, incrementNum+" IncrementButtonPushed", Toast.LENGTH_LONG).show();
                break;
            case R.id.submitButton:
                Item item = new Item();
                item.Text = "すばらしいアイテム";
                item.Date = today;
                item.Steps = incrementNum;
                mClient.getTable(Item.class).insert(item, new TableOperationCallback<Item>() {
                    public void onCompleted(Item entity, Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            // Insert succeeded
                            incrementNum = 0;
                            textView2.setText(Integer.toString(incrementNum));
                            Toast.makeText(MainActivity.this, "Succeeded!", Toast.LENGTH_LONG).show();
                            getSteps();
                        } else {
                            // Insert failed
                            Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
        }
    }
}