package com.szyz.mywechatplugin.wxapi;


import com.szyz.mywechatplugin.MainActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//		int wxSdkVersion = api.getWXAppSupportAPI();
//		if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
//			Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported", Toast.LENGTH_LONG).show();
//		} else {
//			Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
//		}


//        try {
//        	api.handleIntent(getIntent(), this);
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        //api.handleIntent(intent, this);
	}


	@Override
	public void onReq(BaseReq baseReq) {

	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		
		Toast.makeText(this, "baseresp.getType = " + resp.getType(), Toast.LENGTH_SHORT).show();
		SendAuth.Resp _resp = (SendAuth.Resp )resp;
		if (_resp.state.isEmpty()) return;
		String code;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			//登录成功
			code = _resp.code;
			MainActivity.instance.onWechatLoginCallback(code);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			//取消登录
			MainActivity.instance.showToast("用户取消登录");
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			//拒绝
			MainActivity.instance.showToast("用户拒绝登录");
			break;
		case BaseResp.ErrCode.ERR_UNSUPPORT:
			MainActivity.instance.showToast("不支持，请联系客服");
			break;
		default:
			result = 0;
			break;
		}
		
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	

}