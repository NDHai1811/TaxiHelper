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

package com.google.mlkit.vision.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.Nullable;

/** Graphic instance for rendering inference info (latency, FPS, resolution) in an overlay view. */
public class InferenceInfoGraphic extends GraphicOverlay.Graphic {

  private static final int TEXT_COLOR = Color.WHITE;
  private static final float TEXT_SIZE = 30.0f;

  private final Paint textPaint, stkPaint;
  private final GraphicOverlay overlay;
  private final long frameLatency;
  private final long detectorLatency;
  private final String speed;
  // Only valid when a stream of input images is being processed. Null for single image mode.
  @Nullable private final Integer framesPerSecond;


  public InferenceInfoGraphic(
      GraphicOverlay overlay,
      long frameLatency,
      long detectorLatency,
      @Nullable Integer framesPerSecond,
      String speed) {
    super(overlay);
    this.overlay = overlay;
    this.frameLatency = frameLatency;
    this.detectorLatency = detectorLatency;
    this.framesPerSecond = framesPerSecond;
    this.speed = speed;
    textPaint = new Paint();
    textPaint.setColor(TEXT_COLOR);
    textPaint.setTextSize(TEXT_SIZE);
    postInvalidate();
    stkPaint = new Paint();
    stkPaint.setTextSize(TEXT_SIZE);
    stkPaint.setStyle(Paint.Style.STROKE);
    stkPaint.setStrokeWidth(8);
    stkPaint.setColor(Color.BLACK);
  }

  @Override
  public synchronized void draw(Canvas canvas) {

    float x = TEXT_SIZE * 0.5f;
    float y = TEXT_SIZE * 1.5f;



    //Draw border for text
    canvas.drawText(
            "InputImage size: " + overlay.getImageHeight() + "x" + overlay.getImageWidth(),
            x,
            y,
            stkPaint);

    // Draw FPS (if valid) and inference latency
    if (framesPerSecond != null) {
      canvas.drawText(
              "FPS: " + framesPerSecond + ", Frame latency: " + frameLatency + " ms",
              x,
              y + TEXT_SIZE,
              stkPaint);
      canvas.drawText(
              "Detector latency: " + detectorLatency + " ms", x, y + TEXT_SIZE * 2, stkPaint);
    } else {
      canvas.drawText("Frame latency: " + frameLatency + " ms", x, y + TEXT_SIZE, stkPaint);

    }
    canvas.drawText(
            "Speed: " + speed + " miles/hours", x, y + TEXT_SIZE * 3, stkPaint);


    //Draw text
    canvas.drawText(
        "InputImage size: " + overlay.getImageHeight() + "x" + overlay.getImageWidth(),
        x,
        y,
        textPaint);

    // Draw FPS (if valid) and inference latency
    if (framesPerSecond != null) {
      canvas.drawText(
          "FPS: " + framesPerSecond + ", Frame latency: " + frameLatency + " ms",
          x,
          y + TEXT_SIZE,
          textPaint);
      canvas.drawText(
          "Detector latency: " + detectorLatency + " ms", x, y + TEXT_SIZE * 2, textPaint);
    } else {
      canvas.drawText("Frame latency: " + frameLatency + " ms", x, y + TEXT_SIZE, textPaint);

    }
    canvas.drawText(
            "Speed: " + speed + " miles/hours", x, y + TEXT_SIZE * 3, textPaint);

  }
}
