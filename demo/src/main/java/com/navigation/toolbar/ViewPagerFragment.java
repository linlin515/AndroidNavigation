package com.navigation.toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.navigation.BaseFragment;
import com.navigation.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import me.listenzz.navigation.AwesomeToolbar;
import me.listenzz.navigation.BarStyle;
import me.listenzz.navigation.Style;


/**
 * Created by Listen on 2018/2/1.
 */

public class ViewPagerFragment extends BaseFragment {

    AwesomeToolbar toolbar;

    int location;

    @Override
    public boolean isParentFragment() {
        return true;
    }

    @Override
    protected int preferredStatusBarColor() {
        int[] colors = new int[] {Color.RED, Color.GREEN, Color.BLUE};
        return colors[location];
    }

    @NonNull
    @Override
    protected BarStyle preferredStatusBarStyle() {
        BarStyle[] barStyles = new BarStyle[] { BarStyle.LightContent, BarStyle.DarkContent, BarStyle.LightContent};
        return barStyles[location];
    }

    @Override
    protected AwesomeToolbar onCreateAwesomeToolbar(View parent) {
        return toolbar;
    }

    @Override
    protected void onCustomStyle(@NonNull Style style) {
        super.onCustomStyle(style);
        style.setShadow(null);
        style.setElevation(0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_pager, container, false);
        initView(root);
        return root;
    }

    private void initView(View view) {

        toolbar = view.findViewById(R.id.toolbar);

        AppBarLayout appBarLayout = view.findViewById(R.id.appbar_layout);

        // important
        if(isStatusBarTranslucent()) {
            appendStatusBarPadding(appBarLayout, -2);
        }

        TabLayout tabLayout =  view.findViewById(R.id.tab_layout);
        ViewPager viewPager =  view.findViewById(R.id.view_pager);

        tabLayout.addTab(tabLayout.newTab().setText("One"));
        tabLayout.addTab(tabLayout.newTab().setText("Tow"));
        tabLayout.addTab(tabLayout.newTab().setText("Three"));

        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), "One", "Tow", "Three"));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                location = position;
                setNeedsStatusBarAppearanceUpdate();
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle("Toolbar In AppBar");
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        String[] titles;

        public ViewPagerAdapter(FragmentManager fm, String... titles) {
            super(fm);
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            String[] titles = new String[] {"Android", "Awesome", "Navigation"};
            return PageFragment.newInstance(titles[position]);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
