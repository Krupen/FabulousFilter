# FabulousFilter
[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg)](https://android-arsenal.com/api?level=15) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FabulousFilter-2CB3E5.svg)]( https://android-arsenal.com/details/1/5943) [![Material Up](https://img.shields.io/badge/MaterialUp-FabulousFilter-2856b6.svg)](https://material.uplabs.com/posts/fabulousfilter-library)

### Show some :heart: and star the repo to support the project
[![GitHub stars](https://img.shields.io/github/stars/Krupen/FabulousFilter.svg?style=social)](https://github.com/Krupen/FabulousFilter/stargazers) [![GitHub forks](https://img.shields.io/github/forks/Krupen/FabulousFilter.svg?style=social)](https://github.com/Krupen/FabulousFilter/network) [![GitHub watchers](https://img.shields.io/github/watchers/Krupen/FabulousFilter.svg?style=social)](https://github.com/Krupen/FabulousFilter/watchers) [![GitHub followers](https://img.shields.io/github/followers/Krupen.svg?style=social)](https://github.com/Krupen/followers)  
[![Twitter Follow](https://img.shields.io/twitter/follow/KrupenGhetiya.svg?style=social&label=Follow)](https://twitter.com/krupenghetiya)


This library is the implementation of filter-concept posted on <a href="https://material.uplabs.com/posts/filters-interface-resources" target="_blank">MaterialUp.com</a>.

It makes animation of FloatingActionButton to BottomSheetDialog easy to implement.

# Concept
![fabulousfilter concept](https://raw.githubusercontent.com/Krupen/FabulousFilter/master/concept.gif)

# Demo
![fabulousfilter demo 1](https://raw.githubusercontent.com/Krupen/FabulousFilter/master/newDemo1.gif)  ![fabulousfilter demo 1](https://raw.githubusercontent.com/Krupen/FabulousFilter/master/newDemo2.gif)

# Download
**Gradle**

**Step 1.** Add the jCenter repository to your project-level build.gradle file

``` groovy
allprojects {
	repositories {
		jcenter()
	}
}
```

**Step 2.** Add the dependency to your app-level build.gradle file:

``` groovy
dependencies {
	 compile 'com.allattentionhere:fabulousfilter:0.0.5'
}
```

# Usage

Create a Fragment that extends `AAH_FabulousFragment`:
```
public class MySampleFabFragment extends AAH_FabulousFragment {

    public static MySampleFabFragment newInstance() {
        MySampleFabFragment f = new MySampleFabFragment();
        return f;
    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.filter_sample_view, null);
        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        contentView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("closed");
            }
        });

        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
	setAnimationListener((AnimationListener) getActivity()); //optional; to get animation callbacks
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

}
```
Create view for the fragment which has parent element `AAH_FilterView`:
```
<com.allattentionhere.fabulousfilter.AAH_FilterView 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <RelativeLayout
        android:id="@+id/rl_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_alignParentBottom="true"
        android:background="@color/orange"
        android:visibility="invisible"
        tools:ignore="MissingPrefix"
        tools:visibility="visible">

        <LinearLayout
            android:id="@+id/ll_buttons"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:layout_alignParentBottom="true"
            android:background="@color/brown"
            android:orientation="horizontal"
            android:weightSum="2">
        </LinearLayout>

    </RelativeLayout>

</com.allattentionhere.fabulousfilter.AAH_FilterView>
```

Start the fragment on click of FloatingActionButton as below:
```
fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySampleFabFragment dialogFrag = MySampleFabFragment.newInstance();
                dialogFrag.setParentFab(fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
```

# Parameters

* ### Main View (Required)
This parameter specifies the ViewGroup of the bottom sheet to be shown after animation ends. It can be any ViewGroup(LinearLayout/FrameLayout etc):
```
setViewMain(relativelayout_content);
```

* ### Inflated Dialog View (Required)
This parameter specifies the inflated view for the dialog:
```
setMainContentView(contentDialogView);
```

* ### Animation duration (Optional)
This parameter sets animation duration of translate and scale animation in `milliseconds`:
```
setAnimationDuration(600); // default 500ms
```

* ### Peek Height (Optional)
This parameter sets the peek height of the bottom sheet in `dp`:
```
setPeekHeight(300); // default 400dp
```

* ### Callback (Optional)
This paramter is used to get callback from `AAH_FabulousFragment` to the component that called it:
```
setCallbacks((Callbacks) getActivity());
```
To use it, implement the callback in the calling component(Activity/Fragment etc), example:
```
public class MainSampleActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sample);
    }

    @Override
    public void onResult(Object result) {
        Log.d("k9res", "onResult: " + result.toString());
        if (result.toString().equalsIgnoreCase("swiped_down")) {
            //do something or nothing
        } else {
            //handle result
        }
    }
}

```

* ### Animation Listener (Optional)
This parameter is used to get animation callbacks.
```
setAnimationListener((AnimationListener) getActivity());
```
To use it, implement the AnimationListener in the calling component(Activity/Fragment etc), example:
```
public class MainSampleActivity extends AppCompatActivity implements AAH_FabulousFragment.AnimationListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sample);
    }
    
   @Override
    public void onOpenAnimationStart() {
        //do something on open animation start
    }

    @Override
    public void onOpenAnimationEnd() {
        //do something on open animation end
    }

    @Override
    public void onCloseAnimationStart() {
        //do something on close animation start
    }

    @Override
    public void onCloseAnimationEnd() {
        //do something on close animation start
    }
}

```

* ### Static View (Optional)
This parameter is used to make view in Bottom Sheet static when user slides it. It can be any ViewGroup(LinearLayout/FrameLayout etc):
```
setViewgroupStatic(linearlayout_buttons);
```

* ### ViewPager (Optional)
This parameter is used to support scrolling in ViewPager as BottomSheetDialog does not support multiple views with scroll:
```
setViewPager(viewPager);
```
# Libraries by developer
* <a href="https://github.com/Krupen/AutoplayVideos" target="_blank">AutoplayVideos</a>

# Apps by developer
[![Price Stalker](https://github.com/Krupen/AutoplayVideos/blob/master/pricestalker.png?raw=true)](https://play.google.com/store/apps/details?id=com.allattentionhere.pricestalker)  [![Show Card Game](https://github.com/Krupen/AutoplayVideos/blob/master/show.png?raw=true)](https://play.google.com/store/apps/details?id=com.allattentionhere.show)  [![Safio chat](https://github.com/Krupen/AutoplayVideos/blob/master/safiochat.png?raw=true)](https://play.google.com/store/apps/details?id=com.allattentionhere.safio)

# License
Copyright 2017 Krupen Ghetiya

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
