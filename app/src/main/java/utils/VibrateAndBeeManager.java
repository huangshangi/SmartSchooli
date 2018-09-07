package utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;


import com.smartschool.smartschooli.R;

import java.io.IOException;

public class VibrateAndBeeManager {

    static boolean flag=false;//当前是否应该发出声音


    public static void vibrate(long time){
        Vibrator vibrator=(Vibrator) MyApplication.getContext().getSystemService(Service.VIBRATOR_SERVICE);
        if(ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.VIBRATE)== PackageManager.PERMISSION_GRANTED){
            vibrator.vibrate(time);
        }

    }

    public static void vibrate(long[]times,boolean repate){
        Vibrator vibrator=(Vibrator) MyApplication.getContext().getSystemService(Service.VIBRATOR_SERVICE);
        if(ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.VIBRATE)== PackageManager.PERMISSION_GRANTED){
            vibrator.vibrate(times,repate?1:-1);
        }
    }


    public static void Bee(){
        AudioManager audioManager=(AudioManager)MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        if(audioManager.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL){
            flag=false;
        }

        MediaPlayer mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
            }
        });

        AssetFileDescriptor file=MyApplication.getContext().getResources().openRawResourceFd(R.raw.qq_sound);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength()); file.close();
            mediaPlayer.setVolume(0, 1);
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            mediaPlayer = null;
        }
        if (flag && mediaPlayer != null) {
            mediaPlayer.start();
        }



    }
}
