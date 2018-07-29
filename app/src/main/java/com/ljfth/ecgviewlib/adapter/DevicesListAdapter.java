package com.ljfth.ecgviewlib.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljfth.ecgviewlib.Constant;
import com.ljfth.ecgviewlib.R;
import com.ljfth.ecgviewlib.model.DevicesModel;

import java.util.ArrayList;
import java.util.List;

public class DevicesListAdapter extends RecyclerView.Adapter<DevicesListAdapter.ViewHolder> {

    private List<DevicesModel> mDeviceList = new ArrayList<>();
    private OnItemClickListener mListener;
    private Context mContext;
    private int mProPosition = -1;

    public DevicesListAdapter(Context context, List<DevicesModel> list) {
        this.mContext = context;
        this.mDeviceList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_list, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (mDeviceList != null && mDeviceList.size() >0) {
            final DevicesModel devicesModel = mDeviceList.get(position);
            if (devicesModel != null) {
                holder.mTvMac.setText(devicesModel.getMacStr());
                holder.mTvType.setText(getDevicesType(devicesModel.getDeviceType()));
                holder.mTvSignal.setText(devicesModel.getSignalStr());
                holder.mTvConnect.setText(getDevicesConnect(devicesModel.getConnectStr()));
                holder.mLlListRoot.setBackgroundColor(devicesModel.isChecked() ?
                        ContextCompat.getColor(mContext, R.color.black_90) :
                        ContextCompat.getColor(mContext, R.color.white));
                holder.mLlListRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            if (mProPosition >= 0 && mProPosition != position) {
                                mDeviceList.get(mProPosition).setChecked(false);
                            }
                            devicesModel.setChecked(!devicesModel.isChecked());
                            if (devicesModel.isChecked()) {
                                mListener.clickListener(devicesModel.getDevicesData());
                            } else {
                                mListener.clickListener(null);
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    private String getDevicesConnect(String connectStr) {
        if (!TextUtils.isEmpty(connectStr)) {
            return TextUtils.equals(connectStr, "0") ? "未连接" : "已连接";
        }
        return "";
    }

    private String getDevicesType(String deviceType) {
        String type = null;
        switch (deviceType) {
            case Constant.DEVICES_TYPE_BODYSTMSPO2:
                type = "血氧";
                break;
            case Constant.DEVICES_TYPE_BODYSTMECG:
                type = "心电";
                break;
            case Constant.DEVICES_TYPE_BODYSTMNIBP_PWV:
                type = "连续血压";
                break;
            case Constant.DEVICES_TYPE_BODYSTMRESP:
                type = "呼吸";
                break;
            case Constant.DEVICES_TYPE_BODYSTMTEMP1:
                type = "温度";
                break;
            case Constant.DEVICES_TYPE_BODYSTMTEMP2:
                type = "温度";
                break;
            case Constant.DEVICES_TYPE_BODYSTMNIBP_VAL:
                type = "无创血压";
                break;
            default:
                type = null;
                break;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return mDeviceList != null ? mDeviceList.size() : 0;
    }


    public void setDeviceList(List<DevicesModel> list) {
        Log.e("ecg", "==============adapter     设置数据=============" + list.size());
        this.mDeviceList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvMac;
        private final TextView mTvType;
        private final TextView mTvSignal;
        private final TextView mTvConnect;
        private final LinearLayout mLlListRoot;

        public ViewHolder(View itemView) {
            super(itemView);
            mLlListRoot = itemView.findViewById(R.id.ll_list_root);
            mTvMac = itemView.findViewById(R.id.tv_mac);
            mTvType = itemView.findViewById(R.id.tv_type);
            mTvSignal = itemView.findViewById(R.id.tv_signal);
            mTvConnect = itemView.findViewById(R.id.tv_connect);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void clickListener(byte[] data);
    }
}
