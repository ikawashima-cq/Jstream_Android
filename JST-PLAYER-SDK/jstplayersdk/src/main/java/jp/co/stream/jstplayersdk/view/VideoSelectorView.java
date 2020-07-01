package jp.co.stream.jstplayersdk.view;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.stream.jstplayersdk.util.DebugLog;

public class VideoSelectorView extends RelativeLayout {

    private static final String TAG = "jstplayersdk";

    private Activity mActivity;
    private RelativeLayout mBackGroundView;
    private RelativeLayout mMenuView;
    private RelativeLayout mMenuArea;
    private TextView mTitleLabel;
    private VideoSelectorViewCallBacks mVideoSelectorViewCallBacks;
    private List<MenuItemView> mMenuItemList;
    private boolean isSelectable;

    private static final int ID_BACKGROUND_VIEW = 1;
    private static final int ID_MENU_VIEW = 2;
    private static final int ID_TITLE_LABEL = 3;
    private static final int ID_MENU_ITEM_VIEW = 10;

    public interface VideoSelectorViewCallBacks {
        public void onMenuSelect(int value, String url);
        public void onMenuCancel();
    }

    public void setCallbacks(VideoSelectorViewCallBacks callbacks) {
        mVideoSelectorViewCallBacks = callbacks;
    }

