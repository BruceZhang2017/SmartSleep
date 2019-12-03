package com.sunofbeaches.himalaya.adapters;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sunofbeaches.himalaya.utils.FragmentCreator;

/**
 * Created by TrillGates on 2018/12/16.
 * God bless my code!
 */
public class MainContentAdapter extends FragmentPagerAdapter {

    public MainContentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentCreator.getFragment(position);
    }

    @Override
    public int getCount() {
        return FragmentCreator.PAGE_COUNT;
    }
}
