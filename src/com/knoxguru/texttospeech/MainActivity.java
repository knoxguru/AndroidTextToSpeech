package com.knoxguru.texttospeech;

import com.knoxguru.texttospeech.Receivers.SMSReceiver;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnCheckedChangeListener {

	public static boolean SERVICE_STATUS;
	public static boolean AUTO_PLAY = false;
	public static final String PREFS_NAME = "ttsPrefsFile";
	SharedPreferences settings;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View view = getWindow().getDecorView().findViewById(android.R.id.content);
		disableBroadcastReceiver(view);
		enableBroadcastReceiver(view);
		settings = getSharedPreferences(PREFS_NAME, 0);
		toggleAutoStop(settings.getBoolean("AUTO_PLAY", false));
		
		Button b = (Button) findViewById(R.id.end_btn);

		if (SERVICE_STATUS == false)
			b.setText("Start Service");

		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Button b = (Button) v.findViewById(R.id.button1);
				if (b.getText().toString() == "End Service") {
					disableBroadcastReceiver(v);
					b.setText("Start Service");
					Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
				} else {
					enableBroadcastReceiver(v);
					b.setText("End Service");
				}
			}
		});

		CheckBox cb = (CheckBox) findViewById(R.id.btn_auto_play);
		cb.setChecked(settings.getBoolean("AUTO_PLAY", false));
		
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
						toggleAutoStop(isChecked);
			}
		});
	}
	
	public void toggleAutoStop(boolean a) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("AUTO_PLAY", a);
		editor.commit();
		Toast.makeText(getApplicationContext(), "Changed Auto Play setting", Toast.LENGTH_SHORT).show();
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
		Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
	}

	public void disableBroadcastReceiver(View view){
		SERVICE_STATUS = false;
		try{
	    ComponentName receiver = new ComponentName(this, SMSReceiver.class);
	    PackageManager pm = this.getPackageManager();
	        pm.setComponentEnabledSetting(receiver,
	            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
	            PackageManager.DONT_KILL_APP);
		} catch(Exception e) {
			return;
		}
	   }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

	}
}
