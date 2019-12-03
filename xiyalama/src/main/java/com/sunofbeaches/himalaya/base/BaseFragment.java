package com.sunofbeaches.himalaya.base;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by TrillGates on 2018/12/16.
 * God bless my code!
 */
public abstract class BaseFragment extends Fragment {

    private View mRootView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = onSubViewLoaded(inflater, container);
        return mRootView;
    }


    protected abstract View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container);

}
