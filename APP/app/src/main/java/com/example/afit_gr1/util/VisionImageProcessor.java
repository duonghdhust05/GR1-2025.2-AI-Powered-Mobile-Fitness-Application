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

package com.example.afit_gr1.util;

import androidx.camera.core.ImageProxy;

import com.example.afit_gr1.views.graphic.GraphicOverlay;
import com.google.mlkit.common.MlKitException;


/**
 * An interface to process the images with different vision detectors and custom image models.
 * 
 * **Meaning & Role**: This interface defines the standard contract for any class that processes 
 * live camera frames using ML Kit. It ensures uniformity across different types of detectors (e.g., Pose, Face).
 * 
 * **Coupling**: 
 * - High coupling with AndroidX CameraX (`ImageProxy`) and ML Kit (`MlKitException`).
 * - High coupling with the UI layer (`GraphicOverlay`) to draw detection results.
 * 
 * **Cohesion**: 
 * - High cohesion. Its sole responsibility is defining the boundary for image processing pipelines.
 */
public interface VisionImageProcessor {

    /**
     * Processes ImageProxy image data, e.g., used for CameraX live preview case.
     * 
     * **Workflow**: 
     * 1. Receives an `ImageProxy` containing the raw camera frame.
     * 2. The implementing class (like `PoseDetectorProcessor`) converts this frame to an ML Kit `InputImage`.
     * 3. Passes the image through the ML Model.
     * 4. Updates the `GraphicOverlay` with the processed results to render UI elements (like skeleton joints).
     */
    void processImageProxy(ImageProxy image, GraphicOverlay graphicOverlay) throws MlKitException;

    /**
     * Stops the underlying machine learning model and releases resources.
     * 
     * **Workflow**: Called during the Activity/Fragment's `onDestroy` or when the camera stops 
     * to prevent memory leaks and free up hardware ML accelerators.
     */
    void stop();
}
