package com.zhang.xiaofei.smartsleep.UI.Me;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.R;

import java.util.List;

/**
 * Created by hesk on 3/2/16.
 */
public class GridJRAdapter extends UltimateGridLayoutAdapter<DeviceModel, ItemGridCellBinder> {

    public Activity activity;
    private List<DeviceModel> list;
    public Boolean isEdit = false;

    public GridJRAdapter(List<DeviceModel> hand) {
        super(hand);
        list = hand;
    }

    /**
     * the layout id for the normal data
     *
     * @return the ID
     */
    @Override
    protected int getNormalLayoutResId() {
        return ItemGridCellBinder.layout;
    }

    /**
     * this is the Normal View Holder initiation
     *
     * @param view view
     * @return holder
     */
    @Override
    protected ItemGridCellBinder newViewHolder(View view) {
        return new ItemGridCellBinder(view, true);
    }


    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    /**
     * binding normal view holder
     *
     * @param holder   holder class
     * @param data     data
     * @param position position
     */
    @Override
    protected void withBindHolder(ItemGridCellBinder holder, DeviceModel data, int position) {

    }

    @Override
    protected void bindNormal(ItemGridCellBinder b, DeviceModel jRitem, int position) {

        if (jRitem.getId() == 0) {
            b.imageViewSample.setImageResource(R.mipmap.equipment_icon_binding);
            b.imageViewSample.setBackgroundColor(0x00000000);
            b.textViewSample.setText("");
            b.textViewVersion.setText("");
            b.ibDelete.setVisibility(View.INVISIBLE);
        } else {
            b.imageViewSample.setBackgroundColor(0xFF0A1833);
            if (jRitem.getDeviceType() == 1) {
                b.textViewSample.setText(R.string.report_yamy_sleep_belt);
                b.imageViewSample.setImageResource(R.drawable.shuimiandai);
            } else if (jRitem.getDeviceType() == 2) {
                b.textViewSample.setText(R.string.report_yamy_sleep_button);
                b.imageViewSample.setImageResource(R.drawable.shuimiankou);
            } else {
                b.textViewSample.setText(R.string.homepage_breathing_machine);
                b.imageViewSample.setImageResource(R.drawable.huxiji);
            }

            if (jRitem.getDeviceType() > 2) {
                b.textViewVersion.setText("V0.1");
            } else {
                b.textViewVersion.setText("V1." + jRitem.getVersion());
            }
            b.ibDelete.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);
        }
        b.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceManageActivity deviceActivity = (DeviceManageActivity)activity;
                if (deviceActivity.isEdit) {
                    if (b.textViewSample.getText().toString().length() > 0) {
                        deviceActivity.deleteDevice(position);
                    }
                } else {
                    if (b.textViewSample.getText().toString().length() > 0) {
//                        if (b.textViewVersion.getText().toString().equals("V0.1")) {
//                            return;
//                        }
                        deviceActivity.pushToOTA(position);
                    } else {
                        deviceActivity.showDialogBLEScanOrCodeScan();
                    }

                }
            }
        });
    }


    @Override
    public UltimateRecyclerviewViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new UltimateRecyclerviewViewHolder(parent);
    }

    @Override
    public ItemGridCellBinder newFooterHolder(View view) {
        return new ItemGridCellBinder(view, false);
    }

    @Override
    public ItemGridCellBinder newHeaderHolder(View view) {
        return new ItemGridCellBinder(view, false);
    }
}
