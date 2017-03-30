package com.zhhtao.bluedev.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.activity.BombPayActivity;
import com.zhhtao.bluedev.base.BaseFragment;
import com.zhhtao.bluedev.base.MyApplication;
import com.zhhtao.bluedev.base.MyConstant;
import com.zhhtao.bluedev.base.SocketUtil;
import com.zhhtao.bluedev.bean.CurrentParkBean;
import com.zhhtao.bluedev.bean.ParkHistoryBean;
import com.zhhtao.bluedev.ui.ZhtCustomProgressDialog;
import com.zhhtao.bluedev.utils.FormatUtil;
import com.zhhtao.bluedev.utils.LogUtil;
import com.zhhtao.bluedev.utils.SharedPreferencesUtil;
import com.zhhtao.bluedev.utils.StreamUtils;
import com.zhhtao.bluedev.utils.UIUtils;
import com.zhhtao.bluedev.utils.ZhtUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author ZhangHaiTao
 * @ClassName: ParkRecordFragment
 * Description: TODO
 * @date 2016/8/5 21:42
 */
public class ParkRecordFragment extends BaseFragment implements View.OnClickListener {
    private static final int REFRESH_CUR = 1;
    private static final int REFRESH_HIS = 2;
    @Bind(R.id.lv_park)
    ListView mLvPark;
    private View mRootView;
    private Activity mContext;

    private ParkRecordAdapter mAdapter;
    private List<ParkHistoryBean> mParkRecordList = new ArrayList<>();

    private TextView mTvPartName;
    private TextView mTvStartTime;
    private TextView mTvCurrentMoney;
    private Button mBtnLeave;
    private LinearLayout mLlCurrentUse;
    private TextView mTvUseTime;
    private TextView mTvHead;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_CUR://更新当前停车记录

                    StreamUtils.objectToFile(null, "CurrentParkBean", mContext);
                    mLlCurrentUse.setVisibility(View.GONE);
                    mTvNoCar.setVisibility(View.VISIBLE);

