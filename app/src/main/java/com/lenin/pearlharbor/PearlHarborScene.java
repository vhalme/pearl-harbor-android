package com.lenin.pearlharbor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gfx.graphics.Sprite;
import gfx.graphics.Surface;
import gfx.scene.Scene;

/**
 * Created by vhalme on 02/11/15.
 */
public class PearlHarborScene extends Scene implements Runnable {

    private long fpsStart = System.currentTimeMillis();
    private int fpsCount = 0;
    private int fps = 0;
    private long loopTime = 0;
    private long drawTime = 0;

    private long lastPass = System.currentTimeMillis();
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
    Sprite alku;
    Sprite plane0;
    Sprite plane1;
    Sprite plane2;
    Sprite pltmp;
    Sprite blimb;
    Sprite ground;
    Sprite ctrl0;
    Surface ctrl0s;
    Sprite land0;
    Surface land0s;
    Sprite spmap;
    Surface[] clouds0r;
    Surface smoke;
    Surface map;
    Surface city0;
    Sprite[] ships;
    Sprite[] carriers;
    Sprite[] amplanes;
    Sprite[] amcars;
    Sprite[] l2cool;
    Sprite arrow0;
    List<Smoke> clouds;
    List<Smoke> smokes;
    List<SplashSurface> splashes, expiredSplashes;
    List<Bomb> bombs;
    List<Bomb> ammos;
    List<Bomb> ammos2;
    List<Target> targs;
    List<Carrier> cars;
    List<Amplane> ampls;
    Surface zsurf;
    Surface tmpsurf;
    BlurSurface tmpsurf2;
    BlurSurface tmpsurf3;
    Surface tmpsurf4;
    BlurSurface blur;
    PixelRevealSurface prsurf;
    WaterSurface water;
    FireSurface[] fires;
    FireSurface lfire;
    Surface screen;
    Surface pltmps;
    IntroScene intro;
    MediaPlayer mg, expl, splash, psplash, bub, lfa;
    private SoundPool mShortPlayer= null;
    private HashMap mSounds = new HashMap();

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
    String cstr1;
    String cstr2;
    String cstr;
    int strp;
    int strv;
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
    int clevel;
    int intro_state;
    double mapfx;
    double mapfy;
    double blimby;
    long time;

    float touchDownX, touchDownY;

    double accv1 = 0.4d;
    double accv2 = 0.8d;
    double acch1 = 0.4d;
    double acch2 = 0.8d;
    double maxvx = 2d;
    double maxvy = 2d;

    String nickname = "";

