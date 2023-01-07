package com.allattentionhere.fabulousfilter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.allattentionhere.fabulousfilter.viewpagerbottomsheet.BottomSheetUtils;
import com.allattentionhere.fabulousfilter.viewpagerbottomsheet.ViewPagerBottomSheetBehavior;
import com.allattentionhere.fabulousfilter.viewpagerbottomsheet.ViewPagerBottomSheetDialog;
import com.allattentionhere.fabulousfilter.viewpagerbottomsheet.ViewPagerBottomSheetDialogFragment;

/** Created by krupenghetiya on 05/10/16. */
public class AAH_FabulousFragment extends ViewPagerBottomSheetDialogFragment {

  private int fabSize = 56, fabPosY, fabPosX;
  private float scaleBy = 12f;
  private int fabOutsideYOffset = 0;
  private boolean isFabOutsidePeekheight;
  private FloatingActionButton parentFab;
  @NonNull private DisplayMetrics metrics;
  private FrameLayout bottomSheet;
  private ViewPagerBottomSheetBehavior bottomSheetBehavior;

  // user params
  private int screenHeight, screenHeightExcludingTopBar, screenHeightExcludingTopBottomBar;
  private int peekHeight = 400;
  private int animDuration = 500;
  private FloatingActionButton fabulousFab;
  private FrameLayout fabContainer;
  private View viewMain;
  private View viewGroupStatic;
  private Drawable fabIconResource;
  private View contentView;
  private Callbacks callbacks;
  private AnimationListener animationListener;
  private ViewPager viewPager;

