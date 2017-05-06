package com.example.dongvan.zingmp3clone;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dongvan.zingmp3clone.adapter.AdapterSongOffline;
import com.example.dongvan.zingmp3clone.model.AudioSong;

import java.util.ArrayList;

public class ListSongActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ArrayList<AudioSong> audioList;
    AdapterSongOffline adapter;

    ListView lvSongOffline;
    Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);
        loadAudio();
        addControls();

        setSupportActionBar(toolbar);
        toolbar.setTitle("Nhạc Offline");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new AdapterSongOffline(this,R.layout.item_song_offline);
        adapter.addAll(audioList);
        lvSongOffline.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        lvSongOffline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioSong s = (AudioSong) lvSongOffline.getItemAtPosition(position);

                //Gửi bài hát qua activity mới bằng intent + bundle
                Intent myIntent = new Intent(ListSongActivity.this,PlaySongActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SongPlay",s);
                myIntent.putExtra("MyBundle",bundle);
                startActivity(myIntent);
                //playAudio(s.getData());
            }
        });

    }

    private void addControls() {
        lvSongOffline = (ListView) findViewById(R.id.lvSongOffline);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
    }

    private void loadAudio() {
        //Tạo content provider để truy cập vào MediaStore trên máy lấy ra danh sách bài hát có trên máy
        ContentResolver contentResolver = getContentResolver();

        //Xác định URI cho content provider
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        //Tạo câu query cho content provider
        Cursor cursor = contentResolver.query(uri,null,selection,null,sortOrder);
        if(cursor != null && cursor.getCount()>0){
            audioList = new ArrayList<>();
            while (cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                //lưu vào audioList
                audioList.add(new AudioSong(data,title,album,artist));
            }
        }

        cursor.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);

        MenuItem itemSearch = menu.findItem(R.id.search_view);
        searchView = (SearchView) itemSearch.getActionView();
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
