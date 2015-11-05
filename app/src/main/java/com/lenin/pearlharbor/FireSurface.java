package com.lenin.pearlharbor;

import gfx.graphics.Surface;

public class FireSurface extends Surface {

    public FireSurface(int width, int height, int cm[], boolean cscroll, int fx, int fy, int fw, int fh) {

        super(width, height);

        cmp = 0;
        this.fx = fx;
        this.fy = fy;
        this.fw = fw;
        this.fh = fh;
        this.cscroll = cscroll;
        palette = new int[512];
        map = new int[width * height];
        coolmap = new int[width * height];

        for(int i = 0; i < cm.length; i++)
            coolmap[i] = (cm[i] & 0xff) / 5;

        for(int i = 0; i < 128; i++)
            palette[i] = 0xff000000 | i * 2 << 16;

        for(int i = 0; i < 128; i++)
            palette[128 + i] = 0xffff0000 | i * 2 << 8;

        for(int i = 0; i < 255; i++)
            palette[255 + i] = 0xffffff00 | i;

    }

    public void burn() {

        for (int i = 0; i < super.height - 4; i++) {
            for (int j = 0; j < super.width; j++) {
                int index = (i + 1) * super.width + j;
                int cmpi = i + cmp;
                if(cmpi > super.height - 1)
                    cmpi -= super.height;
                int c = coolmap[cmpi * super.width + j];
                if(map[index] - c > 0)
                    map[index] -= c;
                else
                    map[index] = 0;
                map[i * super.width + j] = map[index];
            }

        }

        if (cscroll) {
            cmp++;
            if(cmp > super.height - 1)
                cmp = 0;
        }

        for (int i = 1; i < super.height - 1; i++) {
            for (int j = 1; j < super.width - 1; j++) {
                int r1 = map[(i - 1) * super.width + j];
                int r2 = map[(i + 1) * super.width + j];
                int r3 = map[(i * super.width + j) - 1];
                int r4 = map[i * super.width + j + 1];
                int r5 = map[((i - 1) * super.width + j) - 1];
                int r6 = map[(i - 1) * super.width + j + 1];
                int r7 = map[((i + 1) * super.width + j) - 1];
                int r8 = map[(i + 1) * super.width + j + 1];
                int r = r1 + r2 + r3 + r4 + r5 + r6 + r7 + r8 >> 3;
                map[i * super.width + j] = r;
            }

        }

        for (int i = 0; i < super.width * super.height; i++)
            pixels[i] = palette[map[i]];

        setFire();

    }

    protected void setFire() {
        for (int i = fy; i < fy + fh; i++) {
            for (int j = fx; j < fx + fw; j++)
                map[i * super.width + j] = (int)(Math.random() * 512D);

        }

    }

    int cmp;
    int palette[];
    int map[];
    int coolmap[];
    int fx;
    int fy;
    int fw;
    int fh;
    boolean cscroll;

}

