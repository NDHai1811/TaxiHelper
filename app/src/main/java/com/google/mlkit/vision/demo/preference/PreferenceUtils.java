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

package com.google.mlkit.vision.demo.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.gms.common.images.Size;
import com.google.common.base.Preconditions;
import com.google.mlkit.vision.demo.CameraSource;
import com.google.mlkit.vision.demo.CameraSource.SizePair;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.java.LivePreviewActivity;
import com.google.mlkit.vision.face.FaceDetectorOptions;

/** Utility class to retrieve shared preferences. */
public class PreferenceUtils {

  private static final int POSE_DETECTOR_PERFORMANCE_MODE_FAST = 1;

  static void saveString(Context context, @StringRes int prefKeyId, @Nullable String value) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putString(context.getString(prefKeyId), value)
        .apply();
  }

  @Nullable
  public static SizePair getCameraPreviewSizePair(Context context, int cameraId) {
    Preconditions.checkArgument(
        cameraId == CameraSource.CAMERA_FACING_BACK
            || cameraId == CameraSource.CAMERA_FACING_FRONT);
    String previewSizePrefKey;
    String pictureSizePrefKey;
    if (cameraId == CameraSource.CAMERA_FACING_BACK) {
      previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size);
      pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size);
    } else {
      previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size);
      pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size);
    }

    try {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      return new SizePair(
          Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)),
          Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)));
    } catch (Exception e) {
      return null;
    }
  }

  public static FaceDetectorOptions getFaceDetectorOptionsForLivePreview(Context context) {
    int landmarkMode =
        getModeTypePreferenceValue(
            context,
            R.string.pref_key_live_preview_face_detection_landmark_mode,
            FaceDetectorOptions.LANDMARK_MODE_NONE);
    int contourMode =
        getModeTypePreferenceValue(
            context,
            R.string.pref_key_live_preview_face_detection_contour_mode,
            FaceDetectorOptions.CONTOUR_MODE_ALL);
    int classificationMode =
        getModeTypePreferenceValue(
            context,
            R.string.pref_key_live_preview_face_detection_classification_mode,
            FaceDetectorOptions.CLASSIFICATION_MODE_NONE);
    int performanceMode =
        getModeTypePreferenceValue(
            context,
            R.string.pref_key_live_preview_face_detection_performance_mode,
            FaceDetectorOptions.PERFORMANCE_MODE_FAST);

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    boolean enableFaceTracking =
        sharedPreferences.getBoolean(
            context.getString(R.string.pref_key_live_preview_face_detection_face_tracking), false);
    float minFaceSize =
        Float.parseFloat(
            sharedPreferences.getString(
                context.getString(R.string.pref_key_live_preview_face_detection_min_face_size),
                "0.1"));


    FaceDetectorOptions.Builder optionsBuilder =
        new FaceDetectorOptions.Builder()
            .setLandmarkMode(landmarkMode)
            .setContourMode(contourMode)
            .setClassificationMode(classificationMode)
            .setPerformanceMode(performanceMode)
            .setMinFaceSize(minFaceSize);
    if (enableFaceTracking) {
      optionsBuilder.enableTracking();
    }
    return optionsBuilder.build();
  }

  /**
   * Mode type preference is backed by {@link android.preference.ListPreference} which only support
   * storing its entry value as string type, so we need to retrieve as string and then convert to
   * integer.
   */
  private static int getModeTypePreferenceValue(
      Context context, @StringRes int prefKeyResId, int defaultValue) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(prefKeyResId);
    return Integer.parseInt(sharedPreferences.getString(prefKey, String.valueOf(defaultValue)));
  }

  public static boolean isCameraLiveViewportEnabled(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
    return sharedPreferences.getBoolean(prefKey, false);
  }

  private PreferenceUtils() {}

  public static int getDuration(Context context){
    int duration =
            getModeTypePreferenceValue(
                    context,
                    R.string.key_duration_update_info,
                    5000);
    Log.d("Handler", "getDuration: "+duration);
    return duration;
  }

  public static float getEyeRatio(Context context){
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    float eyeWidth =
            Float.parseFloat(
                    sharedPreferences.getString(
                            context.getString(R.string.edit_text_preference_1),
                            "0.99"));
    Log.d("Thu", "getFaceDetectorOptionsForLivePreview: "+eyeWidth);
    return eyeWidth;
  }
}
