package com.allattentionhere.fabulousfilter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.allattentionhere.fabulousfilter.viewpagerbottomsheet.BottomSheetUtils;
import com.allattentionhere.fabulousfilter.viewpagerbottomsheet.ViewPagerBottomSheetBehavior;
import com.allattentionhere.fabulousfilter.viewpagerbottomsheet.ViewPagerBottomSheetDialog;
import com.allattentionhere.fabulousfilter.viewpagerbottomsheet.ViewPagerBottomSheetDialogFragment;


/**
 * Created by krupenghetiya on 05/10/16.
 */

public class AAH_FabulousFragment extends ViewPagerBottomSheetDialogFragment {

    private FloatingActionButton parent_fab;
    private DisplayMetrics metrics;
    private int fab_size = 56, fab_pos_y, fab_pos_x;
    private float scale_by = 12f;
    private FrameLayout bottomSheet;
    private ViewPagerBottomSheetBehavior mBottomSheetBehavior;
    private int fab_outside_y_offest = 0;
    private boolean is_fab_outside_peekheight;

    //user params
    private int peek_height = 400;
    private int anim_duration = 500;
    private FloatingActionButton fabulous_fab;
    private FrameLayout fl;
    private View view_main;
    private View viewgroup_static;
    private Drawable fab_icon_resource;
    private ColorStateList fab_background_color_resource;
    private View contentView;
    private Callbacks callbacks;
    private AnimationListener animationListener;
    private ViewPager viewPager;


    private ViewPagerBottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new ViewPagerBottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(View bottomSheet, int newState) {
            switch (newState) {
                case ViewPagerBottomSheetBehavior.STATE_HIDDEN:
                    if (callbacks != null) {
                        callbacks.onResult("swiped_down");
                    }
                    dismiss();
                    break;
                case ViewPagerBottomSheetBehavior.STATE_COLLAPSED:
                    ViewGroup.LayoutParams params = view_main.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    view_main.setLayoutParams(params);
                    break;
                case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
                    ViewGroup.LayoutParams params1 = view_main.getLayoutParams();
                    params1.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    view_main.setLayoutParams(params1);
                    break;
            }

        }

