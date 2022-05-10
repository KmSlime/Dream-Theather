package com.dream.dreamtheather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dream.dreamtheather.Fragment.MovieDetail;
import com.dream.dreamtheather.Fragment.TheatherFragment;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Cinema;
import com.dream.dreamtheather.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.ItemHolder> {

    private ArrayList<Cinema> cinemaList = new ArrayList<>();
    Context context;

    public CinemaAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Cinema> cinemaList){
        cinemaList.clear();
        if(cinemaList!=null){
            cinemaList.addAll(cinemaList);
        }
        notifyDataSetChanged();
    }

    public void addCinema(ArrayList<Cinema> cinemas){
        int posBefore = cinemaList.size();
        cinemaList.addAll(cinemas);
        notifyItemRangeInserted(posBefore, cinemas.size());
    }

    @NonNull
    @Override
    public CinemaAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_cinema_tab, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(cinemaList.get(position));
    }

    @Override
    public int getItemCount() {
        return cinemaList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCinemaName)
        TextView tvCinemaName;

        @BindView(R.id.tvAddressCinema)
        TextView tvAddressCinema;

        @BindView(R.id.tvHotline)
        TextView tvHotline;

        @BindView(R.id.img)
        ImageView img;

        @BindView(R.id.itemCinema)
        ConstraintLayout itemCinema;


        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            //ánh xạ trong này

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.itemCinema)
        public void clickCinema(){
        }

        public void bind(Cinema cinema){
            tvCinemaName.setText(cinema.getName());
            tvHotline.setText(cinema.getHotline());
            tvAddressCinema.setText(cinema.getAddress());
            Glide.with(context).load(cinema.getImageUrl()).error(R.drawable.yourname).into(img);
        }
    }
}
