package utils;

import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

import listener.AudioPrepareListener;

//一个进行录音的类,该类使用单例模式
public class AudioManager {

    private static  AudioManager audioManager;

    AudioPrepareListener listener;

    File file;

    MediaRecorder mediaRecorder;

    boolean isPrepared;
    private AudioManager(){

    }


    public void setListener(AudioPrepareListener listener) {
        this.listener = listener;
    }

    public static AudioManager getInstance(){
        if(audioManager==null){
            synchronized (AudioManager.class){
                if(audioManager==null){
                    audioManager=new AudioManager();
                }
            }
        }
        return audioManager;
    }

    //取消录音
    public void cancel(){
        stop();
        file.delete();
    }


    //正常停止
    public void stop(){
        if(file!=null&&file.length()!=0) {

            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            mediaRecorder.setPreviewDisplay(null);
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder=null;
            isPrepared=false;
        }
    }

    //开始录音
    public void start(){

        mediaRecorder=new MediaRecorder();
        //文件准备
        file=new File(MyApplication.getContext().getExternalCacheDir(),"/ceshisound.amr");
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
            Log.d("wenjian!!!","wenjianlujing"+"!!"+file.getAbsolutePath());
            //声音来源：麦克风
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            //声音的输出格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            //声音编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.setOutputFile(file.getAbsolutePath());

            mediaRecorder.prepare();

            mediaRecorder.start();

            isPrepared=true;
            //准备完成后回调接口，告诉客户端开始正常录音
            if(listener!=null){
                listener.wellPrepared();
            }
        }catch (IOException e){
            Log.d("wenjian!!!","录音出错"+e.getMessage());
        }

    }


    public int getLevel(int maxLevel){
        if(isPrepared){
            int result=(maxLevel*mediaRecorder.getMaxAmplitude())/32768+1;

            Log.d("测试音量",mediaRecorder.getMaxAmplitude()+"!"+"!"+maxLevel+result);
            return result;

        }
        return 1;
    }
}
