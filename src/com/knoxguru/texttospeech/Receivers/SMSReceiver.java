package com.knoxguru.texttospeech.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	public static boolean START_STICKY = false;
	private Intent mIntent;
	private String txtText = "";

	@Override
	public void onReceive(Context context, Intent intent) {

		mIntent = intent;

		String action = intent.getAction();

		if (action.equals(ACTION_SMS_RECEIVED)) {

			SmsMessage[] msgs = getMessagesFromIntent(mIntent);
			if (msgs != null) {
				for (int i = 0; i < msgs.length; i++) {
					txtText += "Message From "
							+ getContactName(context, msgs[i]
									.getOriginatingAddress().toString()) + "\n";
					txtText += msgs[i].getMessageBody().toString() + "\n";
				}
			} else {
				txtText = "Could not get message to read";
			}

			Intent i = new Intent();
			i.setClassName("com.knoxguru.texttospeech",
					"com.knoxguru.texttospeech.ReadActivity");
			i.setAction("SMS_RECEIVED_ACTION");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("sms", txtText);
			context.startActivity(i);

		}

	}

	private String getContactName(Context context, String number) {

	    String name = null;

	    String[] projection = new String[] {
	            ContactsContract.PhoneLookup.DISPLAY_NAME,
	            ContactsContract.PhoneLookup._ID};

	    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

	    Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

	    if(cursor != null) {
	        if (cursor.moveToFirst()) {
	            name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
	        } else {
	            return number.replace("", " ").trim();
	        }
	        cursor.close();
	    }
	    return name;
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
