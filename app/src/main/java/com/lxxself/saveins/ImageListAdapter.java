package com.lxxself.saveins;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SizeReadyCallback;

import java.util.List;

/**
 * Created by lxxself on 2016/11/5.
 */

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MyViewHolder> {

    private final Context context;
    private List<InsImg> insImgList;

    public ImageListAdapter(Context context, List<InsImg> insImgList) {
        this.context = context;
        this.insImgList = insImgList;
    }

    @Override
    public ImageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        return new ImageListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageListAdapter.MyViewHolder holder, final int position) {
        holder.textView.setText(insImgList.get(position).getContent());
        Glide.with(context)
                .load(insImgList.get(position).getImgPath())
                .dontAnimate()
                .into(holder.imageView)
                .getSize(new SizeReadyCallback() {
                    @Override
                    public void onSizeReady(int width, int height) {
                        if (!holder.itemView.isShown()) {
                            holder.itemView.setVisibility(View.VISIBLE);
                        }
                    }
                });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick.imageClick(position);
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onImageLongClick.imageLongClick(position);
                return true;
            }
        });
    }

    private OnImageClick onImageClick;

    public void setOnImageClick(OnImageClick onImageClick) {
        this.onImageClick = onImageClick;
    }

    public interface OnImageClick {
        void imageClick(int position);
    }
    private OnImageLongClick onImageLongClick;

    public void setOnImageLongClick(OnImageLongClick onImageLongClick) {
        this.onImageLongClick = onImageLongClick;
    }

    public interface OnImageLongClick {
        void imageLongClick(int position);
    }

    @Override
    public int getItemCount() {
        return insImgList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private View itemView;
        public MyViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = (ImageView) view.findViewById(R.id.img_ins);
            textView = (TextView) view.findViewById(R.id.tv_des);
        }
    }
}