package skawafuc.edu.uci.ics.students;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class INF_133Activity extends Activity {

    private TextView mTextViewOrientationLabel;
	private TextView mTextViewOrientationLabel1;
	private TextView mTextViewOrientationLabel2;
	private TextView mTextSoundLabel;
	private SensorManager mSensorManager;
	private SensorEventListener mEventListenerMagnetic;
	private SensorEventListener mEventListenerAccelerometer;
	private float[] lastOrientationValue = new float[3];
	float[] accel = null;
	float[] magnet = null;
	private MediaPlayer mp;
	AssetFileDescriptor afd[] = new AssetFileDescriptor[5];
	int lastPlayedSound = 0;
	
	private void updateUI(){
		
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				mTextViewOrientationLabel.setText("Azimuth: " + lastOrientationValue[0]);
				mTextViewOrientationLabel1.setText("Pitch: " + lastOrientationValue[1]);
				mTextViewOrientationLabel2.setText("Roll: " + lastOrientationValue[2]);
				switch(lastPlayedSound){
					case 0:
						mTextSoundLabel.setText("A!");
						break;
					case 1:
						mTextSoundLabel.setText("B!");
						break;
					case 2:
						mTextSoundLabel.setText("C!");
						break;
					case 3:
						mTextSoundLabel.setText("D!");
						break;
					case 4:
						mTextSoundLabel.setText("E!");
						break;
				}
			}});
	}
	
	synchronized void playAudio(AssetFileDescriptor afd){
		if(mp.isPlaying()){
			return;
		}else{

			try {
				mp.reset();
				mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				Log.d("playAudio:",""+ e + afd.toString());
			} catch (IllegalStateException e) {
				Log.d("playAudio:",""+ e + afd.toString());
			} catch (IOException e) {
				Log.d("playAudio:",""+ e + afd.toString());
			}

		}
	}
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf_133);
        mTextViewOrientationLabel = (TextView) findViewById(R.id.editText1);
        mTextViewOrientationLabel1 = (TextView) findViewById(R.id.editText2);
        mTextViewOrientationLabel2 = (TextView) findViewById(R.id.editText3);
        mTextSoundLabel = (TextView) findViewById(R.id.editText4);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        mp = new MediaPlayer();
        afd[0] = getApplicationContext().getResources().openRawResourceFd(R.raw.a);
        afd[1] = getApplicationContext().getResources().openRawResourceFd(R.raw.b);
        afd[2] = getApplicationContext().getResources().openRawResourceFd(R.raw.c);
        afd[3] = getApplicationContext().getResources().openRawResourceFd(R.raw.d);
        afd[4] = getApplicationContext().getResources().openRawResourceFd(R.raw.e);
        
        
        mEventListenerMagnetic = new SensorEventListener(){

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				
			}
			

			@Override
			public void onSensorChanged(SensorEvent event) {
				magnet = event.values;
			}
        	
        };
        mEventListenerAccelerometer = new SensorEventListener(){

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				
			}
			@Override
			public void onSensorChanged(SensorEvent event) {
				accel = event.values;
				if (accel != null && magnet!= null){
					float R[] = new float[9];
					float I[] = new float[9];
				      boolean success = SensorManager.getRotationMatrix(R, I, accel, magnet);
				      if (success) {
				        float orientation[] = new float[3];
				        SensorManager.getOrientation(R, orientation);
				        lastOrientationValue[0] = orientation[0]; // orientation contains: azimuth, pitch and roll
				        lastOrientationValue[1] = orientation[1]; 
				        lastOrientationValue[2] = orientation[2]; 
				        updateUI();
				        if (orientation[2] < 0 &&orientation[2] > -1){
				        	if (lastPlayedSound != 0)
				        		playAudio(afd[0]);
				        	lastPlayedSound = 0;
				        }else if (orientation[2] < 1 &&orientation[2] > 0){
				        	if (lastPlayedSound != 1)
				        		playAudio(afd[1]);
				        	lastPlayedSound = 1;
				        }else if (orientation[2] < 2 &&orientation[2] > 1){
				        	if (lastPlayedSound != 2)
				        		playAudio(afd[2]);
				        	lastPlayedSound = 2;
				        }else if (orientation[2] < 3 &&orientation[2] > 2){
				        	if (lastPlayedSound != 3)
				        		playAudio(afd[3]);
				        	lastPlayedSound = 3;
				        }else if (orientation[2] < -1 &&orientation[2] > -2){
				        	if (lastPlayedSound != 4)
				        		playAudio(afd[4]);
				        	lastPlayedSound = 4;
				        }
				      }
					
				}
			}
        };
        
        
        
    }
	


	@Override
	public void onResume(){
		super.onResume();
		mSensorManager.registerListener(mEventListenerMagnetic,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerAccelerometer,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onStop(){
		mSensorManager.unregisterListener(mEventListenerMagnetic);
		mSensorManager.unregisterListener(mEventListenerAccelerometer);
		super.onStop();
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inf_133, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
