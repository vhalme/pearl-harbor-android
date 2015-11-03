package com.lenin.pearlharbor;

import android.graphics.Color;

import gfx.graphics.Surface;

public class BlurSurface extends Surface
{

    public BlurSurface(int width, int height)
    {
        super(width, height);
    }

    public void blur()
    {
        for(int i = 1; i < super.height - 1; i++)
        {
            for(int j = 1; j < super.width - 1; j++)
            {
                int r1 = super.pixels[(i - 1) * super.width + j] >> 16 & 0xff;
                int g1 = super.pixels[(i - 1) * super.width + j] >> 8 & 0xff;
                int b1 = super.pixels[(i - 1) * super.width + j] & 0xff;
                int r2 = super.pixels[(i + 1) * super.width + j] >> 16 & 0xff;
                int g2 = super.pixels[(i + 1) * super.width + j] >> 8 & 0xff;
                int b2 = super.pixels[(i + 1) * super.width + j] & 0xff;
                int r3 = super.pixels[(i * super.width + j) - 1] >> 16 & 0xff;
                int g3 = super.pixels[(i * super.width + j) - 1] >> 8 & 0xff;
                int b3 = super.pixels[(i * super.width + j) - 1] & 0xff;
                int r4 = super.pixels[i * super.width + j + 1] >> 16 & 0xff;
                int g4 = super.pixels[i * super.width + j + 1] >> 8 & 0xff;
                int b4 = super.pixels[i * super.width + j + 1] & 0xff;
                int r5 = super.pixels[((i - 1) * super.width + j) - 1] >> 16 & 0xff;
                int g5 = super.pixels[((i - 1) * super.width + j) - 1] >> 8 & 0xff;
                int b5 = super.pixels[((i - 1) * super.width + j) - 1] & 0xff;
                int r6 = super.pixels[(i - 1) * super.width + j + 1] >> 16 & 0xff;
                int g6 = super.pixels[(i - 1) * super.width + j + 1] >> 8 & 0xff;
                int b6 = super.pixels[(i - 1) * super.width + j + 1] & 0xff;
                int r7 = super.pixels[((i + 1) * super.width + j) - 1] >> 16 & 0xff;
                int g7 = super.pixels[((i + 1) * super.width + j) - 1] >> 8 & 0xff;
                int b7 = super.pixels[((i + 1) * super.width + j) - 1] & 0xff;
                int r8 = super.pixels[(i + 1) * super.width + j + 1] >> 16 & 0xff;
                int g8 = super.pixels[(i + 1) * super.width + j + 1] >> 8 & 0xff;
                int b8 = super.pixels[(i + 1) * super.width + j + 1] & 0xff;
                int r = r1 + r2 + r3 + r4 + r5 + r6 + r7 + r8 >> 3;
                int g = g1 + g2 + g3 + g4 + g5 + g6 + g7 + g8 >> 3;
                int b = b1 + b2 + b3 + b4 + b5 + b6 + b7 + b8 >> 3;
                super.pixels[i * super.width + j] = Color.GREEN; //0 | r << 16 | g << 8 | b;
            }

        }

    }
}

