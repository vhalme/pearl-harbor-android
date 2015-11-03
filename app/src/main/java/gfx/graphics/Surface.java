package gfx.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Surface {

    public int width;
    public int height;
    protected final int[] pixels;
    private int tcol = -1;
    private double alpha = 0.0;

    public Surface(int width, int height) {

        this.width = width;
        this.height = height;
        pixels = new int[width * height];
        for(int i = 0; i < pixels.length; i++)
            pixels[i] = 0;

    }

    public Surface(int[] pixels, int width, int height) {

        this.width = width;
        this.height = height;
        this.pixels = pixels;

    }

    public Surface(Bitmap bitmap) {

        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixelValues(int[] pixels) {

        for(int i = 0; i < pixels.length; i++)
            this.pixels[i] = pixels[i];

    }

    public void fetchPixels(int[] src, int srcw, int x, int y) {

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++)
                pixels[i * width + j] = src[(y + i) * srcw + x + j];

        }

    }

    public void clear(int color) {
        for(int i = 0; i < pixels.length; i++)
            pixels[i] = color;
    }

    public void setTransparentColor(int tcol) {
        this.tcol = tcol;
    }

    public void setAlphaBlending(double alpha) {
        this.alpha = alpha;
    }

    public void putPixel(int x, int y, int color) {
        pixels[y * width + x] = color;
    }

    public void draw(Surface s, int x, int y, int w, int h, boolean t, int mode) {

        if(x > width || y > height) {
            //System.out.println("Surface not visible: " + x + " > " + width + " || " + y + " > " + height);
            return;
        }

        int sx = 0;
        int sy = 0;
        int sw = w;
        int sh = h;
        int dsx = x;
        int dsy = y;

        if (x < 0) {
            sx = -x;
            sw += x;
            dsx = 0;
        }

        if (y < 0) {
            sy = -y;
            sh += y;
            dsy = 0;
        }

        sw = dsx + sw <= width ? sw : width - dsx;
        sh = dsy + sh <= height ? sh : height - dsy;

        int iscr = 0;
        int ibmp = 0;

        //System.out.println("\nDraw surface [" + sw + "x" + sh + "] @ (" + dsx + "," + dsy + ")");

        for (int i = 0; i < sh; i++) {

            for (int j = 0; j < sw; j++) {

                iscr = (dsy + i) * width + (dsx + j);
                if (mode == 0)
                    ibmp = (sy + i) * w + (sx + j);
                else if (mode == 1)
                    ibmp = ((sy + sh) - 1 - i) * w + (sx + j);
                if (s.getPixels()[ibmp] != s.tcol)
                    if (s.alpha == 0.0D) {
                        //System.out.print("Y(" + (dsx + j) + "," + (dsy + i) + ") ");
                        pixels[iscr] = t ? ((pixels[iscr] & 0xf0f0f0f0) >>> 1) + ((s.getPixels()[ibmp] & 0xfefefefe) >>> 1) : s.getPixels()[ibmp];
                    } else {
                        int r0 = pixels[iscr] >> 16 & 0xff;
                        int g0 = pixels[iscr] >> 8 & 0xff;
                        int b0 = pixels[iscr] & 0xff;
                        int r1 = s.getPixels()[ibmp] >> 16 & 0xff;
                        int g1 = s.getPixels()[ibmp] >> 8 & 0xff;
                        int b1 = s.getPixels()[ibmp] & 0xff;
                        int r = (int)((double)r0 * s.alpha + (double)r1 * (1.0D - s.alpha));
                        int g = (int)((double)g0 * s.alpha + (double)g1 * (1.0D - s.alpha));
                        int b = (int)((double)b0 * s.alpha + (double)b1 * (1.0D - s.alpha));
                        //System.out.print("G(" + (dsx + j) + "," + (dsy + i) + ")");
                        pixels[iscr] = 0 | r << 16 | g << 8 | b;
                    }
            }

        }

    }


}