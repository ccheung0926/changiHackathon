/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.projecttango.examples.java.augmentedreality;

import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoPoseData;

import android.content.Context;

import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.RotateAroundAnimation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.ScreenQuad;
import org.rajawali3d.primitives.RectangularPrism;
import org.rajawali3d.renderer.RajawaliRenderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Renderer that implements a basic augmented reality scene using Rajawali.
 * It creates a scene with a background quad taking the whole screen, where the color camera is
 * rendered, and a sphere with the texture of the earth floating ahead of the start position of
 * the Tango device.
 */
public class AugmentedRealityRenderer extends RajawaliRenderer {
    private static final String TAG = AugmentedRealityRenderer.class.getSimpleName();

    // Rajawali texture used to render the Tango color camera.
    private ATexture mTangoCameraTexture;

    // Keeps track of whether the scene camera has been configured.
    private boolean mSceneCameraConfigured;

    public AugmentedRealityRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initScene() {
        // Create a quad covering the whole background and assign a texture to it where the
        // Tango color camera contents will be rendered.
        ScreenQuad backgroundQuad = new ScreenQuad();
        Material tangoCameraMaterial = new Material();
        tangoCameraMaterial.setColorInfluence(0);
        // We need to use Rajawali's {@code StreamingTexture} since it sets up the texture
        // for GL_TEXTURE_EXTERNAL_OES rendering
        mTangoCameraTexture =
                new StreamingTexture("camera", (StreamingTexture.ISurfaceListener) null);
        try {
            tangoCameraMaterial.addTexture(mTangoCameraTexture);
            backgroundQuad.setMaterial(tangoCameraMaterial);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception creating texture for RGB camera contents", e);
        }
        getCurrentScene().addChildAt(backgroundQuad, 0);

        // Add a directional light in an arbitrary direction.
        DirectionalLight light = new DirectionalLight(1, 0.2, -1);
        light.setColor(1, 1, 1);
        light.setPower(0.8f);
        light.setPosition(3, 2, 4);
        getCurrentScene().addLight(light);

        DirectionalLight light2 = new DirectionalLight(1,-5,1);
        light2.setColor(1, 1, 1);
        light2.setPower(1f);
        light2.setPosition(1, 2, 1);
        getCurrentScene().addLight(light2);

        // Create sphere with earth texture and place it in space 3m forward from the origin.
//        mc donald
        Material earthMaterial = new Material();
        try {
            Texture t = new Texture("earth", R.drawable.shop4);
            earthMaterial.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        earthMaterial.setColorInfluence(0);
        earthMaterial.enableLighting(true);
        earthMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        Object3D earth = new RectangularPrism(1,1,1);
        earth.setMaterial(earthMaterial);
        earth.setPosition(1, 2, -13);
        getCurrentScene().addChild(earth);
//terminal arrows
        Material arrowMaterial = new Material();
        try {
            Texture t = new Texture("arrow", R.drawable.arrow2);
            arrowMaterial.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        arrowMaterial.setColorInfluence(0);
        arrowMaterial.enableLighting(true);
        arrowMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        Object3D arrow = new Plane(2,5,1,1);
        arrow.setMaterial(arrowMaterial);
        arrow.setPosition(0,-3, -15);
        arrow.rotate(Vector3.Axis.X,80);
        getCurrentScene().addChild(arrow);

        Material terminal1Material = new Material();
        try {
            Texture t = new Texture("terminal1", R.drawable.terminal123);
            terminal1Material.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        terminal1Material.setColorInfluence(0);
        terminal1Material.enableLighting(true);
        terminal1Material.setDiffuseMethod(new DiffuseMethod.Lambert());
        Object3D terminal1 = new RectangularPrism(2,1,1);
        terminal1.setMaterial(terminal1Material);
        terminal1.setPosition(0, -4, -12);
        getCurrentScene().addChild(terminal1);

//        terminal 4 5 6
        Material arrow3Material = new Material();
        try {
            Texture t = new Texture("arrow3", R.drawable.arrow6);
            arrow3Material.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        arrow3Material.setColorInfluence(0);
        arrow3Material.enableLighting(true);
        arrow3Material.setDiffuseMethod(new DiffuseMethod.Lambert());
        Object3D arrow3 = new Plane(5,3,1,1);
        arrow3.setMaterial(arrow3Material);
        arrow3.setPosition(15,-4, -15);
//        arrow3.rotate(Vector3.Axis.Y,90);
//        arrow3.rotate(Vector3.Axis.X,90);
        getCurrentScene().addChild(arrow3);

        Material bathroomMaterial = new Material();
        try {
            Texture t = new Texture("bathroom", R.drawable.bathroom);
            bathroomMaterial.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        bathroomMaterial.setColorInfluence(0);
        bathroomMaterial.enableLighting(true);
        bathroomMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        Object3D bathroom = new RectangularPrism(2,2,1);
        bathroom.setMaterial(bathroomMaterial);
        bathroom.setPosition(-8, 1, -60);
        getCurrentScene().addChild(bathroom);


//burger king
        Material shop1Material = new Material();
        try {
            Texture t = new Texture("shop1", R.drawable.shop1);
            shop1Material.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        shop1Material.setColorInfluence(0);
        shop1Material.enableLighting(true);
        shop1Material.setDiffuseMethod(new DiffuseMethod.Lambert());
        Object3D shop1 = new RectangularPrism(1,1,1);
        shop1.setMaterial(shop1Material);
        shop1.setPosition(-4, 1, -10);
        getCurrentScene().addChild(shop1);

//        chinese restaurant
        Material shop3Material = new Material();
        try {
            Texture t = new Texture("shop3", R.drawable.shop3);
            shop3Material.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        shop3Material.setColorInfluence(0);
        shop3Material.enableLighting(true);
        shop3Material.setDiffuseMethod(new DiffuseMethod.Lambert());
        Object3D shop3 = new RectangularPrism(2,1,1);
        shop3.setMaterial(shop3Material);
        shop3.setPosition(-1, 1, -8);
        getCurrentScene().addChild(shop3);

//        hello kitty
        Material shop2Material = new Material();
        try {
            Texture t = new Texture("shop2", R.drawable.shop2);
            shop2Material.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        shop2Material.setColorInfluence(0);
        shop2Material.enableLighting(true);
        shop2Material.setDiffuseMethod(new DiffuseMethod.Lambert());
        Object3D shop2 = new RectangularPrism(2,1,1);
        shop2.setMaterial(shop2Material);
        shop2.setPosition(4, 1, -13);
        getCurrentScene().addChild(shop2);

//        hackathon hashtag
//        Material hackathonMaterial = new Material();
//        try {
//            Texture t = new Texture("hackathon", R.drawable.changihackathon);
//            hackathonMaterial.addTexture(t);
//        } catch (ATexture.TextureException e) {
//            Log.e(TAG, "Exception generating earth texture", e);
//        }
//        hackathonMaterial.setColorInfluence(0);
//        hackathonMaterial.enableLighting(true);
//        hackathonMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
//        Object3D hackathon = new RectangularPrism(3,2,1);
//        hackathon.setMaterial(hackathonMaterial);
//        hackathon.setPosition(3, 5, -13);
//        getCurrentScene().addChild(hackathon);



        // Rotate around its Y axis, chinese restuarant rotation
        Animation3D animEarth = new RotateOnAxisAnimation(Vector3.Axis.Y, 0, -180);
        animEarth.setInterpolator(new LinearInterpolator());
        animEarth.setDurationMilliseconds(60000);
        animEarth.setRepeatMode(Animation.RepeatMode.INFINITE);
        animEarth.setTransformable3D(shop3);
        getCurrentScene().registerAnimation(animEarth);
        animEarth.play();

        // Create sphere with moon texture.
//        Material moonMaterial = new Material();
//        try {
//            Texture t = new Texture("moon", R.drawable.moon);
//            moonMaterial.addTexture(t);
//        } catch (ATexture.TextureException e) {
//            Log.e(TAG, "Exception generating moon texture", e);
//        }
//        moonMaterial.setColorInfluence(0);
//        moonMaterial.enableLighting(true);
//        moonMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
//        Object3D moon = new RectangularPrism(2,3,1);
//        moon.setMaterial(moonMaterial);
//        moon.setPosition(0, 0, -1);
//        getCurrentScene().addChild(moon);

//        // Rotate the moon around its Y axis
//        Animation3D animMoon = new RotateOnAxisAnimation(Vector3.Axis.Y, 0, -360);
//        animMoon.setInterpolator(new LinearInterpolator());
//        animMoon.setDurationMilliseconds(60000);
//        animMoon.setRepeatMode(Animation.RepeatMode.INFINITE);
//        animMoon.setTransformable3D(moon);
//        getCurrentScene().registerAnimation(animMoon);
//        animMoon.play();

        // Make the moon orbit around the earth, the first two parameters are the focal point and
//        // periapsis of the orbit.
//        Animation3D translationMoon =  new EllipticalOrbitAnimation3D(new Vector3(0, 0, -5),
//                new Vector3(0, 0, -1), Vector3.getAxisVector(Vector3.Axis.Y), 0,
//                360, EllipticalOrbitAnimation3D.OrbitDirection.COUNTERCLOCKWISE);
//        translationMoon.setDurationMilliseconds(60000);
//        translationMoon.setRepeatMode(Animation.RepeatMode.INFINITE);
//        translationMoon.setTransformable3D(moon);
//        getCurrentScene().registerAnimation(translationMoon);
//        translationMoon.play();
    }

    /**
     * Update the scene camera based on the provided pose in Tango start of service frame.
     * The camera pose should match the pose of the camera color at the time the last rendered RGB
     * frame, which can be retrieved with this.getTimestamp();
     * <p/>
     * NOTE: This must be called from the OpenGL render thread - it is not thread safe.
     */
    public void updateRenderCameraPose(TangoPoseData cameraPose) {
        float[] rotation = cameraPose.getRotationAsFloats();
        float[] translation = cameraPose.getTranslationAsFloats();
//        rotate the logo image
        Quaternion quaternion = new Quaternion(rotation[3], rotation[0], rotation[1], rotation[2]);
        // Conjugating the Quaternion is need because Rajawali uses left handed convention for
        // quaternions.
        getCurrentCamera().setRotation(quaternion.conjugate());
        getCurrentCamera().setPosition(translation[0], translation[1], translation[2]);
    }

    /**
     * It returns the ID currently assigned to the texture where the Tango color camera contents
     * should be rendered.
     * NOTE: This must be called from the OpenGL render thread - it is not thread safe.
     */
    public int getTextureId() {
        return mTangoCameraTexture == null ? -1 : mTangoCameraTexture.getTextureId();
    }

    /**
     * We need to override this method to mark the camera for re-configuration (set proper
     * projection matrix) since it will be reset by Rajawali on surface changes.
     */
    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        mSceneCameraConfigured = false;
    }

    public boolean isSceneCameraConfigured() {
        return mSceneCameraConfigured;
    }

    /**
     * Sets the projection matrix for the scene camera to match the parameters of the color camera,
     * provided by the {@code TangoCameraIntrinsics}.
     */
    public void setProjectionMatrix(float[] matrixFloats) {
        getCurrentCamera().setProjectionMatrix(new Matrix4(matrixFloats));
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xOffsetStep, float yOffsetStep,
                                 int xPixelOffset, int yPixelOffset) {
    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}