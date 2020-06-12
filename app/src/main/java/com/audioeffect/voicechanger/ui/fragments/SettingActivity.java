package com.audioeffect.voicechanger.ui.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.audioeffect.voicechanger.R;
import com.audioeffect.voicechanger.utils.UtilView;
import com.bumptech.glide.Glide;
import com.audioeffect.voicechanger.base.menu.DrawerAdapter;
import com.audioeffect.voicechanger.base.menu.DrawerItem;
import com.audioeffect.voicechanger.base.menu.SimpleItem;
import com.audioeffect.voicechanger.utils.Constans;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragListener;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import java.util.Arrays;

public class SettingActivity implements DrawerAdapter.OnItemSelectedListener {
    Long mLastClickTime = 0L;
    private ViewNavCallback viewCallback;

    public SettingActivity(ViewNavCallback viewCallback, Activity context) {
        this.viewCallback = viewCallback;
        this.context = context;
    }

    private static final int POS_DASHBOARD = 0;
    private static final int POS_ACCOUNT = 1;
    private static final int POS_MESSAGES = 2;
    private static final int POS_CART = 3;
    private static final int POS_LOG = 4;
    private static final int POS_LOGOUT = 5;

    private Activity context;
    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    public SettingActivity(Activity context) {
        this.context = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createView() {
        //  setStatusBarGradiant(context);
        slidingRootNav = new SlidingRootNavBuilder(context)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withMenuLayout(R.layout.menu_left_drawer)
                .withDragDistance(250)
                .withRootViewScale(0.9f)
                .withRootViewElevation(20)
                .withRootViewYTranslation(2)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_ACCOUNT),
                createItemFor(POS_MESSAGES),
                createItemFor(POS_CART),
                createItemFor(POS_LOG),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);

        RelativeLayout btnViewClose = context.findViewById(R.id.btnViewClose);

        RecyclerView list = context.findViewById(R.id.list);
        ImageView imgLogo = context.findViewById(R.id.imgLogoSetting);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setAdapter(adapter);
        Glide.with(context).load(R.drawable.ic_splash).into(imgLogo);
        slidingRootNav.getLayout().addDragStateListener(new DragStateListener() {
            @Override
            public void onDragStart() {
                viewCallback.startNav();
            }

            @Override
            public void onDragEnd(boolean isMenuOpened) {
                viewCallback.endNav(isMenuOpened);
            }
        });

        slidingRootNav.getLayout().addDragListener(progress -> {
            Log.d("slidingRootNav", "progress=" + progress);
            if (slidingRootNav.isMenuOpened()) {
                // viewCallback.closeNav();
            }
        });

        slidingRootNav.getLayout().setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (slidingRootNav.isMenuOpened()) {
                        viewCallback.closeNav();
                        slidingRootNav.closeMenu();
                    }
                    break;
            }

            return false;
        });
    }

    public void StartSetting() {
        slidingRootNav.openMenu();
    }

    @Override
    public void onItemSelected(int position) {
        itemClick(position);
    }

    public boolean isSetting() {
        return slidingRootNav.isMenuOpened();
    }

    public void closeMenu() {
        slidingRootNav.closeMenu();
    }

    private void itemClick(int position) {
        switch (position) {
            case 0:

                break;
            case 1:
                UtilView.intentToPolicy(context);
                break;
            case 2:
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
                    mLastClickTime = SystemClock.elapsedRealtime();
                    shareToFriends();
                }
                break;
            case 3:

                break;
            case 4:

                break;
            case 5:
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
                    mLastClickTime = SystemClock.elapsedRealtime();

                }
                break;
        }

    }


    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.colorWhite))
                .withTextTint(color(R.color.colorWhite))
                .withSelectedIconTint(color(R.color.colorWhite))
                .withSelectedTextTint(color(R.color.colorWhite));
    }

    private String[] loadScreenTitles() {
        return context.getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = context.getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(context, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(context, res);
    }

    private void shareToFriends() {
        String link = Constans.GOOGLE_STORE + context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getApplicationInfo().loadLabel(context.getPackageManager()));
        intent.putExtra(Intent.EXTRA_TEXT, link);
        context.startActivity(Intent.createChooser(intent, "Voice changer 2"));
    }

    public interface ViewNavCallback {
        void startNav();

        void closeNav();

        void endNav(boolean b);
    }
}
