package com.example.dongvan.zingmp3clone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dongvan.zingmp3clone.model.AudioSong;
import com.example.dongvan.zingmp3clone.myservice.AudioService;

public class PlaySongActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    Toolbar toolbar;
    private SearchView searchView;

    private AudioService player;
    boolean boundService = false;

    ImageButton btnPlay;
    ImageView imgDisc,btnNgauNhien,btnPrevious,btnNext, btnRepeat;
    TextView songLyric,txtTimeDurrent,txtTimeTotal;
    SeekBar seekBar;
    Animation animRotate;
    Utilities util;
    int totalTime;

    Handler handlerUpdate;

    boolean checkPlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        addControls();

        //Lấy bài hát từ activity ListSong gửi qua
        Intent songIntent = getIntent();
        Bundle songBundle = songIntent.getBundleExtra("MyBundle");
        AudioSong s = (AudioSong) songBundle.getSerializable("SongPlay");
        Log.d("kiemtra", " => song dc gui qua : "+s.getTitle());

        //Cấu hình toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle(s.getTitle()+"");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load the animation
        animRotate = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);

        imgDisc.startAnimation(animRotate);

        //Cho chạy bài hát được gửi từ activity ListSong
        songLyric.setText(s.getTitle());
        playAudio(s.getData());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.onStopTrackingTouch(seekBar);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlay =! checkPlay;
                if(checkPlay==true){
                    btnPlay.setBackgroundResource(R.drawable.play);
                    player.playMedia();
                }else{
                    btnPlay.setBackgroundResource(R.drawable.pause);
                    player.pauseMedia();
                }
            }
        });

    }

    private void addControls() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        imgDisc = (ImageView) findViewById(R.id.imgDisc);
        btnNgauNhien = (ImageView) findViewById(R.id.btnNgauNhien);
        btnPrevious = (ImageView) findViewById(R.id.btnPrevious);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnNext = (ImageView) findViewById(R.id.btnNext);
        btnRepeat = (ImageView) findViewById(R.id.btnRepeat);
        songLyric = (TextView) findViewById(R.id.txtLyric);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txtTimeDurrent = (TextView) findViewById(R.id.txtTimeDurrent);
        txtTimeTotal = (TextView) findViewById(R.id.txtTimeTotal);
        handlerUpdate = new Handler();
        seekBar.setMax(100);
    }

    private void updateData(){
        handlerUpdate.postDelayed(displayData,500);
    }

    private Runnable displayData = new Runnable() {
            @Override
            public void run() {
                txtTimeDurrent.setText(util.milliSecondsToTimer(player.getCurrentDuration())+"");
                seekBar.setProgress(player.getProgress());
                Log.d("total", "=> "+totalTime+"");
                if(player.getCurrentDuration()<=totalTime){
                    updateData();
                }

            }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);

        MenuItem itemSearch = menu.findItem(R.id.search_view);
        searchView = (SearchView) itemSearch.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    //Binding từ Client đến AudioService
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Nơi nhận dữ kiệu từ service
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
            player = binder.getAudioService();
            boundService = true;

            //Set tổng thời gian chạy bài hát : lấy cố định (còn cách lấy liên tục dùng handler)
            util = new Utilities();
            totalTime = player.getDur();
            txtTimeTotal.setText(util.milliSecondsToTimer(totalTime));

            //Cập nhật dữ liệu
            updateData();
            Toast.makeText(PlaySongActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            boundService = false;
        }
    };

    private void playAudio(String media){
        //kiểm tra service đã active
        if(!boundService){
            Intent playIntent = new Intent(this,AudioService.class);
            playIntent.putExtra("media",media);
            startService(playIntent);
            bindService(playIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus",boundService);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boundService = savedInstanceState.getBoolean("serviceStatus");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(boundService){
            unbindService(serviceConnection);
            player.stopSelf();
        }
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
