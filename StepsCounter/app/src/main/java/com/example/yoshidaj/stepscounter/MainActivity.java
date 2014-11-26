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

import java.net.MalformedURLException;

public class MainActivity extends Activity implements View.OnClickListener{

    private MobileServiceClient mClient;

    private EditText editText;
    private Button addButton1;

    private TextView textView2;
    private Button incrementButton;

    private Button addButton2;

    int num = 0;
    int incrementNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        addButton1 = (Button) findViewById(R.id.button);
        textView2 = (TextView) findViewById(R.id.textView2);
        incrementButton = (Button) findViewById(R.id.button2);
        addButton2 = (Button) findViewById(R.id.button3);

        addButton1.setOnClickListener(this);
        incrementButton.setOnClickListener(this);
        addButton2.setOnClickListener(this);

        textView2.setText(Integer.toString(incrementNum));

        try {
            mClient = new MobileServiceClient("https://mycounter.azure-mobile.net/", "RiIzLgCjTIunvogUaAVZbgMiCRnpaJ39", this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    Item item = new Item();
    item.Text = "すばらしいアイテム";
    mClient.getTable(Item.class).insert(item, new TableOperationCallback<Item>() {
        public void onCompleted(Item entity, Exception exception, ServiceFilterResponse response) {
            if (exception == null) {
                // Insert succeeded
            } else {
                // Insert failed
            }
        }
    });

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
            case R.id.button2:
                textView2.setText(Integer.toString(++incrementNum));
                Toast.makeText(this, incrementNum+" IncrementButtonPushed", Toast.LENGTH_LONG).show();
                break;
            case R.id.button3:
                break;
        }
    }
}