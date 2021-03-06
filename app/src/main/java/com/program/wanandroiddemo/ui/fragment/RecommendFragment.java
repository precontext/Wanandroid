package com.program.wanandroiddemo.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.program.wanandroiddemo.R;
import com.program.wanandroiddemo.base.BaseApplication;
import com.program.wanandroiddemo.base.BaseFragment;
import com.program.wanandroiddemo.model.domain.RecommendPagerContent;
import com.program.wanandroiddemo.model.domain.RecommendTitle;
import com.program.wanandroiddemo.presenter.IRecommendTitlePresenter;
import com.program.wanandroiddemo.presenter.utils.DataUtils;
import com.program.wanandroiddemo.presenter.utils.GetCollectionIds;
import com.program.wanandroiddemo.ui.activity.DetailsActivity;
import com.program.wanandroiddemo.ui.adapter.LooperPagerAdapter;
import com.program.wanandroiddemo.ui.adapter.RecommendAdapter;
import com.program.wanandroiddemo.ui.custom.AutoLoopViewPager;
import com.program.wanandroiddemo.utils.Constants;
import com.program.wanandroiddemo.utils.LogUtils;
import com.program.wanandroiddemo.utils.PresenterManager;
import com.program.wanandroiddemo.utils.SizeUtils;
import com.program.wanandroiddemo.utils.ToastUtils;
import com.program.wanandroiddemo.view.IRecommendTitleCallback;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RecommendFragment extends BaseFragment implements IRecommendTitleCallback, RecommendAdapter.OnRecommendTitleItemClickListener, LooperPagerAdapter.OnLoopPagerItemClickListener {

    @BindView(R.id.home_recommend_title)
    public RecyclerView mContentRv;

    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;

    @BindView(R.id.recommend_refresh)
    public SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.loop_pager)
    public AutoLoopViewPager looperPager;
    private LooperPagerAdapter mLooperPagerAdapter;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    private RecommendAdapter mRecommendAdapter;
    private IRecommendTitlePresenter mRecommendTitlePresenter;
    private GetCollectionIds mGetCollectionIds;
    private ImageView mView;


    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container, false);
    }

    //??????
    protected void initView(View rootView) {
        setupState(State.SUCCESS);
        //??????LayoutManager,?????????????????????
        mContentRv.setLayoutManager(new LinearLayoutManager(BaseApplication.getAppContext()));

        //???????????????
        mRecommendAdapter = new RecommendAdapter();
        mContentRv.setAdapter(mRecommendAdapter);
        barTitleTv.setText("??????");

        mContentRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = SizeUtils.dip2px(getContext(), 2f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 2f);
                outRect.left = SizeUtils.dip2px(getContext(), 2f);
                outRect.right = SizeUtils.dip2px(getContext(), 2f);
            }
        });

        //?????????
        //???????????????
        mLooperPagerAdapter = new LooperPagerAdapter();
        //???????????????
        looperPager.setAdapter(mLooperPagerAdapter);

//        mRefreshLayout.setEnableLoadmore(true);
//        mRefreshLayout.setEnableRefresh(true);


    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mRecommendTitlePresenter = PresenterManager.getInstance().getRecommendTitlePresenter();
        mRecommendTitlePresenter.registerViewCallback(this);
        mGetCollectionIds = DataUtils.getInstance().getGetCollectionIds();

    }


    private boolean initLooper = true;
    @Override
    protected void loadData() {
        super.loadData();
        setupState(State.LOADING);
        //????????????
        if (initLooper){
            mRecommendTitlePresenter.getLooperPager();
            initLooper=false;
        }
        mRecommendAdapter.setCollectionData(new GetCollectionIds().getIds());
        mRecommendTitlePresenter.getUserCollection();
//        mRecommendTitlePresenter.getRecommendTitle();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mLooperPagerAdapter.setOnLoopPagerItemClickListener(this);
        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //??????
            }

            @Override
            public void onPageSelected(int position) {
                //???????????????
                //??????postion???????????????,???????????????
                if (mLooperPagerAdapter.getDataSize()==0) {
                    return;
                }
                int targetPosition = position % mLooperPagerAdapter.getDataSize();
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //????????????
            }
        });

        mRecommendAdapter.setOnTitleItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mRecommendTitlePresenter != null) {
                    mRecommendTitlePresenter.getUserCollection();
                }
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mRecommendTitlePresenter != null) {
                    mRecommendTitlePresenter.loadMore();
                }
            }
        });