    public VideoSelectorView(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    public void setMenu(JSONArray menuList, int selectedValue) {

        isSelectable = true;

        LayoutParams layoutParams;

        mBackGroundView = new RelativeLayout(mActivity);
        mBackGroundView.setId(ID_BACKGROUND_VIEW);
        mBackGroundView.setBackgroundColor(0xAA000000);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mBackGroundView, layoutParams);
        mBackGroundView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoSelectorViewCallBacks != null) {
                    mVideoSelectorViewCallBacks.onMenuCancel();
                }
            }
        });

        mMenuView = new RelativeLayout(mActivity);
        mMenuView.setId(ID_MENU_VIEW);
        mMenuView.setBackgroundColor(0xFFFFFFFF);
        mBackGroundView.addView(mMenuView);
        mMenuView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mTitleLabel = new TextView(mActivity);
        mTitleLabel.setId(ID_TITLE_LABEL);
        mTitleLabel.setBackgroundColor(0x00000000);
        mTitleLabel.setTypeface(Typeface.DEFAULT_BOLD);
        mTitleLabel.setTextColor(0xFF555555);
        mTitleLabel.setText("画質選択");
        mMenuView.addView(mTitleLabel);

        mMenuArea = new RelativeLayout(mActivity);
        mMenuArea.setId(ID_MENU_VIEW);
        mMenuArea.setBackgroundColor(0x00000000);
        mMenuView.addView(mMenuArea);

        if (menuList != null) {
            mMenuItemList = new ArrayList<MenuItemView>();
            try {
                for (int i = 0; i < menuList.length(); i++) {
                    JSONObject menuData = menuList.getJSONObject(i);
                    final int menuValue = menuData.getInt("value");
                    MenuItemView menuItemView = new MenuItemView(mActivity, menuData.getString("title"), menuData.getInt("value"), menuData.getString("url"));
                    int menuId = ID_MENU_ITEM_VIEW + i;
                    menuItemView.setId(menuId);
                    mMenuItemList.add(menuItemView);
                    mMenuArea.addView(menuItemView);
                    DebugLog.d(TAG, "menuData.getInt(value): "+menuData.getInt("value"));
                    DebugLog.d(TAG, "selectedValue: "+selectedValue);
                    if (menuData.has("value") && menuData.getInt("value") == selectedValue) {
                        menuItemView.setChecked(true);
                    } else {
                        menuItemView.setChecked(false);
                    }
                    menuItemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if (isSelectable) {
                                isSelectable = false;
                                for (int i = 0; i < mMenuItemList.size(); i++) {
                                    if (v == mMenuItemList.get(i)) {
                                        mMenuItemList.get(i).setChecked(true);
                                    } else {
                                        mMenuItemList.get(i).setChecked(false);
                                    }
                                }
                                Timer timer = new Timer(true);
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (mVideoSelectorViewCallBacks != null) {
                                            mVideoSelectorViewCallBacks.onMenuSelect(((MenuItemView)v).getValue(), ((MenuItemView)v).getUrl());
                                        }
                                    }
                                }, 300);
                            }
                        }
                    });
                }
            } catch (Exception e) {
            }
        }
    }

    public void setVideoSize(int width, int height) {
        if (mMenuItemList != null && mMenuItemList.size() > 0) {

            int menuViewSize = width * 8 / 9;
            if (height < width) menuViewSize = height * 9 / 10;
            int titleLabelSize = menuViewSize * 1 / 7;
            int menuAreaSize = menuViewSize - titleLabelSize;
            int fontSize = titleLabelSize * 1 / 3;

            LayoutParams layoutParams;

            int topPadding = 0;
            int bottomPadding = menuViewSize * 1 / 15;
            int sidePadding = menuViewSize * 1 / 15;
            mMenuView.setPadding(sidePadding, topPadding, sidePadding, bottomPadding);
            layoutParams = new LayoutParams(menuViewSize, menuViewSize);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mMenuView.setLayoutParams(layoutParams);

            mTitleLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            mTitleLabel.setGravity(Gravity.CENTER);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, titleLabelSize);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mTitleLabel.setLayoutParams(layoutParams);

            mMenuArea.setPadding(0, 2, 0, 2);
            mMenuArea.setBackgroundColor(0xFFCCCCCC);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.BELOW, ID_TITLE_LABEL);

            mMenuArea.setLayoutParams(layoutParams);

            for (int i = 0; i < mMenuItemList.size(); i++) {
                layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (menuAreaSize - (topPadding + bottomPadding)) / mMenuItemList.size());
                int menuId = ID_MENU_ITEM_VIEW + i;
                if (menuId > ID_MENU_ITEM_VIEW) {
                    layoutParams.addRule(RelativeLayout.BELOW, menuId - 1);
                }
                //mMenuItemList.get(i).setPadding(menuViewSize /20, menuViewSize /40, menuViewSize /20, 0);
                mMenuItemList.get(i).setPadding(0, 1, 0, 1);
                mMenuItemList.get(i).setLayoutParams(layoutParams);
                mMenuItemList.get(i).setFontSize(fontSize);
            }
        }
    }

    public void setVisible(boolean visibility) {
        if (visibility) {
            isSelectable = true;
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    private class MenuItemView extends RelativeLayout {

        private RelativeLayout mMenuItem;
        private TextView mTitleLabel;
        private RadioButton mRadioButton;
        private int mValue;
        private String mUrl;

        public MenuItemView(Activity activity, String title, int value, String url) {
            super(activity);

            mValue = value;
            mUrl = url;

            LayoutParams layoutParams;

            mMenuItem = new RelativeLayout(activity);
            mMenuItem.setBackgroundColor(0xFFCCCCCC);
            mMenuItem.setPadding(0, 1, 0, 1);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(mMenuItem, layoutParams);

            RelativeLayout mMenuItemView = new RelativeLayout(activity);
            mMenuItemView.setBackgroundColor(0xFFFFFFFF);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mMenuItem.addView(mMenuItemView, layoutParams);

            mRadioButton = new RadioButton(activity);
            mRadioButton.setClickable(false);
            mRadioButton.setSelected(true);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            mMenuItemView.addView(mRadioButton, layoutParams);

            mTitleLabel = new TextView(activity);
            mTitleLabel.setSingleLine(true);
            mTitleLabel.setBackgroundColor(0x00000000);
            mTitleLabel.setTextColor(0xFF555555);
            mTitleLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, 100);
            mTitleLabel.setText(title);
            mTitleLabel.setGravity(Gravity.CENTER_VERTICAL);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mMenuItemView.addView(mTitleLabel, layoutParams);
        }

        public void setFontSize(int fontSize) {
            mTitleLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        }

        public void setChecked(boolean checked) {
            mRadioButton.setChecked(checked);
        }

        public int getValue() {
            return mValue;
        }

        public String getUrl() {
            return mUrl;
        }
    }
}