package com.szyz.mywechatplugin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


public class MainActivity extends UnityPlayerActivity {
    private Toast mToast;
    private static final int THUMB_SIZE = 150;
    private static final String Wechat_AppID = "wx0f9801acb507830f";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        getIntentData();
        regToWX();
    }
    @Override

    protected void onNewIntent(Intent var1) {
        super.setIntent(var1);
        getIntentData();
    }

    public void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(text);
                }
                mToast.show();
            }
        });
    }


    public void getIntentData(){

        Intent it = getIntent();
        String action = it.getAction();
        String data = it.getDataString();
        Log.d("getIntentData","getIntentData." + data);
        if(Intent.ACTION_VIEW.equals(action)){
            Uri uri = it.getData();
            if(uri != null){
                String rid = uri.getQueryParameter("rid");
                //String age= uri.getQueryParameter("age");
                if(rid != null && !rid.isEmpty()){
                    sendMessageToUnity(rid);
                }
            }
        }
    }

    public static void sendMessageToUnity(String data){
        UnityPlayer.UnitySendMessage("GameCenter","OnAndroid2Unity",data);
    }

    public void sendWechatInvitation(String title,String desc,String url){
        if (api == null ||url == null || url.isEmpty() ) return;
        showToast(url);

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = desc;
        //
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//        bmp.recycle();
        //msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        Bitmap thumbBmp =  Bitmap.createScaledBitmap(GetLocalOrNetBitmap("http://120.132.117.218:8721/images/send_music_thumb.png"),THUMB_SIZE,THUMB_SIZE,true);
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new  SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;

        api.sendReq(req);


    }

    public void sendWechatShare(){


    }


    public void regToWX(){
        api = WXAPIFactory.createWXAPI(this,Wechat_AppID,true);
        api.registerApp(Wechat_AppID);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 把网络资源图片转化成bitmap
     * @param url  网络资源图片
     * @return  Bitmap
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

}
