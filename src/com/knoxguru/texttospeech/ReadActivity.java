package com.knoxguru.texttospeech;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.*;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.os.PowerManager.WakeLock;


public class ReadActivity extends Activity implements OnInitListener {
	
	private TextToSpeech tts;
	private String txtText;
	WakeLock fullWakeLock;
	WakeLock partialWakeLock;
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

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
		
		tts = new TextToSpeech(this, this);
		
		tts.setOnUtteranceProgressListener(new ttsUtteranceListener(this));
		
		Button b = (Button) findViewById(R.id.button2);

		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {	
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
            }  
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) && txtText != null) {
	    	speakOut(txtText);
	    	return false;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	

	private void speakOut(String txt) {
		
		if (txt == null) {
			return;
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		Random r = new Random();
		int i1 = r.nextInt(10000000);
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID"+i1);
        tts.speak(txt, TextToSpeech.QUEUE_ADD, map);
        
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
