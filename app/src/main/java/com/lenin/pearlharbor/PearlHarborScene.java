package com.lenin.pearlharbor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

import gfx.graphics.Sprite;
import gfx.graphics.Surface;
import gfx.scene.Scene;

/**
 * Created by vhalme on 02/11/15.
 */
public class PearlHarborScene extends Scene implements Runnable {

    private Thread thread;
    private SurfaceHolder holder;
    private boolean keepRunning = false;

    private Paint paint = new Paint();

    private float[] mValuesMagnet      = new float[3];
    private float[] mValuesAccel       = new float[3];
    private float[] mValuesOrientation = new float[3];
    private float[] mRotationMatrix    = new float[9];
    private String test = "N/A";

    Zero zero;
    Sprite plane0;
    Sprite plane1;
    Sprite plane2;
    Sprite pltmp;
    Sprite blimb;
    Sprite ground;
    Surface[] clouds0r;
    Sprite[] ships;
    Sprite[] carriers;
    Sprite arrow0;
    List<Smoke> clouds;
    List<SplashSurface> splashes, expiredSplashes;
    List<Bomb> bombs;
    List<Target> targs;
    List<Carrier> cars;
    Surface tmpsurf;
    BlurSurface tmpsurf3;
    BlurSurface blur;
    WaterSurface water;
    FireSurface[] fires;
    Surface screen;

    boolean kshoot;
    boolean kup;
    boolean kdown;
    boolean kleft;
    boolean kright;
    boolean kbomb;
    boolean appr;
    boolean bpause;
    boolean tank;
    boolean tarmed;
    boolean doTest = false;
    int pos;
    int mwidth;
    int lfs0;
    int lfs1;
    int prcid;
    int points;
    int apst;
    int xsp;
    int ysp;
    int blink;
    int dispx;
    int appdist;
    double blimby;
    long time;

    String nickname = "";

    public PearlHarborScene(Context context) {

        super(context);

        setFocusable(true);
        setFocusableInTouchMode(true);
        
        holder = getHolder();

        pos = 0;
        blimby = 0.0D;
        dispx = 320;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }

