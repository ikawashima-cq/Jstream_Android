package jp.co.stream.jstplayersdk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.common.base.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.stream.jstplayersdk.util.DebugLog;

public class SeekBarThumbnailView extends RelativeLayout {

    private static final String TAG = "SeekBarThumbnailView";
    private int mInterval; //seccond

    private Context mContext;
    private ImageView mThumbnailView;
    private String mThumbnailPath;
    private String mMediaId;
    private List<GetImageTask> mGetImageTaskList;

    public SeekBarThumbnailView(Context context) {
        super(context);
        mContext = context;

        mThumbnailView = new ImageView(mContext);
        mThumbnailView.setPadding(0, 0, 0, 0);
        mThumbnailView.setBackgroundColor(0xFF000000);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mThumbnailView.setAdjustViewBounds(true);
        addView(mThumbnailView, layoutParams);
    }

    public void loadThumbnailImage(String mediaId, int duration, int interval) {

        mMediaId = mediaId;
        mInterval = interval;
        String programId = mMediaId.substring(0, 4);

        if (Strings.isNullOrEmpty(mMediaId)) {
            return;
        }
        if (mInterval <= 0) {
            mInterval = 10;
        }
        int thumbnailNumber = duration / mInterval;

        mThumbnailPath = mContext.getFilesDir().getAbsolutePath() + "/player_seekthumbnailimages/";
        File thumbnailPath = new File(mThumbnailPath);
        File mediaThumbnailPath = new File(mThumbnailPath + mMediaId);

        if (!mediaThumbnailPath.exists()) {
            if (thumbnailPath.exists()) {
                deleteDirectory(thumbnailPath);
            }
            mediaThumbnailPath.mkdirs();
        }

        mGetImageTaskList = new ArrayList<GetImageTask>();
        String url;
        int urlId = mContext.getResources().getIdentifier("player_thumbnail", "string", mContext.getPackageName());
        for (int index = 0; index < thumbnailNumber; index ++) {
            File thumbnailFilePath = new File(mThumbnailPath + mMediaId + "/thumbnail" + index + ".png");
            if (!thumbnailFilePath.exists()) {
                String indexString = String.format("%05d", index);
                url = mContext.getResources().getString(urlId, programId, mMediaId, indexString);
                mGetImageTaskList.add(new GetImageTask(index));
                mGetImageTaskList.get(mGetImageTaskList.size()-1).execute(url);
            }
        }
    }

    public int getInterval() {
        return mInterval;
    }

    public void setThumbnailImage(int currentTime) {
        //int index = (int)Math.floor(currentTime / mInterval);
        int index = (int)Math.ceil((float)currentTime / (float)mInterval);
        mThumbnailView.setImageBitmap(readThumbnailImage(index));
    }

    class GetImageTask extends AsyncTask<String, Void, InputStream> {
        private int mIndex;

        public GetImageTask(int index) {
            mIndex = index;
        }
        @Override
        protected InputStream doInBackground(String... params) {
            try {
                if (!isCancelled()) {
                    URL imageUrl = new URL(params[0]);
                    InputStream imageInputStream = imageUrl.openStream();
                    return imageInputStream;
                } else {
                    return null;
                }
            } catch (Exception e) {
                DebugLog.d(TAG, "e:"+e.toString());
                return null;
            }
        }
        @Override
        protected void onPostExecute(InputStream imageInputStream) {
            if (imageInputStream != null) {
                writeThumbnailImage(mIndex, imageInputStream);
            }
        }
    }

    private Bitmap readThumbnailImage(int index) {
        String fileName = mThumbnailPath + mMediaId + "/thumbnail" + index + ".png";
        Bitmap bitmap = null;
        try {
            InputStream fileInputStream = new FileInputStream(fileName);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            return bitmap;
        } catch (Exception e) {
        }
        return bitmap;
    }

    private void writeThumbnailImage(final int index, final InputStream imageInputStream) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream imageOutputStream = new FileOutputStream(mThumbnailPath + mMediaId + "/thumbnail" + index + ".png");
                    try {
                        byte[] bufferSize = new byte[1024 * 8];
                        int length = 0;
                        while ((length = imageInputStream.read(bufferSize)) > 0) {
                            imageOutputStream.write(bufferSize, 0, length);
                        }
                        imageOutputStream.flush();
                    } catch (Exception e) {
                    } finally {
                        imageOutputStream.close();
                    }
                } catch (Exception e) {
                }
            }
        }).start();

		/*
		File[] files;
		files = directory.listFiles();
		long size = 0;
		for (int i = 0; i < files.length; i++) {
			size += files[i].length();
		}
		DebugLog.d(TAG, "size:"+size);
		*/
    }

    private static void deleteDirectory(File file){
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        }
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDirectory(files[i]);
            }
            file.delete();
        }
    }

    public void cancelLoadThumbnailImage() {
        if (mGetImageTaskList != null) {
            for (int i = 0; i < mGetImageTaskList.size(); i++) {
                mGetImageTaskList.get(i).cancel(true);
            }
        }
    }

}