                    break;
                case REFRESH_HIS://更新历史停车记录
                    mAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };
    private TextView mTvNoCar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_park_record, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mRootView);
        initData();
        initView();
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initCurrentParkView();
        mTvHead.requestFocus();
        mLvPark.setFocusable(false);
    }

    private void initData() {
        //从本地读取历史记录
        mParkRecordList = (List<ParkHistoryBean>) StreamUtils.fileToObject("mParkRecordList", mContext);
        if (mParkRecordList == null) {
            mParkRecordList = new ArrayList<>();
        }

    }

    private void initView() {
        mTvNoCar = (TextView) mRootView.findViewById(R.id.tv_no_car);
        mTvHead = (TextView) mRootView.findViewById(R.id.tv_head);
        mTvPartName = (TextView) mRootView.findViewById(R.id.tv_part_name);
        mTvStartTime = (TextView) mRootView.findViewById(R.id.tv_start_time);
        mTvCurrentMoney = (TextView) mRootView.findViewById(R.id.tv_current_money);
        mBtnLeave = (Button) mRootView.findViewById(R.id.btn_leave);
        mBtnLeave.setOnClickListener(this);
        mLlCurrentUse = (LinearLayout) mRootView.findViewById(R.id.ll_current_use);
        mTvUseTime = (TextView) mRootView.findViewById(R.id.tv_use_time);

        mAdapter = new ParkRecordAdapter(mContext, 0, mParkRecordList);
        mLvPark.setAdapter(mAdapter);

        mLvPark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //                ZhtUtils.gotoIntent(mContext, GotoParkActivity.class);
            }
        });

    }

    /**
     * 初始化 当前停车情况
     */
    private void initCurrentParkView() {
        final CurrentParkBean curPark = (CurrentParkBean) StreamUtils.fileToObject("CurrentParkBean", mContext);

        if (curPark == null) {
            mLlCurrentUse.setVisibility(View.GONE);
            mTvNoCar.setVisibility(View.VISIBLE);
        } else {
            mLlCurrentUse.setVisibility(View.VISIBLE);
            mTvNoCar.setVisibility(View.GONE);

            mTvPartName.setText(curPark.getParkName() + "-" + curPark.getCarPort());
            mTvStartTime.setText("开始时间：" + FormatUtil.getDateTime(curPark.getStartTime()));

            long btTime = System.currentTimeMillis() - curPark.getStartTime();
            btTime = btTime / (1000 * 60);
            LogUtil.w("m:" + btTime);
            int hour = (int) (btTime / 60);
            int min = (int) (btTime % 60);
//            if (min == 0) min = 1;
            min++;
            final String durationTime = String.format("%d小时 %d分钟", hour, min);

            mTvUseTime.setText("已停时长：" + hour + "小时" + min + "分钟");
            final int cp = curPark.getPrice() * (hour + min > 0 ? 1 : 0);
            mTvCurrentMoney.setText("当前费用：" + cp + "￥");

            final byte[] send_cmd = {0x55, 0x51, 0x2d, 0x00, 0x01, 0x51};//打开车位锁

            //离开停车位
            mBtnLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isUse = SharedPreferencesUtil.getBoolean(MyApplication.getAppContext(),
                            MyConstant.isUseNow, false);
                    //还没离开停车位，不可用支付费用
                    if (isUse) {
                        //已经离开停车位，可以支付费用了
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("停车费支付提示");
                        builder.setMessage("请您先离开停车位，再支付本次停车费用");
                        builder.setPositiveButton("确定", null);
                        builder.setCancelable(true);
                        builder.show();
                        return;
                    }

                    //已经离开停车位，可以支付费用了
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("停车费支付提示");
                    builder.setMessage("您本次的停车费用为 " + cp + "元");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            UIUtils.showToast(mContext, "正在支付");
                            ZhtCustomProgressDialog.show(mContext, "正在支付", false, null);

                            send_cmd[4] = (byte) (Integer.parseInt(curPark.getCarPort().substring(3)));
                            SocketUtil.getInstance().sendData(send_cmd);

                            //保存到历史记录 到本地
                            ParkHistoryBean historyBean = new ParkHistoryBean();
                            historyBean.setParkName(curPark.getParkName());
                            historyBean.setCarPort(curPark.getCarPort());
                            historyBean.setStartTime(curPark.getStartTime());
                            historyBean.setEndTime(System.currentTimeMillis());
                            historyBean.setDurationTime(durationTime);
                            historyBean.setReal_price(cp);
                            historyBean.setUserId(MyConstant.USER_ID);

                            //保存到历史记录 到云端
                            saveHistoryToCloud(historyBean);

                            mParkRecordList.add(0, historyBean);
                            mAdapter.notifyDataSetChanged();
                            mHandler.sendEmptyMessage(REFRESH_HIS);//刷新listview

                            StreamUtils.objectToFile(mParkRecordList, "mParkRecordList", mContext);//保存到本地

                            //更新个人信息中的 余额和停车次数
                            MyConstant.CUR_MONEY -= cp;
                            MyConstant.PARK_TIMES++;

                            // 修改 content  保存到云端
                            AVObject todo = AVObject.createWithoutData(MyConstant.REGIST_USER,
                                    MyConstant.LEANCLOUD_SAVE_ID);
                            todo.put(MyConstant.REGIST_USER_KEY_MONEY, MyConstant.CUR_MONEY);
                            todo.put(MyConstant.REGIST_USER_KEY_TIMES, MyConstant.PARK_TIMES);
                            todo.saveInBackground();

                            //保存到本地
                            SharedPreferencesUtil.putLong(mContext,"CUR_MONEY", MyConstant.CUR_MONEY);
                            SharedPreferencesUtil.putLong(mContext,"PARK_TIMES", MyConstant.PARK_TIMES);


                            //清除本地记录 刷新界面  有个延时的效果
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mHandler.sendEmptyMessage(REFRESH_CUR);
                                    ZhtCustomProgressDialog.dismiss2();
                                }
                            },3000);

                            ZhtUtils.gotoIntent(mContext, BombPayActivity.class);
                        }
                    }).setNegativeButton("取消", null);

                    builder.setCancelable(false);
                    builder.show();
                }


            });
        }


    }

    /**
     * 将历史停车记录上传到云端
     * @param historyBean
     */
    private void saveHistoryToCloud(ParkHistoryBean historyBean) {
        AVObject hisCloud = new AVObject("HistoryParkBean");
        hisCloud.put("startIime", historyBean.getStartTime());
        hisCloud.put("endTime", historyBean.getEndTime());
        hisCloud.put("parkName", historyBean.getParkName());
        hisCloud.put("carPort", historyBean.getCarPort());
        hisCloud.put("duartionTime", historyBean.getDurationTime());
        hisCloud.put("realPrice", historyBean.getReal_price());
        hisCloud.put("userId", historyBean.getUserId());
        hisCloud.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    UIUtils.showToast(mContext, "上传成功");
                } else {
                    UIUtils.showToast(mContext, "上传失败");
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_leave:

                break;
        }
    }

    class ParkRecordAdapter extends ArrayAdapter<ParkHistoryBean> {

        private Context mContext;

        public ParkRecordAdapter(Context context, int resource, List<ParkHistoryBean> objects) {
            super(context, resource, objects);
            mContext = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_park_order_list, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ParkHistoryBean historyBean = getItem(position);
            viewHolder.mTvPartName.setText(historyBean.getParkName() + "-" + historyBean.getCarPort());
            viewHolder.mTvStartTime.setText("开始时间：" + FormatUtil.getDateTime(historyBean.getStartTime()));
            viewHolder.mTvEndTime.setText("结束时间：" + FormatUtil.getDateTime(historyBean.getEndTime()));
            viewHolder.mTvUseTime.setText("停车时长：" + historyBean.getDurationTime());
            viewHolder.mTvRealPay.setText("实付款：" + historyBean.getReal_price());

            viewHolder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("删除提示");
                    builder.setMessage("确定要删除本条停车记录吗？");

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mParkRecordList.remove(position);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.setCancelable(false);
                    builder.show();
                }
            });
            return convertView;
        }

        public class ViewHolder {
            public View rootView;
            public TextView mTvPartName;
            public TextView mTvStartTime;
            public TextView mTvEndTime;
            public TextView mTvUseTime;
            public TextView mTvRealPay;
            public Button mBtnComment;
            public Button mBtnDelete;

            public ViewHolder(View rootView) {
                this.rootView = rootView;
                this.mTvPartName = (TextView) rootView.findViewById(R.id.tv_part_name);
                this.mTvStartTime = (TextView) rootView.findViewById(R.id.tv_start_time);
                this.mTvEndTime = (TextView) rootView.findViewById(R.id.tv_end_time);
                this.mTvUseTime = (TextView) rootView.findViewById(R.id.tv_use_time);
                this.mTvRealPay = (TextView) rootView.findViewById(R.id.tv_real_pay);
                this.mBtnComment = (Button) rootView.findViewById(R.id.btn_comment);
                this.mBtnDelete = (Button) rootView.findViewById(R.id.btn_delete);
            }

        }
    }

    /**
     * 得到2个时间段的间隔时间 不足一分钟按照一分钟计算
     *
     * @param start
     * @param end
     * @return
     */
    private String getDurationTime(long start, long end) {
        long btTime = end - start;
        btTime = btTime / (1000 * 60);
        int hour = (int) (btTime / 60);
        int min = (int) (btTime % 60);
        if (min == 0) min = 1;
        return String.format("%d小时 %d分钟", hour, min);
    }
}
