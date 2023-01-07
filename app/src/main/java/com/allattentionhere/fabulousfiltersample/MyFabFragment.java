package com.allattentionhere.fabulousfiltersample;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.collection.ArrayMap;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;
import java.util.ArrayList;
import java.util.List;

/** Created by krupenghetiya on 23/06/17. */
public class MyFabFragment extends AAH_FabulousFragment {

  ArrayMap<String, List<String>> appliedFilters = new ArrayMap<>();
  List<TextView> textViews = new ArrayList<>();

  TabLayout tabsTypes;

  ImageButton refreshButton, applyButton;
  SectionsPagerAdapter sectionsPagerAdapter;

  public static MyFabFragment newInstance() {
    return new MyFabFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appliedFilters = ((MainActivity) getActivity()).getAppliedFilters();
  }

  @Override
  public void setupDialog(@NonNull Dialog dialog, int style) {
    View contentView = View.inflate(getContext(), R.layout.filter_view, null);

    RelativeLayout contentContainer = contentView.findViewById(R.id.rl_content);
    LinearLayout buttonsContainer = contentView.findViewById(R.id.ll_buttons);
    refreshButton = contentView.findViewById(R.id.imgbtn_refresh);
    applyButton = contentView.findViewById(R.id.imgbtn_apply);
    ViewPager typesViewPager = contentView.findViewById(R.id.vp_types);
    tabsTypes = contentView.findViewById(R.id.tabs_types);

    applyButton.setOnClickListener(v -> closeFilter(appliedFilters));
    refreshButton.setOnClickListener(
        v -> {
          for (TextView tv : textViews) {
            tv.setTag("unselected");
            tv.setBackgroundResource(R.drawable.chip_unselected);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
          }
          appliedFilters.clear();
        });

    sectionsPagerAdapter = new SectionsPagerAdapter();
    typesViewPager.setOffscreenPageLimit(4);
    typesViewPager.setAdapter(sectionsPagerAdapter);
    sectionsPagerAdapter.notifyDataSetChanged();
    tabsTypes.setupWithViewPager(typesViewPager);

    // params to set
    setAnimationDuration(600); // optional; default 500ms
    setInterpolator(new AccelerateInterpolator()); // optional;
    setPeekHeight(300); // optional; default 400dp
    setCallbacks((Callbacks) getActivity()); // optional; to get back result
    setAnimationListener((AnimationListener) getActivity()); // optional; to get animation callbacks
    setViewgroupStatic(buttonsContainer); // optional; layout to stick at bottom on slide
    setViewPager(typesViewPager); // optional; if you use viewpager that has scrollview
    setViewMain(contentContainer); // necessary; main bottomsheet view
    setMainContentView(contentView); // necessary; call at end before super
    super.setupDialog(dialog, style); // call super at last
  }

  public class SectionsPagerAdapter extends PagerAdapter {

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
      LayoutInflater inflater = LayoutInflater.from(getContext());
      ViewGroup layout =
          (ViewGroup) inflater.inflate(R.layout.view_filters_sorters, collection, false);
      FlexboxLayout flexboxLayout = layout.findViewById(R.id.fbl);
      switch (position) {
        case 0:
          inflateLayoutWithFilters("genre", flexboxLayout);
          break;
        case 1:
          inflateLayoutWithFilters("rating", flexboxLayout);
          break;
        case 2:
          inflateLayoutWithFilters("year", flexboxLayout);
          break;
        case 3:
          inflateLayoutWithFilters("quality", flexboxLayout);
          break;
      }
      collection.addView(layout);
      return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
      collection.removeView((View) view);
    }

    @Override
    public int getCount() {
      return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return "GENRE";
        case 1:
          return "RATING";
        case 2:
          return "YEAR";
        case 3:
          return "QUALITY";
      }
      return "";
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
      return view == object;
    }
  }

  private void inflateLayoutWithFilters(final String filter_category, FlexboxLayout fbl) {
    if (getContext() == null || getActivity() == null || getActivity().isFinishing()) {
      return;
    }
    List<String> keys = new ArrayList<>();
    switch (filter_category) {
      case "genre":
        keys = ((MainActivity) getActivity()).getMovieData().getUniqueGenreKeys();
        break;
      case "rating":
        keys = ((MainActivity) getActivity()).getMovieData().getUniqueRatingKeys();
        break;
      case "year":
        keys = ((MainActivity) getActivity()).getMovieData().getUniqueYearKeys();
        break;
      case "quality":
        keys = ((MainActivity) getActivity()).getMovieData().getUniqueQualityKeys();
        break;
    }

    for (int i = 0; i < keys.size(); i++) {
      View subChild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
      final TextView title = subChild.findViewById(R.id.txt_title);
      title.setText(keys.get(i));
      final int finalI = i;
      final List<String> finalKeys = keys;
      title.setOnClickListener(
          v -> {
            if (title.getTag() != null && title.getTag().equals("selected")) {
              title.setTag("unselected");
              title.setBackgroundResource(R.drawable.chip_unselected);
              title.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
              removeFromSelectedMap(filter_category, finalKeys.get(finalI));
            } else {
              title.setTag("selected");
              title.setBackgroundResource(R.drawable.chip_selected);
              title.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
              addToSelectedMap(filter_category, finalKeys.get(finalI));
            }
          });
      if (appliedFilters != null
          && appliedFilters.get(filter_category) != null
          && appliedFilters.get(filter_category).contains(keys.get(finalI))) {
        title.setTag("selected");
        title.setBackgroundResource(R.drawable.chip_selected);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
      } else {
        title.setBackgroundResource(R.drawable.chip_unselected);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
      }
      textViews.add(title);

      fbl.addView(subChild);
    }
  }

  private void addToSelectedMap(String key, String value) {
    if (appliedFilters.get(key) != null
        && appliedFilters.get(key) != null
        && !appliedFilters.get(key).contains(value)) {
      appliedFilters.get(key).add(value);
    } else {
      List<String> temp = new ArrayList<>();
      temp.add(value);
      appliedFilters.put(key, temp);
    }
  }

  private void removeFromSelectedMap(String key, String value) {
    if (appliedFilters.get(key) == null) {
      return;
    }
    if (appliedFilters.get(key).size() == 1) {
      appliedFilters.remove(key);
    } else {
      appliedFilters.get(key).remove(value);
    }
  }
}
