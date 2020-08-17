package com.zhang.xiaofei.smartsleep.UI.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import com.zhang.xiaofei.smartsleep.R;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Valdio Veliu on 16-07-08.
 */
public class RecyclerView_Adapter extends RecyclerView.Adapter<ViewHolder> {

    List<Audio> list = Collections.emptyList();
    Context context;
    public int currentIndex = 0;
    public int playStatus = 0;
    public GifDrawable gifFromResource;

    public RecyclerView_Adapter(List<Audio> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        try {
            String name = "music_play.gif";
            gifFromResource = new GifDrawable(context.getAssets(), name);
            holder.ivGif.setImageDrawable(gifFromResource);
        } catch(Exception e) {

        }
        holder.title.setText(list.get(position).getTitle());
        if (currentIndex == position) {
            if (playStatus == 0) {
                holder.play_pause.setImageResource(R.mipmap.icon_start);
                if (gifFromResource != null){
                    gifFromResource.stop();
                }
                holder.ivGif.setVisibility(View.INVISIBLE);
                holder.tvTime.setVisibility(View.VISIBLE);
            } else {
                holder.play_pause.setImageResource(R.mipmap.icon_pause);
                if (gifFromResource != null){
                    gifFromResource.start();
                }
                holder.ivGif.setVisibility(View.VISIBLE);
                holder.tvTime.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.play_pause.setImageResource(R.mipmap.icon_start);
            if (gifFromResource != null){
                gifFromResource.stop();
            }
            holder.ivGif.setVisibility(View.INVISIBLE);
            holder.tvTime.setVisibility(View.VISIBLE);
        }
        if (position == 0) {
            holder.cardView.setBackgroundResource(R.drawable.top_corner);
            holder.llItem.setBackgroundResource(R.drawable.top_corner);
        } else if (position == 2) {
            holder.cardView.setBackgroundResource(R.drawable.bottom_corner);
            holder.llItem.setBackgroundResource(R.drawable.bottom_corner);
        }
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

class ViewHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    TextView title;
    ImageView play_pause;
    LinearLayout llItem;
    GifImageView ivGif;
    TextView tvTime;

    ViewHolder(View itemView) {
        super(itemView);
        tvTime = (TextView)itemView.findViewById(R.id.time);
        title = (TextView) itemView.findViewById(R.id.title);
        play_pause = (ImageView) itemView.findViewById(R.id.play_pause);
        cardView = (CardView)itemView.findViewById(R.id.cardview);
        llItem = (LinearLayout)itemView.findViewById(R.id.ll_item);
        ivGif = (GifImageView)itemView.findViewById(R.id.iv_music_dynamic);
    }
}