//        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
//            @Override
//            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
//                super.onLoadMore(refreshLayout);
//                if (mRecommendTitlePresenter != null) {
//                    mRecommendTitlePresenter.loadMore();
//                }
//            }
//
//            @Override
//            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
//                super.onRefresh(refreshLayout);
//                if (mRecommendTitlePresenter != null) {
//                    mRecommendTitlePresenter.getUserCollection();
//                }
//            }
//        });
    }


    /**
     * ???????????????
     *
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_noraml);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        boolean needRefresh = mRecommendTitlePresenter.needRefresh();
        LogUtils.d(RecommendFragment.this, "onResume str boolean =" + needRefresh);
        if (needRefresh) {
            loadData();
        }

        //????????????
        looperPager.startLoop();

        LogUtils.d(RecommendFragment.this, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        //????????????
        looperPager.stopLoop();
    }

    @Override
    protected void relese() {
        super.relese();
        if (mRecommendTitlePresenter != null) {
            mRecommendTitlePresenter.unregisterViewCallback();
        }
    }

    private boolean isFrist = true;

    @Override
    public void onContentLoadedSuccess(RecommendTitle content) {
        //????????????
        setupState(State.SUCCESS);
        LogUtils.d(RecommendFragment.this, "is frist =" + isFrist);
        if (mRefreshLayout != null && !isFrist) {
            ToastUtils.showToast("????????????");
//            mRefreshLayout.finishRefreshing();
            mRefreshLayout.finishRefresh();
        }
        LogUtils.d(RecommendFragment.this, "onContentLoadedSuccess");
        isFrist = false;
        //??????ui
        mRecommendAdapter.setCollectionData(mGetCollectionIds.getIds());
        mRecommendAdapter.setData(content);
    }

    @Override
    public void onLoaderMoreError() {
        ToastUtils.showToast("??????????????????????????????");
        if (mRefreshLayout != null) {
//            mRefreshLayout.finishLoadmore();
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtils.showToast("????????????????????????");
        if (mRefreshLayout != null) {
//            mRefreshLayout.finishLoadmore();
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(RecommendTitle data) {
        mRecommendAdapter.addData(data);
        if (mRefreshLayout != null) {
//            mRefreshLayout.finishLoadmore();
            mRefreshLayout.finishLoadMore();
        }
        ToastUtils.showToast("?????????" + data.getData().getDatas().size() + "?????????");
    }

    @Override
    public void onLooperListLoaded(RecommendPagerContent contents) {
        List<RecommendPagerContent.DataBean> dataBeanList = new ArrayList<>();
        for (int i = 0; i < contents.getData().size(); i++) {
            dataBeanList.add(contents.getData().get(i));
        }
        mLooperPagerAdapter.setData(dataBeanList);

        //??????????????????
        //?????????%?????????????????????0????????????????????????????????????
        int dx = (Integer.MAX_VALUE / 2) % contents.getData().size();
        int targetCenterPostion = (Integer.MAX_VALUE / 2) - dx;
        looperPager.setCurrentItem(targetCenterPostion);
        LogUtils.d(this, "url-->" + contents.getData().get(0));


        //?????????
        for (int i = 0; i < contents.getData().size(); i++) {
            View point = new View(getContext());
            //view?????????LinearLayout???
            int size = SizeUtils.dip2px(getContext(), 8);
            //????????????
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            point.setLayoutParams(layoutParams);
            point.setBackgroundColor(getContext().getColor(R.color.white));
            layoutParams.leftMargin = SizeUtils.dip2px(getContext(), 5);
            layoutParams.rightMargin = SizeUtils.dip2px(getContext(), 5);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_noraml);
            }
            looperPointContainer.addView(point);
        }
    }

    @Override
    public void onError() {
        setupState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setupState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setupState(State.EMPTY);
    }

    @Override
    public void onCollectSuccess() {
        int tag = Integer.parseInt(mView.getTag()+"");
        if (tag==R.mipmap.collect_normal){
            mView.setImageResource(Constants.BULE_LOVE);
            mView.setTag(Constants.BULE_LOVE);
            ToastUtils.showToast("????????????");
        }
    }

    @Override
    public void onCollectError() {
        int tag = Integer.parseInt(mView.getTag()+"");
        if (tag==R.mipmap.collect_normal){
            mView.setImageResource(Constants.WHITE_LOVE);
            mView.setTag(Constants.WHITE_LOVE);
            ToastUtils.showToast("????????????,?????????????????????");
        }
    }

    @Override
    public void onSendUnCollectionSuccess() {
        int tag = Integer.parseInt(mView.getTag()+"");
        if (tag==R.mipmap.collect_press){
            mView.setImageResource(Constants.WHITE_LOVE);
            mView.setTag(Constants.WHITE_LOVE);
            ToastUtils.showToast("???????????????");
        }
    }

    @Override
    public void onSendUnCollectionError() {
        int tag = Integer.parseInt(mView.getTag()+"");
        if (tag==R.mipmap.collect_press){
            mView.setImageResource(Constants.BULE_LOVE);
            mView.setTag(Constants.BULE_LOVE);
            ToastUtils.showToast("?????????????????????????????????");
        }
    }

    @Override
    public void onItemClick(RecommendTitle.DataBean.DatasBean data) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(Constants.TITLE, data.getTitle());
        intent.putExtra(Constants.LINK, data.getLink());
        startActivity(intent);
    }

    @Override
    public void onCollectItemClick(ImageView view, RecommendTitle.DataBean.DatasBean data) {
        mRecommendTitlePresenter.CollectArticle(data.getId());
        this.mView = view;
    }

    @Override
    public void onUnCollectItemClick(ImageView view, RecommendTitle.DataBean.DatasBean data) {
        mRecommendTitlePresenter.unCollect(data.getId(),data.getId());
        this.mView=view;
    }

    @Override
    public void onLoopereItemClick(RecommendPagerContent.DataBean item) {
        Intent intent = new Intent(BaseApplication.getAppContext(), DetailsActivity.class);
        intent.putExtra(Constants.LINK,item.getUrl());
        intent.putExtra(Constants.TITLE,item.getTitle());
        startActivity(intent);
    }
}
