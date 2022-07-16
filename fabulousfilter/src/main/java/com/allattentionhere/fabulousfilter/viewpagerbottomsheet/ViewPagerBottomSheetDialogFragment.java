package com.allattentionhere.fabulousfilter.viewpagerbottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class ViewPagerBottomSheetDialogFragment extends BottomSheetDialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ViewPagerBottomSheetDialog(getContext(), getTheme());
    }

}
