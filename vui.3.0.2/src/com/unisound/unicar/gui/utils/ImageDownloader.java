package com.unisound.unicar.gui.utils;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * @author Brant
 * @decription
 */
public class ImageDownloader {
    private static final String TAG = "ImageLoader";
    public final static int SCALE_BY_IMAGEVIEW_WIDTH = 1;
    private Context mContext;
    private File mImgCacheDir;
    private ImageQueue mImgQueue = new ImageQueue();
    private Thread mWorkThread;
    private int mDefaultResId;

    private Handler mHandler = new Handler();
    /*
     * Cache-related fields and methods. We use a hard and a soft cache. A soft reference cache is
     * too aggressively cleared by the Garbage Collector.
     */

    private static final int HARD_CACHE_CAPACITY = 20;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds
    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
            HARD_CACHE_CAPACITY / 2, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                // Entries push-out of hard reference cache are transferred to
                // soft reference cache
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
            new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);
    private final Handler purgeHandler = new Handler();
    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    public ImageDownloader(String cachePath, int defResId) {
        mDefaultResId = defResId;

        mImgCacheDir = new File(cachePath);
        if (!mImgCacheDir.exists()) {
            mImgCacheDir.mkdirs();
        }
    }

    /**
     * Adds this bitmap to the cache.
     * 
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
            // Save file to file to ensure the same url bitmap is downloaded
            // only one time
            String fileName = url.substring(url.lastIndexOf(File.separator) + 1);
            FileHelper fileIOHelper = new FileHelper(mImgCacheDir, fileName);
            if (!fileIOHelper.checkFileExist()) {
                fileIOHelper.saveImage(bitmap, FileHelper.getImageFileCompressFormat(url), 100);
            }
        }
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(String url) {
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
        }
        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }
        // Lastly try the file cache.
        String fileName = url.substring(url.lastIndexOf(File.separator) + 1);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false; // Disable Dithering mode

        options.inPurgeable = true; // Tell to gc that whether it needs free
                                    // memory, the Bitmap can be cleared

        options.inInputShareable = true; // Which kind of reference will be used
                                         // to recover the Bitmap data after
                                         // being clear, when it will be used
                                         // in the future

        options.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap =
                BitmapFactory.decodeFile(
                        mImgCacheDir.getAbsolutePath() + File.separator + fileName, options);
        // Put into the hard cache.
        if (bitmap != null) {
            sHardBitmapCache.put(url, bitmap);
        }
        return bitmap;
    }

    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     * 
     * @param url The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     * @param hideImageIfNull Hide the imageview if url is null or empty.
     */
    public void download(String url, ImageView imageView, int res, int scaleType) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            imageView.setImageResource(res);
            forceDownload(url, imageView, scaleType);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     * 
     * @param url The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     * @param hideImageIfNull Hide the imageview if url is null or empty.
     */
    public void download(String url, ImageView imageView, boolean hideImageIfNull, int scaleType) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageBitmap(null);
            if (hideImageIfNull) {
                imageView.setVisibility(View.GONE);
            }
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        resetPurgeTimer();
        imageView.setImageResource(mDefaultResId);
        forceDownload(url, imageView, scaleType);
    }

    public void download(String url, ImageView imageView, int scaleType) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageBitmap(null);
            return;
        }
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            forceDownload(url, imageView, scaleType);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Same as download but the image is always downloaded and the cache is not used. Kept private
     * at the moment as its interest is not clear.
     */
    private void forceDownload(String url, ImageView imageView, int scaleType) {
        // State sanity: url is guaranteed to never be null in cache keys.
        if (TextUtils.isEmpty(url)) {
            imageView.setImageBitmap(null);
            return;
        }
        // Add tag for imageview
        imageView.setTag(url);
        queueImage(url, imageView, scaleType);
    }

    /**
     * 将要加载的图片任务加入队列
     * 
     * @param url
     * @param imageView
     */
    private void queueImage(String url, ImageView imageView, int scaleType) {
        // This ImageView might have been used for other images, so we clear
        // the queue of old tasks before starting.
        mImgQueue.Clean(imageView);
        ImageRef p = new ImageRef(url, imageView, scaleType);

        synchronized (mImgQueue.imageRefs) {
            mImgQueue.imageRefs.push(p);
            mImgQueue.imageRefs.notifyAll();
        }

        if (mWorkThread == null || mWorkThread.getState() == Thread.State.TERMINATED) {
            mWorkThread = new Thread(new ImageQueueManager());
            mWorkThread.setPriority(Thread.NORM_PRIORITY - 1);
        }
        // Start thread if it's not started yet
        if (mWorkThread.getState() == Thread.State.NEW) {
            mWorkThread.start();
        }
    }

    /**
     * 下载图片
     * 
     * @param url
     * @return
     */
    public static Bitmap downloadBitmap(Context context, String url) {
        // AndroidHttpClient is not allowed to be used from the main thread
        Log.i(TAG, url);
        final HttpClient client = new DefaultHttpClient();
        final HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = client.execute(httpGet);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.
                    Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
                    if (bitmap == null) {
                        Log.e(TAG, "Loaded image null.");
                    }
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            httpGet.abort();
            Log.w(TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            httpGet.abort();
            Log.w(TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            httpGet.abort();
            Log.w(TAG, "Error while retrieving bitmap from " + url, e);
        }
        return null;
    }

    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    public void setDefaultResId(int defaultResId) {
        mDefaultResId = defaultResId;
    }

    public void stop() {
        synchronized (mImgQueue) {
            mImgQueue.notifyAll();
        }
        if (mWorkThread != null) {
            try {
                Thread t = mWorkThread;
                t.join();
                mWorkThread = null;
            } catch (InterruptedException ex) {
                // so now what?
            }
        }
    }

    private class ImageRef {
        public int scaleType;
        public String url;
        /**
         * Note that this ImageView is stored as a WeakReference, so that a download in progress
         * does not prevent a killed activity's ImageView from being garbage collected. This
         * explains why we have to check that both the weak reference and the imageView are not null
         */
        public WeakReference<ImageView> imageViewReference;

        public ImageRef(String u, ImageView i, int scaleType) {
            url = u;
            imageViewReference = new WeakReference<ImageView>(i);
            this.scaleType = scaleType;
        }
    }

    private class ImageQueue {
        private Stack<ImageRef> imageRefs = new Stack<ImageRef>();

        // removes all instances of this ImageView
        public void Clean(ImageView view) {
            for (int i = 0; i < imageRefs.size();) {
                WeakReference<ImageView> imageViewReference = imageRefs.get(i).imageViewReference;
                if (imageViewReference != null && imageViewReference.get() == view) {
                    imageRefs.remove(i);
                } else
                    ++i;
            }
        }
    }

    // Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        WeakReference<ImageView> imageViewReference;
        int mDefaultResId;

        public BitmapDisplayer(Bitmap b, WeakReference<ImageView> i, int defaultResId, int scaleType) {
            imageViewReference = i;
            mDefaultResId = defaultResId;
            switch (scaleType) {
                case SCALE_BY_IMAGEVIEW_WIDTH:
                    bitmap = b;
                    int w = imageViewReference.get().getWidth();
                    if (b != null && w > 0 && b.getWidth() > 0 && b.getHeight() > 0) {
                        int x = (int) (w * ((float) b.getHeight() / b.getWidth()));
                        Logger.d(TAG, "w : " + w + ";b.getWidth() : " + b.getWidth()
                                + ";b.getHeight() : " + b.getHeight() + ";x : " + x);
                        if (x > 0) {
                            bitmap = Bitmap.createScaledBitmap(b, w, x, false);
                        }
                    }
                    break;
                default:
                    bitmap = b;
                    break;
            }
        }

        public void run() {
            ImageView imageView = null;
            if (imageViewReference != null) {
                imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageResource(mDefaultResId);
                    }
                }
            }
        }
    }

    /**
     * 下载图片的队列
     * 
     * @author Brant
     * @decription
     */
    private class ImageQueueManager implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    // Thread waits until there are images in the
                    // queue to be retrieved
                    if (mImgQueue.imageRefs.size() == 0) {
                        synchronized (mImgQueue.imageRefs) {
                            mImgQueue.imageRefs.wait();
                        }
                    }

                    // When we have images to be loaded
                    if (mImgQueue.imageRefs.size() != 0) {
                        ImageRef imageToLoad;
                        synchronized (mImgQueue.imageRefs) {
                            imageToLoad = mImgQueue.imageRefs.pop();
                        }

                        Bitmap bmp = downloadBitmap(mContext, imageToLoad.url);
                        // Put bitmap into cache
                        addBitmapToCache(imageToLoad.url, bmp);
                        // Display image in ListView on UI thread

                        if (imageToLoad.imageViewReference != null) {
                            ImageView imageView = imageToLoad.imageViewReference.get();
                            if (imageView != null) {
                                Object tag = imageView.getTag();
                                // Make sure we have the right view - thread
                                // safety
                                // defender
                                if (tag != null && ((String) tag).equals(imageToLoad.url)) {
                                    BitmapDisplayer bmpDisplayer =
                                            new BitmapDisplayer(bmp,
                                                    imageToLoad.imageViewReference, mDefaultResId,
                                                    imageToLoad.scaleType);
                                    mHandler.post(bmpDisplayer);
                                    // Activity a = (Activity) imageView.getContext();
                                    // a.runOnUiThread(bmpDisplayer);
                                }
                            }
                        }
                        if (Thread.interrupted()) break;
                    } else {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
