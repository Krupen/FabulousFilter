package com.allattentionhere.fabulousfiltersample;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import biz.laenger.android.vpbs.BottomSheetUtils;

import static android.R.attr.key;
import static com.allattentionhere.fabulousfiltersample.R.id.imgbtn_refresh;
import static com.allattentionhere.fabulousfiltersample.R.id.nsv;


/**
 * Created by krupenghetiya on 23/06/17.
 */

public class MyFabFragment extends AAH_FabulousFragment {


    ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    List<TextView> textviews = new ArrayList<>();

    TabLayout tabs_types;
    ViewPager vp_types;
    ImageButton imgbtn_refresh,imgbtn_apply;
    SectionsPagerAdapter mAdapter;
    private DisplayMetrics metrics;



    public static MyFabFragment newInstance() {
        MyFabFragment mff = new MyFabFragment();
        return mff;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applied_filters = ((MainActivity)getActivity()).getApplied_filters();
        metrics = this.getResources().getDisplayMetrics();

        for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {
            Log.d("k9res", "from activity: "+entry.getKey());
            for(String s: entry.getValue()){
                Log.d("k9res", "from activity val: "+s);

            }
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.filter_view, null);
        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        imgbtn_refresh = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        imgbtn_apply = (ImageButton) contentView.findViewById(R.id.imgbtn_apply);
        vp_types = (ViewPager) contentView.findViewById(R.id.vp_types);
        tabs_types = (TabLayout) contentView.findViewById(R.id.tabs_types);
        imgbtn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter(applied_filters);
            }
        });
        imgbtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (TextView tv : textviews) {
                    tv.setTag("unselected");
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }
                applied_filters.clear();
            }
        });

        mAdapter = new SectionsPagerAdapter();
        vp_types.setOffscreenPageLimit(4);
        vp_types.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        tabs_types.setupWithViewPager(vp_types);

        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView);// necessary; call at end before super
        super.setupDialog(dialog, style);
    }

    public class SectionsPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.view_filters_sorters, collection, false);
            FlexboxLayout fbl = (FlexboxLayout) layout.findViewById(R.id.fbl);
//            LinearLayout ll_scroll = (LinearLayout) layout.findViewById(R.id.ll_scroll);
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels-(104*metrics.density)));
//            ll_scroll.setLayoutParams(lp);
            switch (position) {
                case 0:
                    inflateLayoutWithFilters("genre", fbl);
                    break;
                case 1:
                    inflateLayoutWithFilters("rating", fbl);
                    break;
                case 2:
                    inflateLayoutWithFilters("year", fbl);
                    break;
                case 3:
                    inflateLayoutWithFilters("quality", fbl);
                    break;
            }
            collection.addView(layout);
            return layout;

        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
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
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private void inflateLayoutWithFilters(final String filter_category, FlexboxLayout fbl) {
        List<String> keys = new ArrayList<>();
        switch (filter_category) {
            case "genre":
                keys = ((MainActivity) getActivity()).getmData().getUniqueGenreKeys();
                break;
            case "rating":
                keys = ((MainActivity) getActivity()).getmData().getUniqueRatingKeys();
                break;
            case "year":
                keys = ((MainActivity) getActivity()).getmData().getUniqueYearKeys();
                break;
            case "quality":
                keys = ((MainActivity) getActivity()).getmData().getUniqueQualityKeys();
                break;
        }

        for (int i = 0; i < keys.size(); i++) {
            View subchild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
            final TextView tv = ((TextView) subchild.findViewById(R.id.txt_title));
            tv.setText(keys.get(i));
            final int finalI = i;
            final List<String> finalKeys = keys;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv.getTag() != null && tv.getTag().equals("selected")) {
                        tv.setTag("unselected");
                        tv.setBackgroundResource(R.drawable.chip_unselected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                        removeFromSelectedMap(filter_category, finalKeys.get(finalI));
                    } else {
                        tv.setTag("selected");
                        tv.setBackgroundResource(R.drawable.chip_selected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
                        addToSelectedMap(filter_category, finalKeys.get(finalI));
                    }
                }
            });
            try {
                Log.d("k9res", "key: " + filter_category + " |val:" + keys.get(finalI));
                Log.d("k9res", "applied_filters != null: " + (applied_filters != null));
                Log.d("k9res", "applied_filters.get(key) != null: " + (applied_filters.get(filter_category) != null));
                Log.d("k9res", "applied_filters.get(key).contains(keys.get(finalI)): " + (applied_filters.get(filter_category).contains(keys.get(finalI))));
            }catch (Exception e){

            }
            if (applied_filters != null && applied_filters.get(filter_category) != null && applied_filters.get(filter_category).contains(keys.get(finalI))) {
                tv.setTag("selected");
                tv.setBackgroundResource(R.drawable.chip_selected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
            } else {
                tv.setBackgroundResource(R.drawable.chip_unselected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
            }
            textviews.add(tv);

            fbl.addView(subchild);
        }


    }

    private void addToSelectedMap(String key, String value) {
        if (applied_filters.get(key) != null && !applied_filters.get(key).contains(value)) {
            applied_filters.get(key).add(value);
        } else {
            List<String> temp = new ArrayList<>();
            temp.add(value);
            applied_filters.put(key, temp);
        }
    }

    private void removeFromSelectedMap(String key, String value) {
        if (applied_filters.get(key).size() == 1) {
            applied_filters.remove(key);
        } else {
            applied_filters.get(key).remove(value);
        }
    }
}