  private final ViewPagerBottomSheetBehavior.BottomSheetCallback bottomSheetBehaviorCallback =
      new ViewPagerBottomSheetBehavior.BottomSheetCallback() {

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
            case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
              ViewGroup.LayoutParams params = viewMain.getLayoutParams();
              params.height = ViewGroup.LayoutParams.MATCH_PARENT;
              viewMain.setLayoutParams(params);
              break;
          }
        }

        @Override
        public void onSlide(View bottomSheet, float slideOffset) {
          if (viewGroupStatic != null) {
            int range = (int) (screenHeight - (metrics.density * peekHeight));
            if (slideOffset > 0) {
              viewGroupStatic.setTranslationY((range * slideOffset) - range);
            } else {
              int peekHeightDp = (int) ((metrics.density * peekHeight));
              viewGroupStatic.setTranslationY((peekHeightDp * slideOffset) - range);
            }
          }
        }
      };

  @Override
  public void onStart() {
    super.onStart();
    if (getDialog() != null) {
      getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    metrics = this.getResources().getDisplayMetrics();
    screenHeight = getScreenHeightTopBottomBar(true, true);
    screenHeightExcludingTopBar = getScreenHeightTopBottomBar(false, true);
    screenHeightExcludingTopBottomBar = getScreenHeightTopBottomBar(false, false);
  }

  @SuppressLint("DiscouragedApi")
  private int getScreenHeightTopBottomBar(boolean excludeTopBar, boolean excludeBottomBar) {
    Context context = getContext();
    if (context == null) {
      return metrics.heightPixels;
    }
    WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    Display display = wm.getDefaultDisplay();
    Point screenSize = new Point();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      display.getRealSize(screenSize);
    } else {
      display.getSize(screenSize);
    }
    int total = screenSize.y;
    final Resources resources = context.getResources();
    if (excludeBottomBar) {
      int navBarResourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
      int bottomBarHeight = 0;
      if (navBarResourceId > 0) {
        bottomBarHeight = resources.getDimensionPixelSize(navBarResourceId);
      }
      total -= bottomBarHeight;
    }
    if (excludeTopBar) {
      int topBarHeight;
      final int statusBarResourceId =
          resources.getIdentifier("status_bar_height", "dimen", "android");
      if (statusBarResourceId > 0) {
        topBarHeight = resources.getDimensionPixelSize(statusBarResourceId);
      } else {
        topBarHeight =
            (int)
                Math.ceil(
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * metrics.density);
      }
      total -= topBarHeight;
    }

    return total;
  }

  @SuppressLint("RestrictedApi")
  @Override
  public void setupDialog(@NonNull Dialog dialog, int style) {
    super.setupDialog(dialog, style);
    if (viewPager != null) {
      BottomSheetUtils.setupViewPager(viewPager);
    }

    dialog.setContentView(contentView);

    int[] location = new int[2];
    parentFab.getLocationInWindow(location);
    int x = location[0];
    int y = location[1];

    fabSize = parentFab.getHeight();
    fabPosY = y;
    fabPosX = x;
    fabIconResource = parentFab.getDrawable();
    ColorStateList fabBackgroundColorResource = parentFab.getBackgroundTintList();

    ((View) contentView.getParent())
        .setBackgroundColor(getResources().getColor(android.R.color.transparent));

    bottomSheetBehavior = ViewPagerBottomSheetBehavior.from(((View) contentView.getParent()));
    bottomSheetBehavior.setBottomSheetCallback(bottomSheetBehaviorCallback);
    if ((fabPosY
            - (metrics.heightPixels - (metrics.density * peekHeight))
            + (fabSize * metrics.density)
            - (fabSize * metrics.density))
        <= 0) {
      isFabOutsidePeekheight = true;
      bottomSheetBehavior.setPeekHeight(metrics.heightPixels - fabPosY);
      fabOutsideYOffset = (int) (metrics.heightPixels - fabPosY - (metrics.density * peekHeight));
    } else {
      bottomSheetBehavior.setPeekHeight((int) (metrics.density * peekHeight));
    }
    contentView.requestLayout();
    dialog.setOnShowListener(
        dialog1 -> {
          ViewPagerBottomSheetDialog viewPagerBottomSheetDialog =
              (ViewPagerBottomSheetDialog) dialog1;
          bottomSheet =
              viewPagerBottomSheetDialog.findViewById(
                  com.google.android.material.R.id.design_bottom_sheet);
          if (bottomSheet != null) {
            ViewPagerBottomSheetBehavior.from(bottomSheet)
                .setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
          }
          if (viewGroupStatic != null) {
            int range = (int) (screenHeight - (metrics.density * peekHeight));

            viewGroupStatic.setTranslationY(-range);
          }
          int screenHeightToUse =
              getContext() != null && isSystemBarOnBottom(getContext(), metrics)
                  ? screenHeightExcludingTopBar
                  : screenHeightExcludingTopBottomBar;
          int fabRangeY = (int) (fabPosY - (screenHeightToUse - (metrics.density * peekHeight)));
          fabulousFab.setY(fabRangeY + fabOutsideYOffset);
          fabulousFab.setX(fabPosX);
          viewMain.setVisibility(View.INVISIBLE);
          fabAnim();
        });

    CoordinatorLayout.LayoutParams params =
        (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
    CoordinatorLayout.Behavior behavior = params.getBehavior();

    if (behavior instanceof ViewPagerBottomSheetBehavior) {
      ((ViewPagerBottomSheetBehavior) behavior).setBottomSheetCallback(bottomSheetBehaviorCallback);
    }

    scaleBy = (float) (peekHeight * 1.6 / fabSize) * metrics.density;
    fabulousFab = contentView.findViewWithTag("aah_fab");
    fabContainer = contentView.findViewWithTag("aah_fl");
    int newfabsize = fabSize;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      int elevation = (int) Math.floor(parentFab.getCompatElevation() / 2);
      newfabsize = (int) (fabSize - (metrics.density * (18 + (6 * elevation))));
      scaleBy = (float) (peekHeight * 2 / newfabsize) * metrics.density;
    }

    final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(newfabsize, newfabsize);
    lp.gravity = Gravity.CENTER;
    fabulousFab.setLayoutParams(lp);
    fabulousFab.setImageDrawable(fabIconResource);
    fabulousFab.setBackgroundTintList(fabBackgroundColorResource);
  }

  private static boolean isSystemBarOnBottom(Context context, DisplayMetrics metrics) {
    Configuration configuration = context.getResources().getConfiguration();
    boolean canMove =
        (metrics.widthPixels != metrics.heightPixels && configuration.smallestScreenWidthDp < 600);
    return (!canMove || metrics.widthPixels < metrics.heightPixels);
  }

  private void fabAnim() {
    if (animationListener != null) {
      animationListener.onOpenAnimationStart();
    }
    AAH_ArcTranslateAnimation anim =
        new AAH_ArcTranslateAnimation(
            0,
            metrics.widthPixels / 2 - fabPosX - (fabSize / 2),
            0,
            -(metrics.density
                * ((peekHeight / 2)
                    - ((((metrics.heightPixels - fabPosY) - fabSize) / metrics.density)))));
    anim.setDuration(animDuration);
    anim.setAnimationListener(
        new Animation.AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {
            if (getActivity() != null && !getActivity().isFinishing()) {
              parentFab.setVisibility(View.GONE);
            }
          }

          @Override
          public void onAnimationEnd(Animation animation) {
            fabulousFab.setImageResource(android.R.color.transparent);
            fabulousFab.animate().setListener(null);
            fabulousFab.setVisibility(View.INVISIBLE);
            // Do something after 100ms
            final Handler handler = new Handler();
            handler.postDelayed(
                new Runnable() {
                  @Override
                  public void run() {
                    bottomSheetBehavior.setPeekHeight((int) (metrics.density * peekHeight));
                    ViewPagerBottomSheetBehavior.from(bottomSheet)
                        .setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
                    if (isFabOutsidePeekheight) {
                      bottomSheet.requestLayout();
                    }
                    fabulousFab.setVisibility(View.VISIBLE);
                    fabulousFab.setTranslationX(0f);
                    float offsetY =
                        -(metrics.density
                                * ((peekHeight / 2)
                                    - ((((metrics.heightPixels - fabPosY) - fabSize)
                                        / metrics.density))))
                            - fabOutsideYOffset;
                    fabulousFab.setTranslationY(fabulousFab.getTranslationY() + offsetY);
                    fabulousFab.animate().setListener(null);
                    fabulousFab
                        .animate()
                        .scaleXBy(scaleBy)
                        .scaleYBy(scaleBy)
                        .setDuration(animDuration)
                        .setListener(
                            new AnimatorListenerAdapter() {
                              @Override
                              public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                fabulousFab.animate().setListener(null);
                                fabulousFab.setVisibility(View.GONE);
                                viewMain.setVisibility(View.VISIBLE);
                                if (animationListener != null)
                                  animationListener.onOpenAnimationEnd();
                              }
                            });
                  }
                },
                100);
          }

          @Override
          public void onAnimationRepeat(Animation animation) {}
        });
    fabContainer.startAnimation(anim);
  }

  public void closeFilter(final Object object) {
    if (animationListener != null) {
      animationListener.onCloseAnimationStart();
    }
    if (ViewPagerBottomSheetBehavior.from(bottomSheet).getState()
        == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
      ViewPagerBottomSheetBehavior.from(bottomSheet)
          .setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
    }
    fabulousFab.setVisibility(View.VISIBLE);
    viewMain.setVisibility(View.INVISIBLE);
    fabulousFab
        .animate()
        .scaleXBy(-scaleBy)
        .scaleYBy(-scaleBy)
        .setDuration(animDuration)
        .setListener(
            new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fabulousFab.animate().setListener(null);
                fabulousFab.setImageDrawable(fabIconResource);
                if (isFabOutsidePeekheight) {
                  bottomSheetBehavior.setPeekHeight(metrics.heightPixels - fabPosY);
                  ViewPagerBottomSheetBehavior.from(bottomSheet)
                      .setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
                  bottomSheet.requestLayout();
                } else {
                  bottomSheetBehavior.setPeekHeight((int) (metrics.density * peekHeight));
                }
                float from_y, to_y;
                from_y = fabOutsideYOffset;
                to_y =
                    (metrics.density
                            * ((peekHeight / 2)
                                - ((((metrics.heightPixels - fabPosY) - fabSize)
                                    / metrics.density))))
                        + fabOutsideYOffset;
                AAH_ArcTranslateAnimation anim =
                    new AAH_ArcTranslateAnimation(
                        0, -(metrics.widthPixels / 2 - fabPosX - (fabSize / 2)), from_y, to_y);
                anim.setDuration(animDuration);
                fabContainer.startAnimation(anim);
                anim.setAnimationListener(
                    new Animation.AnimationListener() {
                      @Override
                      public void onAnimationStart(Animation animation) {}

                      @Override
                      public void onAnimationEnd(Animation animation) {
                        fabulousFab.animate().setListener(null);
                        fabulousFab.setVisibility(View.INVISIBLE);
                        final Handler handler = new Handler();
                        handler.postDelayed(
                            () -> {
                              // Do something after 100ms
                              if (animationListener != null)
                                animationListener.onCloseAnimationEnd();
                              if (callbacks != null) {
                                callbacks.onResult(object);
                              }
                              dismiss();
                            },
                            50);
                      }

                      @Override
                      public void onAnimationRepeat(Animation animation) {}
                    });
              }
            });
  }

  @Override
  public void onStop() {
    parentFab.setVisibility(View.VISIBLE);
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
    this.peekHeight = peek_height;
  }

  public void setViewMain(View viewMain) {
    this.viewMain = viewMain;
  }

  public void setViewgroupStatic(View viewGroupStatic) {
    this.viewGroupStatic = viewGroupStatic;
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

  public void setParentFab(FloatingActionButton parentFab) {
    this.parentFab = parentFab;
  }

  public void setAnimationDuration(int animDuration) {
    this.animDuration = animDuration;
  }

  public void setViewPager(ViewPager viewPager) {
    this.viewPager = viewPager;
  }
}
