package com.allattentionhere.fabulousfilter.viewpagerbottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.appcompat.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.allattentionhere.fabulousfilter.R;

public final class ViewPagerBottomSheetDialog extends AppCompatDialog {

  private @Nullable ViewPagerBottomSheetBehavior<FrameLayout> behavior;

  boolean cancelable = true;
  private boolean canceledOnTouchOutside = true;
  private boolean canceledOnTouchOutsideSet;

  public ViewPagerBottomSheetDialog(Context context) {
    this(context, 0);
  }

  public ViewPagerBottomSheetDialog(Context context, int theme) {
    super(context, getThemeResId(context, theme));
    // We hide the title bar for any style configuration. Otherwise, there will be a gap
    // above the bottom sheet when it is expanded.
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  @Override
  public void setContentView(int layoutResId) {
    super.setContentView(wrapInBottomSheet(layoutResId, null, null));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
  }

  @Override
  public void setContentView(View view) {
    super.setContentView(wrapInBottomSheet(0, view, null));
  }

  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(wrapInBottomSheet(0, view, params));
  }

  @Override
  public void setCancelable(boolean cancelable) {
    super.setCancelable(cancelable);
    if (this.cancelable != cancelable) {
      this.cancelable = cancelable;
      if (behavior != null) {
        behavior.setHideable(cancelable);
      }
    }
  }

  @Override
  public void setCanceledOnTouchOutside(boolean cancel) {
    super.setCanceledOnTouchOutside(cancel);
    if (cancel && !cancelable) {
      cancelable = true;
    }
    canceledOnTouchOutside = cancel;
    canceledOnTouchOutsideSet = true;
  }

  private View wrapInBottomSheet(
      int layoutResId, @Nullable View view, @Nullable ViewGroup.LayoutParams params) {
    final CoordinatorLayout coordinator =
        (CoordinatorLayout)
            View.inflate(getContext(), R.layout.design_view_pager_bottom_sheet_dialog, null);
    if (layoutResId != 0 && view == null) {
      view = getLayoutInflater().inflate(layoutResId, coordinator, false);
    }
    FrameLayout bottomSheet = coordinator.findViewById(R.id.design_bottom_sheet);
    behavior = ViewPagerBottomSheetBehavior.from(bottomSheet);
    behavior.setBottomSheetCallback(bottomSheetCallback);
    behavior.setHideable(cancelable);
    if (params == null) {
      bottomSheet.addView(view);
    } else {
      bottomSheet.addView(view, params);
    }
    // We treat the CoordinatorLayout as outside the dialog though it is technically inside
    coordinator
        .findViewById(R.id.touch_outside)
        .setOnClickListener(
            view1 -> {
              if (cancelable && isShowing() && shouldWindowCloseOnTouchOutside()) {
                cancel();
              }
            });
    // Handle accessibility events
    ViewCompat.setAccessibilityDelegate(
        bottomSheet,
        new AccessibilityDelegateCompat() {
          @Override
          public void onInitializeAccessibilityNodeInfo(
              View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            if (cancelable) {
              info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS);
              info.setDismissable(true);
            } else {
              info.setDismissable(false);
            }
          }

          @Override
          public boolean performAccessibilityAction(View host, int action, Bundle args) {
            if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && cancelable) {
              cancel();
              return true;
            }
            return super.performAccessibilityAction(host, action, args);
          }
        });
    return coordinator;
  }

  private boolean shouldWindowCloseOnTouchOutside() {
    if (!canceledOnTouchOutsideSet) {
      TypedArray a =
          getContext().obtainStyledAttributes(new int[] {android.R.attr.windowCloseOnTouchOutside});
      canceledOnTouchOutside = a.getBoolean(0, true);
      a.recycle();
      canceledOnTouchOutsideSet = true;
    }
    return canceledOnTouchOutside;
  }

  @SuppressLint("PrivateResource")
  private static int getThemeResId(@Nullable Context context, int themeId) {
    if (themeId == 0) {
      // If the provided theme is 0, then retrieve the dialogTheme from our theme
      TypedValue outValue = new TypedValue();
      if (context != null
          && context.getTheme().resolveAttribute(R.attr.bottomSheetDialogTheme, outValue, true)) {
        themeId = outValue.resourceId;
      } else {
        // bottomSheetDialogTheme is not provided; we default to our light theme
        themeId = R.style.Theme_Design_Light_BottomSheetDialog;
      }
    }
    return themeId;
  }

  private final ViewPagerBottomSheetBehavior.BottomSheetCallback bottomSheetCallback =
      new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(
            View bottomSheet, @ViewPagerBottomSheetBehavior.State int newState) {
          if (newState == ViewPagerBottomSheetBehavior.STATE_HIDDEN) {
            cancel();
          }
        }

        @Override
        public void onSlide(View bottomSheet, float slideOffset) {}
      };
}