    public void start() {

        SensorManager sensorManager = (SensorManager)this.context.getSystemService(this.context.SENSOR_SERVICE);

        final SensorEventListener mEventListener = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                // Handle the events for which we registered
                switch (event.sensor.getType()) {

                    case Sensor.TYPE_ACCELEROMETER:
                        System.arraycopy(event.values, 0, mValuesAccel, 0, 3);
                        break;

                    case Sensor.TYPE_MAGNETIC_FIELD:
                        System.arraycopy(event.values, 0, mValuesMagnet, 0, 3);
                        break;

                }

                SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
                SensorManager.getOrientation(mRotationMatrix, mValuesOrientation);
                test = "results: " + mValuesOrientation[0] +" "+mValuesOrientation[1]+ " "+ mValuesOrientation[2];

            };
        };

        sensorManager.registerListener(mEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        if (thread == null ||    !thread.isAlive()) {
            thread = new Thread(this);
            keepRunning = true;
            thread.start();
        }

    }

    public void run() {

        loadStuff();
        initLevel(0);

        screen = new Surface(getPixels(), 640, 350);
        tmpsurf = new Surface(640, 90);
        tmpsurf3 = new BlurSurface(150, 100);
        blur = new BlurSurface(30, 30);
        water = new WaterSurface(640, 90);

        Canvas canvas = null;
        while (keepRunning) {

            canvas = holder.lockCanvas();
            if (canvas != null) {

                doGameFrame();
                doDraw(canvas);

                try {
                    thread.sleep(10L);
                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }

                holder.unlockCanvasAndPost(canvas);

            }
        }

    }

    public void stop() {
        if (thread.isAlive()) {
            keepRunning = false;
        }
    }

    public void pause() {

    }

    public void resume() {

    }

    private void loadStuff() {

        Bitmap bmp;

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blimb);
        bmp = Bitmap.createScaledBitmap(bmp, 108, 100, false);
        blimb = new Sprite(bmp);
        blimb.setTransparentColor(blimb.getPixels()[0]);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.sea);
        ground = new Sprite(Bitmap.createScaledBitmap(bmp, 640, 90, false));

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow0);
        arrow0 = new Sprite(Bitmap.createScaledBitmap(bmp, 10, 10, false));

        clouds0r = new Surface[5];
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilvi);
        clouds0r[0] = new Surface(Bitmap.createScaledBitmap(bmp, 150, 78, false));
        clouds0r[0].setTransparentColor(clouds0r[0].getPixels()[0]);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilvi2);
        clouds0r[1] = new Surface(Bitmap.createScaledBitmap(bmp, 150, 58, false));
        clouds0r[1].setTransparentColor(clouds0r[1].getPixels()[0]);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilvi3);
        Sprite spcloud3 = new Sprite(bmp);
        clouds0r[2] = new Surface(Bitmap.createScaledBitmap(bmp, 150, 69, false));
        clouds0r[2].setTransparentColor(clouds0r[2].getPixels()[0]);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilvi4);
        clouds0r[3] = new Surface(Bitmap.createScaledBitmap(bmp, 100, 25, false));
        clouds0r[3].setTransparentColor(clouds0r[3].getPixels()[0]);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilvi5);
        clouds0r[4] = new Surface(Bitmap.createScaledBitmap(bmp, 130, 75, false));
        clouds0r[4].setTransparentColor(clouds0r[4].getPixels()[0]);

        Bitmap planeBmp;

        planeBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane0);
        planeBmp = Bitmap.createScaledBitmap(planeBmp, 35, 35, false);
        plane0 = new Sprite(planeBmp);
        plane0.setTransparentColor(plane0.getPixels()[0]);
        pltmp = new Sprite(planeBmp);
        pltmp.setTransparentColor(pltmp.getPixels()[0]);

        planeBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane1);
        planeBmp = Bitmap.createScaledBitmap(planeBmp, 35, 35, false);
        plane1 = new Sprite(planeBmp);
        plane1.setTransparentColor(plane1.getPixels()[0]);

        planeBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane2);
        planeBmp = Bitmap.createScaledBitmap(planeBmp, 35, 35, false);
        plane2 = new Sprite(planeBmp);
        plane2.setTransparentColor(plane2.getPixels()[0]);

        ships = new Sprite[4];
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship0);
        ships[0] = new Sprite(Bitmap.createScaledBitmap(bmp, 105, 40, false));
        ships[0].setTransparentColor(ships[0].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship1);
        ships[1] = new Sprite(Bitmap.createScaledBitmap(bmp, 105, 40, false));
        ships[1].setTransparentColor(ships[1].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship2);
        ships[2] = new Sprite(Bitmap.createScaledBitmap(bmp, 105, 40, false));
        ships[2].setTransparentColor(ships[2].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship3);
        ships[3] = new Sprite(Bitmap.createScaledBitmap(bmp, 105, 40, false));
        ships[3].setTransparentColor(ships[3].getPixels()[0]);

        carriers = new Sprite[4];
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.carrier);
        carriers[0] = new Sprite(Bitmap.createScaledBitmap(bmp, 135, 40, false));
        carriers[0].setTransparentColor(carriers[0].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.carrier_d0);
        carriers[1] = new Sprite(Bitmap.createScaledBitmap(bmp, 135, 40, false));
        carriers[1].setTransparentColor(carriers[1].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.carrier_d1);
        carriers[2] = new Sprite(Bitmap.createScaledBitmap(bmp, 135, 40, false));
        carriers[2].setTransparentColor(carriers[2].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.carrier_d2);
        carriers[3] = new Sprite(Bitmap.createScaledBitmap(bmp, 135, 40, false));
        carriers[3].setTransparentColor(carriers[3].getPixels()[0]);

        fires = new FireSurface[3];
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.coolmap0);
        Sprite cm0 = new Sprite(Bitmap.createScaledBitmap(bmp, 50, 100, false));
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.coolmap1);
        Sprite cm1 = new Sprite(Bitmap.createScaledBitmap(bmp, 50, 100, false));
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.coolmap2);
        Sprite cm2 = new Sprite(Bitmap.createScaledBitmap(bmp, 50, 100, false));
        fires[0] = new FireSurface(50, 100, cm0.getPixels(), true, 20, 90, 10, 10);
        fires[1] = new FireSurface(50, 100, cm1.getPixels(), true, 20, 90, 10, 10);
        fires[2] = new FireSurface(50, 100, cm2.getPixels(), true, 20, 90, 10, 10);
        fires[0].setTransparentColor(0xff000000);
        fires[1].setTransparentColor(0xff000000);
        fires[2].setTransparentColor(0xff000000);

        Bitmap bgBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
        bgBmp = Bitmap.createScaledBitmap(bgBmp, 640, 350, false);

        setBackground(new Sprite(bgBmp).getPixels());

    }

    private void startGame() {

    }

    private void initLevel(int level) {

        apst = -1;
        xsp = -1;
        ysp = -1;
        blink = 0;
        kshoot = false;
        kup = false;
        kdown = false;
        kleft = false;
        kright = false;
        kbomb = false;
        appr = false;
        zero = new Zero();
        clouds = new ArrayList<Smoke>();
        bombs = new ArrayList<Bomb>();
        targs = new ArrayList<Target>();
        cars = new ArrayList<Carrier>();
        splashes = expiredSplashes = new ArrayList<SplashSurface>();

        switch (level) {

            default:
                break;

            case 0:
                lfs0 = 400;
                lfs1 = 250;
                mwidth = 3000;
                Carrier car0 = new Carrier(1000D, 240D, 135D, 30D, carriers);
                Carrier car1 = new Carrier(2000D, 240D, 135D, 30D, carriers);
                Carrier car2 = new Carrier(2800D, 240D, 135D, 30D, carriers);
                targs.add(car0);
                targs.add(car1);
                targs.add(car2);
                cars.add(car0);
                cars.add(car1);
                cars.add(car2);
                targs.add(new Target(500D, 240D, 100D, 30D, ships));
                targs.add(new Target(1500D, 240D, 100D, 30D, ships));
                targs.add(new Target(2300D, 240D, 100D, 30D, ships));
                break;

        }

        for (int i = 0; i < mwidth; i++) {
            if ((int)(Math.random() * 100D) == 10) {
                Smoke s = new Smoke(i, (int)(Math.random() * 150D));
                s.picid = (int)(Math.random() * 5D);
                s.vx = (int)(Math.random() * 4D);
                clouds.add(s);
            }
        }

    }

    private void doGameFrame() {

        blimby += 0.050000000000000003D;

        if (blimby > 6.2800000000000002D)
            blimby = 0.0D;

        //if (mValuesOrientation[0] > -2.5 && zero.vx > -15D && zero.status != 1 && zero.fuel > 0)
        if (kleft && zero.vx > -15D && zero.status != 1 && zero.fuel > 0)
            if (zero.dvy == 0.0D)
                zero.vx -= 0.40000000000000002D;
            else
                zero.vx -= 0.20000000000000001D;

        //if (mValuesOrientation[2] > -0.8 && zero.vy > -4D && zero.status != 1 && zero.fuel > 0) {
        if (kup && zero.vy > -4D && zero.status != 1 && zero.fuel > 0) {
            if (zero.dvy == 0.0D)
                zero.vy -= 0.40000000000000002D;
            else
                zero.vy -= 0.20000000000000001D;
            zero.pos = 1;
        }

        //if (mValuesOrientation[0] < -2.5 && zero.vx < 15D && zero.status != 1 && zero.fuel > 0)
        if (kright && zero.vx < 15D && zero.status != 1 && zero.fuel > 0)
            if (zero.dvy == 0.0D)
                zero.vx += 0.40000000000000002D;
            else
                zero.vx += 0.20000000000000001D;

        //if (mValuesOrientation[2] < -0.8 && zero.status != 1) {
        if (kdown && zero.status != 1) {
            if (zero.dvy == 0.0D)
                zero.vy += 0.40000000000000002D;
            else
                zero.vx += 0.20000000000000001D;
            zero.pos = 2;
        }


        if (kbomb && zero.bs > 0) {
            System.out.println("drop bomb");
            if (zero.vx > 0.0D)
                bombs.add(new Bomb(zero.x + 10D, zero.y + 20D, zero.vx, zero.vy + zero.dvy));
            else
                bombs.add(new Bomb(zero.x + 20D, zero.y + 20D, zero.vx, zero.vy + zero.dvy));
            zero.bs--;
            kbomb = false;
        }

        /*
        if (kshoot)
            if (zero.ams > 0) {

                if(zero.vx > 0.0D)
                    ammos.addElement(new Bomb(zero.x + 25D, zero.y + 15D + zero.vy, zero.vx + 7D, zero.vy + zero.dvy + zero.vy));
                else
                    ammos.addElement(new Bomb(zero.x + 1.0D, zero.y + 17D + zero.vy, zero.vx - 7D, zero.vy + zero.dvy + zero.vy));
                zero.ams--;
            } else if(mg != null)
                mg.stop();
        */

        clear();

        /*
        map.setPixels(spmap.getPixels());
        for (int i = 0; i < 120; i++)
            map.putPixel(200, 5 + i, 55);

        if (zero.status == -1 || zero.status == 0) {

            zero.fuel--;
            if (zero.fuel == lfs0 && lfa != null)
                lfa.play();
            if (zero.fuel == lfs1 && lfa != null)
                lfa.play();

        }

        if (zero.fuel < 0)

            if (zero.vx > 0.0D)
                zero.vx -= 0.01D;
            else
                zero.vx += 0.01D;

        */

        if (zero.y > 243D) {

            if (zero.status < 3) {
                zero.status = 4;
                SplashSurface ss = new SplashSurface(150, 100, (int)(zero.x + 320D - 50), 160, 500, 100, 1.0D);
                ss.setTransparentColor(0);
                splashes.add(ss);
                System.out.println("added splash: " + splashes.size());
                //if (psplash != null)
                //    psplash.play();
            }

            zero.status++;

        }

        if (zero.x < 320D)
            drawSprite(blimb, (int)(-zero.x), 100 + (int)(Math.sin(blimby) * 50D));
        if (zero.x > (double)(mwidth - 640))
            drawSprite(blimb, 320 + (int)((double)mwidth - zero.x), 100 + (int)(Math.sin(blimby) * 50D));


        if (zero.hits == 0)
            zero.dvy = 0.0D;

        pos = - (int)zero.x;

        fires[0].burn();
        fires[1].burn();
        fires[2].burn();

        List<Target> expiredTargets = new ArrayList<Target>();
        List<Carrier> expiredCarriers = new ArrayList<Carrier>();

        for(Target t : targs) {

            //int mtx = 5 + (int) (mapfx * t.x);
            //if (t instanceof Carrier) {
            //    map.putPixel(65280, mtx, 53);
            //    map.putPixel(65280, mtx + 1, 53);
            //} else {
            //    map.putPixel(0xff0000, mtx, 53);
            //    map.putPixel(0xff0000, mtx + 1, 53);
            //}

            if (t.x + t.w > zero.x && t.x < zero.x + 640D) {

                int sinkf = 230;

                if (t.status > -1 && t.status < 80) {
                    sinkf += t.status;
                    t.status++;
                }

                //if (t.status == 20 && bub != null)
                //    bub.play();

                if (t.status >= 80) {

                    expiredTargets.add(t);
                    if (zero.x + 320D > t.x - 150D && zero.x + 320D < t.x + 250D) {
                        if (zero.status < 3)
                            zero.status = -1;
                        appr = false;
                        apst = -1;
                    }

                    if (t instanceof Carrier) {
                        expiredCarriers.add((Carrier) t);
                    }

                } else {

                    drawSprite(t.pic[t.fp], (int) (t.x - zero.x), sinkf - 10);

                    int i = 0;
                    while (i < 3) {

                        if (t.fs[i] != -1)
                            screen.draw(fires[i], ((int) (t.x - zero.x) + t.fs[i]) - 20, sinkf - 77, 50, 100, false, 0);
                        i++;

                    }

                }

            } else if (t.status > -1) {
                expiredTargets.add(t);
            }

        }

        targs.removeAll(expiredTargets);
        cars.removeAll(expiredCarriers);

        if (doTest) {
            test();
        }

        if ((int)(Math.random() * 100D) == 20) {
            Smoke smktmp = new Smoke(mwidth, (int)(Math.random() * 150D));
            smktmp.picid = (int)(Math.random() * 4D);
            clouds.add(smktmp);
        }

        List<Smoke> expiredClouds = new ArrayList<Smoke>();

        for (Smoke cloud: clouds) {

            cloud.x -= cloud.vx;

            if (cloud.x + clouds0r[cloud.picid].width < 0) {
                expiredClouds.add(cloud);
                continue;
            }

            if ((cloud.x + 200) > zero.x && (cloud.x - 200) < zero.x + 640D) {
                int cloudX = (int)(cloud.x - zero.x);
                //System.out.println("Draw cloud type " + cl.picid + " at " + cloudX + "/" + cl.y);
                screen.draw(clouds0r[cloud.picid], cloudX, cloud.y, clouds0r[cloud.picid].width, clouds0r[cloud.picid].height, true, 0);
            }

        }

        clouds.removeAll(expiredClouds);

        expiredSplashes = new ArrayList<SplashSurface>();
        for (SplashSurface ss: splashes) {

            if (ss.splash()) {

                if (ss.width != 30) {
                    tmpsurf3.fetchPixels(screen.getPixels(), 640, (int)((double)ss.sx - zero.x), ss.sy);
                    tmpsurf3.draw(ss, 0, 0, ss.width, ss.height, false, 0);
                    tmpsurf3.blur();
                    screen.draw(tmpsurf3, (int)((double)ss.sx - zero.x), ss.sy, tmpsurf3.width, tmpsurf3.height, false, 0);
                } else {
                    blur.fetchPixels(screen.getPixels(), 640, (int) ((double) ss.sx - zero.x), ss.sy);
                    blur.draw(ss, 0, 0, ((Surface) (ss)).width, ((Surface) (ss)).height, false, 0);
                    blur.blur();
                    screen.draw(blur, (int) ((double) ss.sx - zero.x), ss.sy, 30, 30, false, 0);
                }

            } else {
                expiredSplashes.add(ss);
            }

        }

        splashes.removeAll(expiredSplashes);

        if (zero.x < 0.0D)
            drawSprite(ground, pos % 640 - 640, 260);

        drawSprite(ground, pos % 640, 260);
        drawSprite(ground, pos % 640 + 640, 260);

        drawScene();

        double inc = (zero.vy + zero.dvy) * 0.10000000000000001D;

        if (inc > 0.0D)
            inc = 6.2831853071795862D + inc;
        if (appr)
            inc = 0.0D;

        if (zero.status < 3) {
            Utils.rotate(inc, plane0.getPixels(), pltmp.getPixels(), 35);
            if (zero.vx > 0.0D)
                drawSprite(pltmp, dispx, (int) zero.y, 0);
            else
                drawSprite(pltmp, dispx, (int) zero.y, -1);
        }


        calcBombs();

        /*
        calcAmmos();
        */

        zero.move();

        /*
        enum=ampls.elements();
        do {

            if (! enum.hasMoreElements())
            break;

            Amplane a = (Amplane) enum.nextElement();
            a.move();
            int mtx = 5 + (int) (mapfx * ((Target) (a)).x);
            int mty = 5 + (int) (mapfy * ((Target) (a)).y);
            map.putPixel(0xffffff, mtx, mty);
            map.putPixel(0xffffff, mtx + 1, mty);

            if (((Target) (a)).y > 255D) {

                ampls.removeElement(a);
                int splfix = a.vx <= 0.0D ? -30 : -55;
                SplashSurface ss = new SplashSurface(150, 100, (int) ((Target) (a)).x + splfix, 160, 500, 100, 1.0D);
                ss.setTransparentColor(0);
                splashes.addElement(ss);
                if (psplash != null)
                    psplash.play();

            } else if (((Target) (a)).x + ((Target) (a)).w > zero.x && ((Target) (a)).x < zero.x + 640D) {

                if (Math.abs(zero.y - ((Target) (a)).y) < 40D && zero.status == -1)

                    if (a.vx > 0.0D) {
                        if (zero.x + 320D > ((Target) (a)).x)
                            a.shoot();
                    } else if (zero.x + 320D < ((Target) (a)).x)
                        a.shoot();

                a.adx = (int) (((Target) (a)).x - zero.x);
                a.checkAmmos();

                if (a.vx > 0.0D) {
                    drawSprite(((Target) (a)).pic[((Target) (a)).fp], (int) (((Target) (a)).x - zero.x), (int) ((Target) (a)).y, 0);
                    if (((Target) (a)).d > ((Target) (a)).md)
                        smokes.addElement(new Smoke((int) (((Target) (a)).x + Math.random() * 3D) - 10, (int) (((Target) (a)).y + Math.random() * 3D)));
                } else {
                    drawSprite(((Target) (a)).pic[((Target) (a)).fp], (int) (((Target) (a)).x - zero.x), (int) ((Target) (a)).y, -1);
                    if (((Target) (a)).d >= ((Target) (a)).md)
                        smokes.addElement(new Smoke((int) (((Target) (a)).x + Math.random() * 3D) + 30, (int) (((Target) (a)).y + Math.random() * 3D)));
                }

            }

        } while (true);

        if (zero.dvy > 0.0D)
            if (zero.vx > 0.0D)
                smokes.addElement(new Smoke((int) (320D + zero.x + Math.random() * 3D) - 10, (int) (zero.y + Math.random() * 3D)));
            else
                smokes.addElement(new Smoke((int) (320D + zero.x + Math.random() * 3D) + 30, (int) (zero.y + Math.random() * 3D)));
        if (Math.abs(zero.vx) > 10D && zero.dvy > 0.0D)
            if (zero.vx > 0.0D)
                smokes.addElement(new Smoke((int) (320D + zero.x + Math.random() * 3D) - 20, (int) (zero.y + Math.random() * 3D)));
            else
                smokes.addElement(new Smoke((int) (320D + zero.x + Math.random() * 3D) + 40, (int) (zero.y + Math.random() * 3D)));

        enum=smokes.elements();
        do {

            if (! enum.hasMoreElements())
            break;

            Smoke ss = (Smoke) enum.nextElement();
            ss.age++;

            if (ss.age > 30) {
                smokes.removeElement(ss);
            } else {
                smoke.setAlphaBlending((double) ss.age * 0.033000000000000002D);
                ss.y -= 0.10000000000000001D;
                if (ss.y - 10 >= 0 && (double) ss.x - zero.x - 10D >= 0.0D) {
                    tmpsurf2.fetchPixels(getPixels(), 640, (int) ((double) ss.x - zero.x) - 10, ss.y - 10);
                    tmpsurf2.draw(smoke, 10, 10, 10, 10, false, 0);
                    blur.setPixels(tmpsurf2.getPixels(), 30, 30);
                    blur.blur();
                    screen.draw(blur, (int) ((double) ss.x - zero.x) - 10, ss.y - 10, 30, 30, false, 0);
                }
            }

        } while (true);

        int mtx = 5 + (int) (mapfx * (zero.x + 320D));
        int mty = 5 + (int) (mapfy * zero.y);
        map.putPixel(0xffff00, mtx, mty);
        map.putPixel(0xffff00, mtx + 1, mty);
        */

        tmpsurf.fetchPixels(getPixels(), 640, 0, 170);
        water.process(tmpsurf.getPixels());
        screen.draw(water, 0, 260, 640, 90, true, 1);

        /*
        screen.draw(map, 490, 10, 130, 70, true, 0);


        if (apst > -1) {
            if (zero.y < 40D)
                land0.setAlphaBlended(true);
            else
                land0.setAlphaBlended(false);
            drawSprite(land0, 260, 0);
        }

        if (zero.status != 1) {

            enum=cars.elements();
            do {

                if (! enum.hasMoreElements())
                break;

                Carrier t = (Carrier) enum.nextElement();
                if (t.isApproached())
                    System.out.println("approach: " + zero.vx + "/" + (zero.vy + zero.dvy));

            } while (true);

        } else if (zero.fuel < 2500 || zero.ams < 200 || zero.bs < 50 || zero.hits > 0) {

            tank = false;
            zero.fuel += 25;
            if (zero.fuel > 2500)
                zero.fuel = 2500;
            zero.bs++;
            if (zero.bs > 50)
                zero.bs = 50;
            zero.ams += 5;
            if (zero.ams > 200)
                zero.ams = 200;
            zero.hits--;
            if (zero.hits < 0)
                zero.hits = 0;

        } else if (zero.y < 220D) {
            if (!tank) {
                if (zero.y > 150D) {
                    zero.y--;
                } else {
                    tank = false;
                    zero.status = -1;
                    if (zero.vx < 0.0D)
                        zero.vx = -3D;
                    else
                        zero.vx = 3D;
                }
            } else {
                zero.status = -1;
                if (zero.vx < 0.0D)
                    zero.vx = -6D;
                else
                    zero.vx = 6D;
            }
        } else {
            zero.y--;
        }

        drawSprite(ctrl0, 10, 10);
        if (zero.status > 5) {
            if (zero.status < 7) {
                lfire = new FireSurface(257, 150, l2cool[6].getPixels(), true, 2, 148, 246, 2);
                zero.status = 7;
            }
            lfire.burn();
            screen.draw(lfire, 230, 80, 257, 148, true, 0);
        }

        update(getPixels());

        if (zero.status > 10) {

            String s1 = "Score: " + points;
            String s2 = "GAME OVER!";
            String s3 = "ENTER to submit score & start over.";
            nickname = pnick;
            getGraphicsBuffer().setColor(Color.white);
            FontMetrics fm = getGraphicsBuffer().getFontMetrics(getGraphicsBuffer().getFont());
            getGraphicsBuffer().drawString(s1, 350 - fm.stringWidth(s1) / 2, 100);
            getGraphicsBuffer().drawString(s2, 350 - fm.stringWidth(s2) / 2, 150);
            getGraphicsBuffer().drawString(s3, 350 - fm.stringWidth(s3) / 2, 170);

        }

        Amplane a;
        for (enum=ampls.elements();
        enum.hasMoreElements();
        drawStatus(a))
        a = (Amplane) enum.nextElement();

        enum=targs.elements();
        do {

            if (! enum.hasMoreElements())
            break;

            Target a = (Target) enum.nextElement();
            drawStatus(a);
            if (tarmed)
                if (zero.vx > 0.0D) {

                    if ((int) (a.x - zero.x - 320D) < 250 && (int) (a.x - zero.x - 320D) >= 0) {
                        getGraphicsBuffer().setColor(Color.yellow);
                        getGraphicsBuffer().drawLine(330, (int) zero.y + 20, (int) (a.x - zero.x) + 20, (int) a.y + 10);
                    }

                } else if ((int) (a.x - zero.x - 320D) > -250 && (int) (a.x - zero.x - 320D) <= 0) {
                    getGraphicsBuffer().setColor(Color.yellow);
                    getGraphicsBuffer().drawLine(330, (int) zero.y + 20, (int) (a.x - zero.x) + 20, (int) a.y + 10);
                }

        } while (true);

        getGraphicsBuffer().setFont(fsmall);
        getGraphicsBuffer().setColor(Color.white);
        double av = Math.sqrt(zero.vx * zero.vx + (zero.vy + zero.dvy) * (zero.vy + zero.dvy)) * 30D;
        getGraphicsBuffer().drawString("" + (int) av, 28, 46);

        if (zero.fuel < lfs0)
            getGraphicsBuffer().setColor(Color.red);
        getGraphicsBuffer().drawString("" + zero.fuel, 80, 46);
        if (zero.fuel < lfs0)
            getGraphicsBuffer().setColor(Color.white);
        getGraphicsBuffer().drawString("" + (int) Math.abs(zero.y - 245D), 55, 77);
        getGraphicsBuffer().drawString("" + zero.bs, 140, 57);
        getGraphicsBuffer().drawString("" + zero.ams, 157, 57);
        getGraphicsBuffer().drawString("" + (zero.mhits - zero.hits), 180, 57);
        getGraphicsBuffer().drawString("" + points, 165, 75);
        if (zero.fuel < lfs1) {
            getGraphicsBuffer().setColor(Color.red);
            getGraphicsBuffer().drawString("FUEL LOW!", 300, 100);
        }
        getGraphicsBuffer().setFont(fnorm);
        getGraphicsBuffer().setColor(Color.red);
        double arxsin = Math.sin(3D + 0.0074999999999999997D * av);
        double arxcos = Math.cos(3D + 0.0074999999999999997D * av);
        int arx = (int) (arxsin * 11D);
        int ary = (int) (arxcos * 11D);
        getGraphicsBuffer().drawLine(34, 36, 34 + ary, 36 + arx);
        arxsin = Math.sin(3D + 0.0011999999999999999D * (double) zero.fuel);
        arxcos = Math.cos(3D + 0.0011999999999999999D * (double) zero.fuel);
        arx = (int) (arxsin * 11D);
        ary = (int) (arxcos * 11D);
        getGraphicsBuffer().drawLine(90, 36, 90 + ary, 36 + arx);
        arxsin = Math.sin(3D + 0.012244897959183673D * Math.abs(zero.y - 245D));
        arxcos = Math.cos(3D + 0.012244897959183673D * Math.abs(zero.y - 245D));
        arx = (int) (arxsin * 11D);
        ary = (int) (arxcos * 11D);
        getGraphicsBuffer().drawLine(61, 67, 61 + ary, 67 + arx);
        ary = (int) (0.44D * (double) zero.bs);
        getGraphicsBuffer().drawLine(144, 45 - ary, 147, 45 - ary);
        ary = (int) (0.11D * (double) zero.ams);
        getGraphicsBuffer().drawLine(164, 45 - ary, 167, 45 - ary);
        ary = (int) (1.1000000000000001D * (double) (zero.mhits - zero.hits));
        if (ary < 0)
            ary = 0;
        getGraphicsBuffer().drawLine(184, 45 - ary, 187, 45 - ary);
        if (apst > -1)
            if (appdist != 1000) {
                getGraphicsBuffer().setColor(Color.red);
                getGraphicsBuffer().drawString("" + appdist, 263, 48);
            } else {
                getGraphicsBuffer().setColor(Color.green);
                getGraphicsBuffer().drawString("OK", 263, 48);
            }
        if (apst > 0) {
            xsp = (int) Math.abs(zero.vx * 30D);
            ysp = (int) Math.abs((zero.vy + zero.dvy) * 30D);
            if (xsp < 150) {
                getGraphicsBuffer().setColor(Color.green);
                getGraphicsBuffer().drawString("OK", 295, 48);
            } else {
                getGraphicsBuffer().setColor(Color.red);
                getGraphicsBuffer().drawString("" + xsp, 295, 48);
            }
            if (ysp < 60) {
                getGraphicsBuffer().setColor(Color.green);
                getGraphicsBuffer().drawString("OK", 330, 48);
            } else {
                getGraphicsBuffer().setColor(Color.red);
                getGraphicsBuffer().drawString("" + ysp, 330, 48);
            }
            if (xsp < 150 && ysp < 60)
                if (zero.y < 230D) {
                    getGraphicsBuffer().setColor(Color.red);
                    getGraphicsBuffer().drawString("" + (int) (230D - zero.y), 355, 48);
                } else if (zero.y < 232.09999999999999D) {
                    getGraphicsBuffer().setColor(Color.green);
                    getGraphicsBuffer().drawString("OK", 355, 48);
                }
        }

        repaint();
        */

    }

    private void doGameAlku() {

    }

    @Override
    public void doDraw(Canvas canvas) {

        super.doDraw(canvas);


        paint.setStrokeWidth(3);
        paint.setTextSize(30);
        paint.setColor(Color.GREEN);
        canvas.drawText(test, 10, 355, paint);

    }

    @Override
    public boolean onKeyDown(int code, KeyEvent ke) {

        System.out.println("key down "+code+", "+ke.getAction());

        if (bpause)
            bpause = false;
        if (zero.status == 1)
            tank = true;

        switch (code) {

            default:
                break;

            case 48:
                doTest = true;
                break;
            case 29: // 'A'
                kleft = true;
                kright = false;
                kup = false;
                kdown = false;
                break;

            case 51: // 'W'
                kup = true;
                kright = false;
                kleft = false;
                kdown = false;
                break;

            case 32: // 'D'
                kright = true;
                kleft = false;
                kup = false;
                kdown = false;
                break;

            case 47 : // 'S'
                kdown = true;
                kright = false;
                kup = false;
                kleft = false;
                break;

            case 62: // ' '
                kbomb = true;
                break;

            case 54: // 'Z'
                //if (!kshoot && zero.ams > 0 && prcid != 0 && mg != null)
                //    mg.loop();
                kshoot = true;
                break;

            case 8: // '\b'
                if (nickname.length() > 0)
                    nickname = nickname.substring(0, nickname.length() - 1);
                break;

            case 10: // '\n'
                if (zero.status <= 5)
                    break;
                points = 0;
                time = System.currentTimeMillis();
                startGame();
                break;

        }

        return false;

    }

    /*
    @Override
    public boolean onKeyUp(int code, KeyEvent ke) {

        System.out.println("key up "+code+", "+ke.getAction());

        if(prcid == 0)

            prcid = 1;
            zero.pos = 0;

        switch(code) {
            default:
                break;

            case 48: // 'T'
            case 29: // 'A'
                kleft = false;
                break;

            case 51: // 'W'
                kup = false;
                break;

            case 32: // 'D'
                kright = false;
                break;

            case 47: // 'S'
                kdown = false;
                break;

            case 62: // ' '
                kbomb = false;
                break;

            case 54: // 'Z'
                kshoot = false;
                //if(mg != null)
                //    mg.stop();
                break;
        }

        // Key typed
        //if(zero.status > 3 && nickname.length() < 12 && ke.getKeyChar() != '\b')
        //    nickname += ke.getKeyChar();

        return false;

    }
    */

    private void calcBombs() {

        List<Bomb> expiredBombs = new ArrayList<Bomb>();

        for (Bomb b: bombs) {

            b.vy += 0.10000000000000001D;
            if(zero.vx > 0.0D)
            {
                if(b.vx > 0.0D)
                    b.vx -= 0.050000000000000003D;
            } else
            if(b.vx < 0.0D)
                b.vx += 0.050000000000000003D;
            b.x += b.vx;
            b.y += b.vy;
            boolean hit = false;
            for (Target t: targs) {

                if(!t.isHit(b) || t.status != -1)
                    continue;

                hit = true;

                t.d++;
                if (t.att == 0)
                    t.att = 20;

                double df = t.md / 3;
                if ((double)(t.fp + 1) * df <= (double)t.d) {
                    t.fs[t.fp] = (int)((320D + b.x) - t.x);
                    t.fp++;
                    //if(expl != null)
                    //    expl.play();
                }

                if (t.d > t.md) {
                    t.status = 0;
                    points += t.val;
                }

                break;

            }

            if (b.y > 260D) {
                SplashSurface ss = new SplashSurface(30, 30, (int)b.x + 320, 230, 50, 20, 0.5D);
                ss.setTransparentColor(0);
                splashes.add(ss);
                //if(splash != null)
                //    splash.play();
            }

            if (b.y > 260D || hit) {
                expiredBombs.add(b);
            } else if (b.y > 0.0D && dispx + ((int)b.x - (int)zero.x) > 0 && b.y < 350D && dispx + ((int)b.x - (int)zero.x) < 640) {
                int bx = dispx - (int)(zero.x - b.x);
                putPixel(0xff000000, bx, (int)b.y);
                putPixel(0xff000000, bx + 1, (int)b.y);
                putPixel(0xff000000, bx, (int)b.y + 1);
                putPixel(0xff000000, bx + 1, (int)b.y + 1);
            }

        }

        bombs.removeAll(expiredBombs);

    }

    private void test() {

        int pixel = Color.GRAY;
        int a = (pixel >> 24) & 0xff;
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;
        int repacked = a << 24 | r << 16 | g << 8 | b;
        System.out.println(pixel + " => " + a+","+r+","+g+","+b + " => " + repacked);

        SplashSurface ss = new SplashSurface(150, 100, (int)(zero.x + 320D), 160, 500, 100, 1.0D);
        //ss.setTransparentColor(0);
        splashes.add(ss);
        doTest = false;

    }

    private class Zero {

        double v;
        double alt;
        double vx;
        double vy;
        double dvy;
        double x;
        double y;
        int pos;
        int status;
        int fuel;
        int hits;
        int mhits;
        int bs;
        int ams;

        public Zero() {
            v = 0.0D;
            alt = 100D;
            vx = 10D;
            vy = 0.0D;
            dvy = 0.0D;
            x = 100D;
            y = 100D;
            pos = 0;
            status = -1;
            fuel = 2500;
            hits = 0;
            mhits = 20;
            bs = 50;
            ams = 200;
        }

        public void move() {

            double vypl = vx;

            if(status != 1 && status < 3) {

                if(vx > 12D)
                    vypl = 12D;

                vy += 0.01D * (10D - Math.abs(vypl));

                if(dvy > 0.0D) {
                    if(vx > 6D)
                        vx = 6D;
                    if(vx < -6D)
                        vx = -6D;
                }

                if(dvy > 0.0D && vy < -2D)
                    vy = -2D;

                if(x < -320D) {
                    x = -319D;
                    vx = -vx;
                }

                if(x > (double)mwidth) {
                    x = mwidth - 1;
                    vx = -vx;
                }

                x += vx;

                if(y + vy > 0.0D)
                    y += dvy + vy;
                else
                    vy++;

            }

        }

    }


    class Smoke {

        int x;
        int y;
        int vx;
        int age;
        int picid;

        public Smoke(int x, int y) {
            age = 0;
            picid = 0;
            this.x = x;
            this.y = y;
        }

    }

    class Target {

        public boolean isHit(Bomb b) {
            return b.x + 320D > x && b.x + 320D < x + w && b.y > y && b.y < y + h;
        }

        double x;
        double y;
        double w;
        double h;
        int d;
        int md;
        int status;
        int val;
        Sprite pic[];
        int fp;
        int fs[];
        int att;

        public Target(double x, double y, double w, double h, Sprite pic[]) {
            d = 0;
            md = 9;
            status = -1;
            val = 200;
            fp = 0;
            att = 0;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.pic = pic;
            fs = new int[3];
            fs[0] = -1;
            fs[1] = -1;
            fs[2] = -1;
        }

    }

    class Carrier extends Target {

        public boolean isApproached() {
            if (zero.x + 320D > super.x - 100D && zero.x + 320D < super.x + 200D) {
                apst = 0;
                //getGraphicsBuffer().setColor(Color.red);
                if (zero.x + 320D < super.x + 60D)
                    drawSprite(arrow0, 95 + (int)(super.x - zero.x), (int)(zero.y + 12D), 0);
                if (zero.x + 320D > super.x)
                    drawSprite(arrow0, (int)(super.x - zero.x) - 10, (int)(zero.y + 12D), -1);
                if (zero.x + 320D < super.x)
                    appdist = (int)(super.x - zero.x) - 320;
                else
                if(zero.x + 320D > super.x + 60D)
                    appdist = (int)Math.abs(((super.x - zero.x) + 60D) - 320D);
                else
                    appdist = 1000;
                if (zero.y > 100D && zero.status < 3) {
                    zero.status = 0;
                    //getGraphicsBuffer().drawLine(0, (int)zero.y, 640, (int)zero.y);
                    //getGraphicsBuffer().drawLine(0, 250, 640, 250);
                } else
                if (zero.status < 3)
                    zero.status = -1;
                if (zero.x + 320D > super.x && zero.x + 320D < super.x + 60D) {
                    appdist = 1000;
                    apst = 1;
                    appr = true;
                    if (zero.y > 230D) {
                        apst = 2;
                        if (Math.abs(zero.vx) < 5D && Math.abs(zero.vy + zero.dvy) < 2D && zero.status < 3 && zero.y < 232.09999999999999D) {
                            System.out.println("OK: " + zero.vx + "/" + (zero.vy + zero.dvy));
                            zero.status = 1;
                        } else if (zero.status < 3) {
                            zero.status = 3;
                            super.fs[super.fp] = 40;
                            //if(expl != null)
                            //    expl.play();
                        } else {
                            zero.status++;
                        }
                    } else {
                        apst = 1;
                    }
                } else {
                    apst = 0;
                    appr = false;
                }
            } else if (zero.x + 320D > super.x - 150D && zero.x + 320D < super.x + 250D && zero.status < 3) {
                zero.status = -1;
                apst = -1;
            }

            return false;

        }

        public Carrier(double x, double y, double w,
                       double h, Sprite pic[])
        {
            super(x, y, w, h, pic);
            super.val = -500;
            super.md = 100;
        }
    }


    class Bomb {

        double x;
        double y;
        double vx;
        double vy;
        int age;
        int type;

        public Bomb(double x, double y, double vx, double vy) {
            age = 0;
            type = -1;
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

    }

}
