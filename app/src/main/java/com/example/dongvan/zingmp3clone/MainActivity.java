package com.example.dongvan.zingmp3clone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dongvan.zingmp3clone.adapter.AdapterOffline;
import com.example.dongvan.zingmp3clone.model.model_off;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView nav_view;
    private SearchView searchView;

    private ListView lv_Online, lv_Offline;
    private AdapterOffline adapterOff;
    private ArrayList<model_off> list_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControllers();
        fakeData();

        //Khởi tạo 1 toolbar mới thay cho ActionBar
        setSupportActionBar(toolbar);

        //Khởi tạo Navigation => tạo toggle để click vào thì show navigation drawer
        //add lắng nghe sự kiên cho toggle button
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);


        adapterOff = new AdapterOffline(this,R.layout.item_offline);
        adapterOff.addAll(list_off);
        lv_Offline.setAdapter(adapterOff);
        adapterOff.setNotifyOnChange(true);

        lv_Offline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent i = new Intent(MainActivity.this,ListSongActivity.class);
                        startActivity(i);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
            }
        });

    }

    private void addControllers() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        lv_Offline = (ListView) findViewById(R.id.lv_Offline);
        lv_Online = (ListView) findViewById(R.id.lv_Online);
    }

    private void fakeData(){
        list_off = new ArrayList<>();
        list_off.add(new model_off("123.jpg","Bài hát","32"));
        list_off.add(new model_off("123.jpg","Album","10"));
        list_off.add(new model_off("123.jpg","Nghệ sỹ","5"));
        list_off.add(new model_off("123.jpg","Playlist","1"));
        list_off.add(new model_off("123.jpg","Thư mục","2"));
        list_off.add(new model_off("123.jpg","Download","22"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main,menu);
        // thêm search vào vào action bar
        MenuItem itemSearch = menu.findItem(R.id.search_view);
        searchView = (SearchView) itemSearch.getActionView();
        //set OnQueryTextListener cho search view để thực hiện search theo text
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
