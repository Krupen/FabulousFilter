package com.allattentionhere.fabulousfilter.viewpagerbottomsheet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public final class BottomSheetUtils {

  public static void setupViewPager(final @NonNull ViewPager viewPager) {
    final @Nullable View bottomSheetParent = findBottomSheetParent(viewPager);
    if (bottomSheetParent != null) {
      viewPager.addOnPageChangeListener(
          new BottomSheetViewPagerListener(viewPager, bottomSheetParent));
    }
  }

  private static class BottomSheetViewPagerListener extends ViewPager.SimpleOnPageChangeListener {
    private final @NonNull ViewPager viewPager;
    private final @NonNull ViewPagerBottomSheetBehavior<View> behavior;

    private BottomSheetViewPagerListener(
        @NonNull ViewPager viewPager, @NonNull View bottomSheetParent) {
      this.viewPager = viewPager;
      this.behavior = ViewPagerBottomSheetBehavior.from(bottomSheetParent);
    }

    @Override
    public void onPageSelected(int position) {
      viewPager.post(behavior::invalidateScrollingChild);
    }
  }

  private static @Nullable View findBottomSheetParent(final @NonNull View view) {
    View current = view;
    while (current != null) {
      final ViewGroup.LayoutParams params = current.getLayoutParams();
      if (params instanceof CoordinatorLayout.LayoutParams
          && ((CoordinatorLayout.LayoutParams) params).getBehavior()
              instanceof ViewPagerBottomSheetBehavior) {
        return current;
      }
      final ViewParent parent = current.getParent();
      current = !(parent instanceof View) ? null : (View) parent;
    }
    return null;
  }
}
