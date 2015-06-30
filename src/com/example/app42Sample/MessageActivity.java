/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.example.app42Sample;

import org.json.JSONException;
import org.json.JSONObject;

import com.shephertz.app42.push.plugin.App42GCMService;
import com.shephertz.app42.push.plugin.App42RichPushType;
import com.shephertz.app42.push.plugin.App42RichPush;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
/**
 * @author Vishnu Garg
 * 
 */
public class MessageActivity extends Activity {

	private WebView webView;
	private FrameLayout frameLayout;
	private ProgressBar progressBar;
	private RichWebView rich;
	private VideoView mVideoView;
	private App42RichPushType richPushType=null;
	MediaController mediaController;
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rich_push);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		frameLayout = (FrameLayout) findViewById(R.id.main_parent);
		webView = (WebView) findViewById(R.id.webView);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	public void onStart(){
		super.onStart();
		String message = getIntent().getStringExtra(
				App42GCMService.ExtraMessage);
		//System.out.println(message);
		if(message!=null){
			try {
				Log.d("MainActivity-onResume", "Message Recieved :" + message);
			   System.out.println("Receive in Message Activity"+message);
				JSONObject json=new JSONObject(message);
				String content=json.optString(App42RichPush.Content.getValue(), "");
				richPushType=App42RichPushType.getByValue(json.optString(App42RichPush.Type.getValue()));
				showRichPushMessage(content);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				showTextPush(message);
			}
		}
	}
	
	
	/**
	 * @param content
	 */
	private void showRichPushMessage(String content){
		if(richPushType==App42RichPushType.Html)
			showHtmlOrImagePush(content, false);
		else if(richPushType==App42RichPushType.Image)
			showHtmlOrImagePush(content, true);
		else if(richPushType==App42RichPushType.Text)
			showTextPush(content);
		else if(richPushType==App42RichPushType.Video)
			showVideoPush(content);
		else if(richPushType==App42RichPushType.YouTubeVideo)
			showYouTubeVideo(content);
		else if(richPushType==App42RichPushType.OpenUrl)
			openUrlInBrowser(content);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if(richPushType!=null&&(richPushType==App42RichPushType.Html||richPushType==App42RichPushType.Image))
		webView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(richPushType!=null&&(richPushType==App42RichPushType.Html||richPushType==App42RichPushType.Image))
		webView.onResume();
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if(richPushType!=null&&(richPushType==App42RichPushType.Html||richPushType==App42RichPushType.Image))
		rich.hideCustomView();
	}
	
	/**
	 * @param view
	 */
	public void onCloseButtonClick(View view){
		if(richPushType==null){
			finish();
			return;
		}
		if((richPushType==App42RichPushType.Html||richPushType==App42RichPushType.Image)&&rich!=null)
		rich.hideCustomView();
		else if(richPushType==App42RichPushType.Video)
		mVideoView.stopPlayback();
		
		finish();
	}
	
	/**
	 * show Rich Push Message in case of image and HTML content
	 * @param content
	 */
	private void showTextPush(String content){
		TextView messageText=(TextView)findViewById(R.id.message);
		messageText.setVisibility(View.VISIBLE);
		messageText.setText(content);
	}
	/**
	 * show Rich Push Message in case of image and HTML content
	 * @param content
	 */
	private void showHtmlOrImagePush(String content,boolean isUrlContent){
		webView.setVisibility(View.VISIBLE);
		 rich=new RichWebView(frameLayout, webView, progressBar, isUrlContent, content);
	}
	/**
	 * Load Video/You tube Push message in VideoView 
	 */
	private void showVideoPush(String videoUrl){
		 final ProgressDialog progress=new ProgressDialog(this);
		progress.setMessage("Loading Video, wait....");
		progress.show();
		progress.setCancelable(true);
			mVideoView = (VideoView)findViewById(R.id.video);
			mVideoView.setVisibility(View.VISIBLE);
			mediaController=new MediaController(this);
			mediaController.setAnchorView(mVideoView);
			mVideoView.setMediaController(mediaController);
			mVideoView.bringToFront();
			mVideoView.setVideoURI(Uri.parse(videoUrl));
			mVideoView.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					progress.dismiss();
					mediaController.show();
					mVideoView.start();
				}
			});
	}
	/**
	 * Loads Url in browser if rich Push type is OpenUrl
	 * @param webUrl
	 */
	private void openUrlInBrowser(String webUrl){
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(webUrl));
		startActivity(intent);
		finish();
	}
	/** Load youTube video in youtube player else in browser
	 * @param youTubeId
	 */
	private void showYouTubeVideo(String youTubeId){
		try {
			Intent intent2 = new Intent(Intent.ACTION_VIEW,
					Uri.parse("vnd.youtube:" + youTubeId));
			startActivity(intent2);
			finish();
		} catch (ActivityNotFoundException ex) {
			openUrlInBrowser("http://www.youtube.com/watch?v=" + youTubeId);
		}
	}
}
