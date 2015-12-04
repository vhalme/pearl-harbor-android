package gfx.buffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by vhalme on 02/11/15.
 */

public abstract class ScreenBuffer extends GLSurfaceView implements SurfaceHolder.Callback {

    protected Bitmap bitmap;
    protected int[] pixels;
    protected Context context;
    protected int displayWidth;
    protected int displayHeight;
    protected float scale;

    public ScreenBuffer(Context context) {

        super(context);
        this.context = context;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;

        bitmap = Bitmap.createBitmap(640, 350, conf);
        pixels = new int[640 * 350];

        for (int y = 0; y < 350; y++) {
            for (int x = 0; x < 640; x++) {
                bitmap.setPixel(x, y, Color.BLACK);
            }
        }

        bitmap.setDensity(DisplayMetrics.DENSITY_HIGH);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        getHolder().addCallback(this);

        //setEGLContextClientVersion(2);
        //mRenderer = new ClearRenderer(pixels);
        //setRenderer(mRenderer);

    }

    public void start() {

        System.out.println("setting renderer");

    }

    protected abstract void drawToCanvas(Canvas canvas);

    public int[] getPixels() {
        return pixels;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }


    protected void doDraw(Canvas canvas) {

        //requestRender();

        canvas.drawColor(Color.TRANSPARENT);
        bitmap.setPixels(pixels, 0, 640, 0, 0, 640, 350);
        int bmpHeight = (int)Math.ceil(new Double(displayWidth).doubleValue() / 1.829);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, displayWidth, bmpHeight, false);
        canvas.drawBitmap(scaledBitmap, 0, 0, null);
        drawToCanvas(canvas);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        displayWidth = w;
        displayHeight = h;

        scale = w / 640f;
        System.out.println("size change: " + w + ", " + h + ", " + oldw + ", " + oldh + ", "+scale);

    }


}

class ClearRenderer implements GLSurfaceView.Renderer {

    private int[] pixels;
    public ClearRenderer(int[] pixels) {
        this.pixels = pixels;
    }

    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        // Do nothing special.
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state
    }

    public void onDrawFrame(GL10 gl10) {

        int[] pxl = new int[640 * 350];
        for (int i = 0; i < pxl.length; i++) {
            pxl[i] = Color.GREEN;
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(pxl.length * 4);
        bb.order(ByteOrder.nativeOrder());

        // native buffer
        IntBuffer pixelBuffer = bb.asIntBuffer();

        // push integer array of pixels into buffer
        pixelBuffer.put(pxl);
        pixelBuffer.position(0);

        // bind buffer to texture
        gl10.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, 640, 350, 0,
                GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixelBuffer);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

    }

}
