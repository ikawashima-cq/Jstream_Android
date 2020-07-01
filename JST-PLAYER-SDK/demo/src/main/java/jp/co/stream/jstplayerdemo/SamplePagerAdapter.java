package jp.co.stream.jstplayerdemo;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class SamplePagerAdapter extends FragmentStatePagerAdapter {

    public static final int LOOPS_COUNT = 1000;

    private final List<SamplePlayerModel> mModelList = new ArrayList<>();
    private final List<String> mTitleList = new ArrayList<>();

    @SuppressLint("WrongConstant")
    public SamplePagerAdapter(FragmentManager manager,List<SamplePlayerModel> data) {
        super(manager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        for (SamplePlayerModel model: data) {
            mModelList.add(model);
            mTitleList.add(model.getName());
        }
    }

    @Override
    public Fragment getItem(int position) {

        position = position % mModelList.size(); // use modulo for infinite cycling
        SamplePlayerFragment.newInstance(mModelList.get(position));
        return SamplePlayerFragment.newInstance(mModelList.get(position));
    }

    @Override
    public int getCount() {
        return mModelList.size()*LOOPS_COUNT; // simulate infinite by big number of products
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitleList != null && mTitleList.size() > 0)
        {
            position = position % mTitleList.size(); // use modulo for infinite cycling
            return mTitleList.get(position);
        }
        else
        {
            return null;
        }
    }


}