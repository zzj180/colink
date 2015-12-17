/**
 * org.hermit.android.core: useful Android foundation classes.
 * 
 * These classes are designed to help build various types of application.
 * 
 * <br>
 * Copyright 2009-2010 Ian Cameron Smith
 * 
 * <p>
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License version 2 as published by the Free Software Foundation (see COPYING).
 * 
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */

package com.unisound.unicar.gui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.unisound.unicar.gui.utils.Logger;

public abstract class SurfaceRunner extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * Create a SurfaceRunner instance.
     * 
     * @param app The application context we're running in.
     */
    public SurfaceRunner(Context app) {
        super(app);
        init(app);
    }

    /**
     * Create a SurfaceRunner instance.
     * 
     * @param app The application context we're running in.
     * @param attrs Layout attributes for this SurfaceRunner.
     */
    public SurfaceRunner(Context app, AttributeSet attrs) {
        super(app, attrs);
        init(app);
    }

    /**
     * Initialize this SurfaceRunner instance.
     * 
     * @param app The application context we're running in.
     * @param options Options for this SurfaceRunner. A bitwise OR of SURFACE_XXX constants.
     */
    private void init(Context context) {
        appContext = context;
        animationDelay = 0;

        // Register for events on the surface.
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Workaround for edge fading -- sometimes after repeated orientation
        // changes one edge will fade; this fixes it.
        setHorizontalFadingEdgeEnabled(false);
        setVerticalFadingEdgeEnabled(false);
    }

    // ******************************************************************** //
    // State Handling.
    // ******************************************************************** //

    /**
     * Set the delay in ms in each iteration of the main loop.
     * 
     * @param delay The time in ms to sleep each time round the main animation loop. If zero, we
     *        will not sleep, but will run continuously.
     * 
     *        <p>
     *        If you want to do all your animation under direct app control using
     *        {@link #postUpdate()}, just set a large delay. You may want to consider using 1000 --
     *        i.e. one second -- to make sure you get a refresh at a decent interval.
     */
    public void setDelay(long delay) {
        animationDelay = delay;
    }

    /**
     * This is called immediately after the surface is first created. Implementations of this should
     * start up whatever rendering code they desire.
     * 
     * Note that only one thread can ever draw into a Surface, so you should not draw into the
     * Surface here if your normal rendering will be in another thread.
     * 
     * @param holder The SurfaceHolder whose surface is being created.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setEnable(ENABLE_SURFACE, "surfaceCreated");
    }

    /**
     * This is called immediately after any structural changes (format or size) have been made to
     * the surface. This method is always called at least once, after surfaceCreated(SurfaceHolder).
     * 
     * @param holder The SurfaceHolder whose surface has changed.
     * @param format The new PixelFormat of the surface.
     * @param width The new width of the surface.
     * @param height The new height of the surface.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        setSize(format, width, height);
        setEnable(ENABLE_SIZE, "set size " + width + "x" + height);
    }

    /**
     * This is called immediately before a surface is destroyed. After returning from this call, you
     * should no longer try to access this surface. If you have a rendering thread that directly
     * accesses the surface, you must ensure that thread is no longer touching the Surface before
     * returning from this function.
     * 
     * @param holder The SurfaceHolder whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        clearEnable(ENABLE_SURFACE, "surfaceDestroyed");
    }

    /**
     * Pause the app. Applications must call this from their Activity.onPause() method.
     */
    // public void onPause() {
    // Logger.d(TAG, "onPause");
    // clearEnable(ENABLE_RESUMED, "onPause");
    // }

    /**
     * We're resuming the app. Applications must call this from their Activity.onResume() method.
     */
    // public void onResume() {
    // Logger.d(TAG, "onResume");
    // setEnable(ENABLE_RESUMED, "onResume");
    // }

    /**
     * Set the given enable flag, and see if we're good to go.
     * 
     * @param flag The flag to set.
     * @param why Short tag explaining why, for debugging.
     */
    private void setEnable(int flag, String why) {
        boolean enabled1 = false;
        boolean enabled2 = false;
        synchronized (surfaceHolder) {
            enabled1 = (enableFlags & ENABLE_ALL) == ENABLE_ALL;
            enableFlags |= flag;
            enabled2 = (enableFlags & ENABLE_ALL) == ENABLE_ALL;

            Logger.i(TAG, "EN + " + why + " -> " + enableString());
        }

        // Are we all set?
        if (!enabled1 && enabled2) startRun();
    }

    /**
     * Start the surface running. Applications must call this to set the surface going. They may use
     * this to implement their own level of start/stop control, for example to implement a "pause"
     * button.
     */
    public void surfaceStart() {
        setEnable(ENABLE_STARTED, "surfaceStart");
    }

    /**
     * Stop the surface running. Applications may call this to stop the surface running. They may
     * use this to implement their own level of start/stop control, for example to implement a
     * "pause" button.
     */
    public void surfaceStop() {
        clearEnable(ENABLE_STARTED, "surfaceStop");
    }

    /**
     * Get the current enable state as a string for debugging.
     * 
     * @return The current enable state as a string.
     */
    private String enableString() {
        char[] buf = new char[3];
        buf[0] = (enableFlags & ENABLE_SURFACE) != 0 ? 'S' : '-';
        buf[1] = (enableFlags & ENABLE_SIZE) != 0 ? 'Z' : '-';
        // buf[2] = (enableFlags & ENABLE_RESUMED) != 0 ? 'R' : '-';
        buf[2] = (enableFlags & ENABLE_STARTED) != 0 ? 'A' : '-';
        return String.valueOf(buf);
    }

    /**
     * Clear the given enable flag, and see if we need to shut down.
     * 
     * @param flag The flag to clear.
     * @param why Short tag explaining why, for debugging.
     */
    private void clearEnable(int flag, String why) {
        boolean enabled1 = false;
        boolean enabled2 = false;
        synchronized (surfaceHolder) {
            enabled1 = (enableFlags & ENABLE_ALL) == ENABLE_ALL;
            enableFlags &= ~flag;
            enabled2 = (enableFlags & ENABLE_ALL) == ENABLE_ALL;

            Logger.i(TAG, "EN - " + why + " -> " + enableString());
        }

        // Do we need to stop?
        if (enabled1 && !enabled2) stopRun();
    }

    /**
     * Start the animation running. All the conditions we need to run are present (surface, size,
     * resumed).
     */
    private void startRun() {
        synchronized (surfaceHolder) {
            if (animTicker != null && animTicker.isAlive()) animTicker.kill();
            Logger.i(TAG, "set running: start ticker");
            animTicker = new ThreadTicker();
        }
    }

    /**
     * Stop the animation running. Our surface may have been destroyed, so stop all accesses to it.
     * If the caller is not the ticker thread, this method will only return when the ticker thread
     * has died.
     */
    private void stopRun() {
        // Kill the thread if it's running, and wait for it to die.
        // This is important when the surface is destroyed, as we can't
        // touch the surface after we return. But if I am the ticker
        // thread, don't wait for myself to die.
        Ticker ticker = null;
        synchronized (surfaceHolder) {
            ticker = animTicker;
        }
        if (ticker != null && ticker.isAlive()) {
            if (onSurfaceThread())
                ticker.kill();
            else
                ticker.killAndWait();
        }
        synchronized (surfaceHolder) {
            animTicker = null;
        }
    }

    /**
     * Set the size of the table.
     * 
     * @param format The new PixelFormat of the surface.
     * @param width The new width of the surface.
     * @param height The new height of the surface.
     */
    private void setSize(int format, int width, int height) {
        synchronized (surfaceHolder) {
            canvasWidth = width;
            canvasHeight = height;

            // Create the pixmap for the background image.
            switch (format) {
                case PixelFormat.A_8:
                    canvasConfig = Bitmap.Config.ALPHA_8;
                    break;
                case PixelFormat.RGBA_4444:
                    canvasConfig = Bitmap.Config.ARGB_4444;
                    break;
                case PixelFormat.RGBA_8888:
                    canvasConfig = Bitmap.Config.ARGB_8888;
                    break;
                case PixelFormat.RGB_565:
                    canvasConfig = Bitmap.Config.RGB_565;
                    break;
                default:
                    canvasConfig = Bitmap.Config.RGB_565;
                    break;
            }

        }

    }

    // ******************************************************************** //
    // Run Control.
    // ******************************************************************** //

    /**
     * Asynchronously schedule an update; i.e. a frame of animation. This can only be called if the
     * SurfaceRunner was created with the option LOOPED_TICKER.
     */
    public void postUpdate() {
        synchronized (surfaceHolder) {
            if (!(animTicker instanceof LoopTicker)) {
                throw new IllegalArgumentException("Can't post updates"
                        + " without LOOPED_TICKER set");
            }
            LoopTicker ticker = (LoopTicker) animTicker;
            ticker.post();
        }
    }

    private void tick() {
        // Do the application's physics.
        long now = System.currentTimeMillis();
        doUpdate(now);

        // And update the screen.
        refreshScreen(now);

    }

    /**
     * Draw the game board to the screen in its current state, as a one-off. This can be used to
     * refresh the screen.
     */
    private void refreshScreen(long now) {
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
                doDraw(canvas, now);
            }
        } finally {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the Surface in an
            // inconsistent state
            if (canvas != null && surfaceHolder != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * Update the state of the application for the current frame.
     * 
     * <p>
     * Applications must override this, and can use it to update for example the physics of a game.
     * This may be a no-op in some cases.
     * 
     * <p>
     * doDraw() will always be called after this method is called; however, the converse is not
     * true, as we sometimes need to draw just to update the screen. Hence this method is useful for
     * updates which are dependent on time rather than frames.
     * 
     * @param now Current time in ms.
     */
    protected abstract void doUpdate(long now);

    /**
     * Draw the current frame of the application.
     * 
     * <p>
     * Applications must override this, and are expected to draw the entire screen into the provided
     * canvas.
     * 
     * <p>
     * This method will always be called after a call to doUpdate(), and also when the screen needs
     * to be re-drawn.
     * 
     * @param canvas The Canvas to draw into.
     * @param now Current time in ms. Will be the same as that passed to doUpdate(), if there was a
     *        preceeding call to doUpdate().
     */
    protected abstract void doDraw(Canvas canvas, long now);

    // ******************************************************************** //
    // Client Utilities.
    // ******************************************************************** //

    /**
     * Get the String value of a resource.
     * 
     * @param resid The ID of the resource we want.
     * @return The resource value.
     */
    public String getRes(int resid) {
        return appContext.getString(resid);
    }

    /**
     * Get a Bitmap which is the same size and format as the surface. This can be used to get an
     * off-screen rendering buffer, for example.
     * 
     * @return A Bitmap which is the same size and pixel format as the screen.
     */
    public Bitmap getBitmap() {
        return Bitmap.createBitmap(canvasWidth, canvasHeight, canvasConfig);
    }

    /**
     * Get a Bitmap of a given size, in the same format as the surface. This can be used to get an
     * off-screen rendering buffer, for example.
     * 
     * @param w Desired width in pixels.
     * @param h Desired height in pixels.
     * @return A Bitmap which is the same size and pixel format as the screen.
     */
    public Bitmap getBitmap(int w, int h) {
        return Bitmap.createBitmap(w, h, canvasConfig);
    }

    /**
     * Determine whether the caller is on the surface's animation thread.
     * 
     * @return The resource value.
     */
    public boolean onSurfaceThread() {
        return Thread.currentThread() == animTicker;
    }

    // ******************************************************************** //
    // Private Classes.
    // ******************************************************************** //

    /**
     * Base interface for the ticker we use to control the animation.
     */
    private interface Ticker {
        // Stop this thread. There will be no new calls to tick() after this.
        public void kill();

        // Stop this thread and wait for it to die. When we return, it is
        // guaranteed that tick() will never be called again.
        //
        // Caution: if this is called from within tick(), deadlock is
        // guaranteed.
        public void killAndWait();

        // Run method for this thread -- simply call tick() a lot until
        // enable is false.
        public void run();

        // Determine whether this ticker is still going.
        public boolean isAlive();
    }

    /**
     * Thread-based ticker class. This may be faster than LoopTicker.
     */
    private class ThreadTicker extends Thread implements Ticker {

        // Constructor -- start at once.
        private ThreadTicker() {
            super("Surface Runner");
            Logger.v(TAG, "ThreadTicker: start");
            enable = true;
            start();
        }

        // Stop this thread. There will be no new calls to tick() after this.
        @Override
        public void kill() {
            Logger.v(TAG, "ThreadTicker: kill");

            enable = false;
        }

        // Stop this thread and wait for it to die. When we return, it is
        // guaranteed that tick() will never be called again.
        //
        // Caution: if this is called from within tick(), deadlock is
        // guaranteed.
        @Override
        public void killAndWait() {
            Logger.v(TAG, "ThreadTicker: killAndWait");

            if (Thread.currentThread() == this)
                throw new IllegalStateException("ThreadTicker.killAndWait()"
                        + " called from ticker thread");

            enable = false;

            // Wait for the thread to finish. Ignore interrupts.
            if (isAlive()) {
                boolean retry = true;
                while (retry) {
                    try {
                        join();
                        retry = false;
                    } catch (InterruptedException e) {}
                }
                Logger.v(TAG, "ThreadTicker: killed");
            } else {
                Logger.v(TAG, "Ticker: was dead");
            }
        }

        // Run method for this thread -- simply call tick() a lot until
        // enable is false.
        @Override
        public void run() {
            while (enable) {
                tick();

                if (animationDelay != 0) try {
                    sleep(animationDelay);
                } catch (InterruptedException e) {}
            }
        }

        // Flag used to terminate this thread -- when false, we die.
        private boolean enable = false;
    }

    /**
     * Looper-based ticker class. This has the advantage that asynchronous updates can be scheduled
     * by passing it a message.
     */
    private class LoopTicker extends Thread implements Ticker {
        // Constructor -- start at once.
        private LoopTicker() {
            super("Surface Runner");
            Logger.v(TAG, "Ticker: start");
            start();
        }

        // Post a tick. An update will be done near-immediately on the
        // appropriate thread.
        public void post() {
            synchronized (this) {
                if (msgHandler == null) return;

                // Remove any delayed ticks.
                msgHandler.removeMessages(MSG_TICK);

                // Do a tick right now.
                msgHandler.sendEmptyMessage(MSG_TICK);
            }
        }

        // Stop this thread. There will be no new calls to tick() after this.
        @Override
        public void kill() {
            Logger.v(TAG, "LoopTicker: kill");

            synchronized (this) {
                if (msgHandler == null) return;

                // Remove any delayed ticks.
                msgHandler.removeMessages(MSG_TICK);

                // Do an abort right now.
                msgHandler.sendEmptyMessage(MSG_ABORT);
            }
        }

        // Stop this thread and wait for it to die. When we return, it is
        // guaranteed that tick() will never be called again.
        //
        // Caution: if this is called from within tick(), deadlock is
        // guaranteed.
        @Override
        public void killAndWait() {
            Logger.v(TAG, "LoopTicker: killAndWait");

            if (Thread.currentThread() == this)
                throw new IllegalStateException("LoopTicker.killAndWait()"
                        + " called from ticker thread");

            synchronized (this) {
                if (msgHandler == null) return;

                // Remove any delayed ticks.
                msgHandler.removeMessages(MSG_TICK);

                // Do an abort right now.
                msgHandler.sendEmptyMessage(MSG_ABORT);
            }

            // Wait for the thread to finish. Ignore interrupts.
            if (isAlive()) {
                boolean retry = true;
                while (retry) {
                    try {
                        join();
                        retry = false;
                    } catch (InterruptedException e) {}
                }
                Logger.v(TAG, "LoopTicker: killed");
            } else {
                Logger.v(TAG, "LoopTicker: was dead");
            }
        }

        @Override
        public void run() {
            Looper.prepare();

            msgHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_TICK:
                            tick();
                            if (!msgHandler.hasMessages(MSG_TICK))
                                msgHandler.sendEmptyMessageDelayed(MSG_TICK, animationDelay);
                            break;
                        case MSG_ABORT:
                            Looper.myLooper().quit();
                            break;
                    }
                }
            };

            // Schedule the first tick.
            msgHandler.sendEmptyMessageDelayed(MSG_TICK, animationDelay);

            // Go into the processing loop.
            Looper.loop();
        }

        // Message codes.
        private static final int MSG_TICK = 6;
        private static final int MSG_ABORT = 9;

        // Our message handler.
        private Handler msgHandler = null;
    }

    // ******************************************************************** //
    // Class Data.
    // ******************************************************************** //

    // Debugging tag.
    private static final String TAG = "SurfaceRunner";
    // Enable flags. In order to run, we need onSurfaceCreated() and
    // onResume(), which can come in either order. So we track which ones
    // we have by these flags. When all are set, we're good to go. Note
    // that this is distinct from the game state machine, and its pause
    // and resume actions -- the whole game is enabled by the combination
    // of these flags set in enableFlags.
    private static final int ENABLE_SURFACE = 0x01;
    private static final int ENABLE_SIZE = 0x02;
    // private static final int ENABLE_RESUMED = 0x04;
    private static final int ENABLE_STARTED = 0x08;
    private static final int ENABLE_ALL = ENABLE_SURFACE | ENABLE_SIZE | ENABLE_STARTED;
    // Application handle.
    private Context appContext;

    // The surface manager for the view.
    private SurfaceHolder surfaceHolder = null;

    // The time in ms to sleep each time round the main animation loop.
    // If zero, we will not sleep, but will run continuously.
    private long animationDelay = 0;

    // Enablement flags; see comment above.
    private int enableFlags = 0;

    // Width, height and pixel format of the surface.
    private int canvasWidth = 0;
    private int canvasHeight = 0;
    private Bitmap.Config canvasConfig = null;

    // The ticker thread which runs the animation. null if not active.
    private Ticker animTicker = null;

}
