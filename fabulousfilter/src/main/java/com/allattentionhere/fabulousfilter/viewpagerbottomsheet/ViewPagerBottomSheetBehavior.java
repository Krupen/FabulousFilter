/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allattentionhere.fabulousfilter.viewpagerbottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.os.ParcelableCompat;
import androidx.core.os.ParcelableCompatCreatorCallbacks;
import androidx.customview.view.AbsSavedState;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.VelocityTrackerCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.allattentionhere.fabulousfilter.R;

import java.lang.ref.WeakReference;

public class ViewPagerBottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

  /** Callback for monitoring events about bottom sheets. */
  public abstract static class BottomSheetCallback {

    public abstract void onStateChanged(View bottomSheet, int newState);

    public abstract void onSlide(View bottomSheet, float slideOffset);
  }

  /** The bottom sheet is dragging. */
  public static final int STATE_DRAGGING = 1;
  /** The bottom sheet is settling. */
  public static final int STATE_SETTLING = 2;

  public static final int STATE_EXPANDED = 3;
  public static final int STATE_COLLAPSED = 4;
  public static final int STATE_HIDDEN = 5;

  public @interface State {}

  public static final int PEEK_HEIGHT_AUTO = -1;
  private static final float HIDE_THRESHOLD = 0.5f;
  private static final float HIDE_FRICTION = 0.1f;

  private boolean hideable;
  private boolean ignoreEvents;
  private boolean skipCollapsed;
  private boolean peekHeightAuto;
  private boolean nestedScrolled;
  private boolean touchingScrollingChild;
  private int initialY;
  private int minOffset;
  private int maxOffset;
  private int peekHeight;
  private int parentHeight;
  private int peekHeightMin;
  private int activePointerId;
  private int lastNestedScrollDy;
  private int state = STATE_COLLAPSED;
  private float maximumVelocity;
  private @Nullable ViewDragHelper viewDragHelper;
  private @Nullable WeakReference<V> viewRef;
  private @Nullable WeakReference<View> nestedScrollingChildRef;
  private @Nullable BottomSheetCallback callback;
  private @Nullable VelocityTracker velocityTracker;

  public ViewPagerBottomSheetBehavior() {}

  @SuppressLint("PrivateResource")
  public ViewPagerBottomSheetBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray styledAttributes =
        context.obtainStyledAttributes(attrs, R.styleable.BottomSheetBehavior_Layout);
    TypedValue peekValue =
        styledAttributes.peekValue(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight);
    if (peekValue != null && peekValue.data == PEEK_HEIGHT_AUTO) {
      setPeekHeight(peekValue.data);
    } else {
      setPeekHeight(
          styledAttributes.getDimensionPixelSize(
              R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, PEEK_HEIGHT_AUTO));
    }
    setHideable(
        styledAttributes.getBoolean(
            R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false));
    this.skipCollapsed =
        styledAttributes.getBoolean(
            R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed, false);
    styledAttributes.recycle();
    ViewConfiguration configuration = ViewConfiguration.get(context);
    maximumVelocity = configuration.getScaledMaximumFlingVelocity();
  }

  @Override
  public Parcelable onSaveInstanceState(@NonNull CoordinatorLayout parent, @NonNull V child) {
    return new SavedState(super.onSaveInstanceState(parent, child), state);
  }

  @Override
  public void onRestoreInstanceState(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull Parcelable state) {
    SavedState savedState = (SavedState) state;
    super.onRestoreInstanceState(parent, child, savedState.getSuperState());
    // Intermediate states are restored as collapsed state
    if (savedState.state == STATE_DRAGGING || savedState.state == STATE_SETTLING) {
      this.state = STATE_COLLAPSED;
    } else {
      this.state = savedState.state;
    }
  }

  @SuppressLint("PrivateResource")
  @Override
  public boolean onLayoutChild(
      @NonNull CoordinatorLayout parent, @NonNull V child, int layoutDirection) {
    if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
      ViewCompat.setFitsSystemWindows(child, true);
    }
    int savedTop = child.getTop();
    // First let the parent lay it out
    parent.onLayoutChild(child, layoutDirection);
    // Offset the bottom sheet
    parentHeight = parent.getHeight();
    int peekHeight = this.peekHeight;
    if (peekHeightAuto) {
      if (peekHeightMin == 0) {
        peekHeightMin =
            parent
                .getResources()
                .getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min);
      }
      peekHeight = Math.max(peekHeightMin, parentHeight - parent.getWidth() * 9 / 16);
    }
    minOffset = Math.max(0, parentHeight - child.getHeight());
    maxOffset = Math.max(parentHeight - peekHeight, minOffset);
    if (state == STATE_EXPANDED) {
      ViewCompat.offsetTopAndBottom(child, minOffset);
    } else if (hideable && state == STATE_HIDDEN) {
      ViewCompat.offsetTopAndBottom(child, parentHeight);
    } else if (state == STATE_COLLAPSED) {
      ViewCompat.offsetTopAndBottom(child, maxOffset);
    } else if (state == STATE_DRAGGING || state == STATE_SETTLING) {
      ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
    }
    if (viewDragHelper == null) {
      viewDragHelper = ViewDragHelper.create(parent, mDragCallback);
    }
    viewRef = new WeakReference<>(child);
    nestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
    return true;
  }

  @Override
  public boolean onInterceptTouchEvent(
      @NonNull CoordinatorLayout parent, V child, @NonNull MotionEvent event) {
    if (!child.isShown()) {
      ignoreEvents = true;
      return false;
    }
    int action = MotionEventCompat.getActionMasked(event);
    // Record the velocity
    if (action == MotionEvent.ACTION_DOWN) {
      reset();
    }
    if (velocityTracker == null) {
      velocityTracker = VelocityTracker.obtain();
    }
    velocityTracker.addMovement(event);
    switch (action) {
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        touchingScrollingChild = false;
        activePointerId = MotionEvent.INVALID_POINTER_ID;
        // Reset the ignore flag
        if (ignoreEvents) {
          ignoreEvents = false;
          return false;
        }
        break;
      case MotionEvent.ACTION_DOWN:
        int initialX = (int) event.getX();
        initialY = (int) event.getY();
        View scroll = null;
        if (nestedScrollingChildRef != null) {
          scroll = nestedScrollingChildRef.get();
        }
        if (scroll != null && parent.isPointInChildBounds(scroll, initialX, initialY)) {
          activePointerId = event.getPointerId(event.getActionIndex());
          touchingScrollingChild = true;
        }
        ignoreEvents =
            activePointerId == MotionEvent.INVALID_POINTER_ID
                && !parent.isPointInChildBounds(child, initialX, initialY);
        break;
    }
    if (viewDragHelper != null
        && !ignoreEvents
        && viewDragHelper.shouldInterceptTouchEvent(event)) {
      return true;
    }
    // We have to handle cases that the ViewDragHelper does not capture the bottom sheet because
    // it is not the top most view of its parent. This is not necessary when the touch event is
    // happening over the scrolling content as nested scrolling logic handles that case.
    View scroll = null;
    if (nestedScrollingChildRef != null) {
      scroll = nestedScrollingChildRef.get();
    }
    return action == MotionEvent.ACTION_MOVE
        && scroll != null
        && !ignoreEvents
        && state != STATE_DRAGGING
        && !parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY())
        && Math.abs(initialY - event.getY()) > viewDragHelper.getTouchSlop();
  }

  @Override
  public boolean onTouchEvent(
      @NonNull CoordinatorLayout parent, V child, @NonNull MotionEvent event) {
    if (!child.isShown()) {
      return false;
    }
    int action = MotionEventCompat.getActionMasked(event);
    if (state == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
      return true;
    }
    if (viewDragHelper == null) {
      viewDragHelper = ViewDragHelper.create(parent, mDragCallback);
    }
    viewDragHelper.processTouchEvent(event);
    // Record the velocity
    if (action == MotionEvent.ACTION_DOWN) {
      reset();
    }
    if (velocityTracker == null) {
      velocityTracker = VelocityTracker.obtain();
    }
    velocityTracker.addMovement(event);
    // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
    // to capture the bottom sheet in case it is not captured and the touch slop is passed.
    if (action == MotionEvent.ACTION_MOVE && !ignoreEvents) {
      if (Math.abs(initialY - event.getY()) > viewDragHelper.getTouchSlop()) {
        viewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
      }
    }
    return !ignoreEvents;
  }

  @Override
  public boolean onStartNestedScroll(
      @NonNull CoordinatorLayout coordinatorLayout,
      @NonNull V child,
      @NonNull View directTargetChild,
      @NonNull View target,
      int nestedScrollAxes) {
    lastNestedScrollDy = 0;
    nestedScrolled = false;
    return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override
  public void onNestedPreScroll(
      @NonNull CoordinatorLayout coordinatorLayout,
      @NonNull V child,
      @NonNull View target,
      int dx,
      int dy,
      @NonNull int[] consumed) {
    View scrollingChild = null;
    if (nestedScrollingChildRef != null) {
      scrollingChild = nestedScrollingChildRef.get();
    }
    if (target != scrollingChild) {
      return;
    }
    int currentTop = child.getTop();
    int newTop = currentTop - dy;
    if (dy > 0) { // Upward
      if (newTop < minOffset) {
        consumed[1] = currentTop - minOffset;
        ViewCompat.offsetTopAndBottom(child, -consumed[1]);
        setStateInternal(STATE_EXPANDED);
      } else {
        consumed[1] = dy;
        ViewCompat.offsetTopAndBottom(child, -dy);
        setStateInternal(STATE_DRAGGING);
      }
    } else if (dy < 0) { // Downward
      if (!ViewCompat.canScrollVertically(target, -1)) {
        if (newTop <= maxOffset || hideable) {
          consumed[1] = dy;
          ViewCompat.offsetTopAndBottom(child, -dy);
          setStateInternal(STATE_DRAGGING);
        } else {
          consumed[1] = currentTop - maxOffset;
          ViewCompat.offsetTopAndBottom(child, -consumed[1]);
          setStateInternal(STATE_COLLAPSED);
        }
      }
    }
    dispatchOnSlide(child.getTop());
    lastNestedScrollDy = dy;
    nestedScrolled = true;
  }

  @Override
  public void onStopNestedScroll(
      @NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target) {
    if (child.getTop() == minOffset) {
      setStateInternal(STATE_EXPANDED);
      return;
    }
    if (nestedScrollingChildRef != null
        && (target != nestedScrollingChildRef.get() || !nestedScrolled)) {
      return;
    }
    int top;
    int targetState;
    if (lastNestedScrollDy > 0) {
      top = minOffset;
      targetState = STATE_EXPANDED;
    } else if (hideable && shouldHide(child, getYVelocity())) {
      top = parentHeight;
      targetState = STATE_HIDDEN;
    } else if (lastNestedScrollDy == 0) {
      int currentTop = child.getTop();
      if (Math.abs(currentTop - minOffset) < Math.abs(currentTop - maxOffset)) {
        top = minOffset;
        targetState = STATE_EXPANDED;
      } else {
        top = maxOffset;
        targetState = STATE_COLLAPSED;
      }
    } else {
      top = maxOffset;
      targetState = STATE_COLLAPSED;
    }
    if (viewDragHelper != null && viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
      setStateInternal(STATE_SETTLING);
      ViewCompat.postOnAnimation(child, new SettleRunnable(child, targetState));
    } else {
      setStateInternal(targetState);
    }
    nestedScrolled = false;
  }

  @Override
  public boolean onNestedPreFling(
      @NonNull CoordinatorLayout coordinatorLayout,
      @NonNull V child,
      @NonNull View target,
      float velocityX,
      float velocityY) {
    return nestedScrollingChildRef != null
        && target == nestedScrollingChildRef.get()
        && (state != STATE_EXPANDED
            || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY));
  }

  public void invalidateScrollingChild() {
    View scrollingChild = null;
    if (viewRef != null) {
      scrollingChild = findScrollingChild(viewRef.get());
    }
    nestedScrollingChildRef = new WeakReference<>(scrollingChild);
  }

  public final void setPeekHeight(int peekHeight) {
    boolean layout = false;
    if (peekHeight == PEEK_HEIGHT_AUTO) {
      if (!peekHeightAuto) {
        peekHeightAuto = true;
        layout = true;
      }
    } else if (peekHeightAuto || this.peekHeight != peekHeight) {
      peekHeightAuto = false;
      this.peekHeight = Math.max(0, peekHeight);
      maxOffset = parentHeight - peekHeight;
      layout = true;
    }
    if (layout && state == STATE_COLLAPSED && viewRef != null) {
      V view = viewRef.get();
      if (view != null) {
        view.requestLayout();
      }
    }
  }

  public void setHideable(boolean hideable) {
    this.hideable = hideable;
  }

  public void setBottomSheetCallback(BottomSheetCallback callback) {
    this.callback = callback;
  }

  public final void setState(final int state) {
    if (state == this.state) {
      return;
    }
    if (viewRef == null) {
      // The view is not laid out yet; modify mState and let onLayoutChild handle it later
      if (state == STATE_COLLAPSED
          || state == STATE_EXPANDED
          || (hideable && state == STATE_HIDDEN)) {
        this.state = state;
      }
      return;
    }
    final V child = viewRef.get();
    if (child == null) {
      return;
    }
    // Start the animation; wait until a pending layout if there is one.
    ViewParent parent = child.getParent();
    if (parent != null && parent.isLayoutRequested() && ViewCompat.isAttachedToWindow(child)) {
      child.post(() -> startSettlingAnimation(child, state));
    } else {
      startSettlingAnimation(child, state);
    }
  }

  public final int getState() {
    return state;
  }

  public void setStateInternal(int state) {
    if (this.state == state) {
      return;
    }
    this.state = state;
    View bottomSheet = null;
    if (viewRef != null) {
      bottomSheet = viewRef.get();
    }
    if (bottomSheet != null && callback != null) {
      callback.onStateChanged(bottomSheet, state);
    }
  }

  private void reset() {
    activePointerId = ViewDragHelper.INVALID_POINTER;
    if (velocityTracker != null) {
      velocityTracker.recycle();
      velocityTracker = null;
    }
  }

  private boolean shouldHide(@NonNull View child, float yvel) {
    if (skipCollapsed) {
      return true;
    }
    if (child.getTop() < maxOffset) {
      // It should not hide, but collapse.
      return false;
    }
    final float newTop = child.getTop() + yvel * HIDE_FRICTION;
    return Math.abs(newTop - maxOffset) / (float) peekHeight > HIDE_THRESHOLD;
  }

  private @Nullable View findScrollingChild(@Nullable View view) {
    if (view instanceof NestedScrollingChild) {
      return view;
    }
    if (view instanceof ViewPager) {
      ViewPager viewPager = (ViewPager) view;
      View currentViewPagerChild = getCurrentView(viewPager);
      return findScrollingChild(currentViewPagerChild);
    } else if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0, count = group.getChildCount(); i < count; i++) {
        View scrollingChild = findScrollingChild(group.getChildAt(i));
        if (scrollingChild != null) {
          return scrollingChild;
        }
      }
    }
    return null;
  }

  private static @Nullable View getCurrentView(@NonNull ViewPager viewPager) {
    final int currentItem = viewPager.getCurrentItem();
    for (int i = 0; i < viewPager.getChildCount(); i++) {
      final View child = viewPager.getChildAt(i);
      final ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();
      if (!layoutParams.isDecor && currentItem == viewPager.getCurrentItem()) {
        return child;
      }
    }
    return null;
  }

  private float getYVelocity() {
    if (velocityTracker != null) {
      velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
    }
    return VelocityTrackerCompat.getYVelocity(velocityTracker, activePointerId);
  }

  private void startSettlingAnimation(View child, int state) {
    int top;
    if (state == STATE_COLLAPSED) {
      top = maxOffset;
    } else if (state == STATE_EXPANDED) {
      top = minOffset;
    } else if (hideable && state == STATE_HIDDEN) {
      top = parentHeight;
    } else {
      throw new IllegalArgumentException("Illegal state argument: " + state);
    }
    setStateInternal(STATE_SETTLING);
    if (viewDragHelper != null && viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
      ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
    }
  }

  private final ViewDragHelper.Callback mDragCallback =
      new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
          if (state == STATE_DRAGGING) {
            return false;
          }
          if (touchingScrollingChild) {
            return false;
          }
          if (state == STATE_EXPANDED && activePointerId == pointerId) {
            View scroll = nestedScrollingChildRef.get();
            if (scroll != null && ViewCompat.canScrollVertically(scroll, -1)) {
              // Let the content scroll up
              return false;
            }
          }
          return viewRef != null && viewRef.get() == child;
        }

        @Override
        public void onViewPositionChanged(
            @NonNull View changedView, int left, int top, int dx, int dy) {
          dispatchOnSlide(top);
        }

        @Override
        public void onViewDragStateChanged(int state) {
          if (state == ViewDragHelper.STATE_DRAGGING) {
            setStateInternal(STATE_DRAGGING);
          }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
          int top;
          int targetState;
          if (yvel < 0) { // Moving up
            top = minOffset;
            targetState = STATE_EXPANDED;
          } else if (hideable && shouldHide(releasedChild, yvel)) {
            top = parentHeight;
            targetState = STATE_HIDDEN;
          } else if (yvel == 0.f) {
            int currentTop = releasedChild.getTop();
            if (Math.abs(currentTop - minOffset) < Math.abs(currentTop - maxOffset)) {
              top = minOffset;
              targetState = STATE_EXPANDED;
            } else {
              top = maxOffset;
              targetState = STATE_COLLAPSED;
            }
          } else {
            top = maxOffset;
            targetState = STATE_COLLAPSED;
          }
          if (viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
            setStateInternal(STATE_SETTLING);
            ViewCompat.postOnAnimation(
                releasedChild, new SettleRunnable(releasedChild, targetState));
          } else {
            setStateInternal(targetState);
          }
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
          return constrain(top, minOffset, hideable ? parentHeight : maxOffset);
        }

        private int constrain(int amount, int low, int high) {
          return amount < low ? low : (Math.min(amount, high));
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
          return child.getLeft();
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
          if (hideable) {
            return parentHeight - minOffset;
          } else {
            return maxOffset - minOffset;
          }
        }
      };

  private void dispatchOnSlide(int top) {
    View bottomSheet = null;
    if (viewRef != null) {
      bottomSheet = viewRef.get();
    }
    if (bottomSheet != null && callback != null) {
      if (top > maxOffset) {
        callback.onSlide(bottomSheet, (float) (maxOffset - top) / (parentHeight - maxOffset));
      } else {
        callback.onSlide(bottomSheet, (float) (maxOffset - top) / ((maxOffset - minOffset)));
      }
    }
  }

  private class SettleRunnable implements Runnable {

    private final View mView;

    private final int mTargetState;

    SettleRunnable(View view, int targetState) {
      mView = view;
      mTargetState = targetState;
    }

    @Override
    public void run() {
      if (viewDragHelper != null && viewDragHelper.continueSettling(true)) {
        ViewCompat.postOnAnimation(mView, this);
      } else {
        setStateInternal(mTargetState);
      }
    }
  }

  protected static class SavedState extends AbsSavedState {

    final int state;

    public SavedState(Parcel source) {
      this(source, null);
    }

    public SavedState(Parcel source, ClassLoader loader) {
      super(source, loader);
      //noinspection ResourceType
      state = source.readInt();
    }

    public SavedState(Parcelable superState, int state) {
      super(superState);
      this.state = state;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(state);
    }
  }

  @SuppressWarnings("unchecked")
  public static <V extends View> ViewPagerBottomSheetBehavior<V> from(V view) {
    ViewGroup.LayoutParams params = view.getLayoutParams();
    if (!(params instanceof CoordinatorLayout.LayoutParams)) {
      throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }
    CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
    if (!(behavior instanceof ViewPagerBottomSheetBehavior)) {
      throw new IllegalArgumentException(
          "The view is not associated with ViewPagerBottomSheetBehavior");
    }
    return (ViewPagerBottomSheetBehavior<V>) behavior;
  }
}
