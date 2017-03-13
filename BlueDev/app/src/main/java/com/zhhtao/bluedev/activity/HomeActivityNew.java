package com.zhhtao.bluedev.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.base.BaseActivity;
import com.zhhtao.bluedev.fragment.ParkListFragment;
import com.zhhtao.bluedev.fragment.ParkRecordFragment;
import com.zhhtao.bluedev.fragment.PayFragment;
import com.zhhtao.bluedev.fragment.UserInfoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhangHaiTao on 2016/5/12.
 * 登录进入的主界面
 */
public class HomeActivityNew extends BaseActivity {

    @Bind(R.id.iv_index_clinic)
    ImageView ivIndexClinic;
    @Bind(R.id.tv_index_clinic)
    TextView tvIndexClinic;
    @Bind(R.id.ll_index_clinic)
    LinearLayout llIndexClinic;
    @Bind(R.id.iv_index_health_manage)
    ImageView ivIndexHealthManage;
    @Bind(R.id.tv_index_health_manage)
    TextView tvIndexHealthManage;
    @Bind(R.id.ll_index_health_manage)
    LinearLayout llIndexHealthManage;
    @Bind(R.id.iv_index_message)
    ImageView ivIndexMessage;
    @Bind(R.id.tv_index_message)
    TextView tvIndexMessage;
    @Bind(R.id.ll_index_message)
    LinearLayout llIndexMessage;
    @Bind(R.id.view_center)
    View viewCenter;
    @Bind(R.id.tv_index_message_notify)
    TextView tvIndexMessageNotify;
    @Bind(R.id.iv_index_mygaia)
    ImageView ivIndexMygaia;
    @Bind(R.id.tv_index_mygaia)
    TextView tvIndexMygaia;
    @Bind(R.id.ll_index_mygaia)
    LinearLayout llIndexMygaia;
    @Bind(R.id.fragment_container)
    FrameLayout fragmentContainer;


    boolean selectGy = false;
    MyIndexListener myIndexListener;
    TextView[] indexTextViews;
    ImageView[] indexImageViews;


//    private Activity mContext;

    private ParkListFragment parkListFragment;
    private PayFragment payFragment;
    private ParkRecordFragment parkRecordFragment;
    private UserInfoFragment userInfoFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        indexTextViews = new TextView[]{tvIndexClinic, tvIndexHealthManage, tvIndexMessage, tvIndexMygaia};
        indexImageViews = new ImageView[]{ivIndexClinic, ivIndexHealthManage, ivIndexMessage, ivIndexMygaia};

        myIndexListener = new MyIndexListener();
        llIndexClinic.setOnClickListener(myIndexListener);
        llIndexHealthManage.setOnClickListener(myIndexListener);
        llIndexMessage.setOnClickListener(myIndexListener);
        llIndexMygaia.setOnClickListener(myIndexListener);

        fragmentManager = getFragmentManager();

        selectIndexImageAndText(0);
        selectIndexFragment(0);
    }

    class MyIndexListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_index_clinic:
                    selectIndexImageAndText(0);
                    selectIndexFragment(0);
                    break;

                case R.id.ll_index_health_manage:
                    selectIndexImageAndText(1);
                    selectIndexFragment(1);
                    break;

                case R.id.ll_index_message:
                    selectIndexImageAndText(2);
                    selectIndexFragment(2);
                    break;

                case R.id.ll_index_mygaia:
                    selectIndexImageAndText(3);
                    selectIndexFragment(3);
                    break;

            }
        }
    }

    /**
     * 设置下面指示tab的图片和文字的显示状态
     *
     * @param index
     */
    private void selectIndexImageAndText(int index) {
        for (int i = 0; i < 4; i++) {
            if (i == index) {
                indexTextViews[i].setSelected(true);
                indexImageViews[i].setSelected(true);
                continue;
            }
            indexTextViews[i].setSelected(false);
            indexImageViews[i].setSelected(false);
        }
    }

    /**
     * 设置选中的Fragment
     *
     * @param index
     */
    private void selectIndexFragment(int index) {
        Fragment selectedFragment = null;
        switch (index) {
            case 0:
                if (parkListFragment == null) {
                    parkListFragment = new ParkListFragment();
                    addFragment(parkListFragment);
                }
                selectedFragment = parkListFragment;
                break;

            case 1:
                if (payFragment == null) {
                    payFragment = new PayFragment();
                    addFragment(payFragment);
                }
                selectedFragment = payFragment;
                break;
            case 2:
                if (parkRecordFragment == null) {
                    parkRecordFragment = new ParkRecordFragment();
                    addFragment(parkRecordFragment);
                }
                selectedFragment = parkRecordFragment;
                break;
            case 3:
//                if (userInfoFragment == null) {
                    userInfoFragment = new UserInfoFragment();
                    addFragment(userInfoFragment);
//                }
                selectedFragment = userInfoFragment;
                break;
        }
        showSelectedFragment(selectedFragment);
    }

    /**
     * 添加fragment
     *
     * @param fragment
     */
    private void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * 显示指定的fragment
     *
     * @param showTarget
     */
    private void showSelectedFragment(Fragment showTarget) {
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment tempFragment;
        for (int i = 0; i < fragmentList.size(); i++) {
            tempFragment = fragmentList.get(i);
            if (tempFragment != null) {
                if (tempFragment == showTarget) {
                    fragmentTransaction.show(tempFragment);
                    continue;
                } else {
                    fragmentTransaction.hide(tempFragment);
                }
            }
        }

        fragmentTransaction.commit();
    }


    private static final int REQUEST_ENABLE_BT = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
