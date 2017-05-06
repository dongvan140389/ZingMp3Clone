package com.example.dongvan.zingmp3clone.myservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;

import com.example.dongvan.zingmp3clone.Utilities;

import java.io.IOException;

/**
 * Created by VoNga on 30-Apr-17.
 */

public class AudioService extends Service implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener
                                        ,MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnInfoListener
                                        ,MediaPlayer.OnSeekCompleteListener,AudioManager.OnAudioFocusChangeListener,
                                        SeekBar.OnSeekBarChangeListener{
    //tạo đối tượng mediaPlayer để điều khiển các phương thức chơi nhạc
    private MediaPlayer mediaPlayer;
    //path to the audio file
    private String mediaFile;

    //Used to pause/resume MediaPlayer
    private int resumePosition;

    private AudioManager audioManager;

    //Handler để xử lý seeBar
    private Handler seekBarHandler;

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    int progress;
    int currentDuration;

    //get set để bên activity chơi nhạc có thể gọi tới đc
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(int currentDuration) {
        this.currentDuration = currentDuration;
    }




    /**
     * Service lifecycle methods
     */
    //Trả về instance IBinder => nên phải return về một instance kế thừa Binder
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        seekBarHandler = new Handler();
    }

    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        try {
            //An audio file is passed to the service through putExtra();
            mediaFile = intent.getExtras().getString("media");
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaFile != null && mediaFile != "")
            initMediaPlayer();
        return super.onStartCommand(intent, flags, startId);
    }

    //Chỉ ra trạng thái buffer của file media stream trên mạng
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Được gọi khi một file media chạy xong
        //gọi file stopMedia để stop file
        stopMedia();
        //stop luôn service
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    //được gọi để truyền đạt một số thông tin
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    //được gọi khi file media sẵn sàng chạy lại
    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //giải phóng tài nguyên
        if(mediaPlayer!=null){
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
    }

    //được gọi khi các file âm thanh của hệ thống có thay đổi
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_GAIN://service đã focus vào file audio, có thể play
                // resume playback
                if(mediaPlayer == null) initMediaPlayer();
                else if(!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f,1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS://service mất focus vào file audio, cần release mediaplayer
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://service mất focus trong thời gian ngắn => pause mediaplayer
                if(mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://service mất focus trog thời gian ngắn =>
                //có thể xuất thông báo hoặc làm nhỏ volume
                if(mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f,0.1f);
                break;
        }
    }

    /**
     * AudioFocus
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    //Cầu nối đẩy dữ liệu từ service ra activity
    public class LocalBinder extends Binder {
        public AudioService getAudioService() {
            return AudioService.this;
        }
    }

    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();

        //Set up MediaPlayer event listeners
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);

        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(mediaFile);
        }catch (IOException e){
            e.printStackTrace();
            stopSelf();
        }

        mediaPlayer.prepareAsync();
    }

    public void playMedia(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            updateSeekBar();
        }
    }
    private void stopMedia(){
        if(mediaPlayer==null) return;
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }
    public void pauseMedia(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }
    private void resumeMedia(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    public int getDur(){
        return mediaPlayer.getDuration();
    }

    public void updateSeekBar(){
        try{
            seekBarHandler.postDelayed(mUpdateTimeTask, 100);
        }catch(Exception e){

        }
    }

    public Runnable mUpdateTimeTask = new Runnable() {
        public void run(){
            int totalDuration = 0;
            currentDuration = 0;

            try {

                totalDuration = mediaPlayer.getDuration();
                currentDuration = mediaPlayer.getCurrentPosition();

                progress = (int)(Utilities.getProgressPercentage(currentDuration, totalDuration));
                //seekBarHandler.postDelayed(this, 100);
                if(currentDuration<=totalDuration){
                    updateSeekBar();
                }

                Log.d("Test", "==> chạy tới đây 111");
            } catch(Exception e){
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Lấy ra phần trăm seekbar đã kéo
        int percent = seekBar.getProgress();

        //Số minisecond mà audio dịch đến
        int currentTime_SeekTo = percent*mediaPlayer.getDuration()/100;
        mediaPlayer.seekTo(currentTime_SeekTo);
    }

}
