package com.hj.app.oschina.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hj.app.oschina.interf.BaseFragmentInterface;

import butterknife.ButterKnife;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusFragment;

/**
 * Created by huangjie08 on 2016/8/18.
 */
public abstract class BaseFragment<P extends Presenter> extends NucleusFragment<P> implements BaseFragmentInterface {

    protected View mRoot;
    protected Bundle mBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null) {
                parent.removeView(mRoot);
            }
        } else {
            mRoot = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, mRoot);
            initView(mRoot);
            initData();
        }
        return mRoot;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRoot = null;
        mBundle = null;
    }

    protected void initBundle(Bundle bundle) {
    }

    public abstract int getLayoutId();
}
