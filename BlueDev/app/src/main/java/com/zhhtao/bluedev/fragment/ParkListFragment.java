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
import android.widget.ListView;
import android.widget.TextView;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.activity.GotoParkActivity;
import com.zhhtao.bluedev.bean.ParkInfoBean;

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
        for (int i = 1; i < 11; i++) {
            ParkInfoBean bean = new ParkInfoBean();
            bean.setName("交大第" + i + "停车场");

            mParkInfoList.add(bean);
        }
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
