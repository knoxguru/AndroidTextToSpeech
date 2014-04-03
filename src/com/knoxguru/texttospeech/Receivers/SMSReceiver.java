package com.knoxguru.texttospeech.Receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;


public class SMSReceiver extends BroadcastReceiver {

	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	public static boolean START_STICKY = false;
	private Intent mIntent;
	private String txtText ="";
	

	@Override
	public void onReceive(Context context, Intent intent) {

		mIntent = intent;

		String action = intent.getAction();

		if (action.equals(ACTION_SMS_RECEIVED)) {

			SmsMessage[] msgs = getMessagesFromIntent(mIntent);
			if (msgs != null) {
				for (int i = 0; i < msgs.length; i++) {
					String l = msgs[i].getMessageBody().toString();
					if (l != null || l != "null") 
						txtText += msgs[i].getMessageBody().toString() + "\n";
				}
			} else {
				txtText = "Could not get message to read";
			}
			
			Intent i = new Intent();
			i.setClassName("com.knoxguru.texttospeech", "com.knoxguru.texttospeech.ReadActivity");
			i.setAction("SMS_RECEIVED_ACTION");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			i.putExtra("sms", txtText);
			context.startActivity(i);
			
		}

	}

	public static SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
		byte[][] pduObjs = new byte[messages.length][];

		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}
		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return msgs;
	}

}
