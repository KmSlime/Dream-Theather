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

public class ChooseSeatAdapter extends RecyclerView.Adapter<ChooseSeatAdapter.ItemHolder> {
    private static final String TAG = "ChooseSeatAdapter";
    private String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String NUM = "123456789";
    private int mColumn = 5, mRow = 5;

    Context mContext;

    ArrayList<Integer> mStateData = new ArrayList<>(); // -1 means used, 0 means blank, 1 means choosen
    ArrayList<Boolean> mSeatData = new ArrayList<>();
    ArrayList<Integer> mSelectedList = new ArrayList<>();
    List<Boolean> listSeatPerShowing = new ArrayList<>();

    public ChooseSeatAdapter(Context context) {
        this.mContext = context;
    }

    public void setRowAndColumn(int column, int row) {
        mColumn = column;
        mRow = row;
    }

    public void setData(List<Boolean> data) {
        mSeatData.clear();
        mStateData.clear();
        mSelectedList.clear();
        if (data != null)
            listSeatPerShowing = data;
            for (Boolean item :data) {
                mSeatData.add(item);
                if (item)
                    mStateData.add(-1);
                else mStateData.add(0);
            }
        notifyDataSetChanged();
    }


    public interface OnSelectedChangedListener {
        void onSelectedChanged(List<Integer> selects);
    }

    private OnSelectedChangedListener mListener;

    public void setOnSelectedChangedListener(OnSelectedChangedListener listener) {
        mListener = listener;
    }

    private void addToSelectedList(int pos) {
        mSelectedList.add(pos);
        mStateData.set(pos, 1);
        notifyItemChanged(pos);
        if (mListener != null) mListener.onSelectedChanged(mSelectedList);
    }

    private void removeFromSelectedList(int pos) {
        mSelectedList.remove((Integer) pos);
        mStateData.set(pos, 0);
        notifyItemChanged(pos);
        if (mListener != null) mListener.onSelectedChanged(mSelectedList);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_choose_seat_square, viewGroup, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        itemHolder.bind(mStateData.get(i));
    }

    @Override
    public int getItemCount() {
        return mStateData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Integer state) {

            int pos = getBindingAdapterPosition();
            int myRow = pos / mRow;
            int myColumn = pos % mColumn;
            if (itemView instanceof TextView) {
                String s = ("" + ABC.charAt(myRow)) + NUM.charAt(myColumn);
                ((TextView) itemView).setText(s);
            }

            if (state == -1) {
                itemView.setBackgroundResource(R.drawable.background_item_choose_seat_used);
            } else if (state == 0) {
                itemView.setBackgroundResource(R.drawable.background_item_choose_seat);

            } else {
                itemView.setBackgroundResource(R.drawable.background_item_choose_seat_choosen);
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getBindingAdapterPosition();
            if (mStateData.get(pos) == 0) {
                // mean that blank
                addToSelectedList(pos);

            } else if (mStateData.get(pos) == 1) {
                // mean that choosen
                removeFromSelectedList(pos);
            }

        }
    }
}
