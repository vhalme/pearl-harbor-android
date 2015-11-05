package gfx.graphics;

import android.content.Context;

import gfx.buffer.ScreenBuffer;

/**
 * Created by vhalme on 02/11/15.
 */
public abstract class ScreenGraphics extends ScreenBuffer {

    public ScreenGraphics(Context context) {
        super(context);
    }

    public void putPixel(int color, int x, int y) {
        pixels[y * 640 + x] = color;
    }

    public void drawSprite(Sprite s, int x, int y) {
        drawSprite(s, x, y, -1);
    }

    public void drawSprite(Sprite s, int x, int y, int flip) {

        //System.out.println("draSprite1 "+x+","+y);
        int w = 640;
        int h = 350;
        s.isDrawn();
        int bmp[] = s.getPixels();
        int sx = x;
        int cx = x;
        int sy = y;
        int bmpsx = 0;
        int bmpsy = 0;
        int bmpex = s.getWidth();
        int bmpey = s.getHeight();
        int bmpi = 0;
        int scri = 0;
        int transp = s.getTransparentColor();
        boolean alpha = s.isAlphaBlended();

        if (y < 0) {
            bmpsy += -y;
            sy = 0;
        }

        if (y + bmpey >= h)
            bmpey -= (y + bmpey) - h;
        if (x < 0) {
            bmpsx += -x;
            cx = 0;
            sx = 0;
        }

        if (x + bmpex >= w)
            bmpex -= (x + bmpex) - w;

        for (int i = bmpsy; i < bmpey; i++) {
            sx = cx;
            for (int j = bmpsx; j < bmpex; j++) {
                scri = sy * w + sx;
                if (flip == 0)
                    bmpi = (i * s.getWidth() + s.getWidth()) - 1 - j;
                else if (flip == 1)
                    bmpi = (s.getHeight() - 1 - i) * s.getWidth() + j;
                else if (flip == 2)
                    bmpi = ((s.getHeight() - 1 - i) * s.getWidth() + s.getWidth()) - 1 - j;
                else
                    bmpi = i * s.getWidth() + j;

                //System.out.print(bmp[bmpi]+"/"+transp+" ");
                if (bmp[bmpi] != transp)
                    if (alpha)
                        //putPixel(((pixels[scri] & 0xf0f0f0f0) >>> 1) + (bmp[bmpi] & 0xfefefefe) >>> 1, sx, sy);
                        pixels[scri] = ((pixels[scri] & 0xf0f0f0f0) >>> 1) + (bmp[bmpi] & 0xfefefefe) >>> 1;
                    else
                        //putPixel(bmp[bmpi], sx, sy);
                        pixels[scri] = bmp[bmpi];
                sx++;
            }

            sy++;

        }

    }

    public void drawSprite(Sprite s, int x, int y, int sw, int sh, int flip) {

        //System.out.println("draSprite2 "+x+","+y);

        s.isDrawn();
        int w = getWidth();
        int h = getHeight();
        float stepw = (float)s.getWidth() / (float)sw;
        float steph = (float)s.getHeight() / (float)sh;
        int bmp[] = s.getPixels();
        int sx = x;
        int cx = x;
        int sy = y;
        int bmpsx = 0;
        int bmpsy = 0;
        int bmpex = sw;
        int bmpey = sh;
        int bmpi = 0;
        int scri = 0;
        int transp = s.getTransparentColor();
        boolean alpha = s.isAlphaBlended();
        float bmptx = 0.0F;
        float bmpty = 0.0F;

        if (y < 0) {
            bmpsy += -y;
            sy = 0;
        }

        if (y + bmpey >= h)
            bmpey -= (y + bmpey) - h;

        if (x < 0) {
            bmpsx += -x;
            cx = 0;
            sx = 0;
        }

        if (x + bmpex >= w)
            bmpex -= (x + bmpex) - w;

        for (int i = bmpsy; i < bmpey; i++) {

            sx = cx;
            bmptx = 0.0F;

            for (int j = bmpsx; j < bmpex; j++) {

                scri = sy * w + sx;
                if (flip == 0)
                    bmpi = ((int)bmpty * s.getWidth() + s.getWidth()) - 1 - (int)bmptx;
                else if (flip == 1)
                    bmpi = (s.getHeight() - 1 - (int)bmpty) * s.getWidth() + (int)bmptx;
                else if (flip == 2)
                    bmpi = ((s.getHeight() - 1 - (int)bmpty) * s.getWidth() + s.getWidth()) - 1 - (int)bmptx;
                else
                    bmpi = (int)bmpty * s.getWidth() + (int)bmptx;
                if (bmp[bmpi] != transp)
                    if (alpha)
                        //putPixel(((pixels[scri] & 0xf0f0f0f0) >>> 1) + (bmp[bmpi] & 0xfefefefe) >>> 1, sx, sy);
                        pixels[scri] = ((pixels[scri] & 0xf0f0f0f0) >>> 1) + (bmp[bmpi] & 0xfefefefe) >>> 1;
                    else
                        //putPixel(bmp[bmpi], sx, sy);
                        pixels[scri] = bmp[bmpi];
                bmptx += stepw;
                sx++;
            }

            bmpty += steph;
            sy++;

        }

    }


}
