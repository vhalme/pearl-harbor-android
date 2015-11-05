package com.lenin.pearlharbor;

import android.graphics.Color;

import gfx.graphics.Surface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class SplashSurface extends Surface {

    int status;
    List<Particle> particles;
    List<Particle> expiredParticles;
    int ctr;
    int palette[];
    int sx;
    int sy;
    int maxage;
    double f;

    class Particle {

        public boolean move() {
            if(age < maxage - 1)
                age++;
            x += vx;
            y += vy;
            vy += 0.29999999999999999D * f;
            return (int)x > 0 && (int)x < width - 1 && (int)y > 0 && (int)y < height - 1;
        }

        double x;
        double y;
        double vx;
        double vy;
        int age;

        public Particle() {
            age = 0;
            x = width >> 1;
            y = height - 1;
            vy = -f - Math.random() * 2D * f;
            vx = -0.75D * f + Math.random() * 1.5D * f;
            double vf = 1.0D + Math.random() * 1.0D * f;
            vx *= vf;
            vy *= vf;
        }
    }


    public SplashSurface(int width, int height, int sx, int sy, int ps, int maxage, double f) {

        super(width, height);
        status = 1;
        this.sx = 0;
        this.sy = 0;
        this.maxage = 100;
        this.sx = sx;
        this.sy = sy;
        this.maxage = maxage;
        this.f = f;
        setTransparentColor(0);
        clear(0);
        particles = new ArrayList<Particle>();
        expiredParticles = new ArrayList<Particle>();
        for (int i = 0; i < ps; i++)
            particles.add(new Particle());

        palette = new int[maxage];
        int c = 255;
        double dc = 255D / (double)maxage;
        for (int i = 0; i < maxage; i++) {
            palette[i] = 0xffffffff;
            c = (int)((double)c - dc);
        }

    }

    public boolean splash() {

        clear(0);
        expiredParticles.clear();

        for (Particle p : particles) {

            if (p.move()) {
                int pos = (int)p.y * super.width + (int)p.x;
                pixels[pos] = palette[p.age];
                pixels[pos + 1] = palette[p.age];
                pixels[pos + super.width] = palette[p.age];
                pixels[pos + super.width + 1] = palette[p.age];
            } else {
                expiredParticles.add(p);
            }

        }

        particles.removeAll(expiredParticles);
        return particles.size() > 0;

    }


}

