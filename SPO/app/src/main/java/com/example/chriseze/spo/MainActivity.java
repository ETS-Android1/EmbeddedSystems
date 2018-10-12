package com.example.chriseze.spo;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chriseze.spo.Models.SessionManager;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private SessionManager sessionManager;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private TextView mTvStatus;
    private ImageView mImgIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar)findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Home");

        //Custom App Bar
        //============================================================================================//
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.custom_appbar, null);
        actionBar.setCustomView(action_bar_view);

        mTvStatus = (TextView)findViewById(R.id.status);
        mImgIndicator = (ImageView)findViewById(R.id.indicator);

        //============================================================================================//

        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mTabLayout = (TabLayout)findViewById(R.id.mainTabs);

        sessionManager = new SessionManager(this);
        sessionManager.setLogin(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.logout){
            sessionManager.setLogin(false);
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }else if(id == R.id.clear_energy){

        }
        return true;
    }
}
