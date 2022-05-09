package com.dream.dreamtheather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dream.dreamtheather.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ItemHolder> {
    private static final String TAG ="DateAdapter";
            public DateAdapter(Context context) {
                this.mContext = context;
            }

    private int mSelected = 0;
    public interface OnSelectedChangedListener {
        void onSelectedChanged(int position);
    }
    private Context mContext;
    private OnSelectedChangedListener mListener;
    public void setOnSelectedChangedListener(OnSelectedChangedListener listener) {
        mListener = listener;
        mListener = listener;
    }
    private ArrayList<String> mData = new ArrayList<>();
    public void setData(List<String> data) {
        mData.clear();
        if(data!=null)
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_date_booking, viewGroup, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        itemHolder.bind(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.text)
        TextView mText;
//        @BindView(R.id.mark)
//        View mMark;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        void bind(String date) {
            int pos = getAdapterPosition();
            if(pos!=mSelected) {
                mText.setText(date);
              //  mMark.setVisibility(View.INVISIBLE);
                mText.setTextColor(mContext.getResources().getColor(R.color.setting_label_color));
                mText.setBackgroundColor(0);
            }
            else {
                mText.setText(date);
             //   mMark.setVisibility(View.VISIBLE);
                mText.setTextColor(mContext.getResources().getColor(R.color.FlatWhite));
                mText.setBackgroundResource(R.drawable.round_item_date_selected);
            }
        }

        @Override
        public void onClick(View v) {
            if(getAdapterPosition()!=mSelected) {
                int old = mSelected;
                mSelected = getAdapterPosition();
                if(mListener!=null) mListener.onSelectedChanged(mSelected);
                notifyItemChanged(mSelected);
                notifyItemChanged(old);
            }
        }
    }
}
