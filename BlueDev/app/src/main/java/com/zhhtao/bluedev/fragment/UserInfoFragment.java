package com.zhhtao.bluedev.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.activity.LoginActivity;
import com.zhhtao.bluedev.base.BaseFragment;
import com.zhhtao.bluedev.base.MyConstant;
import com.zhhtao.bluedev.utils.ZhtUtils;

/**
 * Created by ZhangHaiTao on 2017/1/10.
 */

public class UserInfoFragment extends BaseFragment implements View.OnClickListener {


    View rootView;
    private TextView mTvUserId;
    private TextView mTvCurrentMoney;
    private TextView mTvParkTimes;
    private LinearLayout mLlChangePassword;
    private LinearLayout mLlChangeBusId;//修改车牌号
    private Button mBtnExit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_info, container, false);

        initView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView(View rootView) {
        mTvUserId = (TextView) rootView.findViewById(R.id.tv_user_id);
        mTvCurrentMoney = (TextView) rootView.findViewById(R.id.tv_current_money);
        mTvParkTimes = (TextView) rootView.findViewById(R.id.tv_park_times);
        mLlChangePassword = (LinearLayout) rootView.findViewById(R.id.ll_change_password);
        mLlChangeBusId = (LinearLayout) rootView.findViewById(R.id.ll_change_bus_id);
        mBtnExit = (Button) rootView.findViewById(R.id.btn_exit);

        mBtnExit.setOnClickListener(this);
        mLlChangePassword.setOnClickListener(this);
        mLlChangeBusId.setOnClickListener(this);
    }

    private void setView() {
        mTvUserId.setText(MyConstant.USER_ID);
        mTvCurrentMoney.setText(MyConstant.CUR_MONEY + "");
        mTvParkTimes.setText(MyConstant.PARK_TIMES + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("您确定要退出账号");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyConstant.changeUser = true;
                        ZhtUtils.gotoIntent(mContext, LoginActivity.class);
                        mContext.finish();
                    }
                }).setNegativeButton("取消", null);

                builder.setCancelable(false);
                builder.show();
                break;

            case R.id.ll_change_password:

                final AlertDialog.Builder cp = new AlertDialog.Builder(mContext);
                View rootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_change_password, null, false);
                ViewHolder viewHolder = new ViewHolder(rootView);


                cp.setView(rootView);
                cp.setCancelable(true);
                final Dialog dialog = cp.show();

                viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                viewHolder.mBtnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;

            case R.id.ll_change_bus_id:

                final AlertDialog.Builder cp2 = new AlertDialog.Builder(mContext);
                View rootView2 = LayoutInflater.from(mContext).inflate(R.layout.dialog_change_bus_id, null, false);
                ViewHolder viewHolder2 = new ViewHolder(rootView2);


                cp2.setView(rootView2);
                cp2.setCancelable(true);
                final Dialog dialog2 = cp2.show();

                viewHolder2.mBtnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });

                viewHolder2.mBtnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                break;

        }
    }


    public static class ViewHolder {
        public View rootView;
        public EditText mEtOldPwd;
        public EditText mEtNewPwd;
        public EditText mEtNewPwd2;
        public Button mBtnOk;
        public Button mBtnCancle;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mEtOldPwd = (EditText) rootView.findViewById(R.id.et_old_pwd);
            this.mEtNewPwd = (EditText) rootView.findViewById(R.id.et_new_pwd);
            this.mEtNewPwd2 = (EditText) rootView.findViewById(R.id.et_new_pwd2);
            this.mBtnOk = (Button) rootView.findViewById(R.id.btn_ok);
            this.mBtnCancle = (Button) rootView.findViewById(R.id.btn_cancle);
        }

    }
}
