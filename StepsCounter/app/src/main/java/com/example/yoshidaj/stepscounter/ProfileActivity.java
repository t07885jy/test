package com.example.yoshidaj.stepscounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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
        }else if(id == R.id.action_profile){
            //Toast.makeText(this, "profile selected", Toast.LENGTH_LONG).show();
            intent.setClassName("com.example.yoshidaj.stepscounter", "com.example.yoshidaj.stepscounter.ProfileActivity");
            //intent.putExtra();
            startActivity(intent);
            return true;
        }else if(id == R.id.action_ranking){
            Toast.makeText(this, "ranking selected", Toast.LENGTH_LONG).show();
            return true;
        }else if(id == R.id.action_facebook){
            Toast.makeText(this, "facebook selected", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
