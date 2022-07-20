package com.allattentionhere.fabulousfilter.viewpagerbottomsheet;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ViewPagerBottomSheetDialogFragment extends BottomSheetDialogFragment {

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new ViewPagerBottomSheetDialog(getContext(), getTheme());
  }
}
