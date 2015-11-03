package gfx.scene;

import android.content.Context;
import android.graphics.Color;

import gfx.graphics.ScreenGraphics;

/**
 * Created by vhalme on 02/11/15.
 */
public abstract class Scene extends ScreenGraphics {

    private int bgPic[];
    private int bgCol;

    //private ResourceManager _rm;

    public Scene(Context context) {

        super(context);

        bgPic = null;
        bgCol = Color.BLACK;

    }

    public abstract void start();

    public abstract void stop();

    public abstract void pause();

    public abstract void resume();

    public void init() {

        System.out.println("Initializing scene:");

        //_rm = new ResourceManager(this);
        System.err.println("Scene initialized. Starting...");
        start();

    }

    /*
    public ResourceManager getResourceManager() {
        return _rm;
    }
    */

    public void drawScene() {

        /*
        for(Enumeration enum = leaves.elements(); enum.hasMoreElements();)
        {
            LeafNode lf = (LeafNode)enum.nextElement();
            if(lf.isLightable())
            {
                Lightable lt;
                for(Enumeration enum_lights = lights.elements(); enum_lights.hasMoreElements(); lt.applyLighting((LightSource)enum_lights.nextElement()))
                {
                    lt = (Lightable)lf;
                    lt.applyAmbientLight(ambient_light);
                }

            }
            cam.transform();
            cam.compile();
            Math3D.matmult(lf.getLocalMatrix(), cam.getLocalMatrix());
            lf.compile();
            lf.correctView(cam, center_x, center_y);
            if(lf.isDrawable())
                ((Drawable)lf).draw(getPixels(), getFloatZBuffer(), getWidth(), getHeight());
        }
        */

    }

    public void setBackgroundColor(int bgCol) {
        this.bgCol = bgCol;
    }

    public void setBackground(int[] bgPic) {
        this.bgPic = bgPic;
    }

    public void clear() {

        if (bgPic == null) {
            for (int y = 0; y < 350; y++) {
                for (int x = 0; x < 640; x++) {
                    pixels[y * 640 + x] = bgCol;
                    //bitmap.setPixel(x, y, bgCol);
                }
            }
        } else {
            for (int y = 0; y < 350; y++) {
                for (int x = 0; x < 640; x++) {
                    pixels[y * 640 + x] = bgPic[y * 640 + x];
                    //bitmap.setPixel(x, y, bgPic[y * 640 + x]);
                }
            }
        }

        /*
        int dotx = 200 + (int)(Math.random()*240);
        //System.out.println("ditx = " + dotx);
        for (int y = 100; y < 120; y++) {
            for (int x = dotx; x < dotx + 20; x++) {
                bitmap.setPixel(x, y, Color.GREEN);
            }
        }
        */

    }


}