        @Override
        public void onSlide(View bottomSheet, float slideOffset) {
            if (viewgroup_static != null) {
                int range = (int) (metrics.heightPixels - (metrics.density * peek_height) - getStatusBarHeight(getContext()));
                viewgroup_static.animate().translationY(-range + (range * slideOffset)).setDuration(0).start();
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metrics = this.getResources().getDisplayMetrics();

    }

    public static int getStatusBarHeight(final Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return resources.getDimensionPixelSize(resourceId);
        else
            return (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        if (viewPager != null) {
            BottomSheetUtils.setupViewPager(viewPager);
        }

        dialog.setContentView(contentView);

        int[] location = new int[2];
        parent_fab.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];

        fab_size = parent_fab.getHeight();
        fab_pos_y = y;
        fab_pos_x = x;
        fab_icon_resource = parent_fab.getDrawable();
        fab_background_color_resource = parent_fab.getBackgroundTintList();

        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetBehavior = ViewPagerBottomSheetBehavior.from(((View) contentView.getParent()));
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            if ((fab_pos_y - (metrics.heightPixels - (metrics.density * peek_height)) + (fab_size * metrics.density) - (fab_size * metrics.density)) <= 0) {
                is_fab_outside_peekheight = true;
                mBottomSheetBehavior.setPeekHeight(metrics.heightPixels - fab_pos_y);
                fab_outside_y_offest = (int) (metrics.heightPixels - fab_pos_y - (metrics.density * peek_height));
            } else {
                mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
            }
            contentView.requestLayout();
        }
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ViewPagerBottomSheetDialog d = (ViewPagerBottomSheetDialog) dialog;
                bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                ViewPagerBottomSheetBehavior.from(bottomSheet).setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
                if (viewgroup_static != null) {
                    int range = (int) (metrics.heightPixels - (metrics.density * peek_height) - getStatusBarHeight(getContext()));
                    viewgroup_static.animate().translationY(-range).setDuration(0).start();
                }
                int fab_range_y = (int) (fab_pos_y - (metrics.heightPixels - (metrics.density * peek_height)));
                fabulous_fab.setY(fab_range_y + fab_outside_y_offest);
                fabulous_fab.setX(fab_pos_x);
                view_main.setVisibility(View.INVISIBLE);
                fabAnim();

            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof ViewPagerBottomSheetBehavior) {
            ((ViewPagerBottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        scale_by = (float) (peek_height * 1.6 / fab_size) * metrics.density;
        fabulous_fab = (FloatingActionButton) contentView.findViewWithTag("aah_fab");
        fl = (FrameLayout) contentView.findViewWithTag("aah_fl");
        int newfabsize = fab_size;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int ele = (int) Math.floor(parent_fab.getCompatElevation() / 2);
            newfabsize = (int) (fab_size - (metrics.density * (18 + (6 * ele))));
            scale_by = (float) (peek_height * 2 / newfabsize) * metrics.density;

        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(newfabsize, newfabsize);
        lp.gravity = Gravity.CENTER;
        fabulous_fab.setLayoutParams(lp);
        fabulous_fab.setImageDrawable(fab_icon_resource);
        fabulous_fab.setBackgroundTintList(fab_background_color_resource);


    }


    private void fabAnim() {
        if (animationListener != null) animationListener.onOpenAnimationStart();
        AAH_ArcTranslateAnimation anim = new AAH_ArcTranslateAnimation(0, metrics.widthPixels / 2 - fab_pos_x - (fab_size / 2), 0, -(metrics.density * ((peek_height / 2) - ((((metrics.heightPixels - fab_pos_y) - fab_size) / metrics.density)))));
        anim.setDuration(anim_duration);
        fl.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    parent_fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabulous_fab.setImageResource(android.R.color.transparent);
                fabulous_fab.animate().setListener(null);
                fabulous_fab.setVisibility(View.INVISIBLE);
                //Do something after 100ms
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
                        ViewPagerBottomSheetBehavior.from(bottomSheet).setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
                        if (is_fab_outside_peekheight) {
                            bottomSheet.requestLayout();
                        }

                        fabulous_fab.animate().translationXBy(metrics.widthPixels / 2 - fab_pos_x - (fab_size / 2))
                                .translationYBy(-(metrics.density * ((peek_height / 2) - ((((metrics.heightPixels - fab_pos_y) - fab_size) / metrics.density)))) - fab_outside_y_offest).setDuration(0)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        fabulous_fab.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        fabulous_fab.animate().setListener(null);
                                        fabulous_fab.animate().scaleXBy(scale_by)
                                                .scaleYBy(scale_by)
                                                .setDuration(anim_duration)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        fabulous_fab.animate().setListener(null);
                                                        fabulous_fab.setVisibility(View.GONE);
                                                        view_main.setVisibility(View.VISIBLE);
                                                        if (animationListener != null)
                                                            animationListener.onOpenAnimationEnd();

                                                    }
                                                });


                                    }
                                });
                    }
                }, 10);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void closeFilter(final Object o) {
        if (animationListener != null) animationListener.onCloseAnimationStart();
        if (ViewPagerBottomSheetBehavior.from(bottomSheet).getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
            ViewPagerBottomSheetBehavior.from(bottomSheet).setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        }
        fabulous_fab.setVisibility(View.VISIBLE);
        view_main.setVisibility(View.INVISIBLE);
        fabulous_fab.animate().scaleXBy(-scale_by)
                .scaleYBy(-scale_by)
                .setDuration(anim_duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fabulous_fab.animate().setListener(null);
                        fabulous_fab.setImageDrawable(fab_icon_resource);
                        if (is_fab_outside_peekheight) {
                            mBottomSheetBehavior.setPeekHeight(metrics.heightPixels - fab_pos_y);
                            ViewPagerBottomSheetBehavior.from(bottomSheet).setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
                            bottomSheet.requestLayout();
//                            fabulous_fab.setY(fab_outside_y_offest - fab_pos_y + getStatusBarHeight(getContext()));
                        } else {
                            mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
                        }
                        float from_y, to_y;

                        from_y = fab_outside_y_offest;
                        to_y = (metrics.density * ((peek_height / 2) - ((((metrics.heightPixels - fab_pos_y) - fab_size) / metrics.density)))) + fab_outside_y_offest;
                        AAH_ArcTranslateAnimation anim = new AAH_ArcTranslateAnimation(0, -(metrics.widthPixels / 2 - fab_pos_x - (fab_size / 2)), from_y, to_y);
                        anim.setDuration(anim_duration);
                        fl.startAnimation(anim);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                fabulous_fab.animate().setListener(null);
                                fabulous_fab.setVisibility(View.INVISIBLE);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms
                                        if (animationListener != null)
                                            animationListener.onCloseAnimationEnd();
                                        if (callbacks != null) {
                                            callbacks.onResult(o);
                                        }
                                        dismiss();
                                    }
                                }, 50);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }
                });
    }

    @Override
    public void onStop() {
        parent_fab.setVisibility(View.VISIBLE);
        super.onStop();
    }


    public interface Callbacks {
        void onResult(Object result);
    }

    public interface AnimationListener {
        void onOpenAnimationStart();

        void onOpenAnimationEnd();

        void onCloseAnimationStart();

        void onCloseAnimationEnd();
    }

    public void setPeekHeight(int peek_height) {
        this.peek_height = peek_height;
    }

    public void setViewMain(View view_main) {
        this.view_main = view_main;
    }

    public void setViewgroupStatic(View viewgroup_static) {
        this.viewgroup_static = viewgroup_static;
    }

    public void setMainContentView(View contentView) {
        this.contentView = contentView;
    }


    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void setAnimationListener(AnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    public void setParentFab(FloatingActionButton parent_fab) {
        this.parent_fab = parent_fab;
    }

    public void setAnimationDuration(int anim_duration) {
        this.anim_duration = anim_duration;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
