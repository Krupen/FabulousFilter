package com.allattentionhere.fabulousfilter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
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

import static android.R.attr.fragment;
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;


/**
 * Created by krupenghetiya on 05/10/16.
 */

public class AAH_FabulousFragment extends BottomSheetDialogFragment {

    private DisplayMetrics metrics;

    private int peek_height = 400;
    private int anim_duration = 1000;

    private FloatingActionButton fabulous_fab;
    private View view_main;
    private View viewgroup_static;
    private int fab_icon_resource;
    private View contentView;
    private Callbacks callbacks;
    private int fab_size, fab_pos_y,fab_pos_x;
    private float scale_by = 12f;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
                case BottomSheetBehavior.STATE_HIDDEN:
                    callbacks.onResult("okay");
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
            int range = (int) (metrics.heightPixels - (metrics.density * peek_height) - getStatusBarHeight(getContext()));
            viewgroup_static.animate().translationY(-range + (range * slideOffset)).setDuration(0).start();
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

        Log.d("k9xy", "width : "+metrics.widthPixels+" | height: "+metrics.heightPixels+ "| density: "+metrics.density);

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
        Log.d("k9xy", "initial fab_pos_y : "+fab_pos_y);
//        fab_pos_y= (int) (((metrics.heightPixels-fab_pos_y)-fab_size)/metrics.density);
        Log.d("k9xy", "fab_pos_y : "+fab_pos_y);

        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(((View) contentView.getParent()));
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            mBottomSheetBehavior.setPeekHeight((int) (metrics.density * peek_height));
            contentView.requestLayout();
        }
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                int range = (int) (metrics.heightPixels - (metrics.density * peek_height) - getStatusBarHeight(getContext()));
                viewgroup_static.animate().translationY(-range).setDuration(0).start();
//                int fab_range_y = (int) (metrics.heightPixels - (metrics.density * peek_height) - getStatusBarHeight(getContext())  + (metrics.density * fab_pos_y) - fab_size);
                int fab_range_y = (int) (fab_pos_y - (metrics.heightPixels - (metrics.density * peek_height)));
//                fabulous_fab.animate().translationY(-fab_range_y).setDuration(0).start();
                Log.d("k9xy", "metrics.heightPixels: +"+metrics.heightPixels);
                Log.d("k9xy", "peek_height: -"+peek_height);
                Log.d("k9xy", "getStatusBarHeight: -"+getStatusBarHeight(getContext()));
                Log.d("k9xy", "fab_pos_y: +"+fab_pos_y);
                Log.d("k9xy", "fab_size: -"+fab_size);
                Log.d("k9xy", "fab_range_y: ="+fab_range_y);
                fabulous_fab.setY(fab_range_y);
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

        scale_by = (float) (peek_height*1.6/fab_size);

    }


    private void fabAnim() {
        Log.d("k9lib", "fabanim called : ");
        AAH_ArcTranslateAnimation anim = new AAH_ArcTranslateAnimation(0, metrics.widthPixels / 2 - fab_pos_x - (fab_size/2), 0, -(metrics.density * ((peek_height / 2) - ((((metrics.heightPixels-fab_pos_y)-fab_size)/metrics.density)))));
        anim.setDuration(anim_duration);
        fabulous_fab.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    callbacks.setFabVisibility(View.GONE);
                    Log.d("k9lib", "callbacks working : ");
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
                        fabulous_fab.animate().translationXBy(metrics.widthPixels / 2 - fab_pos_x - (fab_size/2))
                                .translationYBy(-(metrics.density * ((peek_height / 2) - ((((metrics.heightPixels-fab_pos_y)-fab_size)/metrics.density))))).setDuration(0)
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

    public void closeFilter() {
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
                        fabulous_fab.setImageResource(fab_icon_resource);
                        AAH_ArcTranslateAnimation anim = new AAH_ArcTranslateAnimation(0, -(metrics.widthPixels / 2 - fab_pos_x - (fab_size/2)), 0, (metrics.density * ((peek_height / 2) - ((((metrics.heightPixels-fab_pos_y)-fab_size)/metrics.density)))));
                        anim.setDuration(anim_duration);
                        fabulous_fab.startAnimation(anim);
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
                                        callbacks.onResult("okay");
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
        callbacks.setFabVisibility(View.VISIBLE);
        super.onStop();
    }


    public interface Callbacks {
        void setFabVisibility(int visibility);
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

    public void setFab_icon_resource(int fab_icon_resource) {
        this.fab_icon_resource = fab_icon_resource;
    }


    public void setMainContentView(View contentView) {
        this.contentView = contentView;
    }

    public void setFab_size(int fab_size) {
        this.fab_size = fab_size;
    }

    public void setFab_pos_y(int fab_pos_y) {
        this.fab_pos_y = fab_pos_y;
    }

    public void setFab_pos_x(int fab_pos_x) {
        this.fab_pos_x = fab_pos_x;
    }

    public void setFabulous_fab(FloatingActionButton fabulous_fab) {
        this.fabulous_fab = fabulous_fab;
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }


}
