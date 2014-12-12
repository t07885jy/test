package com.example.yoshidaj.stepscounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends Activity implements View.OnClickListener, LocationListener {

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

    int incrementNum = 0;

    //Calendar calendar;
    //String today;
    Date date;

    private LocationManager locationManager;

    private LocationRequest locationRequest;
    private Location currentBestLocation;
    private Location netLoc;
    private Location gpsLoc;

    private final static String API_KEY = "AIzaSyDF_s1typK1Z5UzldTadOp_RJxPc0pbROk";

    private int targetLocalX;
    private int targetLocalY;
    private int screenX;
    private int screenY;
    private int defLeft;
    private int defTop;

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

        incrementButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        textView2.setText(Integer.toString(incrementNum));

        date = new Date(System.currentTimeMillis());
        //df = new SimpleDateFormat("yyyy/MM/dd");
        //Toast.makeText(this, dat.toString(), Toast.LENGTH_LONG).show();
        //calendar = Calendar.getInstance();
        //today = calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        this.onLocationChanged(netLoc);
        gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        this.onLocationChanged(gpsLoc);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            mClient = new MobileServiceClient("https://mycounter.azure-mobile.net/", "RiIzLgCjTIunvogUaAVZbgMiCRnpaJ39", this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.getSteps();
    }

    @OnTouch(R.id.imageButton)
    public boolean touchButton(View v, MotionEvent event){
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //Toast.makeText(this, "Down", Toast.LENGTH_SHORT).show();
                targetLocalX = defLeft = mImageButton.getLeft();
                targetLocalY = defTop = mImageButton.getTop();
                screenX = x;
                screenY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //Toast.makeText(this, "Move", Toast.LENGTH_LONG).show();
                int diffX = screenX - x;
                int diffY = screenY - y;
                targetLocalX -= diffX;
                targetLocalY -= diffY;
                mImageButton.layout(targetLocalX,
                        targetLocalY,
                        targetLocalX + mImageButton.getWidth(),
                        targetLocalY + mImageButton.getHeight());
                screenX = x;
                screenY = y;
                break;
            case MotionEvent.ACTION_UP:
                /*
                final AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
                adBuilder.setTitle("Message");
                adBuilder.setMessage("本当にタバコを吸いますか？");

                adBuilder.setPositiveButton("吸う！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Add button clicked
                        incrementNum++;
                        mImageButton.setVisibility(View.INVISIBLE);
                        TranslateAnimation translate = new TranslateAnimation(200, 0, 200, 0);
                        translate.setDuration(500);
                        mImageButton.startAnimation(translate);
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
                        item.DateToday = date;
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
                adBuilder.show();*/
                //Toast.makeText(this, "Up", Toast.LENGTH_LONG).show();

                mImageButton.setVisibility(View.INVISIBLE);

                CountDownTimer cdt = new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        mImageButton.layout(defLeft,
                                defTop,
                                defLeft+ mImageButton.getWidth(),
                                defTop + mImageButton.getHeight());
                        mImageButton.setVisibility(View.VISIBLE);
                    }
                }.start();

                Item item = new Item();
                item.Text = "すばらしいアイテム";
                //item.DateToday = date;
                incrementNum++;
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
                        mTextView10.setText(Integer.toString(finalTotal) + " / 目標の本数 "+"Latitude: "+String.valueOf(currentBestLocation.getLatitude())+" & Longitude: "+String.valueOf(currentBestLocation.getLongitude()));
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

    public void onStart(){
        if(locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        super.onResume();
    }

    public void onStop(){
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (currentBestLocation == null) {
                currentBestLocation = location;
                //textView.setText("Latitude: "+String.valueOf(location.getLatitude())+"\nLongitude: "+String.valueOf(location.getLongitude()));
            } else if (location.getTime() > currentBestLocation.getTime()) {
                if (location.getAccuracy() > currentBestLocation.getAccuracy()) {
                    currentBestLocation = location;
                }
            }
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
}