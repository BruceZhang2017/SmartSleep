package com.zhang.xiaofei.smartsleep.UI.Me;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.deadline.statebutton.StateButton;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Kit.ImagePicker.PicassoImageLoader;
import com.zhang.xiaofei.smartsleep.Kit.ValidateHelper;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends BaseAppActivity {

    private TextView tvTitle;
    private EditText etContact;
    private EditText etQuestionDesc;
    private EditText etPhone;
    private ImageButton ibLeft;
    private Button btnSubmit;
    private StateButton sbOperation;
    private StateButton sbProduct;
    private StateButton sbAdvice;
    private ImageView ibAddPiture;
    private int type = 0;
    private static final int IMAGE_PICKER = 100;
    private ImagePicker imagePicker;
    private int imageCount = 0;
    private ImageView ivPiture;
    private ConstraintLayout constraintLayout;
    private List<String> imageFiles = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.mine_feedback);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etContact = (EditText)findViewById(R.id.et_contact);
        etPhone = (EditText)findViewById(R.id.et_contact_phone);
        etQuestionDesc = (EditText)findViewById(R.id.et_question_desc);
        btnSubmit = (Button)findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String contact = etContact.getText().toString();
                String phone = etPhone.getText().toString();
                String desc = etQuestionDesc.getText().toString();
                if (contact == null || contact.length() == 0) {
                    return;
                }
                if (phone == null || phone.length() == 0 || !ValidateHelper.isPhoneNumber(phone)){
                    return;
                }
                if (type == 0) {
                    return;
                }
                if (desc == null || desc.length() == 0) {
                    return;
                }
                showHUD();
                submit(contact,phone, desc,type);
            }
        });
        initQuestionType();
        initialImagePicker();
        ibAddPiture = (ImageView)findViewById(R.id.ib_picture);
        ibAddPiture.setOnClickListener(clickListener);
        constraintLayout = (ConstraintLayout)findViewById(R.id.cl_feedback);
    }

    // 设置问题类型
    private void initQuestionType() {
        sbOperation = (StateButton)findViewById(R.id.btn_use_question);
        sbProduct = (StateButton)findViewById(R.id.btn_product_question);
        sbAdvice = (StateButton)findViewById(R.id.btn_suggestion_question);
        sbOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                changeColor(true,sbOperation);
                changeColor(false, sbProduct);
                changeColor(false, sbAdvice);
            }
        });
        sbProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
                changeColor(false,sbOperation);
                changeColor(true, sbProduct);
                changeColor(false, sbAdvice);
            }
        });
        sbAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 3;
                changeColor(false,sbOperation);
                changeColor(false, sbProduct);
                changeColor(true, sbAdvice);
            }
        });
    }

    private void changeColor(boolean value, StateButton btn) {
        if (value) {
            btn.setNormalBackgroundColor(getResources().getColor(R.color.color_090E17));
            btn.setNormalStrokeColor(getResources().getColor(R.color.color_4176E3));
            btn.setNormalTextColor(getResources().getColor(R.color.color_4176E3));
        } else {
            btn.setNormalBackgroundColor(getResources().getColor(R.color.color_090E17));
            btn.setNormalStrokeColor(getResources().getColor(R.color.color_555A63));
            btn.setNormalTextColor(getResources().getColor(R.color.color_555A63));
        }
    }

    private void submit(String username, String phone, String content, int type) {
        YMUserInfoManager userManager = new YMUserInfoManager( FeedbackActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "multipart/form-data");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        System.out.println("token:" + userModel.getToken());
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("userName",username));
        postParam.add(new NameValuePair("phone",phone));
        postParam.add(new NameValuePair("userId",userModel.getUserInfo().getUserId() + ""));
        postParam.add(new NameValuePair("time",(System.currentTimeMillis() / 1000) + ""));
        postParam.add(new NameValuePair("content",content));
        postParam.add(new NameValuePair("type",type + ""));
        if (imageFiles.size() > 0) {
            for (String file:
                 imageFiles) {
                postParam.add(new NameValuePair("uploadFile", file,true));
            }
        }
        HTTPCaller.getInstance().postFile(
                BaseProtocol.class,
                YMApplication.getInstance().domain() + "app/idea/submitIdea",
                new com.ansen.http.net.Header[]{header, headerToken},
                postParam,
                requestDataCallback
        );
    }

    private RequestDataCallback requestDataCallback = new RequestDataCallback<BaseProtocol>() {
        @Override
        public void dataCallback(int status, BaseProtocol user) {
            hideHUD();
            System.out.println("网络请求返回的Status:" + status);
            if(user==null || user.getCode() != 200){
                if (user == null) {
                    Toast.makeText(FeedbackActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FeedbackActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(FeedbackActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                FeedbackActivity.this.finish();
            }

        }

        @Override
        public void dataCallback(BaseProtocol obj) {
            hideHUD();
            if (obj == null) {
                Toast.makeText(FeedbackActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };

    ArrayList<ImageItem> images = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) {
                    imageFiles.add(images.get(0).path);
                    imagePicker.getImageLoader().displayImage(FeedbackActivity.this, images.get(0).path, ivPiture, 96, 96);
                    imageCount++;
                    addImageView();
                }
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
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
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ivPiture = (ImageView) v;
            Intent intentC = new Intent(FeedbackActivity.this, ImageGridActivity.class);
            intentC.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
            startActivityForResult(intentC, IMAGE_PICKER);
        }
    };

    // 添加图片后，动态修改图片的约束
    private void addImageView() {
        ImageView ivLeft = new ImageView(this);
        ivLeft.setId(R.id.iv_image_more);
        ivLeft.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivLeft.setImageResource(R.mipmap.suggest_icon_photo);

        ConstraintLayout.LayoutParams ivLeftLayoutParams = new ConstraintLayout.LayoutParams(
                0, 0);
        ivLeftLayoutParams.setMarginStart(DisplayUtil.dip2px(16, FeedbackActivity.this));
        ivLeftLayoutParams.leftToRight = R.id.ib_picture;
        //设置宽高比为16:9
        ivLeftLayoutParams.dimensionRatio = "w,1:1";
        ivLeftLayoutParams.topToTop = R.id.ib_picture;
        ivLeftLayoutParams.bottomToBottom = R.id.ib_picture;
        ivLeft.setLayoutParams(ivLeftLayoutParams);
        images = null;
        ivLeft.setOnClickListener(clickListener);
        constraintLayout.addView(ivLeft);
    }
}
