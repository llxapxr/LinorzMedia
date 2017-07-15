package linorz.com.linorzmedia.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import linorz.com.linorzmedia.R;
import linorz.com.linorzmedia.customview.LoadMoreRecyclerView;

/**
 * Created by linorz on 2016/5/9.
 */
public abstract class MediaFragment extends Fragment {
    protected View rootView;
    protected Context context;
    protected LoadMoreRecyclerView recyclerView;
    protected RecyclerView.Adapter adapter;
    protected List<Map<String, Object>> items;
    protected int num = 0;
    protected boolean isEnd = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) parent.removeView(rootView);
        } else {
            rootView = inflater.inflate(R.layout.media_fragment, container, false);
            context = rootView.getContext();
            initAllView(rootView);
        }
        return rootView;
    }

    protected void initAllView(View view) {
        recyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOnPauseListenerParams(ImageLoader.getInstance(), false, true);
        recyclerView.setAutoLoadMoreEnable(true);
        items = new ArrayList<>();
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!isEnd) load();
            }
        });
        if (!isEnd) load();
    }

    public void jumpTop() {
        recyclerView.smoothScrollToPosition(0);
    }

    protected abstract RecyclerView.Adapter getAdapter();

    protected abstract void load();
}
