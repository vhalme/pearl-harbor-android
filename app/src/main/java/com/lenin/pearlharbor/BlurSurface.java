package com.lenin.pearlharbor;

import android.graphics.Color;

import gfx.graphics.Surface;

public class BlurSurface extends Surface {

    public BlurSurface(int width, int height) {
        super(width, height);
    }

    public void blur() {

        int gray = Color.GRAY;
        int cr = gray >> 16 & 0xff;
        int cg = gray >> 8 & 0xff;
        int cb = gray & 0xff;
        int regray = 0xff000000 | cr << 16 | cg << 8 | cb;

        //System.out.println(gray+" => "+cr+","+cg+","+cb+" => "+regray);

        for(int i = 1; i < height - 1; i++) {
            for(int j = 1; j < width - 1; j++) {
                int r0 = (pixels[i * width + j] >> 16) & 0xff;
                int g0 = (pixels[i * width + j] >> 8) & 0xff;
                int b0 = (pixels[i * width + j]) & 0xff;
                int r1 = (pixels[(i - 1) * width + j] >> 16) & 0xff;
                int g1 = (pixels[(i - 1) * width + j] >> 8) & 0xff;
                int b1 = (pixels[(i - 1) * width + j]) & 0xff;
                int r2 = (pixels[(i + 1) * width + j] >> 16) & 0xff;
                int g2 = (pixels[(i + 1) * width + j] >> 8) & 0xff;
                int b2 = (pixels[(i + 1) * width + j]) & 0xff;
                int r3 = (pixels[(i * width + j) - 1] >> 16) & 0xff;
                int g3 = (pixels[(i * width + j) - 1] >> 8) & 0xff;
                int b3 = (pixels[(i * width + j) - 1]) & 0xff;
                int r4 = (pixels[i * width + j + 1] >> 16) & 0xff;
                int g4 = (pixels[i * width + j + 1] >> 8) & 0xff;
                int b4 = (pixels[i * width + j + 1]) & 0xff;
                int r5 = (pixels[((i - 1) * width + j) - 1] >> 16) & 0xff;
                int g5 = (pixels[((i - 1) * width + j) - 1] >> 8) & 0xff;
                int b5 = (pixels[((i - 1) * width + j) - 1]) & 0xff;
                int r6 = (pixels[(i - 1) * width + j + 1] >> 16) & 0xff;
                int g6 = (pixels[(i - 1) * width + j + 1] >> 8) & 0xff;
                int b6 = (pixels[(i - 1) * width + j + 1]) & 0xff;
                int r7 = (pixels[((i + 1) * width + j) - 1] >> 16) & 0xff;
                int g7 = (pixels[((i + 1) * width + j) - 1] >> 8) & 0xff;
                int b7 = (pixels[((i + 1) * width + j) - 1]) & 0xff;
                int r8 = (pixels[(i + 1) * width + j + 1] >> 16) & 0xff;
                int g8 = (pixels[(i + 1) * width + j + 1] >> 8) & 0xff;
                int b8 = (pixels[(i + 1) * width + j + 1]) & 0xff;
                int r = r1 + r2 + r3 + r4 + r5 + r6 + r7 + r8 >> 3;
                int g = g1 + g2 + g3 + g4 + g5 + g6 + g7 + g8 >> 3;
                int b = b1 + b2 + b3 + b4 + b5 + b6 + b7 + b8 >> 3;
                //System.out.println(r1+","+g1+","+b1);
                pixels[i * width + j] = 0xff000000 | r << 16 | g << 8 | b;
            }

        }

    }
}

