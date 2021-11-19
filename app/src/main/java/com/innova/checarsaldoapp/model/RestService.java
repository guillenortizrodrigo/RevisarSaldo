package com.innova.checarsaldoapp.model;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;

import java.util.ArrayList;


/**
 * This is an Android Service to provide REST functionality outside of the Application thread.
 */

public class RestService{
	
	private ArrayList <ParcelableNameValuePair> params;
	private ArrayList <ParcelableNameValuePair> headers;
	
	
	private String url;
	private final Handler mHandler;
	private Context mContext;
	private int RequestType;
	private String entity;
	
	public final static int GET = 1;
	public final static int POST = 2;
	public final static int PUT = 3;
	public final static int DELETE = 4;
	


	
	public RestService(Handler mHandler, Context mContext, String url,int RequestType){
		this.mHandler = mHandler;
		this.mContext = mContext;
		this.url = url;
		this.RequestType = RequestType;
		params = new ArrayList<ParcelableNameValuePair>();
		headers = new ArrayList<ParcelableNameValuePair>();
	}


	public void addParam(String name, String value){
		params.add(new ParcelableNameValuePair(name, value));
	}


	public void addHeader(String name, String value){
		headers.add(new ParcelableNameValuePair(name,value));
	}

	public void setEntity(String entity){

		this.entity = entity;
	}

	

	
	public void execute() {
	     ResultReceiver receiver;
	     receiver = new ResultReceiver(mHandler){
    	    @Override
    	    protected void onReceiveResult(int resultCode, Bundle resultData) {
    	    	mHandler.obtainMessage(0,0,Integer.valueOf(resultData.getString("codigo")),resultData.getString("result")).sendToTarget();
				System.err.println(resultData.getString("result"));
				System.err.println(resultData.getString("codigo"));
    	    }
	     };

	     //final Intent intent = new Intent(mContext, CDealIntentService.class);
		final Intent intent = new Intent(mContext, ExecuteRequest.class);
	     intent.putParcelableArrayListExtra("headers", (ArrayList<? extends Parcelable>) headers);
	     intent.putExtra("params", params);
	     intent.putExtra("url", url);
	     intent.putExtra("receiver", receiver);
	     intent.putExtra("method", RequestType);
	     intent.putExtra("entity", entity);
	     mContext.startService(intent);
	}
}