package com.zhhtao.bluedev.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.base.BaseActivity;
import com.zhhtao.bluedev.base.SocketUtil;
import com.zhhtao.bluedev.bean.CarPortInfoBean;
import com.zhhtao.bluedev.bean.CurrentParkBean;
import com.zhhtao.bluedev.bean.ParkInfoBean;
import com.zhhtao.bluedev.ui.ZhtCustomProgressDialog;
import com.zhhtao.bluedev.utils.LogUtil;
import com.zhhtao.bluedev.utils.StreamUtils;
import com.zhhtao.bluedev.utils.UIUtils;
import com.zhhtao.bluedev.utils.ZhtUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GotoParkActivity extends BaseActivity {

    @Bind(R.id.lv_park)
    ListView mLvPark;
    @Bind(R.id.tv_title_title_bar)
    TextView mTvTitleTitleBar;

    private List<CarPortInfoBean> mCarPortList = new ArrayList<>();
    private GotoParkAdapter mAdapter;
    private ParkInfoBean mParkInfoBean;

    public static final int REFRESH_STATE = 1;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STATE:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    byte[] rec_cmd;
    private ImageView mIvRightTitleBar;

    public static void start(Context context, ParkInfoBean bean) {
        Intent intent = new Intent(context, GotoParkActivity.class);
        intent.putExtra("park", bean);
        context.startActivity(intent);
    }

    private void parseIntent() {
        mParkInfoBean = (ParkInfoBean) getIntent().getSerializableExtra("park");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goto_park);
        ButterKnife.bind(this);
        parseIntent();
        initData();
        initView();

        query_state();
    }

    /**
     * 查询当前停车位的状态
     */
    private void query_state() {
        SocketUtil.getInstance().sendData(query_cmd);

        new Thread(new Runnable() {
            @Override
            public void run() {
                rec_cmd = SocketUtil.getInstance().readBytes();
                LogUtil.w(Arrays.toString(rec_cmd));
                for (int i = 4; i < 14; i++) {
                    if (rec_cmd[i] == 1) {
                        mCarPortList.get(i - 4).setState("不可用");
                    } else {
                        mCarPortList.get(i - 4).setState("可用");
                    }
                }

                mHandler.sendEmptyMessage(REFRESH_STATE);
                ZhtCustomProgressDialog.dismiss2();
            }
        }).start();

        ZhtCustomProgressDialog.show(mContext, "", true, null);
    }

    private void initView() {
        mTvTitleTitleBar.setText(mParkInfoBean.getName());
        mAdapter = new GotoParkAdapter(mContext, 0, mCarPortList);
        mLvPark.setAdapter(mAdapter);
        mIvRightTitleBar = (ImageView) findViewById(R.id.iv_right_title_bar);
        mIvRightTitleBar.setImageResource(R.drawable.park_map);
        mIvRightTitleBar.setVisibility(View.VISIBLE);
        mIvRightTitleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.showToast(mContext, "park");
                ZhtUtils.gotoIntent(mContext, ParkMapActivity.class);
            }
        });
    }

    private void initData() {
        for (int i = 1; i < 11; i++) {
            CarPortInfoBean bean = new CarPortInfoBean();
            bean.setName("停车位" + i);
            bean.setState("可用");
            mCarPortList.add(bean);
        }
    }

    @OnClick(R.id.tv_title_title_bar)
    public void onClick() {
    }

    byte[] send_cmd = {0x55, 0x51, 0x2d, 0x00, 0x01, 0x52};
    byte[] query_cmd = {0x55, 0x51, 0x2d, 0x54};

    class GotoParkAdapter extends ArrayAdapter<CarPortInfoBean> {

        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_state)
        TextView mTvState;
        @Bind(R.id.tv_price)
        TextView mTvPrice;
        @Bind(R.id.btn_park_in)
        Button mBtnParkIn;

        public GotoParkAdapter(Context context, int resource, List<CarPortInfoBean> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_carport_list,
                        parent, false);
            }

            ButterKnife.bind(this, convertView);
            final CarPortInfoBean bean = getItem(position);
            mTvName.setText(bean.getName());

            mTvState.setText(bean.getState());
            if (bean.getState().equals("不可用")) {
                mTvState.setBackgroundResource(R.drawable.bg_red_text);
            } else {
                mTvState.setBackgroundResource(R.drawable.bg_blue_text);
            }

            mBtnParkIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("提示");
                    builder.setMessage("您确定要在 " + bean.getName() + " 停车吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UIUtils.showToast(mContext, "正在为您开启 " + bean.getName());
                            send_cmd[4] = (byte) (position + 1);
                            bean.setState("不可用");
                            mAdapter.notifyDataSetChanged();
                            SocketUtil.getInstance().sendData(send_cmd);


                            //保存本地记录
                            CurrentParkBean curPark = new CurrentParkBean();
                            curPark.setParkName(mParkInfoBean.getName());
                            curPark.setCarPort("停车位" + (position + 1));

                            curPark.setStartTime(System.currentTimeMillis());
                            StreamUtils.objectToFile(curPark, "CurrentParkBean", mContext);
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.setCancelable(false);
                    builder.show();
                }
            });

            if (bean.getState().equals("不可用")) {
                mBtnParkIn.setClickable(false);
            } else {
                mBtnParkIn.setClickable(true);
            }

            return convertView;
        }
    }

}
