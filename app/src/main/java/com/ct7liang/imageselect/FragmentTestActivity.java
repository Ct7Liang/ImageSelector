package com.ct7liang.imageselect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);

        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, new MyFragment()).commit();
    }
}
