package com.ljfth.ecgviewlib.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ljfth.ecgviewlib.R;
import com.ljfth.ecgviewlib.model.FileModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveListAdapter extends RecyclerView.Adapter<SaveListAdapter.SaveViewHolder> {

    private List<FileModel> mFileList;
    private List<String> mPathList = new ArrayList<>();
    public static final String ACTION_ITEM_CLICK = "action_item_click";

    public SaveListAdapter(List<FileModel> list) {
        this.mFileList = list;
    }

    @NonNull
    @Override
    public SaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_save_layout, parent, false);
        SaveViewHolder holder = new SaveViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SaveViewHolder holder, final int position) {
        if (mFileList != null && mFileList.size() > 0 && position < mFileList.size()) {
            final FileModel fileModel = mFileList.get(position);
            if (fileModel != null) {
                holder.tvFileName.setText(fileModel.getName());
                holder.tvFileSize.setText(fileModel.getSize());
                holder.mCheckBox.setChecked(fileModel.isChecked());
                holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        FileModel item = mFileList.get(position);
                        if (isChecked) {
                            mPathList.add(item.getPath());
                        } else {
                            if (mPathList != null && mPathList.size() > 0) {
                                int removeIndex = -1;
                                for (int i = 0; i < mPathList.size(); i++) {
                                    String path = mPathList.get(i);
                                    if (TextUtils.equals(path, item.getPath())) {
                                        removeIndex = i;
                                        break;
                                    }
                                }
                                if (removeIndex >= 0 && removeIndex < mPathList.size()) {
                                    mPathList.remove(removeIndex);
                                }
                            }
                        }
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("action", ACTION_ITEM_CLICK);
                        params.put("list", mPathList);
                        EventBus.getDefault().post(params);
                    }
                });
                holder.clItemRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mFileList != null && position < mFileList.size()) {
                            FileModel item = mFileList.get(position);
                            if (item != null) {
                                item.setChecked(!item.isChecked());
                                notifyItemChanged(position);
                                if (item.isChecked()) {
                                    mPathList.add(item.getPath());
                                } else {
                                    if (mPathList != null && mPathList.size() > 0) {
                                        int removeIndex = -1;
                                        for (int i = 0; i < mPathList.size(); i++) {
                                            String path = mPathList.get(i);
                                            if (TextUtils.equals(path, item.getPath())) {
                                                removeIndex = i;
                                                break;
                                            }
                                        }
                                        if (removeIndex >= 0 && removeIndex < mPathList.size()) {
                                            mPathList.remove(removeIndex);
                                        }
                                    }
                                }
                                HashMap<String, Object> params = new HashMap<>();
                                params.put("action", ACTION_ITEM_CLICK);
                                params.put("list", mPathList);
                                EventBus.getDefault().post(params);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFileList == null ? 0 : mFileList.size();
    }

    /**
     * 设置数据并更新列表
     * @param list
     */
    public void setData(List<FileModel> list) {
        this.mFileList = list;
        notifyDataSetChanged();
    }


    public void clearPathList() {
        mPathList.clear();
    }

    public class SaveViewHolder extends RecyclerView.ViewHolder {

        TextView tvFileName;
        TextView tvFileSize;
        CheckBox mCheckBox;
        ConstraintLayout clItemRoot;

        public SaveViewHolder(View itemView) {
            super(itemView);
            clItemRoot = itemView.findViewById(R.id.cl_item_root);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvFileSize = itemView.findViewById(R.id.tv_file_size);
            mCheckBox = itemView.findViewById(R.id.check_box);
        }
    }
}
