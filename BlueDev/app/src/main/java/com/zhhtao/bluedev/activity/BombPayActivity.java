package com.zhhtao.bluedev.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;

public class BombPayActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvFee;
    private LinearLayout mLlAliPay;
    private LinearLayout mLlWeixinPay;
    private LinearLayout mLlYuerPay;
    private Button mBtnConfirmPay;
    private Button mBtnBack;
    private ImageView mIvAliSelected;
    private ImageView mIvYuerSelected;
    private ImageView mIvWeixinSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bomb_pay);

        // 初始化BmobPay对象,可以在支付时再初始化
        BP.init("229bbc809f2a50ab933e6d975a36b68f");
        initView();
    }

    private void initView() {
        mTvFee = (TextView) findViewById(R.id.tv_fee);
        mLlAliPay = (LinearLayout) findViewById(R.id.ll_ali_pay);
        mLlWeixinPay = (LinearLayout) findViewById(R.id.ll_weixin_pay);
        mLlYuerPay = (LinearLayout) findViewById(R.id.ll_yuer_pay);
        mBtnConfirmPay = (Button) findViewById(R.id.btn_confirm_pay);
        mBtnBack = (Button) findViewById(R.id.btn_back);

        mLlAliPay.setOnClickListener(this);
        mLlWeixinPay.setOnClickListener(this);
        mLlYuerPay.setOnClickListener(this);
        mBtnConfirmPay.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mIvAliSelected = (ImageView) findViewById(R.id.iv_ali_selected);
        mIvAliSelected.setOnClickListener(this);
        mIvYuerSelected = (ImageView) findViewById(R.id.iv_yuer_selected);
        mIvYuerSelected.setOnClickListener(this);
        mIvWeixinSelected = (ImageView) findViewById(R.id.iv_weixin_selected);
        mIvWeixinSelected.setOnClickListener(this);

        tv = new TextView(mContext);
    }

    private int mode = 1; //默认支付宝
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_ali_pay:
                mode = 1;
                changePayMethodSelectedState(mode);
                break;
            case R.id.ll_weixin_pay:
                mode = 2;
                changePayMethodSelectedState(mode);
                break;
            case R.id.ll_yuer_pay:
                mode = 3;
                changePayMethodSelectedState(mode);
                break;

            case R.id.btn_confirm_pay:
                if (mode == 1) {
                    pay(true);//支付宝
                } else if (mode == 2) {
                    pay(false);//微信
                } else if (mode == 3) {

                }

                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    /**
     * 设置支付方式
     *mode:  1:支付宝  2：微信  3：账户余额
     * @param 
     */
    private void changePayMethodSelectedState(int mode) {
        if (mode == 1) {
            mIvAliSelected.setVisibility(View.VISIBLE);
            mIvWeixinSelected.setVisibility(View.INVISIBLE);
            mIvYuerSelected.setVisibility(View.INVISIBLE);
        } else if (mode == 2) {
            mIvWeixinSelected.setVisibility(View.VISIBLE);
            mIvAliSelected.setVisibility(View.INVISIBLE);
            mIvYuerSelected.setVisibility(View.INVISIBLE);
        } else if (mode == 3){
            mIvYuerSelected.setVisibility(View.VISIBLE);
            mIvAliSelected.setVisibility(View.INVISIBLE);
            mIvWeixinSelected.setVisibility(View.INVISIBLE);
        }
    }



    TextView tv;
    // 此为微信支付插件的官方最新版本号,请在更新时留意更新说明
    int PLUGINVERSION = 7;
    ProgressDialog dialog;

    /**
     * 检查某包名应用是否已经安装
     *
     * @param packageName 包名
     * @param browserUrl  如果没有应用市场，去官网下载
     * @return
     */
    private boolean checkPackageInstalled(String packageName, String browserUrl) {
        try {
            // 检查是否有支付宝客户端
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有安装支付宝，跳转到应用市场
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                startActivity(intent);
            } catch (Exception ee) {// 连应用市场都没有，用浏览器去支付宝官网下载
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(browserUrl));
                    startActivity(intent);
                } catch (Exception eee) {
                    Toast.makeText(mContext,
                            "您的手机上没有没有应用市场也没有浏览器，我也是醉了，你去想办法安装支付宝/微信吧",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }
    private static final int REQUESTPERMISSION = 101;

    private void installApk(String s) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
        } else {
            installBmobPayPlugin(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTPERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    installBmobPayPlugin("bp.db");
                } else {
                    //提示没有权限，安装不了
                    Toast.makeText(mContext,"您拒绝了权限，这样无法安装支付插件",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    /**
     * 调用支付
     *
     * @param alipayOrWechatPay 支付类型，true为支付宝支付,false为微信支付
     */
    void pay(final boolean alipayOrWechatPay) {
        if (alipayOrWechatPay) {
            if (!checkPackageInstalled("com.eg.android.AlipayGphone",
                    "https://www.alipay.com")) { // 支付宝支付要求用户已经安装支付宝客户端
                Toast.makeText(mContext, "请安装支付宝客户端", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        } else {
            if (checkPackageInstalled("com.tencent.mm", "http://weixin.qq.com")) {// 需要用微信支付时，要安装微信客户端，然后需要插件
                // 有微信客户端，看看有无微信支付插件
                int pluginVersion = BP.getPluginVersion(this);
                if (pluginVersion < PLUGINVERSION) {// 为0说明未安装支付插件,
                    // 否则就是支付插件的版本低于官方最新版
                    Toast.makeText(
                            mContext,
                            pluginVersion == 0 ? "监测到本机尚未安装支付插件,无法进行支付,请先安装插件(无流量消耗)"
                                    : "监测到本机的支付插件不是最新版,最好进行更新,请先更新插件(无流量消耗)",
                            Toast.LENGTH_SHORT).show();
                    //                    installBmobPayPlugin("bp.db");

                    installApk("bp.db");
                    return;
                }
            } else {// 没有安装微信
                Toast.makeText(mContext, "请安装微信客户端", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        showDialog("正在获取订单...\nSDK版本号:" + BP.getPaySdkVersion());
        final String name = getName();

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.bmob.app.sport",
                    "com.bmob.app.sport.wxapi.BmobActivity");
            intent.setComponent(cn);
            this.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        BP.pay(name, getBody(), getPrice(), alipayOrWechatPay, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(mContext, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                tv.append(name + "'s pay status is unknow\n\n");
                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(mContext, "支付成功!", Toast.LENGTH_SHORT).show();
                tv.append(name + "'s pay status is success\n\n");
                hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
//                order.setText(orderId);
                tv.append(name + "'s orderid is " + orderId + "\n\n");
                showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            mContext,
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_SHORT).show();
                    //                    installBmobPayPlugin("bp.db");
                    installApk("bp.db");
                } else {
                    Toast.makeText(mContext, "支付中断!", Toast.LENGTH_SHORT)
                            .show();
                }
                tv.append(name + "'s pay status is fail, error code is \n"
                        + code + " ,reason is " + reason + "\n\n");
                hideDialog();
            }
        });
    }

    // 执行订单查询
    void query() {
        showDialog("正在查询订单...");
        final String orderId = getOrder();

        BP.query(orderId, new QListener() {

            @Override
            public void succeed(String status) {
                Toast.makeText(mContext, "查询成功!该订单状态为 : " + status,
                        Toast.LENGTH_SHORT).show();
                tv.append("pay status of" + orderId + " is " + status + "\n\n");
                hideDialog();
            }

            @Override
            public void fail(int code, String reason) {
                Toast.makeText(mContext, "查询失败", Toast.LENGTH_SHORT).show();
                tv.append("query order fail, error code is " + code
                        + " ,reason is \n" + reason + "\n\n");
                hideDialog();
            }
        });
    }


    // 默认为0.02
    double getPrice() {
        double price = 0.02;

        return price;
    }

    // 商品详情(可不填)
    String getName() {
        //        return this.name.getText().toString();
        return "停车费用结算";
    }

    // 商品详情(可不填)
    String getBody() {
        //        return this.body.getText().toString();
        return "本次停车费用";
    }

    // 支付订单号(查询时必填)
    String getOrder() {
//        return this.order.getText().toString();
        return "";
    }

    void showDialog(String message) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(this);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }

    /**
     * 安装assets里的apk文件
     *
     * @param fileName
     */
    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
