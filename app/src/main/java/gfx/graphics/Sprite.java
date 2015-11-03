package gfx.graphics;

import android.graphics.Bitmap;

public class Sprite {

    private int[] pxl;
    private int w;
    private int h;
    private int transp;
    private boolean alpha;

    public Sprite(Bitmap bitmap) {

        transp = -1;
        alpha = false;
        w = bitmap.getWidth();
        h = bitmap.getHeight();
        pxl = new int[w * h];

        bitmap.getPixels(pxl, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

    }

    public Sprite(int pxl[], int w, int h) {
        transp = -1;
        alpha = false;
        this.pxl = pxl;
        this.w = w;
        this.h = h;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public void setPixels(int pxl[]) {
        this.pxl = pxl;
    }

    public int[] getPixels() {
        return pxl;
    }

    public void setTransparentColor(int color) {
        transp = color;
    }

    public int getTransparentColor() {
        return transp;
    }

    public void setAlphaBlended(boolean alpha) {
        this.alpha = alpha;
    }

    public boolean isAlphaBlended() {
        return alpha;
    }

    protected void isDrawn() {
    }


}

