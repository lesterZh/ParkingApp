package com.zhhtao.bluedev.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.activity.GotoParkActivity;
import com.zhhtao.bluedev.bean.ParkInfoBean;
import com.zhhtao.bluedev.utils.UIUtils;
import com.zhhtao.bluedev.utils.ZhtUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author ZhangHaiTao
 * @ClassName: ParkListFragment
 * Description: TODO
 * @date 2016/8/5 20:27
 */
public class ParkListFragment extends Fragment {

    @Bind(R.id.lv_park)
    ListView mLvPark;

    private ImageView mIvMap;
    private View mRootView;
    private Activity mContext;

    private ParkListAdapter mAdapter;
    private List<ParkInfoBean> mParkInfoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_park_list, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mRootView);
        initData();
        initView();
        return mRootView;
    }

    private void initData() {
//        for (int i = 1; i < 11; i++) {
//            ParkInfoBean bean = new ParkInfoBean();
//            bean.setName("交大第" + i + "停车场");
//
//            mParkInfoList.add(bean);
//        }
        ParkInfoBean bean = new ParkInfoBean();
        bean.setName("西南交大" + "停车场");
        bean.setAddress("地址：成都安靖镇西南交通大学犀浦校区");
        bean.setContact("028-8768934");
        bean.setPrice("每小时15元");
        bean.setTotal(100);
        bean.setCanUse(86);
        mParkInfoList.add(bean);

        ParkInfoBean bean2 = new ParkInfoBean();
        bean2.setName("交大体育馆" + "停车场");
        bean2.setAddress("地址：成都犀浦西南交通大学体育馆");
        bean2.setContact("028-8768935");
        bean2.setPrice("每小时10元");
        bean2.setTotal(80);
        bean2.setCanUse(56);
        mParkInfoList.add(bean2);

        ParkInfoBean bean3 = new ParkInfoBean();
        bean3.setName("锦园三期停车场");
        bean3.setAddress("地址：四川省成都市郫县校园路381号");
        bean3.setContact("028-8768378");
        bean3.setPrice("每小时18元");
        bean3.setTotal(150);
        bean3.setCanUse(76);
        mParkInfoList.add(bean3);

        ParkInfoBean bean4 = new ParkInfoBean();
        bean4.setName("西郡兰庭-停车场");
        bean4.setAddress("地址：四川省成都市郫县锦宁巷11");
        bean4.setContact("028-8762451");
        bean4.setPrice("每小时10元");
        bean4.setTotal(200);
        bean4.setCanUse(47);
        mParkInfoList.add(bean4);

        ParkInfoBean bean5 = new ParkInfoBean();
        bean5.setName("浦园酒店-停车场");
        bean5.setAddress("地址：四川省成都市郫县林湾村");
        bean5.setContact("028-8769783");
        bean5.setPrice("每小时14元");
        bean5.setTotal(50);
        bean5.setCanUse(32);
        mParkInfoList.add(bean5);
    }

    private void initView() {
        mAdapter = new ParkListAdapter(mContext, 0, mParkInfoList);
        mLvPark.setAdapter(mAdapter);

        mLvPark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //                ZhtUtils.gotoIntent(mContext, GotoParkActivity.class);
                GotoParkActivity.start(mContext, mAdapter.getItem(position));
            }
        });

        mIvMap = (ImageView) mRootView.findViewById(R.id.iv_map);
        mIvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.showToast(mContext, "进入地图查看模式");
                ZhtUtils.gotoIntent(mContext, com.zhhtao.bluedev.activity.MapActivity.class);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class ParkListAdapter extends ArrayAdapter<ParkInfoBean> {

        private Context mContext;

        public ParkListAdapter(Context context, int resource, List<ParkInfoBean> objects) {
            super(context, resource, objects);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_park_list, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ParkInfoBean bean = getItem(position);
            viewHolder.mTvParkName.setText(bean.getName());
            viewHolder.mTvAdress.setText(bean.getAddress());
            viewHolder.mTvContact.setText(bean.getContact());
            viewHolder.mTvPrice.setText(bean.getPrice());
            viewHolder.mTvUseState.setText("总共:"+bean.getTotal()+" 可用:"+bean.getCanUse());
            return convertView;

        }

        public class ViewHolder {
            public View rootView;
            public TextView mTvParkName;
            public TextView mTvUseState;
            public TextView mTvAdress;
            public TextView mTvContact;
            public TextView mTvPrice;

            public ViewHolder(View rootView) {
                this.rootView = rootView;
                this.mTvParkName = (TextView) rootView.findViewById(R.id.tv_park_name);
                this.mTvUseState = (TextView) rootView.findViewById(R.id.tv_use_state);
                this.mTvAdress = (TextView) rootView.findViewById(R.id.tv_adress);
                this.mTvContact = (TextView) rootView.findViewById(R.id.tv_contact);
                this.mTvPrice = (TextView) rootView.findViewById(R.id.tv_price);
            }

        }
    }
}
