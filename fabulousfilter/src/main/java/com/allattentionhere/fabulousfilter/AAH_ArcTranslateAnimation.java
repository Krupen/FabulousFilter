package com.allattentionhere.fabulousfilter;

import android.graphics.PointF;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/** Created by krupenghetiya on 19/06/17. */
public class AAH_ArcTranslateAnimation extends Animation {
  private final int fromXType;
  private final int toXType;

  private final int fromYType;
  private final int toYType;

  private final float fromXValue;
  private final float toXValue;

  private final float fromYValue;
  private final float toYValue;

  private PointF start;
  private PointF control;
  private PointF end;

  /**
   * Constructor to use when building a ArcTranslateAnimation from code
   *
   * @param fromXDelta Change in X coordinate to apply at the start of the animation
   * @param toXDelta Change in X coordinate to apply at the end of the animation
   * @param fromYDelta Change in Y coordinate to apply at the start of the animation
   * @param toYDelta Change in Y coordinate to apply at the end of the animation
   */
  public AAH_ArcTranslateAnimation(
      float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
    fromXValue = fromXDelta;
    toXValue = toXDelta;
    fromYValue = fromYDelta;
    toYValue = toYDelta;

    fromXType = ABSOLUTE;
    toXType = ABSOLUTE;
    fromYType = ABSOLUTE;
    toYType = ABSOLUTE;
  }

  /**
   * Constructor to use when building a ArcTranslateAnimation from code
   *
   * @param fromXType Specifies how fromXValue should be interpreted. One of Animation.ABSOLUTE,
   *     Animation.RELATIVE_TO_SELF, or Animation.RELATIVE_TO_PARENT.
   * @param fromXValue Change in X coordinate to apply at the start of the animation. This value can
   *     either be an absolute number if fromXType is ABSOLUTE, or a percentage (where 1.0 is 100%)
   *     otherwise.
   * @param toXType Specifies how toXValue should be interpreted. One of Animation.ABSOLUTE,
   *     Animation.RELATIVE_TO_SELF, or Animation.RELATIVE_TO_PARENT.
   * @param toXValue Change in X coordinate to apply at the end of the animation. This value can
   *     either be an absolute number if toXType is ABSOLUTE, or a percentage (where 1.0 is 100%)
   *     otherwise.
   * @param fromYType Specifies how fromYValue should be interpreted. One of Animation.ABSOLUTE,
   *     Animation.RELATIVE_TO_SELF, or Animation.RELATIVE_TO_PARENT.
   * @param fromYValue Change in Y coordinate to apply at the start of the animation. This value can
   *     either be an absolute number if fromYType is ABSOLUTE, or a percentage (where 1.0 is 100%)
   *     otherwise.
   * @param toYType Specifies how toYValue should be interpreted. One of Animation.ABSOLUTE,
   *     Animation.RELATIVE_TO_SELF, or Animation.RELATIVE_TO_PARENT.
   * @param toYValue Change in Y coordinate to apply at the end of the animation. This value can
   *     either be an absolute number if toYType is ABSOLUTE, or a percentage (where 1.0 is 100%)
   *     otherwise.
   */
  public AAH_ArcTranslateAnimation(
      int fromXType,
      float fromXValue,
      int toXType,
      float toXValue,
      int fromYType,
      float fromYValue,
      int toYType,
      float toYValue) {

    this.fromXValue = fromXValue;
    this.toXValue = toXValue;
    this.fromYValue = fromYValue;
    this.toYValue = toYValue;

    this.fromXType = fromXType;
    this.toXType = toXType;
    this.fromYType = fromYType;
    this.toYType = toYType;
  }

  @Override
  protected void applyTransformation(float interpolatedTime, Transformation t) {
    float dx = calcBezier(interpolatedTime, start.x, control.x, end.x);
    float dy = calcBezier(interpolatedTime, start.y, control.y, end.y);

    t.getMatrix().setTranslate(dx, dy);
  }

  @Override
  public void initialize(int width, int height, int parentWidth, int parentHeight) {
    super.initialize(width, height, parentWidth, parentHeight);
    float fromXDelta = resolveSize(fromXType, fromXValue, width, parentWidth);
    float toXDelta = resolveSize(toXType, toXValue, width, parentWidth);
    float fromYDelta = resolveSize(fromYType, fromYValue, height, parentHeight);
    float toYDelta = resolveSize(toYType, toYValue, height, parentHeight);

    start = new PointF(fromXDelta, fromYDelta);
    end = new PointF(toXDelta, toYDelta);
    control = new PointF(fromXDelta, toYDelta); // How to choose the
    // Control point(we can use the cross of the two tangents from p0,p1)
  }

  /**
   * Calculate the position on a quadratic bezier curve by given three points and the percentage of
   * time passed.
   *
   * <p>from http://en.wikipedia.org/wiki/B%C3%A9zier_curve
   *
   * @param interpolatedTime the fraction of the duration that has passed where 0 <= time <= 1
   * @param p0 a single dimension of the starting point
   * @param p1 a single dimension of the control point
   * @param p2 a single dimension of the ending point
   */
  private long calcBezier(float interpolatedTime, float p0, float p1, float p2) {
    return Math.round(
        (Math.pow((1 - interpolatedTime), 2) * p0)
            + (2 * (1 - interpolatedTime) * interpolatedTime * p1)
            + (Math.pow(interpolatedTime, 2) * p2));
  }
}
