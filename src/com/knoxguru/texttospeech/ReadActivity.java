package com.knoxguru.texttospeech;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.*;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.PowerManager.WakeLock;


public class ReadActivity extends Activity implements OnInitListener {
	
	private TextToSpeech tts = null;
	private String txtText;
	WakeLock fullWakeLock;
	WakeLock partialWakeLock;
	public static final String PREFS_NAME = "ttsPrefsFile";
	SharedPreferences settings;
	boolean autoPlay = false;
	boolean playing = false;


    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);
		
        tts = new TextToSpeech(this, this);
        tts.setOnUtteranceProgressListener(new ttsUtteranceListener(this));
        
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		        txtText = null;
		    } else {
		        txtText = extras.getString("sms");
		    }
		} else {
		    txtText = (String) savedInstanceState.getSerializable("sms");
		}
		
		settings = getSharedPreferences(PREFS_NAME, 0);
		autoPlay = settings.getBoolean("AUTO_PLAY", false);
		
		if (txtText == null || txtText == "")
				txtText = "Could not get message";

		TextView et = (TextView) findViewById(R.id.display_msg);
		et.setText(txtText);
		
		
		Button b = (Button) findViewById(R.id.play_btn);

		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!playing)
					speakOut(txtText);
			}
		});
			
	}
		
	@Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onInit(int status) {
		
		if (status == TextToSpeech.SUCCESS) {
			 
            int result = tts.setLanguage(Locale.US);
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }  else {
            	// we are ready to play if autoplay is set
            	if (autoPlay == true) {
        			Toast.makeText(getApplicationContext(), "Auto Play Activated", Toast.LENGTH_SHORT).show();
        			speakOut(txtText);
        		}	
            }
        } else {
        	Toast.makeText(this, "TTS Initilization Failed!", Toast.LENGTH_LONG).show();
            Log.e("TTS", "Initilization Failed!");
        }
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
	    if (playing || (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_UP)) {
	    	return super.onKeyDown(keyCode, event);
	    }
	    
	    speakOut(txtText);
	    return true;
	}
	

	private void speakOut(String txt) {
		
		playing = true;
		if (txt.length() > 0) {	
			HashMap<String, String> map = new HashMap<String, String>();
			Random r = new Random();
			int i1 = r.nextInt(10000000);
			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID"+i1);
			
			// for bluetooth enabled devices
			map.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_VOICE_CALL));
			
			tts.speak(txt, TextToSpeech.QUEUE_FLUSH, map);
		}
		playing = false;
		
    }
	
	


	class ttsUtteranceListener extends UtteranceProgressListener {
		
		private ReadActivity ra;
		
		public ttsUtteranceListener(ReadActivity read) {
			ra = read;
		}
	    @Override
	    public void onDone(String utteranceId) {
	    	ra.finish();
	    }

	    @Override
	    public void onError(String utteranceId) {
	    	ra.finish();
	    }

	    @Override
	    public void onStart(String utteranceId) {
	    }    
	}
	
}
