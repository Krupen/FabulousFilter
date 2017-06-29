package com.allattentionhere.fabulousfilter.viewpagerbottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;


public class ViewPagerBottomSheetDialogFragment extends BottomSheetDialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ViewPagerBottomSheetDialog(getContext(), getTheme());
    }

}
