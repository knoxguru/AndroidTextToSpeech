package com.knoxguru.texttospeech;

import com.knoxguru.texttospeech.Receivers.SMSReceiver;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	public static boolean SERVICE_STATUS = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		View view = getWindow().getDecorView().findViewById(android.R.id.content);


		enableBroadcastReceiver(view);
		
		Button b = (Button) findViewById(R.id.button1);

		if (SERVICE_STATUS == false)
			b.setText("Start Service");
		
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Button b = (Button) v.findViewById(R.id.button1);
				if (b.getText().toString() == "End Service") {
					disableBroadcastReceiver(v);
					b.setText("Start Service");
				} else {
					enableBroadcastReceiver(v);
					b.setText("End Service");
				
				}
				//finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	public void enableBroadcastReceiver(View view) {
		SERVICE_STATUS = true;
		ComponentName receiver = new ComponentName(this, SMSReceiver.class);
	    PackageManager pm = this.getPackageManager();

	    pm.setComponentEnabledSetting(receiver,
	            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
	            PackageManager.DONT_KILL_APP);
	}
	public void disableBroadcastReceiver(View view){
		SERVICE_STATUS = false;
	    ComponentName receiver = new ComponentName(this, SMSReceiver.class);
	    PackageManager pm = this.getPackageManager();
	        pm.setComponentEnabledSetting(receiver,
	            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
	            PackageManager.DONT_KILL_APP);
	   }}
