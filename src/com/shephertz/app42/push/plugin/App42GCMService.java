/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.push.plugin;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.app42Sample.MainActivity;
import com.example.app42Sample.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shephertz.app42.paas.sdk.android.App42Log;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 * @author Vishnu Garg
 */

public class App42GCMService extends IntentService {
	private static final int NotificationId = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	public static final String ExtraMessage = "extraMessage";
	static int msgCount = 0;
	public static final String DisplayMessageAction = "com.example.app42sample.DisplayMessage";
	public static final String TAG = "App42 Push Demo";
	
	public static boolean isActivtyActive=false;
	public App42GCMService() {
		super("GcmIntentService");
	}

	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				App42Log.debug("Send error: " + extras.toString());
				App42GCMReceiver.completeWakefulIntent(intent);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				App42Log.debug("Deleted messages on server: "
						+ extras.toString());
				App42GCMReceiver.completeWakefulIntent(intent);
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				String message = intent.getExtras().getString("message");
				App42Log.debug("Received: " + extras.toString());
				if(message==null)
				return;
				App42Log.debug("Message: " + message);
				if(!isActivtyActive)
				generateNotification(message);
				broadCastMessage(message);
				App42GCMReceiver.completeWakefulIntent(intent);
			}
		}
	}


	/**
	 * Function generate Notification on device and can be customize as per requirement
	 * @param jsonMessage
	 */
	private void generateNotification(String jsonMessage) {
		long when = System.currentTimeMillis();
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent;
		try {
			notificationIntent = new Intent(this,
					Class.forName(getActivityName()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			notificationIntent = new Intent(this, MainActivity.class);
		}
		String title=getString(R.string.app_name);
		notificationIntent.putExtra("message_delivered", true);
		notificationIntent.putExtra(ExtraMessage, jsonMessage);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_gcm)
				.setContentTitle(title)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(getPushMessage(jsonMessage)))
				.setContentText(getPushMessage(jsonMessage)).setWhen(when).setNumber(++msgCount)
				.setLights(Color.YELLOW, 1, 2).setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setDefaults(Notification.DEFAULT_VIBRATE);
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NotificationId, mBuilder.build());
	}
	/**
	 * parse message that should be shown inNotification UI
	 * @param jsonMessage
	 * @return
	 */
	private String getPushMessage(String jsonMessage){
		try {
			 JSONObject	jsonPush = new JSONObject(jsonMessage);
			return jsonPush.optString(App42RichPush.Alert.getValue());
		} catch (JSONException e) {
			return jsonMessage;
		}
	}
	/**
     * 
     */
	public static void resetMsgCount() {
		msgCount = 0;
	}

	/**
	 * @param message
	 */
	public void broadCastMessage(String message) {
		Intent intent = new Intent(DisplayMessageAction);
		intent.putExtra(ExtraMessage, message);
		this.sendBroadcast(intent);
	}

	/**
	 * @return
	 */
	private String getActivityName() {
		ApplicationInfo ai;
		try {
			ai = this.getPackageManager().getApplicationInfo(
					this.getPackageName(), PackageManager.GET_META_DATA);
			Bundle aBundle = ai.metaData;
			return aBundle.getString("onMessageOpen");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "MainActivity";
		}
	}
	
}
