package com.lenin.pearlharbor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import gfx.buffer.GLRenderer;
import gfx.buffer.ScreenBuffer;
import gfx.scene.Scene;

public class MainActivity extends Activity {

    private PearlHarborScene gameScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameScene = new PearlHarborScene(this);
        setContentView(gameScene);

        //ClearGLSurfaceView glSurfaceView = new ClearGLSurfaceView(this);
        //setContentView(glSurfaceView);
    }

    /*
    public void clearCanvas(View v) {
        gameScene.clear();
    }
    */

}

class ClearGLSurfaceView extends Scene implements Runnable {

    public void start() {}
    public void stop() {}
    public void pause() {}
    public void resume() {}

    public void run() {}

    public void drawToCanvas(Canvas canvas) {

    }

    public ClearGLSurfaceView(Context context) {
        super(context);
        //mRenderer = new ClearRenderer();
        //setRenderer(mRenderer);
    }

    ClearRenderer mRenderer;
}

class ClearRenderer implements GLSurfaceView.Renderer {
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Do nothing special.
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

}