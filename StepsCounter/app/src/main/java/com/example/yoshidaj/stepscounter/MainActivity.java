package com.example.yoshidaj.stepscounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.imageButton)
    ImageButton mImageButton;
    @InjectView(R.id.textView10)
    TextView mTextView10;
    private MobileServiceClient mClient;

    private TextView textView2;
    private Button incrementButton;

    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private Button submitButton;
    private Button toMapViewButton;

    int incrementNum = 0;

    //Calendar calendar;
    //String today;
    Date date;
    /*
    private LocationManager locationManager;

    private LocationRequest locationRequest;
    private Location currentBestLocation;
    private Location netLoc;
    private Location gpsLoc;

    private Button button;
    private Button button2;

    LatLng defaultCurrent;

    private MapView mapView;
    GoogleMap googleMap;
    private final static String API_KEY = "AIzaSyDF_s1typK1Z5UzldTadOp_RJxPc0pbROk";

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        textView2 = (TextView) findViewById(R.id.textView2);
        incrementButton = (Button) findViewById(R.id.incrementButton);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);

        submitButton = (Button) findViewById(R.id.submitButton);
        toMapViewButton = (Button) findViewById(R.id.toMapViewButton);

        incrementButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        toMapViewButton.setOnClickListener(this);

        textView2.setText(Integer.toString(incrementNum));

        date = new Date(System.currentTimeMillis());
        //df = new SimpleDateFormat("yyyy/MM/dd");
        //Toast.makeText(this, dat.toString(), Toast.LENGTH_LONG).show();

        //calendar = Calendar.getInstance();
        //today = calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);

        try {
            mClient = new MobileServiceClient("https://mycounter.azure-mobile.net/", "RiIzLgCjTIunvogUaAVZbgMiCRnpaJ39", this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.getSteps();
    }

    @OnTouch(R.id.imageButton)
    public boolean touchButton(View v, MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_MOVE:
                //Toast.makeText(this, "Move", Toast.LENGTH_LONG).show();
                break;
            case MotionEvent.ACTION_DOWN:
                //Toast.makeText(this, "Down", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_UP:
                final AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
                adBuilder.setTitle("Message");
                adBuilder.setMessage("本当にタバコを吸いますか？");

                adBuilder.setPositiveButton("吸う！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Add button clicked
                        incrementNum++;
                        mImageButton.setVisibility(View.INVISIBLE);
                        CountDownTimer cdt = new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                mImageButton.setVisibility(View.VISIBLE);
                            }
                        }.start();

                        Item item = new Item();
                        item.Text = "すばらしいアイテム";
                        //item.DateToday = date;
                        item.Steps = incrementNum;
                        mClient.getTable(Item.class).insert(item, new TableOperationCallback<Item>() {
                            public void onCompleted(Item entity, Exception exception, ServiceFilterResponse response) {
                                if (exception == null) {
                                    // Insert succeeded
                                    incrementNum = 0;
                                    Toast.makeText(MainActivity.this, "Succeeded!", Toast.LENGTH_LONG).show();
                                    getSteps();
                                } else {
                                    // Insert failed
                                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                adBuilder.setNegativeButton("やっぱり吸わない。", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Decline button clicked
                    }
                });
                adBuilder.show();
                //Toast.makeText(this, "Up", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    public void getSteps() {
        MobileServiceTable<Item> items = mClient.getTable(Item.class);

        items.where().field("Text").eq("すばらしいアイテム").select("Steps").execute(new TableQueryCallback<Item>() {
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
                        textView3.setText("Total Steps : " + Integer.toString(finalTotal) + " 歩");
                        textView5.setText("Total Cost : " + (finalTotal * 21) + "円");
                    }
                });
            }
        });

        items.where().field("Date").eq(date).select("Steps").execute(new TableQueryCallback<Item>() {
            @Override
            public void onCompleted(List<Item> result, int count, Exception exception, ServiceFilterResponse response) {
                int totalToday = 0;
                for (Item i : result) {
                    totalToday += i.Steps;
                }
                final int finalTotal = totalToday;
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView4.setText("今日の歩数 : " + Integer.toString(finalTotal) + " 歩");
                        mTextView10.setText(Integer.toString(finalTotal) + " / 目標の本数");
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
        Intent intent = new Intent();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_main) {
            return true;
        } else if (id == R.id.action_profile) {
            intent.setClassName("com.example.yoshidaj.stepscounter", "com.example.yoshidaj.stepscounter.ProfileActivity");
            intent.putExtra("KeyWord", "moved to Profile Page");
            startActivity(intent);
            return true;
        } else if (id == R.id.action_ranking) {
            return true;
        } else if (id == R.id.action_facebook) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toMapViewButton:

                break;
            case R.id.incrementButton:
                //textView2.setText(Integer.toString(++incrementNum));
                break;
            case R.id.submitButton:
                /*Item item = new Item();
                item.Text = "すばらしいアイテム";
                item.DateToday = date;
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
                });*/
                break;
        }
    }
}