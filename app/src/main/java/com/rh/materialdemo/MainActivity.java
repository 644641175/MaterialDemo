package com.rh.materialdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//设置toolbar替代原ActionBar
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.camera:
                Toast.makeText(this,"You clicked camera",Toast.LENGTH_SHORT).show();
                break;
            case R.id.backup:
                Toast.makeText(this,"You clicked backup",Toast.LENGTH_SHORT).show();
                break;
            case R.id.sunny:
                Toast.makeText(this,"You clicked sunny",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

}
