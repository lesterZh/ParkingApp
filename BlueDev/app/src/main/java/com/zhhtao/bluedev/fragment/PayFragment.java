package com.zhhtao.bluedev.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.activity.PayResultActivity;
import com.zhhtao.bluedev.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ZhangHaiTao on 2017/1/10.
 */

public class PayFragment extends BaseFragment {


    @Bind(R.id.et_input_money)
    EditText etInputMoney;
    @Bind(R.id.iv_ali_selected)
    ImageView ivAiliSelected;
    @Bind(R.id.ll_ali_pay)
    LinearLayout llAiliPay;
    @Bind(R.id.iv_weixin_selected)
    ImageView ivWeixinSelected;
    @Bind(R.id.ll_weixin_pay)
    LinearLayout llWeixinPay;
    @Bind(R.id.btn_confirm_pay)
    Button btnConfirmPay;

    private boolean isAliPayMethod = true;

    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pay, container, false);

        ButterKnife.bind(this, rootView);

        etInputMoney.setHint("10");

        return rootView;
    }

    @OnClick({R.id.ll_ali_pay, R.id.ll_weixin_pay, R.id.btn_confirm_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_ali_pay://选择支付宝支付
                isAliPayMethod = true;
                changePayMethodSelectedState(isAliPayMethod);
                break;

            case R.id.ll_weixin_pay://选择微信支付
                isAliPayMethod = false;
                changePayMethodSelectedState(isAliPayMethod);

                break;
            case R.id.btn_confirm_pay://确认支付
                PayResultActivity.actionStart(mContext, true, "充值结果", "充值成功");

                break;
        }
    }

    /**
     * 设置支付方式
     * @param isAliPay
     */
    private void changePayMethodSelectedState(boolean isAliPay) {
        if (isAliPay) {
            ivAiliSelected.setVisibility(View.VISIBLE);
            ivWeixinSelected.setVisibility(View.INVISIBLE);
        } else {
            ivAiliSelected.setVisibility(View.INVISIBLE);
            ivWeixinSelected.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
