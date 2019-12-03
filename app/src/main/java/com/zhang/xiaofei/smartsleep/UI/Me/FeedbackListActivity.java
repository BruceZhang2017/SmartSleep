package com.zhang.xiaofei.smartsleep.UI.Me;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.RequestDataCallback;
import com.deadline.statebutton.StateButton;

import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.Webview.WebActivity;
import com.zhang.xiaofei.smartsleep.Model.Feedback.FeedbackItemModel;
import com.zhang.xiaofei.smartsleep.Model.Feedback.FeedbackModel;
import com.zhang.xiaofei.smartsleep.Model.Feedback.FeedbackViewHolder;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListActivity extends BaseAppActivity {

    private TextView tvTitle;
    private ImageButton ibLeft;
    private ListView listView;
    private FeedbackListAdapter adapter;
    private ImageView ivNull;
    private TextView tvNull;
    private List fdList = new ArrayList<FeedbackItemModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);
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
        listView = (ListView) findViewById(R.id.list);
        adapter = new FeedbackListAdapter(fdList);
        listView.setAdapter(adapter);
        ivNull = (ImageView)findViewById(R.id.iv_null);
        tvNull = (TextView)findViewById(R.id.tv_null);
        ivNull.setVisibility(View.INVISIBLE);
        tvNull.setVisibility(View.INVISIBLE);
        downloadFeedback();

        // common_feedback_null
        String agree = getResources().getString(R.string.mine_feedback);
        String text = getResources().getString(R.string.common_feedback_null);
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(text);
        // 单独设置字体颜色
        int index = text.indexOf(agree);
        spannableBuilder.setSpan(new MyClickableSpan(), index, index + agree.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvNull.setText(spannableBuilder);
        tvNull.setMovementMethod(LinkMovementMethod.getInstance());
    }

    class MyClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            //设置文本的颜色
            ds.setColor(0xFF8EA9E1);
            //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View view) {
            startActivity(new Intent(FeedbackListActivity.this, FeedbackActivity.class));
        }
    }

    private class FeedbackListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private List feedbackList;

        private FeedbackListAdapter(List feedbackList) {
            mInflater = LayoutInflater.from(FeedbackListActivity.this);
            this.feedbackList = feedbackList;
        }

        @Override
        public int getCount() {
            if(feedbackList == null) {
                return 0;
            }
            return feedbackList.size();
        }

        @Override
        public Object getItem(int i) {
            return feedbackList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final FeedbackViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_feedback_item, parent, false);
                holder = new FeedbackViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.title = (TextView) convertView.findViewById(R.id.tv_type);
                holder.status = (TextView) convertView.findViewById(R.id.tv_state);
                holder.desc = (TextView)convertView.findViewById(R.id.tv_desc);
                holder.button1 = (StateButton)convertView.findViewById(R.id.btn_solved);
                holder.button2 = (StateButton)convertView.findViewById(R.id.btn_unsolved);
                holder.button1.setVisibility(View.INVISIBLE);
                holder.button2.setVisibility(View.INVISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (FeedbackViewHolder) convertView.getTag();
            }

            FeedbackItemModel item = (FeedbackItemModel)fdList.get(position);

            holder.desc.setText(item.getContent());
            if (item.getType() == 1) {
                holder.title.setText(R.string.mine_operation);
            } else if (item.getType() == 2) {
                holder.title.setText(R.string.mine_product);
            } else {
                holder.title.setText(R.string.mine_advice);
            }

            return convertView;
        }
    }

    private void downloadFeedback() {
        YMUserInfoManager userManager = new YMUserInfoManager( FeedbackListActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "application/x-www-form-urlencoded");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        showHUD();
        HTTPCaller.getInstance().post(
                FeedbackModel.class,
                YMApplication.getInstance().domain() + "app/idea/list?userId=" + userModel.getUserInfo().getUserId(),
                new com.ansen.http.net.Header[]{header, headerToken},
                null,
                requestDataCallback
        );
    }

    private RequestDataCallback requestDataCallback = new RequestDataCallback<FeedbackModel>() {
        @Override
        public void dataCallback(int status, FeedbackModel model) {
            hideHUD();
            System.out.println("网络请求返回的Status:" + status);
            if(model==null || model.getCode() != 200){
                if (model != null) {
                    Toast.makeText(FeedbackListActivity.this, model.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }else{
                if (model.getData() == null || model.getData().length == 0) {
                    ivNull.setVisibility(View.VISIBLE);
                    tvNull.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    return;
                }
                for (FeedbackItemModel item:
                     model.getData()) {
                    fdList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

        }

        @Override
        public void dataCallback(FeedbackModel obj) {
            hideHUD();
            if (obj == null) {
                Toast.makeText(FeedbackListActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };

}
