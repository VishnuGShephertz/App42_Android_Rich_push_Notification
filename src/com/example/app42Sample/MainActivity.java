/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.example.app42Sample;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Log;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.push.plugin.App42GCMController;
import com.shephertz.app42.push.plugin.App42GCMService;
import com.shephertz.app42.push.plugin.App42RichPushType;
import com.shephertz.app42.push.plugin.App42RichPush;

/**
 * This Class for testing purpose used to register device on App42. You can also
 * send different type of Rich Push Message to user as well from here.
 * 
 * @author Vishnu Garg
 * 
 */
public class MainActivity extends Activity  {

	private static final String GoogleProjectNo = "Your Google Project No";
	private TextView responseTv;
	private EditText edUserName, edMessage;
	private App42RichPushType currentSelected;

	// Static content to check RichPush can be changed accordingly
	private static final String OpenWebUrl = "http://blogs.shephertz.com/";
	private static final String ImageUrl = "http://cdn.shephertz.com/repository/files/a97bf47de509c32f702e562cab2e7508389fb7d67d3322c4714c09aef4305f7c/e07985e3ddb8a537c457313aaa4402b80c5d66b3/port1push.jpg";
	private static final String VideoUrl = "http://cdn.shephertz.com/repository/files/cf077a193208596a23db85efe861c415f07b02ab3c605a234e594cb49b617762/689baa570914fa57f19b40c90f55e9912cacd547/ssassa.mp4";
	private static final String YouTubeId = "yRh1kIVjs6o";
	private static final String HtmlContent = "<html><head> <title>App42 Rich HTML Push</title></head><body><p>App42 Rich Push Sample</p>"
			+ " <img src='"+ImageUrl+"' alt='App42 Rish HTML Push' border='0'/><video controls><source src='http://www.broken-links.com/tests/media/BigBuck.m4v'></video></body></html> ";
	private static final String TextContent = "App42 Provides complete cloud API for application development in different SDKs e.g \n PushNotification \n LeaderBoard \n SocialService \n File Storage \n Custom Code";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		responseTv = ((TextView) findViewById(R.id.response_msg));
		edUserName = ((EditText) findViewById(R.id.uname));
		edMessage = ((EditText) findViewById(R.id.message));
		buildSpinnerLayout();
		App42API.initialize(
				this,
				"Your API Key",
				"Your Secret Key");
		App42Log.setDebug(true);
		// UserName You want to register on App42
		App42API.setLoggedInUser("YourUserName");
	}

	private void buildSpinnerLayout() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		// Spinner click listener
		ArrayAdapter<App42RichPushType> dataAdapter = new ArrayAdapter<App42RichPushType>(
				this, android.R.layout.simple_spinner_item,
				App42RichPushType.values());
		// Drop down layout style - list view
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				currentSelected = App42RichPushType.values()[pos];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	public void onStart() {
		super.onStart();
		if (App42GCMController.isPlayServiceAvailable(this)) {
			App42GCMController.getRegistrationId(MainActivity.this,
					GoogleProjectNo);
		} else {
			Log.i("App42PushNotification",
					"No valid Google Play Services APK found.");
		}
	}

	/*
	 * * This method is called when a Activty is stop disable all the events if
	 * occuring (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	public void onStop() {
		super.onStop();
		App42GCMService.isActivtyActive = false;
	}

	/*
	 * This method is called when a Activty is finished or user press the back
	 * button (non-Javadoc)
	 * 
	 * @override method of superclass
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();
		App42GCMService.isActivtyActive = false;
	}

	/*
	 * called when this activity is restart again
	 * 
	 * @override method of superclass
	 */
	public void onReStart() {
		super.onRestart();

	}

	/*
	 * called when activity is paused
	 * 
	 * @override method of superclass (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		super.onPause();
		App42GCMService.isActivtyActive = false;
		unregisterReceiver(mBroadcastReceiver);
	}

	/*
	 * called when activity is resume
	 * 
	 * @override method of superclass (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
		App42GCMService.isActivtyActive = true;
		String message = getIntent().getStringExtra(
				App42GCMService.ExtraMessage);
		if (message != null)
			Log.d("MainActivity-onResume", "Message Recieved :" + message);
		IntentFilter filter = new IntentFilter(
				App42GCMService.DisplayMessageAction);
		filter.setPriority(2);
		registerReceiver(mBroadcastReceiver, filter);
	}

	final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.BroadcastReceiver#onReceive(android.content.Context,
		 * android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			String message = intent
					.getStringExtra(App42GCMService.ExtraMessage);
			Log.i("MainActivity-BroadcastReceiver", "Message Recieved " + " : "
					+ message);
			responseTv.setText(message);
			Intent newIntent = new Intent(MainActivity.this,
					MessageActivity.class);
			newIntent.putExtra(App42GCMService.ExtraMessage, message);
			startActivity(newIntent);
		}
	};

	/**
	 * Handle on click event on Send Button
	 * 
	 * @param view
	 */
	public void onSendPushClicked(View view) {
		responseTv.setText("Sending Push to User ");
		String message = getMessageJsonString();
		System.out.println(message);
//		if (message != null)
//	App42API.buildPushNotificationService().sendPushMessageToUser(edUserName.getText().toString(),
//			message, new App42CallBack() {
//			
//			@Override
//			public void onSuccess(Object arg0) {
//				// TODO Auto-generated method stub
//				App42Response response=(App42Response) arg0;
//				onApp42Response(response.getStrResponse());
//			}
//			
//			@Override
//			public void onException(Exception arg0) {
//				// TODO Auto-generated method stub
//				onApp42Response(arg0.getMessage());
//			}
//		});
	}

	/**
	 * @return Content according to desired Rich type
	 */
	private String getContent() {
		if (currentSelected == App42RichPushType.Html)
			return HtmlContent;
		else if (currentSelected == App42RichPushType.YouTubeVideo)
			return YouTubeId;
		else if (currentSelected == App42RichPushType.Image)
			return ImageUrl;
		else if (currentSelected == App42RichPushType.Video)
			return VideoUrl;
		else if (currentSelected == App42RichPushType.OpenUrl)
			return OpenWebUrl;
		else
			return TextContent;
	}

	/**
	 * Generate Json String of Rich Push Message user wants to send.
	 * 
	 * @return
	 */
	private String getMessageJsonString() {
		String message = edMessage.getText().toString();
		String userName = edUserName.getText().toString();
		if (isNullBlank(userName) ||  isNullBlank(message)) {
			Toast.makeText(this, "All Fields are mandatory", Toast.LENGTH_SHORT)
					.show();
			return null;
		} else {
			try {
				JSONObject json = new JSONObject();
				json.put(App42RichPush.Alert.getValue(), message);
				json.put(App42RichPush.Type.getValue(), currentSelected.getValue());
				json.put(App42RichPush.Content.getValue(), getContent());
				return json.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

	}

	/**
	 * @param value
	 * @return
	 */
	private boolean isNullBlank(String value) {
		if (value == null || value.equals(""))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.push.plugin.App42GCMController.App42GCMListener#onError
	 * (java.lang.String)
	 */

	public void onError(String errorMsg) {
		// TODO Auto-generated method stub
		responseTv.setText("Error -" + errorMsg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.push.plugin.App42GCMController.App42GCMListener#
	 * onApp42Response(java.lang.String)
	 */
	
	public void onApp42Response(final String responseMessage) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				responseTv.setText(responseMessage);
			}
		});
	}
}
