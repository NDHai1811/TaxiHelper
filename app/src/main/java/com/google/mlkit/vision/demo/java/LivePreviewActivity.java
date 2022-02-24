/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.vision.face.Face;
import com.google.mlkit.vision.demo.CameraSource;
import com.google.mlkit.vision.demo.CameraSourcePreview;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.java.facedetector.FaceDetectorProcessor;
import com.google.mlkit.vision.demo.map.MapsFragment;
import com.google.mlkit.vision.demo.preference.PreferenceUtils;
import com.google.mlkit.vision.demo.preference.SettingsActivity;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback,
        OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {
  private static final String FACE_DETECTION = "Face Detection";

  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = FACE_DETECTION;
  public StringBuilder stFromFm;

  Handler handler = new Handler();
  Runnable runnable;
  int delay = 10;
  FragmentManager fm;
  private boolean fmAppear = false;
  private Fragment fragment;
  private MapsFragment mapsFragment;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getSupportActionBar().hide();
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_vision_live_preview);

    preview = findViewById(R.id.preview_view);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    cdText = findViewById(R.id.countdown_text);
    cdBtn = findViewById(R.id.countdown_btn);
    blink = findViewById(R.id.blinkCount);
    ms = findViewById(R.id.millisec);
    fm = getSupportFragmentManager();


    Button exitbtn = (Button) findViewById(R.id.exitBtn);
    exitbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });

    cdBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startStop();
      }
    });

    updateTimer();

    ToggleButton facingSwitch = findViewById(R.id.facing_switch);

//    facingSwitch.setOnCheckedChangeListener(this);
    FragmentTransaction ft_add = fm.beginTransaction();
    ft_add.add(R.id.frame_layout, new MapsFragment(), "fragment1");
    ft_add.commit();
    fmAppear=true;

    Fragment fragment = fm.findFragmentById(R.id.frame_layout);
    FragmentTransaction ft_remo = fm.beginTransaction();
    if (fragment!=null){
      ft_remo.hide(fragment);
    }
    ft_remo.commit();

    facingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          Fragment fragment = fm.findFragmentById(R.id.frame_layout);
          FragmentTransaction ft_remo = fm.beginTransaction();
          ft_remo.hide(fragment);
          ft_remo.commit();
        } else {
          Fragment fragment = fm.findFragmentById(R.id.frame_layout);
          FragmentTransaction ft_remo = fm.beginTransaction();
          ft_remo.show(fragment);
          ft_remo.commit();
        }
      }
    });

    ImageView settingsButton = findViewById(R.id.settings_button);
    settingsButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
          intent.putExtra(
              SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
          startActivity(intent);
        });

    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    } else {
      getRuntimePermissions();
    }
  }

  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent.getItemAtPosition(pos).toString();
    Log.d(TAG, "Selected model: " + selectedModel);
    preview.stop();
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
      startCameraSource();
    } else {
      getRuntimePermissions();
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      }
    }
    preview.stop();
    startCameraSource();
  }

  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
      switch (model) {
        case FACE_DETECTION:
          Log.i(TAG, "Using Face Detector Processor");
          FaceDetectorOptions faceDetectorOptions =
              PreferenceUtils.getFaceDetectorOptionsForLivePreview(this);
          FaceDetectorProcessor processor = new FaceDetectorProcessor(this, faceDetectorOptions);
          cameraSource.setMachineLearningFrameProcessor(processor);
          handler.postDelayed(runnable = new Runnable() {
            public void run() {
              handler.postDelayed(runnable, delay);
              getData(processor);
            }
          }, delay);
          break;
        default:
          Log.e(TAG, "Unknown model: " + model);
      }
    } catch (RuntimeException e) {
      Log.e(TAG, "Can not create image processor: " + model, e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createCameraSource(selectedModel);
    startCameraSource();
  }


  double data;
  float left, right;
  public void getData(FaceDetectorProcessor processor) {
    data = processor.look;
    left = processor.left;
    right = processor.right;
//    Toast.makeText(this, "Here is data"+lOrR, Toast.LENGTH_SHORT).show();
    Log.v("ddd", "OK "+data);
    Log.v("ddd", "eye "+value);
    sleepDetection();
  }
  int blinkCount=0, counter=0;
  long start, end, time;
  boolean blinkyet = false;
  float value;
  private final float OPEN_THRESHOLD = (float) 0.85;
  private final float CLOSE_THRESHOLD = (float) 0.05;

  private int state = 0;

  public void sleepDetection(){
    if(data<-30){
      Log.v("ddd", "You're looking right ");
      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
    }
    if(data>30){
      Log.v("ddd", "You're looking left ");
      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    if ((left == Face.UNCOMPUTED_PROBABILITY) ||
            (right == Face.UNCOMPUTED_PROBABILITY)) {
      // One of the eyes was not detected.
      return;
    }
    switch (state) {
      case 0:
        if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
          // Both eyes are initially open
          Log.d("BlinkTracker", "Both eyes are initially open");
          state = 1;
        }
        break;

      case 1:
        if ((left < CLOSE_THRESHOLD) && (right < CLOSE_THRESHOLD)) {
          // Both eyes become closed
          Log.d("BlinkTracker", "Both eyes become closed");
          start = System.nanoTime();
          state = 2;
        }
        break;

      case 2:
        if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
          // Both eyes are open again
          Log.d("BlinkTracker", "Both eyes are open again");
          end = System.nanoTime();
          time = ((end - start) / 1000000);
          state = 0;
        }
        break;
    }



    value = Math.min(left, right);
    ms.setText(""+value);
    blink.setText(""+time+"ms");

  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }

  private String[] getRequiredPermissions() {
    try {
      PackageInfo info =
          this.getPackageManager()
              .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      if (ps != null && ps.length > 0) {
        return ps;
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      return new String[0];
    }
  }

  private boolean allPermissionsGranted() {
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        return false;
      }
    }
    return true;
  }

  private void getRuntimePermissions() {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(
          this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String[] permissions, int[] grantResults) {
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private static boolean isPermissionGranted(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission granted: " + permission);
      return true;
    }
    Log.i(TAG, "Permission NOT granted: " + permission);
    return false;
  }

  private TextView cdText, blink, ms;
  private Button cdBtn;
  private CountDownTimer countDownTimer;
  private long timerLeftInMilisec = 600000;
  private boolean timerRunning = false;

  public void startStop(){
    if (timerRunning==false){
      startTimer();
    }
    else{
      stopTimer();
    }
  }

  public void startTimer(){
    countDownTimer = new CountDownTimer(timerLeftInMilisec, 1000) {
      @Override
      public void onTick(long l) {
        timerLeftInMilisec = l;
        updateTimer();
      }

      @Override
      public void onFinish() {

      }
    }.start();
    cdBtn.setText("PAUSE");
    timerRunning = true;
  }
  public void stopTimer(){
    countDownTimer.cancel();
    cdBtn.setText("START");
    timerRunning = false;
  }

  public void updateTimer() {
    int minutes = (int) timerLeftInMilisec/60000;
    int seconds = (int) timerLeftInMilisec%60000/1000;
    String timeLeftText;

    timeLeftText = ""+minutes;
    timeLeftText += ":";
    if(seconds<10){
      timeLeftText+="0";
    }

    if (seconds==0){
      blinkCount=0;
    }
    timeLeftText+=seconds;
    cdText.setText(timeLeftText);
  }

}