    public PearlHarborScene(Context context) {

        super(context);

        setFocusable(true);
        setFocusableInTouchMode(true);
        
        holder = getHolder();

        pos = 0;
        blimby = 0.0d;
        dispx = 320;
        clevel = 0;
        prcid = 0;
        intro_state = -1;
        cstr1 = "Code: lenin_ra (V. Halme)";
        cstr2 = "Gfx: ugli (J. Lajunen)";
        cstr = cstr1;
        strp = 0;
        strv = 0;

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

    @Override
    public boolean onTouchEvent(MotionEvent me) {

        float tx = me.getX();
        float ty = me.getY();

        switch (me.getAction()) {

            case MotionEvent.ACTION_MOVE:

                if (tx < 320 * scale) {

                    float tdx = tx - touchDownX;
                    float tdy = ty - touchDownY;

                    if (tdx < 0) {
                        kleft = true;
                    } else {
                        kright = true;
                    }

                    if (tdy < 0) {
                        kup = true;
                    } else {
                        kdown = true;
                    }

                    //float tdxd = Math.abs(tdx) / displayWidth;
                    //float tdyd = Math.abs(tdy) / displayHeight;
                    acch1 = 0.8; //tdxd * 25;
                    acch2 = 1.6; //tdxd * 50;
                    accv1 = 0.6; //tdyd * 25;
                    accv2 = 1.2; //tdyd * 50;
                    touchDownX = tx;
                    touchDownY = ty;
                    //System.out.println(acch1+"/"+acch2);

                }

                break;

            case MotionEvent.ACTION_DOWN:

                if (tx > 540 * scale) {
                    if (ty < 175 * scale) {
                        mg.start();
                        kshoot = true;
                    } else {
                        kbomb = true;
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                if (prcid == 0) {
                    prcid = 1;
                    break;
                }

                if (intro_state == 2) {
                    initLevel(clevel);
                    break;
                }

                if (zero.status <= 5) {
                    break;
                }

                points = 0;
                time = System.currentTimeMillis();
                startGame();

        }

        return true;

    }

    class IntroScene {

        boolean bdrop = false;
        int bffx = 0;
        int blfx = 0;
        boolean bpause = true;
        int i = -35;

        public IntroScene() {

            System.out.println((clevel - 1) + "/" + l2cool[clevel - 1].getPixels().length);
            bombs = new ArrayList<Bomb>();

        }

        public void draw(Surface surface) {

            int alt = 70 + (int) (50D * Math.sin((double) i * 0.0080000000000000002D));
            if (i > 180 && i < 430 && i % 5 == 0)
                bombs.add(new Bomb(i + 10, alt + 20, 1.0D, 0.0D));

            surface.clear(0xff000000);
            surface.draw(city0, 200, 207, 257, 43, false, 0);

            if (i < 640) {
                surface.draw(zsurf, i, alt, 35, 35, false, 0);
                i += 2;
            }

            if (bdrop) {
                lfire.burn();
                surface.draw(lfire, 200, 100, 257, 148, true, 0);
            }

            List<Bomb> expiredBombs = new ArrayList<Bomb>();
            for (Bomb b: bombs) {

                if (b.y > 240D) {

                    expiredBombs.add(b);
                    if (!bdrop) {
                        bdrop = true;
                        bffx = (int) (b.x - 200D);
                    }

                    blfx = (int) (b.x - (double) (200 + bffx));
                    lfire.fx = bffx;
                    lfire.fw = blfx;

                } else {

                    surface.putPixel((int) b.x, (int) b.y, 0xffffffff);
                    if (i < 640)
                        b.vy += 0.0050000000000000001D;
                    else
                        b.vy += 0.01D;
                    if (b.vx > 0.0D)
                        b.vx -= 0.050000000000000003D;
                    b.x += b.vx;
                    b.y += b.vy;
                }



            }

            bombs.removeAll(expiredBombs);
            //pixels = tmpsurf4.getPixels();
            paint.setColor(Color.RED);
            //canvas.drawText("Level " + clevel + " cleared.", 280 * scale, 30 * scale, paint);
            //canvas.drawText("Any key to continue...", 275 * scale, 330 * scale, paint);
            //doDraw(canvas);

        }

    }

    public void run() {

        loadStuff();
        initLevel(0);

        screen = new Surface(getPixels(), 640, 350);
        map = new Surface(130, 70);
        tmpsurf = new Surface(640, 90);
        tmpsurf2 = new BlurSurface(30, 30);
        tmpsurf3 = new BlurSurface(150, 100);
        tmpsurf4 = new Surface(640, 350);
        water = new WaterSurface(640, 90);
        ammos2 = new ArrayList<Bomb>();
        intro_state = -1;

        while (keepRunning) {

            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {

                long timePoint = System.currentTimeMillis();

                if(prcid == 0) {

                    doGameAlku();

                } else if(prcid == 1) {

                    if (ampls.size() == 0 && targs.size() == cars.size() && zero.status < 3) {

                        if (intro_state == -1) {
                            intro_state = 0;
                            intro = new IntroScene();
                            lfire = new FireSurface(257, 150, l2cool[clevel - 1].getPixels(), true, 10, 148, 230, 2);
                            lfire.setTransparentColor(0xff000000);
                        }

                        if (intro_state == 0) {

                            prsurf = new PixelRevealSurface(640, 350, getPixels(), 50);
                            prsurf.fetchPixels(getPixels(), 640, 0, 0);
                            tmpsurf4.clear(0xff000000);
                            tmpsurf4.draw(city0, 200, 207, 257, 43, false, 0);
                            intro_state = 1;

                        }

                        if (intro_state == 1) {

                            if (prsurf.reveal(tmpsurf4)) {
                                pixels = prsurf.getPixels(); //tmpsurf4.getPixels();
                            } else {
                                System.out.println("Finished revealing surface");
                                intro_state = 2;
                            }

                        }

                        if (intro_state == 2) {

                            intro.draw(screen);
                            pixels = screen.getPixels();

                        }

                    } else {

                        long passed = System.currentTimeMillis() - lastPass;
                        if (passed > 150) {
                            lastPass = System.currentTimeMillis();
                            //adjustVectorByTilting();
                            adjustVectorByKeys();
                        }

                        doGameFrame();

                    }

                }

                loopTime = System.currentTimeMillis() - timePoint;
                timePoint = System.currentTimeMillis();
                doDraw(canvas);
                drawTime = System.currentTimeMillis() - timePoint;

                long now = System.currentTimeMillis();
                //long passed = now - lastPass;
                //lastPass = now;

                if (now - fpsStart > 1000) {
                    fpsStart = now;
                    fps = fpsCount;
                    fpsCount = 0;
                }

                fpsCount++;

                /*
                if (passed < 100) {
                    try {
                        System.out.println("sleeping, passed "+passed);
                        thread.sleep(100 - passed);
                    } catch(InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                */

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

        mg = MediaPlayer.create(context, R.raw.mg1);
        expl = MediaPlayer.create(context, R.raw.expl1);
        splash = MediaPlayer.create(context, R.raw.splash);
        psplash = MediaPlayer.create(context, R.raw.psplash);
        bub = MediaPlayer.create(context, R.raw.bub);
        lfa = MediaPlayer.create(context, R.raw.siren);

        Bitmap bmp;

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.alku8);
        alku = new Sprite(Bitmap.createScaledBitmap(bmp, 640, 350, false));

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.mittarit2);
        ctrl0 = new Sprite(Bitmap.createScaledBitmap(bmp, 200, 80, false));
        ctrl0.setAlphaBlended(true);
        ctrl0s = new Surface(ctrl0.getPixels(), ctrl0.getWidth(), ctrl0.getHeight());

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.landing2);
        land0 = new Sprite(Bitmap.createScaledBitmap(bmp, 122, 52, false));
        land0.setAlphaBlended(true);
        land0s = new Surface(land0.getPixels(), land0.getWidth(), land0.getHeight());

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.blimb);
        blimb = new Sprite(Bitmap.createScaledBitmap(bmp, 108, 100, false));
        blimb.setTransparentColor(blimb.getPixels()[0]);

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.kartta);
        spmap = new Sprite(Bitmap.createScaledBitmap(bmp, 130, 70, false));

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.sea);
        ground = new Sprite(Bitmap.createScaledBitmap(bmp, 640, 90, false));

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow0);
        arrow0 = new Sprite(Bitmap.createScaledBitmap(bmp, 10, 10, false));

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.smoke0);
        Sprite spsmoke = new Sprite(Bitmap.createScaledBitmap(bmp, 10, 10, false));
        smoke = new Surface(spsmoke.getPixels(), spsmoke.getWidth(), spsmoke.getHeight());
        smoke.setTransparentColor(smoke.getPixels()[0]);
        smoke.setAlphaBlending(0.80000000000000004D);

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

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.city);
        city0 = new Surface(Bitmap.createScaledBitmap(bmp, 257, 43, false));

        Bitmap planeBmp;

        planeBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane0);
        planeBmp = Bitmap.createScaledBitmap(planeBmp, 35, 35, false);
        plane0 = new Sprite(planeBmp);
        plane0.setTransparentColor(plane0.getPixels()[0]);
        pltmp = new Sprite(planeBmp);
        pltmp.setTransparentColor(pltmp.getPixels()[0]);
        zsurf = new Surface(planeBmp);
        zsurf.setTransparentColor(zsurf.getPixels()[0]);

        planeBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane1);
        planeBmp = Bitmap.createScaledBitmap(planeBmp, 35, 35, false);
        plane1 = new Sprite(planeBmp);
        plane1.setTransparentColor(plane1.getPixels()[0]);

        planeBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane2);
        planeBmp = Bitmap.createScaledBitmap(planeBmp, 35, 35, false);
        plane2 = new Sprite(planeBmp);
        plane2.setTransparentColor(plane2.getPixels()[0]);

        pltmps = new Surface(35, 35);

        amplanes = new Sprite[4];
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.amplane0);
        amplanes[0] = new Sprite(Bitmap.createScaledBitmap(bmp, 32, 10, false));
        amplanes[0].setTransparentColor(amplanes[0].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.amplane1);
        amplanes[1] = new Sprite(Bitmap.createScaledBitmap(bmp, 32, 10, false));
        amplanes[1].setTransparentColor(amplanes[1].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.amplane1);
        amplanes[2] = new Sprite(Bitmap.createScaledBitmap(bmp, 32, 10, false));
        amplanes[2].setTransparentColor(amplanes[2].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.amplane1);
        amplanes[3] = new Sprite(Bitmap.createScaledBitmap(bmp, 32, 10, false));
        amplanes[3].setTransparentColor(amplanes[3].getPixels()[0]);

        amcars = new Sprite[4];
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.amcar0);
        amcars[0] = new Sprite(Bitmap.createScaledBitmap(bmp, 160, 40, false));
        amcars[0].setTransparentColor(amcars[0].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.amcar1);
        amcars[1] = new Sprite(Bitmap.createScaledBitmap(bmp, 160, 40, false));
        amcars[1].setTransparentColor(amcars[1].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.amcar2);
        amcars[2] = new Sprite(Bitmap.createScaledBitmap(bmp, 160, 40, false));
        amcars[2].setTransparentColor(amcars[2].getPixels()[0]);
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.amcar3);
        amcars[3] = new Sprite(Bitmap.createScaledBitmap(bmp, 160, 40, false));
        amcars[3].setTransparentColor(amcars[3].getPixels()[0]);

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

        l2cool = new Sprite[7];
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.l2cool2);
        l2cool[0] = new Sprite(Bitmap.createScaledBitmap(bmp, 257, 150, false));
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.l2cool3);
        l2cool[1] = new Sprite(Bitmap.createScaledBitmap(bmp, 257, 150, false));
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.l2cool4);
        l2cool[2] = new Sprite(Bitmap.createScaledBitmap(bmp, 257, 150, false));
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.l2cool5);
        l2cool[3] = new Sprite(Bitmap.createScaledBitmap(bmp, 257, 150, false));
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.l2cool6);
        l2cool[4] = new Sprite(Bitmap.createScaledBitmap(bmp, 257, 150, false));
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.l2cool7);
        l2cool[5] = new Sprite(Bitmap.createScaledBitmap(bmp, 257, 150, false));
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.l2cool8);
        l2cool[6] = new Sprite(Bitmap.createScaledBitmap(bmp, 257, 150, false));

        Bitmap bgBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
        bgBmp = Bitmap.createScaledBitmap(bgBmp, 640, 350, false);

        blur = new BlurSurface(30, 30);

        setBackground(new Sprite(bgBmp).getPixels());

    }

    private void startGame() {

        time = System.currentTimeMillis();
        nickname = "";
        points = 0;
        clevel = 0;
        keepRunning = true;
        zero = new Zero();
        initLevel(clevel);

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
        intro_state = -1;
        zero = new Zero();
        clouds = new ArrayList<Smoke>();
        smokes = new ArrayList<Smoke>();
        bombs = new ArrayList<Bomb>();
        ammos = new ArrayList<Bomb>();
        targs = new ArrayList<Target>();
        cars = new ArrayList<Carrier>();
        ampls = new ArrayList<Amplane>();
        splashes = expiredSplashes = new ArrayList<SplashSurface>();

        switch (level) {

            default:
                break;

            case 0: {
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
                //targs.add(new Target(1500D, 240D, 100D, 30D, ships));
                //targs.add(new Target(2300D, 240D, 100D, 30D, ships));
                for (int i = 0; i < 3; i++) {
                    Amplane ampl = new Amplane(Math.random() * 2800D, 40D + Math.random() * 120D, 30D, 30D, amplanes);
                    //ampls.add(ampl);
                }

                break;

            } case 1: {
                mwidth = 5000;
                lfs0 = 500;
                lfs1 = 300;
                Carrier car0 = new Carrier(2000D, 240D, 135D, 30D, carriers);
                Carrier car1 = new Carrier(3000D, 240D, 135D, 30D, carriers);
                targs.add(car0);
                targs.add(car1);
                cars.add(car0);
                cars.add(car1);
                targs.add(new Target(500D, 240D, 100D, 30D, ships));
                targs.add(new Target(800D, 240D, 100D, 30D, ships));
                targs.add(new Target(2500D, 240D, 100D, 30D, ships));
                targs.add(new Target(4200D, 240D, 100D, 30D, ships));
                targs.add(new Target(4700D, 240D, 100D, 30D, ships));
                for (int i = 0; i < 5; i++) {
                    Amplane ampl = new Amplane(Math.random() * 4800D, 40D + Math.random() * 120D, 30D, 30D, amplanes);
                    ampls.add(ampl);
                }

                break;
            }

            case 2: {
                mwidth = 6000;
                lfs0 = 600;
                lfs1 = 400;
                Carrier car0 = new Carrier(3700D, 240D, 135D, 30D, carriers);
                Carrier car1 = new Carrier(5000D, 240D, 135D, 30D, carriers);
                targs.add(car1);
                targs.add(car0);
                cars.add(car0);
                cars.add(car1);
                targs.add(new Target(1000D, 240D, 100D, 30D, ships));
                targs.add(new Target(1500D, 240D, 100D, 30D, ships));
                targs.add(new Target(2000D, 240D, 100D, 30D, ships));
                targs.add(new Target(2800D, 240D, 100D, 30D, ships));
                targs.add(new Target(3200D, 240D, 100D, 30D, ships));
                targs.add(new Target(5500D, 240D, 100D, 30D, ships));
                targs.add(new Target(5700D, 240D, 100D, 30D, ships));
                for(int i = 0; i < 10; i++) {
                    Amplane ampl = new Amplane(Math.random() * 5800D, 40D + Math.random() * 120D, 30D, 30D, amplanes);
                    ampls.add(ampl);
                }

                break;
            }

            case 3: {
                mwidth = 7000;
                lfs0 = 650;
                lfs1 = 500;
                Carrier car0 = new Carrier(2000D, 240D, 135D, 30D, carriers);
                Carrier car1 = new Carrier(3500D, 240D, 135D, 30D, carriers);
                targs.add(car1);
                targs.add(car0);
                cars.add(car0);
                cars.add(car1);
                targs.add(new Target(700D, 240D, 100D, 30D, ships));
                targs.add(new Target(1200D, 240D, 100D, 30D, ships));
                targs.add(new Target(1800D, 240D, 100D, 30D, ships));
                targs.add(new Target(4000D, 240D, 100D, 30D, ships));
                targs.add(new Target(4600D, 240D, 100D, 30D, ships));
                targs.add(new Target(5400D, 240D, 100D, 30D, ships));
                targs.add(new Target(6100D, 240D, 100D, 30D, ships));
                Target amc = new Target(2500D, 230D, 160D, 40D, amcars);
                amc.md = 150;
                amc.val = 400;
                targs.add(amc);
                for(int i = 0; i < 15; i++) {
                    Amplane ampl = new Amplane(Math.random() * 6800D, 40D + Math.random() * 120D, 30D, 30D, amplanes);
                    ampls.add(ampl);
                }

                break;
            }

            case 4: // '\004'
            {
                mwidth = 10000;
                lfs0 = 1000;
                lfs1 = 700;
                Carrier car0 = new Carrier(8000D, 240D, 135D, 30D, carriers);
                Carrier car1 = new Carrier(9000D, 240D, 135D, 30D, carriers);
                targs.add(car1);
                targs.add(car0);
                cars.add(car0);
                cars.add(car1);
                targs.add(new Target(1200D, 240D, 100D, 30D, ships));
                targs.add(new Target(1600D, 240D, 100D, 30D, ships));
                targs.add(new Target(2600D, 240D, 100D, 30D, ships));
                targs.add(new Target(3500D, 240D, 100D, 30D, ships));
                targs.add(new Target(5200D, 240D, 100D, 30D, ships));
                targs.add(new Target(6100D, 240D, 100D, 30D, ships));
                targs.add(new Target(7200D, 240D, 100D, 30D, ships));
                targs.add(new Target(7500D, 240D, 100D, 30D, ships));
                targs.add(new Target(8500D, 240D, 100D, 30D, ships));
                targs.add(new Target(9200D, 240D, 100D, 30D, ships));
                Target amc1 = new Target(4000D, 230D, 160D, 40D, amcars);
                amc1.md = 150;
                amc1.val = 400;
                Target amc2 = new Target(4300D, 230D, 160D, 40D, amcars);
                amc2.md = 150;
                amc2.val = 400;
                Target amc3 = new Target(4600D, 230D, 160D, 40D, amcars);
                amc3.md = 150;
                amc3.val = 400;
                targs.add(amc1);
                targs.add(amc2);
                targs.add(amc3);
                for(int i = 0; i < 30; i++)
                {
                    Amplane ampl = new Amplane(Math.random() * 9800D, 40D + Math.random() * 120D, 30D, 30D, amplanes);
                    ampls.add(ampl);
                }

                break;
            }

            case 5: // '\005'
            {
                mwidth = 12000;
                lfs0 = 1200;
                lfs1 = 800;
                Carrier car0 = new Carrier(1000D, 240D, 135D, 30D, carriers);
                Carrier car1 = new Carrier(8000D, 240D, 135D, 30D, carriers);
                targs.add(car1);
                targs.add(car0);
                cars.add(car0);
                cars.add(car1);
                targs.add(new Target(1500D, 240D, 100D, 30D, ships));
                targs.add(new Target(1800D, 240D, 100D, 30D, ships));
                targs.add(new Target(2800D, 240D, 100D, 30D, ships));
                targs.add(new Target(3500D, 240D, 100D, 30D, ships));
                targs.add(new Target(4000D, 240D, 100D, 30D, ships));
                targs.add(new Target(4800D, 240D, 100D, 30D, ships));
                targs.add(new Target(7200D, 240D, 100D, 30D, ships));
                targs.add(new Target(7500D, 240D, 100D, 30D, ships));
                targs.add(new Target(8500D, 240D, 100D, 30D, ships));
                targs.add(new Target(9200D, 240D, 100D, 30D, ships));
                targs.add(new Target(11000D, 240D, 100D, 30D, ships));
                targs.add(new Target(11300D, 240D, 100D, 30D, ships));
                Target amc1 = new Target(600D, 230D, 160D, 40D, amcars);
                amc1.md = 150;
                amc1.val = 400;
                Target amc2 = new Target(2200D, 230D, 160D, 40D, amcars);
                amc2.md = 150;
                amc2.val = 400;
                Target amc3 = new Target(4600D, 230D, 160D, 40D, amcars);
                amc3.md = 150;
                amc3.val = 400;
                Target amc4 = new Target(10000D, 230D, 160D, 40D, amcars);
                amc4.md = 150;
                amc4.val = 400;
                Target amc5 = new Target(10300D, 230D, 160D, 40D, amcars);
                amc5.md = 150;
                amc5.val = 400;
                targs.add(amc1);
                targs.add(amc2);
                targs.add(amc3);
                targs.add(amc4);
                targs.add(amc5);
                for(int i = 0; i < 40; i++)
                {
                    Amplane ampl = new Amplane(Math.random() * 11800D, 40D + Math.random() * 120D, 30D, 30D, amplanes);
                    ampls.add(ampl);
                }

                break;
            }

            case 6: // '\006'
            {
                mwidth = 13000;
                lfs0 = 1300;
                lfs1 = 800;
                Carrier car0 = new Carrier(4300D, 240D, 135D, 30D, carriers);
                targs.add(car0);
                cars.add(car0);
                targs.add(new Target(500D, 240D, 100D, 30D, ships));
                targs.add(new Target(2200D, 240D, 100D, 30D, ships));
                targs.add(new Target(2800D, 240D, 100D, 30D, ships));
                targs.add(new Target(3600D, 240D, 100D, 30D, ships));
                targs.add(new Target(4000D, 240D, 100D, 30D, ships));
                targs.add(new Target(4700D, 240D, 100D, 30D, ships));
                targs.add(new Target(6700D, 240D, 100D, 30D, ships));
                targs.add(new Target(7500D, 240D, 100D, 30D, ships));
                targs.add(new Target(8700D, 240D, 100D, 30D, ships));
                targs.add(new Target(9000D, 240D, 100D, 30D, ships));
                targs.add(new Target(9300D, 240D, 100D, 30D, ships));
                targs.add(new Target(10800D, 240D, 100D, 30D, ships));
                targs.add(new Target(11500D, 240D, 100D, 30D, ships));
                targs.add(new Target(12000D, 240D, 100D, 30D, ships));
                targs.add(new Target(12200D, 240D, 100D, 30D, ships));
                Target amc1 = new Target(1000D, 230D, 160D, 40D, amcars);
                amc1.md = 150;
                amc1.val = 400;
                Target amc2 = new Target(1300D, 230D, 160D, 40D, amcars);
                amc2.md = 150;
                amc2.val = 400;
                Target amc3 = new Target(1600D, 230D, 160D, 40D, amcars);
                amc3.md = 150;
                amc3.val = 400;
                Target amc4 = new Target(5000D, 230D, 160D, 40D, amcars);
                amc4.md = 150;
                amc4.val = 400;
                Target amc5 = new Target(5800D, 230D, 160D, 40D, amcars);
                amc5.md = 150;
                amc5.val = 400;
                Target amc6 = new Target(6300D, 230D, 160D, 40D, amcars);
                amc6.md = 150;
                amc6.val = 400;
                Target amc7 = new Target(8000D, 230D, 160D, 40D, amcars);
                amc7.md = 150;
                amc7.val = 400;
                Target amc8 = new Target(10000D, 230D, 160D, 40D, amcars);
                amc8.md = 150;
                amc8.val = 400;
                targs.add(amc1);
                targs.add(amc2);
                targs.add(amc3);
                targs.add(amc4);
                targs.add(amc5);
                targs.add(amc6);
                targs.add(amc7);
                targs.add(amc8);
                for(int i = 0; i < 65; i++)
                {
                    Amplane ampl = new Amplane(Math.random() * 12800D, 40D + Math.random() * 120D, 30D, 30D, amplanes);
                    ampls.add(ampl);
                }

                break;
            }


        }

        for (int i = 0; i < mwidth; i++) {
            if ((int)(Math.random() * 100D) == 10) {
                Smoke s = new Smoke(i, (int)(Math.random() * 150D));
                s.picid = (int)(Math.random() * 5D);
                s.vx = (int)(Math.random() * 4D);
                clouds.add(s);
            }
        }

        mapfx = 120D / (double)mwidth;
        mapfy = 0.17142857142857143D;
        clevel++;

    }

    private void adjustVectorByKeys() {

        if (kleft && zero.vx > -15D && zero.status != 1 && zero.fuel > 0) {

            if (zero.dvy == 0.0d)
                zero.vx -= acch2;
            else
                zero.vx -= acch1;

            kleft = false;

        }

        if (kup && zero.vy > -4D && zero.status != 1 && zero.fuel > 0) {

            double boost = zero.vy > 0 ? 2 : 1;
            if (zero.dvy == 0.0D) {
                zero.vy -= accv2 * boost;
            } else {
                zero.vy -= accv1 * boost;
            }

            zero.pos = 1;
            kup = false;

        }

        if (kright && zero.vx < 15D && zero.status != 1 && zero.fuel > 0) {

            if (zero.dvy == 0.0D)
                zero.vx += acch2;
            else
                zero.vx += acch1;

            kright = false;

        }

        if (kdown && zero.status != 1) {

            double brake = zero.y > 200 ? 0.5 : 1;
            if (zero.dvy == 0.0D)
                zero.vy += accv2 * brake;
            else
                zero.vx += accv1 * brake;

            zero.pos = 2;
            kdown = false;

        }


    }


    private void adjustVectorByTilting() {

        if (mValuesOrientation[0] == 0 && mValuesOrientation[1] == 0 && mValuesOrientation[2] == 0) {
            return;
        }

        if (mValuesOrientation[1] > 0.1 && zero.vx > -15D && zero.status != 1 && zero.fuel > 0) {

            if (zero.dvy == 0.0d)
                zero.vx -= acch2;
            else
                zero.vx -= acch1;

            kleft = false;

        }

        if (mValuesOrientation[2] > -0.75 && zero.vy > -4D && zero.status != 1 && zero.fuel > 0) {

            double boost = zero.vy > 0 ? 2 : 1;
            if (zero.dvy == 0.0D) {
                zero.vy -= accv2 * boost;
            } else {
                zero.vy -= accv1 * boost;
            }

            zero.pos = 1;
            kup = false;

        }

        if (mValuesOrientation[1] < -0.1 && zero.vx < 15D && zero.status != 1 && zero.fuel > 0) {

            if (zero.dvy == 0.0D)
                zero.vx += acch2;
            else
                zero.vx += acch1;

            kright = false;

        }

        if (mValuesOrientation[2] < -0.82 && zero.status != 1) {

            double brake = zero.y > 200 ? 0.5 : 1;
            if (zero.dvy == 0.0D)
                zero.vy += accv2 * brake;
            else
                zero.vx += accv1 * brake;

            zero.pos = 2;
            kdown = false;

        }


    }


    private void doGameFrame() {

        blimby += 0.050000000000000003D;

        if (blimby > 6.2800000000000002D)
            blimby = 0.0D;

        //adjustVectorByTilting();
        //adjustVectorByKeys();

        if (kbomb && zero.bs > 0) {
            System.out.println("drop bomb");
            if (zero.vx > 0.0D)
                bombs.add(new Bomb(zero.x + 10D, zero.y + 20D, zero.vx, zero.vy + zero.dvy));
            else
                bombs.add(new Bomb(zero.x + 20D, zero.y + 20D, zero.vx, zero.vy + zero.dvy));
            zero.bs--;
            kbomb = false;
        }

        if (kshoot) {
            if (zero.ams > 0) {
                System.out.println("shoot!");
                if (zero.vx > 0.0D)
                    ammos.add(new Bomb(zero.x + 25D, zero.y + 15D + zero.vy, zero.vx + 7D, zero.vy + zero.dvy + zero.vy));
                else
                    ammos.add(new Bomb(zero.x + 1.0D, zero.y + 17D + zero.vy, zero.vx - 7D, zero.vy + zero.dvy + zero.vy));
                zero.ams--;

            } else { //if(mg != null) {
                //mg.stop();
            }

            kshoot = false;

        }

        clear();

        map.setPixelValues(spmap.getPixels());
        for (int i = 0; i < 120; i++)
            map.putPixel(5 + i, 55, 200);

        if (zero.status == -1 || zero.status == 0) {

            zero.fuel--;
            if (zero.fuel == lfs0 && lfa != null)
                lfa.start();
            if (zero.fuel == lfs1 && lfa != null)
                lfa.start();

        }

        if (zero.fuel < 0)

            if (zero.vx > 0.0D)
                zero.vx -= 0.01D;
            else
                zero.vx += 0.01D;


        if (zero.y > 243D) {

            if (zero.status < 3) {
                zero.status = 4;
                SplashSurface ss = new SplashSurface(150, 100, (int)(zero.x + 320D - 50), 160, 500, 100, 1.0D);
                ss.setTransparentColor(0);
                splashes.add(ss);
                System.out.println("added splash: " + splashes.size());
                if (psplash != null)
                    psplash.start();
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

            int mtx = 5 + (int) (mapfx * t.x);
            if (t instanceof Carrier) {
                map.putPixel(mtx, 53, 0xff00ff00);
                map.putPixel(mtx + 1, 53, 0xff00ff00);
            } else {
                map.putPixel(mtx, 53, 0xffff0000);
                map.putPixel(mtx + 1, 53, 0xffff0000);
            }

            if (t.x + t.w > zero.x && t.x < zero.x + 640D) {

                int sinkf = 230;

                if (t.status > -1 && t.status < 80) {
                    sinkf += t.status;
                    t.status++;
                }

                if (t.status == 20 && bub != null)
                    bub.start();

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
            smktmp.vx = 1 + (int)(Math.random() * 3);
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
            //pltmp.clear(plane0.getPixels()[0]);
            //pltmp.setTransparentColor(plane0.getPixels()[0]);
            Utils.rotate(inc, plane0.getPixels(), pltmp.getPixels(), 35, plane0.getPixels()[0]);
            //pltmps.setPixelValues(pltmp.getPixels());
            //pltmps.setTransparentColor(0);
            if (zero.vx > 0.0D)
                drawSprite(pltmp, dispx, (int)zero.y, 0);
            else
                drawSprite(pltmp, dispx, (int)zero.y, -1);
        }


        calcBombs();
        calcAmmos();

        zero.move();

        List<Integer> expiredAmplanes = new ArrayList<Integer>();
        for (int i = 0; i < ampls.size(); i++) {

            Amplane a = ampls.get(i);
            a.move();

            int mtx = 5 + (int) (mapfx * a.x);
            int mty = 5 + (int) (mapfy * a.y);
            map.putPixel(mtx, mty, 0xffff0000);
            map.putPixel(mtx + 1, mty, 0xffff0000);

            if (a.y > 255D) {

                expiredAmplanes.add(i);
                int splfix = a.vx <= 0.0D ? -30 : -55;
                SplashSurface ss = new SplashSurface(150, 100, (int)a.x + splfix, 160, 500, 100, 1.0D);
                ss.setTransparentColor(0);
                splashes.add(ss);
                if (psplash != null)
                    psplash.start();

            } else if (a.x + a.w > zero.x && a.x < zero.x + 640D) {

                if (Math.abs(zero.y - a.y) < 40D && zero.status == -1)

                    if (a.vx > 0.0D) {
                        if (zero.x + 320D > a.x)
                            a.shoot();
                    } else if (zero.x + 320D < a.x)
                        a.shoot();

                a.adx = (int)(a.x - zero.x);
                a.checkAmmos();

                if (a.vx > 0.0D) {
                    drawSprite(a.pic[a.fp], (int)(a.x - zero.x), (int)a.y, 0);
                    if (a.d > a.md)
                        smokes.add(new Smoke((int)(a.x + Math.random() * 3D) - 10, (int)(a.y + Math.random() * 3D)));
                } else {
                    drawSprite(a.pic[a.fp], (int)(a.x - zero.x), (int)a.y, -1);
                    if (a.d >= a.md)
                        smokes.add(new Smoke((int)(a.x + Math.random() * 3D) + 30, (int)(a.y + Math.random() * 3D)));
                }


            }

        }

        for (Integer expired: expiredAmplanes)
            ampls.remove(ampls.get(expired));

        if (zero.dvy > 0.0D)
            if (zero.vx > 0.0D)
                smokes.add(new Smoke((int) (320D + zero.x + Math.random() * 3D) - 10, (int) (zero.y + Math.random() * 3D)));
            else
                smokes.add(new Smoke((int) (320D + zero.x + Math.random() * 3D) + 30, (int) (zero.y + Math.random() * 3D)));
        if (Math.abs(zero.vx) > 10D && zero.dvy > 0.0D)
            if (zero.vx > 0.0D)
                smokes.add(new Smoke((int) (320D + zero.x + Math.random() * 3D) - 20, (int) (zero.y + Math.random() * 3D)));
            else
                smokes.add(new Smoke((int) (320D + zero.x + Math.random() * 3D) + 40, (int) (zero.y + Math.random() * 3D)));

        List<Smoke> expiredSmokes = new ArrayList<Smoke>();
        for (Smoke ss: smokes) {

            ss.age++;

            if (ss.age > 30) {
                expiredSmokes.add(ss);
            } else {
                smoke.setAlphaBlending((double)ss.age * 0.033000000000000002D);
                ss.y -= 0.10000000000000001D;
                if (ss.y - 10 >= 0 && (double) ss.x - zero.x - 10D >= 0.0D) {
                    tmpsurf2.fetchPixels(getPixels(), 640, (int)((double) ss.x - zero.x) - 10, ss.y - 10);
                    tmpsurf2.draw(smoke, 10, 10, 10, 10, false, 0);
                    tmpsurf2.blur();
                    screen.draw(tmpsurf2, (int) ((double) ss.x - zero.x) - 10, ss.y - 10, 30, 30, false, 0);
                }
            }

        }

        smokes.removeAll(expiredSmokes);

        int mtx = 5 + (int)(mapfx * (zero.x + 320D));
        int mty = 5 + (int)(mapfy * zero.y);
        map.putPixel(mtx, mty, 0xffffff00);
        map.putPixel(mtx + 1, mty, 0xffffff00);

        tmpsurf.fetchPixels(getPixels(), 640, 0, 170);
        water.process(tmpsurf.getPixels());
        screen.draw(water, 0, 260, 640, 90, true, 1);

        screen.draw(map, 490, 10, 130, 70, true, 0);

        if (apst > -1) {
            if (zero.y < 40D)
                land0.setAlphaBlended(true);
            else
                land0.setAlphaBlended(false);
            drawSprite(land0, 260, 0);
        }

        if (zero.status != 1) {

            for (Carrier c: cars) {
                if (c.isApproached())
                    System.out.println("approach: " + zero.vx + "/" + (zero.vy + zero.dvy));

            }

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

        screen.draw(ctrl0s, 10, 10, 200, 80, true, 0);
        if (zero.status > 5) {
            if (zero.status < 7) {
                lfire = new FireSurface(257, 150, l2cool[6].getPixels(), true, 2, 148, 246, 2);
                zero.status = 7;
            }
            lfire.burn();
            screen.draw(lfire, 230, 80, 257, 148, true, 0);
        }


    }

    private void doGameAlku() {
        drawSprite(alku, 0, 0);
    }

    private void drawStatus(Target a, Canvas canvas) {

        if (Math.abs(a.x - zero.x) > 640) {
            return;
        }

        float slice = (float)(a.pic[0].getWidth() - 1) / (float)a.md;
        float per3 = (float)a.md / 3f;

        float pitch = a.pic[0].getWidth() > 35 ? 30 * scale : 9 * scale;
        float statusx = (float)((a.x - zero.x)) * scale;
        float statusy = (float)(a.y - pitch) * scale;
        float statusw = a.pic[0].getWidth() * scale;
        float statush = 6f * scale;

        if (a.att > 0) {

            if ((float)a.d <= per3)
                paint.setColor(Color.GREEN);
            else if((float)a.d > per3 && (float)a.d <= per3 * 2f)
                paint.setColor(Color.YELLOW);
            else if ((float)a.d > per3 * 2f && (float)a.d <= per3 * 3f)
                paint.setColor(Color.RED);

            statusw = statusw * (1f - ((float)a.d / (float)a.md));

            if (a.d <= a.md) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(statusx, statusy, statusx + statusw, statusy + statush, paint);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(statusx - 1f, statusy - 1f, statusx + statusw + 2f, statusy + statush + 2, paint);
            }

            a.att--;

        }

    }

    private void drawZeroStatus(Zero z, Canvas canvas) {

        float slice = (float)(plane0.getWidth() - 1) / (float)z.mhits;
        float per3 = (float)z.mhits / 3f;

        float statusx = (float)320 * scale;
        float statusy = (float)(z.y + plane0.getHeight()) * scale;
        float statusw = plane0.getWidth() * scale;
        float statush = 6f * scale;

        if (z.att > 0) {

            if ((float)z.hits <= per3)
                paint.setColor(Color.GREEN);
            else if((float)z.hits > per3 && (float)z.hits <= per3 * 2f)
                paint.setColor(Color.YELLOW);
            else if ((float)z.hits > per3 * 2f && (float)z.hits <= per3 * 3f)
                paint.setColor(Color.RED);

            statusw = statusw * (1f - ((float)z.hits / (float)z.mhits));

            if (z.hits <= z.mhits) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(statusx, statusy, statusx + statusw, statusy + statush, paint);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(statusx - 1f, statusy - 1f, statusx + statusw + 2f, statusy + statush + 2, paint);
            }

            z.att--;

        }

    }

    @Override
    protected void drawToCanvas(Canvas canvas) {

        if (prcid == 1 && intro_state == -1) {

            drawZeroStatus(zero, canvas);

            for (Amplane a : ampls) {
                drawStatus(a, canvas);
            }

            for (Target t : targs) {

                drawStatus(t, canvas);

                if (tarmed) {

                    if (zero.vx > 0.0D) {

                        if ((int) (t.x - zero.x - 320d) < 250 && (int) (t.x - zero.x - 320d) >= 0) {
                            paint.setColor(Color.YELLOW);
                            canvas.drawLine(330 * scale, (float) (zero.y + 20) * scale, (float) ((t.x - zero.x) + 20) * scale, (float) (t.y + 10) * scale, paint);
                        }

                    } else if ((int) (t.x - zero.x - 320d) > -250 && (int) (t.x - zero.x - 320d) <= 0) {
                        paint.setColor(Color.YELLOW);
                        canvas.drawLine(330 * scale, (float) (zero.y + 20) * scale, (float) ((t.x - zero.x) + 20) * scale, (float) (t.y + 10) * scale, paint);
                    }

                }

            }

            paint.setTextSize(9 * scale);
            paint.setColor(Color.WHITE);
            double av = Math.sqrt(zero.vx * zero.vx + (zero.vy + zero.dvy) * (zero.vy + zero.dvy)) * 30d;
            canvas.drawText("" + (int) av, 28 * scale, 46 * scale, paint);

            if (zero.fuel < lfs0)
                paint.setColor(Color.RED);
            canvas.drawText("" + zero.fuel, 80 * scale, 46 * scale, paint);

            if (zero.fuel < lfs0)
                paint.setColor(Color.WHITE);

            canvas.drawText("" + (int) Math.abs(zero.y - 245D), 55 * scale, 77 * scale, paint);
            canvas.drawText("" + zero.bs, 140 * scale, 57 * scale, paint);
            canvas.drawText("" + zero.ams, 157 * scale, 57 * scale, paint);
            canvas.drawText("" + (zero.mhits - zero.hits), 180 * scale, 57 * scale, paint);
            canvas.drawText("" + points, 165 * scale, 75 * scale, paint);
            if (zero.fuel < lfs1) {
                paint.setColor(Color.RED);
                canvas.drawText("FUEL LOW!", 300 * scale, 100 * scale, paint);
            }

            paint.setTextSize(12 * scale);
            paint.setColor(Color.RED);
            double arxsin = Math.sin(3d + 0.0074999999999999997D * av);
            double arxcos = Math.cos(3d + 0.0074999999999999997D * av);
            int arx = (int) (arxsin * (11d * scale));
            int ary = (int) (arxcos * (11d * scale));
            canvas.drawLine(34 * scale, 36 * scale, (34 * scale) + ary, (36 * scale) + arx, paint);
            arxsin = Math.sin(3D + 0.0011999999999999999D * (double) zero.fuel);
            arxcos = Math.cos(3D + 0.0011999999999999999D * (double) zero.fuel);
            arx = (int) (arxsin * (11d * scale));
            ary = (int) (arxcos * (11d * scale));
            canvas.drawLine(90 * scale, 36 * scale, (90 * scale) + ary, (36 * scale) + arx, paint);
            arxsin = Math.sin(3d + 0.012244897959183673D * Math.abs(zero.y - 245D));
            arxcos = Math.cos(3d + 0.012244897959183673D * Math.abs(zero.y - 245D));
            arx = (int) (arxsin * (11d * scale));
            ary = (int) (arxcos * (11d * scale));
            canvas.drawLine(61 * scale, 67 * scale, (61 * scale) + ary, (67 * scale) + arx, paint);
            ary = (int) (0.44d * (double) zero.bs);
            canvas.drawLine(144 * scale, (45 * scale) - ary, 147 * scale, (45 * scale) - ary, paint);
            ary = (int) (0.11d * (double) zero.ams);
            canvas.drawLine(164 * scale, (45 * scale) - ary, 167 * scale, (45 * scale) - ary, paint);
            ary = (int) (1.1000000000000001D * (double) (zero.mhits - zero.hits));
            if (ary < 0)
                ary = 0;
            canvas.drawLine(184 * scale, (45 * scale) - ary, 187 * scale, (45 * scale) - ary, paint);

            if (apst > -1)
                if (appdist != 1000) {
                    paint.setColor(Color.RED);
                    canvas.drawText("" + appdist, 263 * scale, 48 * scale, paint);
                } else {
                    paint.setColor(Color.GREEN);
                    canvas.drawText("OK", 263 * scale, 48 * scale, paint);
                }

            if (apst > 0) {

                xsp = (int) Math.abs(zero.vx * 30D);
                ysp = (int) Math.abs((zero.vy + zero.dvy) * 30D);

                if (xsp < 150) {
                    paint.setColor(Color.GREEN);
                    canvas.drawText("OK", 295 * scale, 48 * scale, paint);
                } else {
                    paint.setColor(Color.RED);
                    canvas.drawText("" + xsp, 295 * scale, 48 * scale, paint);
                }
                if (ysp < 60) {
                    paint.setColor(Color.GREEN);
                    canvas.drawText("OK", 330 * scale, 48 * scale, paint);
                } else {
                    paint.setColor(Color.RED);
                    canvas.drawText("" + ysp, 330 * scale, 48 * scale, paint);
                }

                if (xsp < 150 && ysp < 60)
                    if (zero.y < 230D) {
                        paint.setColor(Color.RED);
                        canvas.drawText("" + (int) (230D - zero.y), 355 * scale, 48 * scale, paint);
                    } else if (zero.y < 232.09999999999999D) {
                        paint.setColor(Color.GREEN);
                        canvas.drawText("OK", 355 * scale, 48 * scale, paint);
                    }
            }

            if (zero.status > 10) {

                String s1 = "Score: " + points;
                String s2 = "GAME OVER!";
                String s3 = "Tap anywhere to start over.";
                nickname = "lenin_ra";
                paint.setColor(Color.WHITE);
                Rect textBounds1 = new Rect();
                paint.getTextBounds(s1, 0, s1.length(), textBounds1);
                Rect textBounds2 = new Rect();
                paint.getTextBounds(s2, 0, s2.length(), textBounds2);
                Rect textBounds3 = new Rect();
                paint.getTextBounds(s3, 0, s3.length(), textBounds3);
                canvas.drawText(s1, (350 * scale) - (textBounds1.width() / 2), 100 * scale, paint);
                canvas.drawText(s2, (350 * scale) - (textBounds2.width() / 2), 150 * scale, paint);
                canvas.drawText(s3, (350 * scale) - (textBounds3.width() / 2), 170 * scale, paint);

            }
        }

        if (prcid == 0) {

            String str = cstr.substring(0, strp) + (char)(int)(Math.random() * 28D + 65D);
            if(strv < 7) {
                strv++;
            } else {
                if(strp < cstr.length()) {
                    strp++;
                } else {
                    if(cstr.startsWith("Code"))
                        cstr = cstr2;
                    else
                        cstr = cstr1;
                    strp = 0;
                }
                strv = 0;
            }

            paint.setColor(Color.BLACK);
            canvas.drawText(str, 480 * scale, 30 * scale, paint);

        }

    }

    @Override
    public void doDraw(Canvas canvas) {

        super.doDraw(canvas);

        //paint.setStrokeWidth(3);
        paint.setTextSize(30);
        paint.setColor(Color.GREEN);
        canvas.drawText(test, 10, 355, paint);
        canvas.drawText(fps + " fps", 10, 400, paint);
        canvas.drawText("Game loop: " + loopTime + " ms", 10, 430, paint);
        canvas.drawText("Draw pass: " + drawTime + " ms", 10, 460, paint);

    }

    @Override
    public boolean onKeyDown(int code, KeyEvent ke) {

        //System.out.println("key down "+code+", "+ke.getAction());

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
                mg.start();
                //if (!kshoot && zero.ams > 0 && prcid != 0 && mg != null)
                //    mg.loop();
                kshoot = true;
                break;

            case 8: // '\b'
                if (nickname.length() > 0)
                    nickname = nickname.substring(0, nickname.length() - 1);
                break;

            case 66: // '\n'
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

    private void calcAmmos() {

        List<Bomb> expiredAmmos = new ArrayList<Bomb>();
        for (Bomb b: ammos) {

            b.vy += 0.0050000000000000001D;
            if(zero.vx > 0.0D)
            {
                if(b.vx > 0.0D)
                    b.vx -= 0.0050000000000000001D;
            } else
            if(b.vx < 0.0D)
                b.vx += 0.0050000000000000001D;
            b.x += b.vx;
            b.y += b.vy;
            b.age++;
            boolean hit = false;

            for (int i = 0; i < ampls.size(); i++) {

                Amplane a = ampls.get(i);

                if (!a.isHit(b) || a.status != -1)
                    continue;

                hit = true;
                if (a.att == 0)
                    a.att = 20;
                a.d++;
                if (a.d >= a.md) {
                    a.vy += 0.5D;
                    if(a.d == a.md) {
                        points += a.val;
                        a.fp = 1;
                    }
                }
                break;
            }

            int bx = dispx - (int)(zero.x - b.x);
            if (b.age > 100 || b.y > 260D || b.y < 0.0D || hit)
                expiredAmmos.add(b);
            else if (bx > 0 && bx < 640 && b.type == -1)
                putPixel(0xffffff00, bx, (int)b.y);

        }

        ammos.removeAll(expiredAmmos);

    }

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
                    if(expl != null)
                        expl.start();
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
                if(splash != null)
                    splash.start();
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
        int att;

        public Zero() {
            v = 0.0D;
            alt = 100D;
            vx = 5d;
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
            att = 0;
        }

        public void move() {

            double vypl = vx;

            if(status != 1 && status < 3) {

                if(vx > 12D)
                    vypl = 12D;

                vy += 0.002d * (10d - Math.abs(vypl));

                if(dvy > 0.0D) {
                    if(vx > maxvx)
                        vx = maxvx;
                    if(vx < -maxvx)
                        vx = -maxvx;
                }

                if(vy > maxvy)
                    vy = maxvy;
                if (vy < -maxvy) {
                    vy = -maxvy;
                }

                if(x < -320d) {
                    x = -319d;
                    vx = -vx;
                }

                if(x > (double)mwidth) {
                    x = mwidth - 1;
                    vx = -vx;
                }

                x += vx;

                if(y + vy > 0.0D)
                    y += dvy + vy;

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
            return b.x + 320d > x && b.x + 320d < x + w && b.y > y && b.y < y + h;
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
                        if (Math.abs(zero.vx) < 8d && Math.abs(zero.vy + zero.dvy) < 4d && zero.status < 3 && zero.y < 232.09999999999999D) {
                            System.out.println("OK: " + zero.vx + "/" + (zero.vy + zero.dvy));
                            zero.status = 1;
                        } else if (zero.status < 3) {
                            zero.status = 3;
                            super.fs[super.fp] = 40;
                            if(expl != null)
                                expl.start();
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

        public Carrier(double x, double y, double w, double h, Sprite pic[]) {
            super(x, y, w, h, pic);
            super.val = -500;
            super.md = 100;
        }

    }

    class Amplane extends Target {

        double vx;
        double vy;
        List<Bomb> fire;
        int sctr;
        int adx;

        public Amplane(double x, double y, double w, double h, Sprite[] pic) {
            super(x, y, w, h, pic);
            sctr = 0;
            adx = 0;
            if(-1D + Math.random() * 2D < 0.0D)
                vx = 6D;
            else
                vx = -6D;
            fire = new ArrayList<Bomb>();
            super.val = 100;
            super.md = 9;
        }

        public void shoot() {

            sctr++;

            if(sctr < 6) {
                Bomb b;
                if(vx > 0.0D)
                    b = new Bomb(x + 25d, super.y, vx + 7d, vy);
                else
                    b = new Bomb(x + 1d, super.y, vx - 7d, vy);
                b.type = 0;
                fire.add(b);
                //ammos2.add(b);
            }

            if(sctr > 20)
                sctr = 0;

        }

        public void checkAmmos() {

            List<Bomb> expiredAmmos = new ArrayList<Bomb>();
            List<Bomb> expiredFire = new ArrayList<Bomb>();

            for (Bomb b: fire) {

                b.vy += 0.0050000000000000001D;
                if(zero.vx > 0.0D) {
                    if(b.vx > 0.0D)
                        b.vx -= 0.0050000000000000001D;
                } else if(b.vx < 0.0D)
                    b.vx += 0.0050000000000000001D;
                b.x += b.vx;
                b.y += b.vy;
                b.age++;
                int bx = adx - (int)(super.x - b.x);
                boolean hit = false;
                if(bx > 320 && bx < 350 && b.y > zero.y && b.y < zero.y + 30D) {
                    zero.hits++;
                    if (zero.att == 0) {
                        zero.att = 20;
                    }
                    if(zero.hits > zero.mhits) {
                        zero.dvy += 0.29999999999999999D;
                        if(zero.vx > 6D)
                            zero.vx = 6D;
                        if(zero.vx < -6D)
                            zero.vx = -6D;
                    }
                    hit = true;
                }

                if(b.age > 100 || b.y > 260D || b.y < 0.0D || hit) {
                    expiredFire.add(b);
                    //expiredAmmos.add(b);
                } else if(bx > 0 && bx < 640) {
                    putPixel(0xffff0000, bx, (int) b.y);
                    putPixel(0xffff0000, bx + 1, (int)b.y);
                }

            }

            //ammos2.removeAll(expiredAmmos);
            fire.removeAll(expiredFire);

        }

        public void move() {

            super.x += vx;
            super.y += vy;
            if(super.x < 0.0D)
            {
                super.x = 1.0D;
                vx = -vx;
            } else
            if(super.x > (double)mwidth)
            {
                super.x = mwidth - 1;
                vx = -vx;
            }

            List<Target> expiredTargets = new ArrayList<Target>();

            for (Target t: targs) {

                if(super.x <= t.x || super.x >= t.x + t.w || super.y <= 245D)
                    continue;

                t.d += 50;
                double df = t.md / 3;
                if((double)(t.fp + 1) * df <= (double)t.d && t.fp < 3)
                {
                    t.fs[t.fp] = 10 + (int)(((t.w - 6D) / 3D) * (double)t.fp);
                    t.fp++;
                    if (expl != null)
                        expl.start();
                }
                if(t.d > t.md && t.status == -1)
                {
                    t.status = 0;
                    points += t.val;
                }

                expiredTargets.add(this);
                break;

            }

            ampls.removeAll(expiredTargets);

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
