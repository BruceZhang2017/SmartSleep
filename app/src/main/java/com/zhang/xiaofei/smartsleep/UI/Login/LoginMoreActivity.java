package com.zhang.xiaofei.smartsleep.UI.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.zhang.xiaofei.smartsleep.Kit.AndroidWorkaround;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.ImagePicker.PicassoImageLoader;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;
import com.zhang.xiaofei.smartsleep.Model.CardBean;
import com.zhang.xiaofei.smartsleep.Model.Feedback.FeedbackItemModel;
import com.zhang.xiaofei.smartsleep.Model.Feedback.FeedbackModel;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Login.UserRefreshModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Home.HomeActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.FeedbackActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.FeedbackListActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginMoreActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "LoginMoreActivity";
    @BindView(R.id.et_nickname) EditText etNickName;
    @BindView(R.id.btn_sex) Button btnSex;
    @BindView(R.id.btn_birthday) Button btnBirthday;
    @BindView(R.id.btn_height) Button btnHeight;
    @BindView(R.id.btn_weight) Button btnWeight;
    @BindView(R.id.btn_next) Button btnNext;
    @BindView(R.id.tv_r) TextView btnSkip;
    @BindView(R.id.iv_head) ImageView ivHead;
    @BindView(R.id.tv_cardId) TextView tvID;
    @BindView(R.id.tv_nickname)TextView tvNickNmae;
    @BindView(R.id.tv_sex)TextView tvSex;
    @BindView(R.id.tv_birthday)TextView tvBirthday;
    @BindView(R.id.tv_height)TextView tvHeight;
    @BindView(R.id.tv_weight) TextView tvWeight;
    private OptionsPickerView pvOptions;
    private ArrayList<CardBean> options1Items = new ArrayList<>();
    private OptionsPickerView HeightOptions;
    private ArrayList<CardBean> optionsHeight1Items = new ArrayList<>();
    private OptionsPickerView weightOptions;
    private ArrayList<CardBean> optionsWeight1Items = new ArrayList<>();
    private TimePickerView pvTime;
    private ImageButton ibLeft;
    private static final int IMAGE_PICKER = 100;
    private ImagePicker imagePicker;
    String uerHeadImageUrl; // 头像图片链接
    KProgressHUD hud;
    String nickname;
    int iSex = 0;
    String birth;
    String height;
    String weight;
    int value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_more);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        value = intent.getIntExtra("value", 0);
        btnSex.setOnClickListener(this);
        btnBirthday.setOnClickListener(this);
        btnHeight.setOnClickListener(this);
        btnWeight.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSkip.setVisibility(View.VISIBLE);
        btnSkip.setTextColor(getResources().getColor(R.color.navigationBarRightColor));
        btnSkip.setTextSize(14);
        btnSkip.setText(value > 0 ? getResources().getString(R.string.common_save) : getResources().getString(R.string.login_skip));
        btnSkip.setOnClickListener(this);
        ivHead.setOnClickListener(this);

        etNickName.setFocusable(true);
        etNickName.setFocusableInTouchMode(true);

        initializeForSex();
        initOptionPicker();
        initTimePicker();
        initializeForHeight();
        initOptionHeightPicker();
        initializeForWeight();
        initOptionWeightPicker();

        if (value > 0) {
            ibLeft = (ImageButton)findViewById(R.id.im_l);
            ibLeft.setImageResource(R.mipmap.suggest_icon_back);
            ibLeft.setVisibility(View.VISIBLE);
            ibLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            btnNext.setVisibility(View.INVISIBLE);
        }

        initialImagePicker();

        YMUserInfoManager userManager = new YMUserInfoManager( this);
        UserModel userModel = userManager.loadUserInfo();
        String photo = userModel.getUserInfo().getPhoto();
        if (photo != null && photo.startsWith("http")) {
            photo = photo.replace("http://","https://");
            Glide.with(this).load(photo).placeholder(R.mipmap.login_icon_head).into(ivHead);
        }
        String nickname = userModel.getUserInfo().getNikeName();
        if (nickname != null && nickname.length() > 0) {
            etNickName.setText(nickname);
        }
        int sex = userModel.getUserInfo().getSex();
        if (sex >= 0 && sex <= 1) {
            btnSex.setText(options1Items.get(sex).getCardNo());
        }
        String birthday = userModel.getUserInfo().getBirthday();
        if (birthday != null && birthday.length() >= 8) {
            btnBirthday.setText(birthday.substring(0,4) + "-" + birthday.substring(4,6) + "-" + birthday.substring(6, 8));
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int year = Integer.parseInt(birthday.substring(0,4));
            int month = Integer.parseInt(birthday.substring(4,6));
            int day = Integer.parseInt(birthday.substring(6, 8));
            calendar.set(year, month - 1, day);
            pvTime.setDate(calendar);
        }
        int height = userModel.getUserInfo().getHeight();
        if (height > 0) {
            btnHeight.setText(height + " cm");
            HeightOptions.setSelectOptions(height - 30);
        } else {
            HeightOptions.setSelectOptions(160 - 30);
        }
        int weight = userModel.getUserInfo().getWeight();
        if (weight > 0) {
            btnWeight.setText(weight + " kg");
            weightOptions.setSelectOptions(weight);
        } else {
            weightOptions.setSelectOptions(50);
        }
        int iID = userModel.getUserInfo().getUserId();
        if (iID > 0) {
            tvID.setText("ID " + iID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String language = SpUtil.getInstance(YMApplication.getContext()).getString(SpUtil.LANGUAGE);
        if (language.equals("en") || language.contains("en")) {
            tvNickNmae.setText("Nickname");
            tvSex.setText("Sex");
            tvBirthday.setText("Birth");
            tvHeight.setText("Height");
            tvWeight.setText("Weight");
            btnNext.setText("Next");
            btnSkip.setText(value > 0 ? "Save" : "Skip");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_r:
                if (value > 0) {
                    refreshUserInfo();
                } else {
                    Intent intentB = new Intent(LoginMoreActivity.this, HomeActivity.class);
                    intentB.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentB);
                }
                break;
            case R.id.btn_next:
                refreshUserInfo();
                break;
            case R.id.btn_sex:
                etNickName.clearFocus();
                btnSex.requestFocus();
                hideKeyboard();
                pvOptions.show();
                break;
            case R.id.btn_birthday:
                etNickName.clearFocus();
                btnBirthday.requestFocus();
                hideKeyboard();
                pvTime.show(view);
                break;
            case R.id.btn_height:
                etNickName.clearFocus();
                btnHeight.requestFocus();
                hideKeyboard();
                HeightOptions.show();
                break;
            case R.id.btn_weight:
                etNickName.clearFocus();
                btnWeight.requestFocus();
                hideKeyboard();
                weightOptions.show();
                break;
            case R.id.iv_head:
                Intent intentC = new Intent(this, ImageGridActivity.class);
                intentC.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                startActivityForResult(intentC, IMAGE_PICKER);
                break;
                default:
                    Log.d(TAG, "没有该按钮");
                    break;

        }
    }

    private void initOptionPicker() {//条件选择器初始化

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText();
                btnSex.setText(tx);
            }
        })
                .setTitleText(getResources().getString(R.string.login_select_sex))
                .setContentTextSize(20)//设置滚轮文字大小
                .setSelectOptions(0)//默认选中项
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {

                    }
                })
                .build();

        pvOptions.setPicker(options1Items);//一级选择器*/
    }

    private void initTimePicker() {//Dialog 模式下，在底部弹出
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Log.i("pvTime", "onTimeSelect");
                btnBirthday.setText(getTime(date));
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isDialog(false) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabel(
                        getResources().getString(R.string.pickerview_year),
                        getResources().getString(R.string.pickerview_month),
                        getResources().getString(R.string.pickerview_day),
                        "",
                        "",
                        "")
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                })
                //.setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setLineSpacingMultiplier(2.0f)
                .setContentTextSize(20)//设置滚轮文字大小
                .isAlphaGradient(true)
                .build();

        Dialog mDialog = pvTime.getDialog();

        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }

    private void initOptionHeightPicker() {//条件选择器初始化

        HeightOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = optionsHeight1Items.get(options1).getPickerViewText();
                btnHeight.setText(tx + " cm");
            }
        })
                .setTitleText(getResources().getString(R.string.login_height))
                .setContentTextSize(20)//设置滚轮文字大小
                .setSelectOptions(0)//默认选中项
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isDialog(false) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("cm  ", "","")
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {

                    }
                })
                .build();

        HeightOptions.setPicker(optionsHeight1Items);//一级选择器*/
    }

    private void initOptionWeightPicker() {//条件选择器初始化

        weightOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = optionsWeight1Items.get(options1).getPickerViewText();
                btnWeight.setText(tx + " kg");
            }
        })
                .setTitleText(getResources().getString(R.string.login_weight))
                .setContentTextSize(20)//设置滚轮文字大小
                .setSelectOptions(0)//默认选中项
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isDialog(false) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("kg  ", "","")
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {

                    }
                })
                .build();

        weightOptions.setPicker(optionsWeight1Items);//一级选择器*/
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void initializeForSex() {
        options1Items.add(new CardBean(0, getResources().getString(R.string.common_male)));
        options1Items.add(new CardBean(1, getResources().getString(R.string.common_female)));
    }

    private void initializeForHeight() {
        for (int i = 30; i <= 250; i++) {
            optionsHeight1Items.add(new CardBean(i, "" + i));
        }
    }

    private void initializeForWeight() {
        for (int i = 0; i < 150; i++) {
            optionsWeight1Items.add(new CardBean(i, "" + i));
        }
    }

    // 处理点击下一步
    private void handleNext() {

    }

    ArrayList<ImageItem> images = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) {
                    uerHeadImageUrl = images.get(0).path;
                    imagePicker.getImageLoader().displayImage(LoginMoreActivity.this, uerHeadImageUrl, ivHead, 100, 100);
                }
            } else {
                Log.d(TAG, "没有数据");
            }
        }
    }

    private void initialImagePicker() {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setMultiMode(false);
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(5);    //选中数量限制

        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(800);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(800);//保存文件的高度。单位像素
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etNickName.getWindowToken(), 0);
        }
    }

    // 完善用户信息
    private void refreshUserInfo() {
        nickname = etNickName.getText().toString();
        if (nickname == null || nickname.trim().length() <= 0) {
            Toast.makeText(this,R.string.login_input_nickname,Toast.LENGTH_SHORT).show();
            return;
        }
        String sex = btnSex.getText().toString();
        if (sex == null) {
            Toast.makeText(this,R.string.login_select_sex,Toast.LENGTH_SHORT).show();
            return;
        }
        if (sex != options1Items.get(0).getCardNo()) {
            iSex = 1;
        }
        birth = btnBirthday.getText().toString();
        if (birth == null || birth.trim().length() <= 0) {
            Toast.makeText(this,R.string.middle_date_of_birth,Toast.LENGTH_SHORT).show();
            return;
        }
        birth = birth.replace("-","");
        height = btnHeight.getText().toString();
        if (height == null || height.trim().length() <= 0) {
            Toast.makeText(this,R.string.login_height,Toast.LENGTH_SHORT).show();
            return;
        }
        height = height.replace(" cm", "");
        weight = btnWeight.getText().toString();
        if (weight == null || weight.trim().length() <= 0) {
            Toast.makeText(this,R.string.login_weight,Toast.LENGTH_SHORT).show();
            return;
        }
        weight = weight.replace(" kg","");
        showHUD();
        YMUserInfoManager userManager = new YMUserInfoManager( LoginMoreActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "multipart/form-data");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        System.out.println("token:" + userModel.getToken());
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("nikeName",nickname));
        postParam.add(new NameValuePair("sex",iSex + ""));
        postParam.add(new NameValuePair("userId",userModel.getUserInfo().getUserId() + ""));
        postParam.add(new NameValuePair("height",height));
        postParam.add(new NameValuePair("birthday",birth));
        postParam.add(new NameValuePair("weight",weight));
        if (uerHeadImageUrl != null && uerHeadImageUrl.length() > 0) {
            postParam.add(new NameValuePair("uploadFile", uerHeadImageUrl,true));
        }
        HTTPCaller.getInstance().postFile(
                UserRefreshModel.class,
                YMApplication.getInstance().domain() + "app/user/updateUserInfo",
                new com.ansen.http.net.Header[]{header, headerToken},
                postParam,
                requestDataCallback
        );
    }

    private RequestDataCallback requestDataCallback = new RequestDataCallback<UserRefreshModel>() {
        @Override
        public void dataCallback(int status, UserRefreshModel model) {
            hideHUD();
            System.out.println("网络请求返回的Status:" + status);
            if(model==null || model.getCode() != 200){
                if (model == null) {

                } else {
                    Toast.makeText(LoginMoreActivity.this, model.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }else{
                YMUserInfoManager userManager = new YMUserInfoManager( LoginMoreActivity.this);
                UserModel userModel = userManager.loadUserInfo();
                userModel.getUserInfo().setNikeName(nickname);
                userModel.getUserInfo().setSex(iSex);
                userModel.getUserInfo().setBirthday(birth);
                userModel.getUserInfo().setHeight(Integer.parseInt(height));
                userModel.getUserInfo().setWeight(Integer.parseInt(weight));
                if (uerHeadImageUrl != null && uerHeadImageUrl.length() > 0) {
                    userModel.getUserInfo().setPhoto(model.getData().getPhoto());
                }
                userManager.saveUserInfo(userModel);
                if (value > 0) {
                    finish();
                    return;
                }
                Intent intentB = new Intent(LoginMoreActivity.this, HomeActivity.class);
                intentB.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentB);
            }

        }

        @Override
        public void dataCallback(UserRefreshModel obj) {
            hideHUD();
            if (obj == null) {
                Toast.makeText(LoginMoreActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void showHUD() {
        hud = KProgressHUD.create(this)
                .setSize(150,150)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        hud.show();
    }

    public void hideHUD() {
        hud.dismiss();
    }
}
