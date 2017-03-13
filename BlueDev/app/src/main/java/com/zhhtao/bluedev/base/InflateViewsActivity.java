package com.zhhtao.bluedev.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.zhhtao.bluedev.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InflateViewsActivity extends AppCompatActivity {

    @Bind(R.id.tv_name)
    TextView mTvName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_carport_list);
        ButterKnife.bind(this);

    }

}
