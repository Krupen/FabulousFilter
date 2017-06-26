package com.allattentionhere.fabulousfilter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import static android.R.attr.fragment;
import static android.R.attr.y;
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;


/**
 * Created by krupenghetiya on 05/10/16.
 */

public class AAH_FabulousFragment extends BottomSheetDialogFragment {

    private FloatingActionButton parent_fab;
    private DisplayMetrics metrics;

    private int peek_height = 400;
    private int anim_duration = 400;

    private FloatingActionButton fabulous_fab;
    private View view_main;
    private View viewgroup_static;
    private Drawable fab_icon_resource;
    private ColorStateList fab_background_color_resource;
    private View contentView;
    private Callbacks callbacks;
    private int fab_size = 56, fab_pos_y, fab_pos_x;
    private float scale_by = 12f;
    private FrameLayout bottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private int y_offest = 0;
    private LinearLayout ll;
    private boolean is_fab_outside_peekheight;


    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
                case BottomSheetBehavior.STATE_HIDDEN:
                    if (callbacks != null) {
                        callbacks.onResult("swiped_down");
                    }
                    dismiss();
                    break;
                case BottomSheetBehavior.STATE_COLLAPSED:
                    ViewGroup.LayoutParams params = view_main.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    view_main.setLayoutParams(params);
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    ViewGroup.LayoutParams params1 = view_main.getLayoutParams();
                    params1.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    view_main.setLayoutParams(params1);
                    break;
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metrics = this.getResources().getDisplayMetrics();

        Log.d("k9xy", "width : " + metrics.widthPixels + " | height: " + metrics.heightPixels + "| density: " + metrics.density);

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

        mBottomSheetBehavior = BottomSheetBehavior.from(((View) contentView.getParent()));
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            if ((fab_pos_y - (metrics.heightPixels - (metrics.density * peek_height)) + (fab_size * metrics.density)) <= 0) {
                is_fab_outside_peekheight = true;
                mBottomSheetBehavior.setPeekHeight(metrics.heightPixels - fab_pos_y);
                y_offest = (int) (metrics.heightPixels - fab_pos_y - (metrics.density * peek_height));
                ll = (LinearLayout) contentView.findViewWithTag("aah_ll");
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, y_offest);
                ll.setLayoutParams(lp);
                ll.setBackgroundResource(android.R.color.transparent);
            } else {
                mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
            }
            contentView.requestLayout();
        }
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (viewgroup_static != null) {
                    int range = (int) (metrics.heightPixels - (metrics.density * peek_height) - getStatusBarHeight(getContext()));
                    viewgroup_static.animate().translationY(-range).setDuration(0).start();
                }
//                int fab_range_y = (int) (metrics.heightPixels - (metrics.density * peek_height) - getStatusBarHeight(getContext())  + (metrics.density * fab_pos_y) - fab_size);
                int fab_range_y = (int) (fab_pos_y - (metrics.heightPixels - (metrics.density * peek_height)));
//                fabulous_fab.animate().translationY(-fab_range_y).setDuration(0).start();
                Log.d("k9xy", "metrics.heightPixels: +" + metrics.heightPixels);
                Log.d("k9xy", "peek_height: -" + peek_height);
                Log.d("k9xy", "getStatusBarHeight: -" + getStatusBarHeight(getContext()));
                Log.d("k9xy", "fab_pos_y: +" + fab_pos_y);
                Log.d("k9xy", "fab_size: -" + fab_size);
                Log.d("k9xy", "fab_range_y: =" + fab_range_y);
                fabulous_fab.setY(fab_range_y + y_offest);
                fabulous_fab.setX(fab_pos_x);
                view_main.setVisibility(View.INVISIBLE);
                fabAnim();

            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        scale_by = (float) (peek_height * 1.6 / fab_size);
        fabulous_fab = (FloatingActionButton) contentView.findViewWithTag("aah_fab");
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(fab_size, fab_size);
        fabulous_fab.setLayoutParams(lp);
        fabulous_fab.setImageDrawable(fab_icon_resource);
        fabulous_fab.setBackgroundTintList(fab_background_color_resource);


    }


    private void fabAnim() {
        Log.d("k9lib", "fabanim called : ");
        AAH_ArcTranslateAnimation anim = new AAH_ArcTranslateAnimation(0, metrics.widthPixels / 2 - fab_pos_x - (fab_size / 2), 0, -(metrics.density * ((peek_height / 2) - ((((metrics.heightPixels - fab_pos_y) - fab_size) / metrics.density)))));
        anim.setDuration(anim_duration);
        fabulous_fab.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    parent_fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("k9anim", "onAnimationEnd parent: ");
                fabulous_fab.setImageResource(android.R.color.transparent);
                fabulous_fab.animate().setListener(null);
                fabulous_fab.setVisibility(View.INVISIBLE);
                //Do something after 100ms
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
                        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                        if (is_fab_outside_peekheight) {
                            ll.setVisibility(View.VISIBLE);
                        }

                        fabulous_fab.animate().translationXBy(metrics.widthPixels / 2 - fab_pos_x - (fab_size / 2))
                                .translationYBy(-(metrics.density * ((peek_height / 2) - ((((metrics.heightPixels - fab_pos_y) - fab_size) / metrics.density)))) - y_offest).setDuration(0)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        fabulous_fab.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.d("k9anim", "onAnimationEnd child: ");
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
                                                        Log.d("k9anim", "withEndAction: ");
                                                        view_main.setVisibility(View.VISIBLE);
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

        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
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
                        } else {
                            mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
                        }
                        AAH_ArcTranslateAnimation anim = new AAH_ArcTranslateAnimation(0, -(metrics.widthPixels / 2 - fab_pos_x - (fab_size / 2)), y_offest, (metrics.density * ((peek_height / 2) - ((((metrics.heightPixels - fab_pos_y) - fab_size) / metrics.density)))) + y_offest);
                        anim.setDuration(anim_duration);
                        fabulous_fab.startAnimation(anim);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                if (is_fab_outside_peekheight) {
                                    ll.setVisibility(View.GONE);
                                }

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

    public void setPeek_height(int peek_height) {
        this.peek_height = peek_height;
    }

    public void setView_main(View view_main) {
        this.view_main = view_main;
    }

    public void setViewgroup_static(View viewgroup_static) {
        this.viewgroup_static = viewgroup_static;
    }

    public void setMainContentView(View contentView) {
        this.contentView = contentView;
    }


    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }


    public void setParent_fab(FloatingActionButton parent_fab) {
        this.parent_fab = parent_fab;
    }

    public void setAnim_duration(int anim_duration) {
        this.anim_duration = anim_duration;
    }
}
