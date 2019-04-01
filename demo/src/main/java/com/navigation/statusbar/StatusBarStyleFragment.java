package com.navigation.statusbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.listenzz.navigation.BarStyle;
import me.listenzz.navigation.Style;
import me.listenzz.navigation.ToolbarButtonItem;


/**
 * Created by listen on 2018/1/13.
 */

public class StatusBarStyleFragment extends TestStatusBarFragment {

    private BarStyle barStyle = BarStyle.DarkContent;

    @NonNull
    @Override
    protected BarStyle preferredStatusBarStyle() {
        return barStyle;
    }

    @Override
    protected void onCustomStyle(@NonNull Style style) {
        super.onCustomStyle(style);
        style.setStatusBarStyle(BarStyle.DarkContent);
        style.setStatusBarColor(Color.WHITE);
        style.setToolbarBackgroundColor(Color.WHITE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarButtonItem.Builder builder = new ToolbarButtonItem.Builder();
        builder.title("切换").tintColor(Color.RED).listener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferredStatusBarStyle() == BarStyle.DarkContent) {
                    barStyle = BarStyle.LightContent;
                } else {
                    barStyle = BarStyle.DarkContent;
                }
                setNeedsStatusBarAppearanceUpdate();
            }
        });
        setRightBarButtonItem(builder.build());
    }

}
