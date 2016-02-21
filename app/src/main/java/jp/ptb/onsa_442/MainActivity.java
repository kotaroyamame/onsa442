package jp.ptb.onsa_442;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends Activity {
AdView adView;
private static final String TAG         = "AudioActivity";
private static boolean flg=false;
private AudioTrack audioTrack  = null;
protected short[]           buffer      = null;
protected int               SAMPLERATE  = 44100; // [Hz]
protected static final int  CHANNEL     = 1;     // 1:MONO, 2:STEREO
protected static final int  BITRATE     = 16;    // [bit/sec]

// signal funcion params
static private double amplification = 0.5;  // [0.0, 1.0]
static private double frequency = 442;      // [Hz]
static private double duration = 1.0;       // [sec]

/**
 * ATTENTION: This was auto-generated to implement the App Indexing API.
 * See https://g.co/AppIndexing/AndroidStudio for more information.
 */
private GoogleApiClient client;

@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
  this.adV();
}
@Override
public void onResume(){
  super.onResume();
  adView.resume();
  generateBuffer();
  int bufferSizeInBytes = buffer.length * CHANNEL * BITRATE / 8;
  // cf
  // Log.v(TAG, "length:" + buffer.length);
  // Log.v(TAG, "bufferSize:" + bufferSizeInBytes);

  // create AudioTrack instance
  audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
      SAMPLERATE,  //[Hz]
      AudioFormat.CHANNEL_OUT_MONO,
      AudioFormat.ENCODING_PCM_16BIT, //[bit]
      bufferSizeInBytes, //[byte]
      AudioTrack.MODE_STATIC);
  //	write buffer
  audioTrack.write(buffer, 0, buffer.length);
}
@Override
public void onStart() {
  super.onStart();
}
@Override
protected void onPause() {
  adView.pause();
  super.onPause();

}
@Override
public void onStop() {
  if(buffer != null){
    buffer = null;
  }
  if(audioTrack != null){
    audioTrack.stop();
    audioTrack.release(); // release buffer
    audioTrack = null;
  }
  super.onStop();
}
@Override
public void onDestroy() {
  adView.destroy();
  super.onDestroy();
}
@Override
public boolean onTouchEvent(MotionEvent event) {
  switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
      if(audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING){
        audioTrack.stop();
        audioTrack.reloadStaticData();
        audioTrack.setLoopPoints(0,buffer.length,-1);
        audioTrack.play();
      }else{
        audioTrack.stop();
      }
      break;
    case MotionEvent.ACTION_UP:
//      if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
//        audioTrack.stop();
//      }
      break;
  }
  return true;
}
public void generateBuffer(){
  int SAMPLES = (int) (duration * SAMPLERATE);
  buffer = new short[ SAMPLES * CHANNEL ];
  double signal = 0;
  for (int i = 0; i < SAMPLES; i++) {
    signal = generateSignal(i);
    buffer[i] = (short)( signal * Short.MAX_VALUE );
  }
}
public double generateSignal(int sample){
  double t = (double)(sample) / SAMPLERATE;
  // y = a * sin (2PI * f * t), t = i/fs, 0 <= i < TotalSamples
  return amplification * Math.sin(2.0 * Math.PI * frequency * t);
}

public void adV() {
  //Log.d("add","ad開始ー"+this.getDeviceID(this));
  //AdView mAdView = (AdView) findViewById(R.id.adView);
  adView = new AdView(this);
//        AdRequest adRequest = new AdRequest.Builder().build();
  adView.setAdUnitId("ca-app-pub-3252364606728850/4141912896");
  adView.setAdSize(AdSize.BANNER);

  // 属性 android:id="@+id/mainLayout" が与えられているものとして
  // LinearLayout をルックアップする
  LinearLayout layout = (LinearLayout) findViewById(R.id.adviewWrap);

  // adView を追加する
  layout.addView(adView);
  AdRequest adRequest = new AdRequest.Builder()
      .addTestDevice("B81429079C29DA0EDAB39048BAA7510B")
      .addTestDevice("ECCF7D6F6F576DD5FC18EB07CECD4ED6")
      .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
      .build();

//       adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdOpened() {
//                // 広告オーバーレイに移動する前にアプリの状態を保存する
//                Log.d("add","onAdOPEN");
//            }
//            @Override
//            public void onAdFailedToLoad(int errorCode){
//                Log.d("add","エラーコード"+errorCode);
//
//            }
//        });

  adView.loadAd(adRequest);

}
}
