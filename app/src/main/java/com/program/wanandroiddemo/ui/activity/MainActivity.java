package com.program.wanandroiddemo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.program.wanandroiddemo.R;
import com.program.wanandroiddemo.base.BaseActivity;
import com.program.wanandroiddemo.base.BaseApplication;
import com.program.wanandroiddemo.base.BaseFragment;
import com.program.wanandroiddemo.presenter.IMainActivityDataHandle;
import com.program.wanandroiddemo.ui.fragment.RecommendFragment;
import com.program.wanandroiddemo.ui.fragment.SystemFragment;
import com.program.wanandroiddemo.ui.fragment.UserFragment;
import com.program.wanandroiddemo.utils.Constants;
import com.program.wanandroiddemo.utils.LogUtils;
import com.program.wanandroiddemo.utils.PresenterManager;
import com.program.wanandroiddemo.utils.SharedPreferencesUtils;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView mNavigationView;

    private FragmentManager mFm;
    private RecommendFragment mRecommendFragment;
    private SystemFragment mSystemFragment;
    private UserFragment mUserFragment;
    private IMainActivityDataHandle mMainActivityDataHandle;


    @Override
    protected void initPresenter() {
    }

    @Override
    protected void initEvent() {
        initListener();
    }

    @Override
    protected void initView() {
        initBaseData();
        initFragments();
    }

    private void initBaseData() {
        mMainActivityDataHandle = PresenterManager.getInstance().getMainActivityDataHandle();
        mMainActivityDataHandle.initToken();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }


    private void initFragments() {
        mFm = getSupportFragmentManager();
        mRecommendFragment = new RecommendFragment();
        mSystemFragment = new SystemFragment();
        mUserFragment = new UserFragment();

        //?????????????????????????????????????????????

        switchFragment(mRecommendFragment);
    }



    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        LogUtils.d(MainActivity.this,"???????????????");
//                        mRecommendFragment = new RecommendFragment();
                        switchFragment(mRecommendFragment);
                        break;
                    case R.id.system:
                        LogUtils.d(MainActivity.this,"???????????????");
                        switchFragment(mSystemFragment);
                        break;
                    case R.id.user:
                        LogUtils.d(MainActivity.this,"???????????????");
                        switchFragment(mUserFragment);
                        break;

                }
                return true;
            }
        });

    }

    /**
     * ??????????????????Fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment) {
        //???????????????Fragment?????????????????????fragment????????????????????????????????????
        if (lastOneFragment == targetFragment){
            return;
        }
        //?????????add???hide??????????????????Fragment
        FragmentTransaction fragmentTransaction = mFm.beginTransaction();
        //???????????????????????????
        if (!targetFragment.isAdded()) {
            fragmentTransaction.add(R.id.main_page_container,targetFragment);
        }else {
            fragmentTransaction.show(targetFragment);
        }
        if (lastOneFragment!=null){
            fragmentTransaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;
        fragmentTransaction.commit();

//        //????????????
//        FragmentTransaction fragmentTransaction = mFm.beginTransaction();
//        fragmentTransaction.replace(R.id.main_page_container,targetFragment);
//        //????????????
//        fragmentTransaction.commit();
    }


    /**
     * ??????????????????
     */
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if( keyCode== KeyEvent.KEYCODE_HOME){
            return true;
        } else if( keyCode== KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()- exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}