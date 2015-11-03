package com.lenin.pearlharbor;

import gfx.graphics.Surface;

public class WaterSurface extends Surface
{

    public WaterSurface(int width, int height)
    {
        super(width, height);
        oldsin = 0.0D;
        oldsin2 = 0.0D;
    }

    public void process(int src[])
    {
        double vpf = oldsin2;
        double vf = 0.10000000000000001D;
        int vptr = 0;
        for(int i = 0; i < super.height; i++)
        {
            vpf += vf;
            if(vpf > 6.2800000000000002D)
                vpf -= 6.2800000000000002D;
            vptr = i + (int)(5D * Math.cos(vpf));
            if(vptr > super.height - 1)
                vptr = super.height - 1;
            if(vptr < 0)
                vptr = 0;
            vpf += 0.10000000000000001D;
            int start = 0;
            for(int j = 0; j < super.width; j++)
                if(j + start >= 0 && j + start < super.width)
                    getPixels()[i * super.width + j] = src[vptr * super.width + j + start];

            vf += 0.0050000000000000001D;
        }

        oldsin2 += 0.5D;
        if(oldsin2 > 6.2800000000000002D)
            oldsin2 = oldsin2 - 6.2800000000000002D;
    }

    double oldsin;
    double oldsin2;

}

