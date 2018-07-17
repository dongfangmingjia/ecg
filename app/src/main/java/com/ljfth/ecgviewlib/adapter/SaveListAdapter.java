package com.ljfth.ecgviewlib.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ljfth.ecgviewlib.R;
import com.ljfth.ecgviewlib.model.FileModel;

import java.util.List;

public class SaveListAdapter extends RecyclerView.Adapter<SaveListAdapter.SaveViewHolder> {

    private List<FileModel> mFileList;

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
    public void onBindViewHolder(@NonNull SaveViewHolder holder, int position) {
        if (mFileList != null && mFileList.size() > 0 && position < mFileList.size()) {
            FileModel fileModel = mFileList.get(position);
            if (fileModel != null) {
                if (!TextUtils.isEmpty(fileModel.getName())) {
                    String name = fileModel.getName().substring(fileModel.getName().lastIndexOf("/") + 1, fileModel.getName().length());
                    holder.tvFileName.setText(name);
                }
                holder.tvFileSize.setText(fileModel.getSize());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    /**
     * 设置数据并更新列表
     * @param list
     */
    public void setData(List<FileModel> list) {
        this.mFileList = list;
        notifyDataSetChanged();
    }

    public class SaveViewHolder extends RecyclerView.ViewHolder {

        TextView tvFileName;
        TextView tvFileSize;
        CheckBox mCheckBox;

        public SaveViewHolder(View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvFileSize = itemView.findViewById(R.id.tv_file_size);
            mCheckBox = itemView.findViewById(R.id.check_box);
        }
    }
}
