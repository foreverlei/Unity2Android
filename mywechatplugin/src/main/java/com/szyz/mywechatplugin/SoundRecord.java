package com.szyz.mywechatplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by swift on 2017/5/17.
 */

public class SoundRecord {
    public static int battery = 50 ;//0 - 100
    static class BatteryReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                battery = (level*100)/scale ;
            }
        }
    }

    private static SoundRecord instance ;
    public static SoundRecord instance(){
        if(instance == null){
            instance = new SoundRecord() ;
        }
        return instance ;
    }

    private SoundPool player = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

    public SoundRecord(){
        player.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                soundPool.play(sampleId, 1, 1, 0, 0, 1);
                soundPool.unload(sampleId) ;
            }
        });
    }

    private MediaRecorder mRecorder = null;
    public void recordStart(Context context, String name) {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            String path = getRecordPath(context, name) ;
            File file = new File(path) ;
            if(file.exists()){
                file.delete() ;
            }
            try {
                file.createNewFile() ;
                mRecorder.setOutputFile(path);
                mRecorder.prepare();
                mRecorder.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void recordStop() {
        try{
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        }catch (Exception e) {
            e.printStackTrace() ;
        }
    }

    public String recordGet(Context context, String name){
        String path = getRecordPath(context, name) ;
        File file = new File(path) ;

        if(file.exists()) {
            try {
                InputStream is = new FileInputStream(file);
                byte[] b = toByteArray(is) ;
                final String voice = new String(Base64.encode(b, Base64.DEFAULT));
                closeQuietly(is);
                return voice ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private String getRecordPath(Context context, String name){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + context.getCacheDir() + "/record/" ;

        System.out.println(path + name);
        File file = new File(path) ;
        if (!file.exists()){
            file.mkdirs() ;
        }
        return path + name ;
    }

    public void playerSave(Context context, String name, String sound){
        String path = getRecordPath(context, name) ;
        File file = new File(path) ;
        file.deleteOnExit();

        FileOutputStream fos;
        try {
            byte[] content = Base64.decode(sound, Base64.DEFAULT) ;
            fos = new FileOutputStream(path);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerSave(Context context, String name, byte[] sound){
        String path = getRecordPath(context, name) ;
        File file = new File(path) ;
        file.deleteOnExit();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            fos.write(sound);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerStart(Context context, String name){
        String path = getRecordPath(context, name) ;
        player.load(path, 1) ;
    }

    public void release(){
        try{
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        }catch (Exception e) {
            e.printStackTrace() ;
        }
    }

    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        byte[] arr = output.toByteArray() ;
        closeQuietly(output);
        return arr ;
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
