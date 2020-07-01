package jp.co.stream.jstplayersdk.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.InputStream;
import java.net.URL;

import jp.co.stream.jstplayersdk.util.MyUtils;

public class PreviewCoverView extends RelativeLayout {

    private static final String TAG = "PreviewCoverView";

    private PreviewCoverViewCallbacks mPreviewCoverViewCallbacks;
    private Activity mActivity;
    private Context mContext;
    private ImageView mPreviewCoverImageView;
    private ImageButton mCloseButton;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mBaseSize;

    public interface PreviewCoverViewCallbacks {
        public void onClickCoverImage();
        public void onClose();
    }

    public void setCallbacks(PreviewCoverViewCallbacks callbacks){
        mPreviewCoverViewCallbacks = callbacks;
    }

    public PreviewCoverView(Context context) {
        super(context);
        mContext = context;
        mActivity = (Activity)this.getContext();
        mBaseSize = MyUtils.convertDpToPx(mContext, 100);
    }

    public void initView(String previewCoverURL, int screenWidth, int screenHeight) {

        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        this.setBackgroundColor(0x88000000);

        mPreviewCoverImageView = new ImageView(mContext);
        GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask(mPreviewCoverImageView);
        getImageAsyncTask.execute(previewCoverURL);
        mPreviewCoverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mPreviewCoverImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviewCoverViewCallbacks != null) {
                    mPreviewCoverViewCallbacks.onClickCoverImage();
                }
            }
        });
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.addView(mPreviewCoverImageView, layoutParams);

        mCloseButton = new ImageButton(mContext);
        //mCloseButton.setId(ID_CLOSE_BUTTON);
        mCloseButton.setPadding(0, 0, 0, 0);
        int closeButtonId = getResources().getIdentifier("close_button", "drawable", mContext.getPackageName());
        mCloseButton.setImageResource(closeButtonId);
        mCloseButton.setBackgroundColor(0x00000000);
        mCloseButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mCloseButton.setAdjustViewBounds(true);
        mCloseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviewCoverViewCallbacks != null) {
                    mPreviewCoverViewCallbacks.onClose();
                }
            }
        });
        layoutParams = new LayoutParams(mBaseSize /4, mBaseSize /4);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        this.addView(mCloseButton, layoutParams);
    }

    public void setVisible(final boolean visibility) {
        if (visibility) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(INVISIBLE);
        }
    }

    public void resize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;

        int imageSize = mBaseSize * 4/5;

        mPreviewCoverImageView.setX(mScreenWidth /2 - imageSize /2);
        mPreviewCoverImageView.setY(mScreenHeight /2 - imageSize /2);
    }


    public class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;

        public GetImageAsyncTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap;
            try {
                URL imageURL = new URL(params[0]);
                InputStream inputStream;
                inputStream = imageURL.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mImageView.setImageBitmap(result);
        }
    }

}
