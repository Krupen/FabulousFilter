package com.allattentionhere.fabulousfiltersample;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);

    findViewById(R.id.btn_bottom)
        .setOnClickListener(
            v -> {
              Intent i = new Intent(MenuActivity.this, MainActivity.class);
              i.putExtra("fab", 1);
              startActivity(i);
            });

    findViewById(R.id.btn_top)
        .setOnClickListener(
            v -> {
              Intent i = new Intent(MenuActivity.this, MainActivity.class);
              i.putExtra("fab", 2);
              startActivity(i);
            });

    findViewById(R.id.btn_understanding)
        .setOnClickListener(
            v -> {
              Intent i = new Intent(MenuActivity.this, MainSampleActivity.class);
              startActivity(i);
            });

    findViewById(R.id.btn_fragment)
        .setOnClickListener(
            v -> {
              Intent i = new Intent(MenuActivity.this, FragmentExampleActivity.class);
              startActivity(i);
            });
  }
}
