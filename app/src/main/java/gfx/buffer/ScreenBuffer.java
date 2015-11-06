package gfx.buffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by vhalme on 02/11/15.
 */

public abstract class ScreenBuffer extends SurfaceView implements SurfaceHolder.Callback {

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

    }

    protected abstract void drawToCanvas(Canvas canvas);

    public int[] getPixels() {
        return pixels;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    protected void doDraw(Canvas canvas) {

        //canvas.drawColor(Color.WHITE);
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
