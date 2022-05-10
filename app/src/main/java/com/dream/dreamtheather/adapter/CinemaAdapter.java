package com.dream.dreamtheather.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dream.dreamtheather.Fragment.MovieDetail;
import com.dream.dreamtheather.Fragment.NowShowingMoviesOfCinema;
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

    private List<Cinema> cinemaList = new ArrayList<>();
    Context context;

    public interface CinemaOnClickListener {
        void onItemClick(Cinema cinema);
    }

    private CinemaOnClickListener  mListener;
    public void setListener(CinemaOnClickListener listener) {
        mListener = listener;
    }
    public void removeListener() {
        mListener = null;
    }

    public CinemaAdapter(Context context) {
        this.context = context;
    }


    public void setData(List<Cinema> cinemas) {
        cinemaList.clear();
        if (cinemas !=null) {
            cinemaList.addAll(cinemas);
        }
        notifyDataSetChanged();
    }


    public void addCinema(List<Cinema> cinemas) {
        if(cinemas!=null) {
            int posBefore = cinemaList.size();
            cinemaList.addAll(cinemas);
            notifyItemRangeInserted(posBefore,cinemas.size());
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_cinema_tab, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(cinemaList.get(position));
    }

    @Override
    public int getItemCount() {
        return cinemaList.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCinemaName) TextView tvCinemaName;
        @BindView(R.id.tvAddressCinema) TextView tvAddressCinema;
        @BindView(R.id.tvHotline) TextView tvHotline;
        @BindView(R.id.imageCinema) ImageView imageCinema;
        @BindView(R.id.itemCinema)
        CardView itemCinema;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.itemCinema)
        void clickPanel() {
            if(mListener!=null) mListener.onItemClick(cinemaList.get(getAdapterPosition()));
            else if(context instanceof MainActivity) {
                ((MainActivity) context).loadFragment(NowShowingMoviesOfCinema.newInstance(
                        cinemaList.get(getAdapterPosition()).getMovies(), cinemaList.get(getAdapterPosition()).getName()));
            }
        }

        public void bind(Cinema cinema) {

            tvCinemaName.setText(cinema.getName());

            if(context instanceof Activity) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            }


            String Address = cinema.getAddress();

//            if (Address.length() >= 78) // Nhieu hon 78 ky tu thi nhung ky tu sau phai ghi bang ...
//            {
//                Address = Address.substring(0, Math.min(Address.length(), 78));
//                Address += "...";
//            }

            tvAddressCinema.setText(Address);
            tvHotline.setText(cinema.getHotline());

            RequestOptions requestOptions = new RequestOptions().override(imageCinema.getWidth());
            Glide.with(context)
                    .load(cinema.getImageUrl())
                    .apply(requestOptions)
                    .into(imageCinema);
        }
    }

//
//
//    public class ItemHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.tvCinemaName)
//        TextView tvCinemaName;
//
//        @BindView(R.id.tvAddressCinema)
//        TextView tvAddressCinema;
//
//        @BindView(R.id.tvHotline)
//        TextView tvHotline;
//
//        @BindView(R.id.img)
//        ImageView img;
//
//        @BindView(R.id.itemCinema)
//        ConstraintLayout itemCinema;
//
//
//        public ItemHolder(@NonNull View itemView) {
//            super(itemView);
//            //ánh xạ trong này
//
//            ButterKnife.bind(this, itemView);
//        }
//
//        @OnClick(R.id.itemCinema)
//        public void clickCinema(){
//        }
//
//        public void bind(Cinema cinema){
//            tvCinemaName.setText(cinema.getName());
//            tvHotline.setText(cinema.getHotline());
//            tvAddressCinema.setText(cinema.getAddress());
//            Glide.with(context).load(cinema.getImageUrl()).error(R.drawable.yourname).into(img);
//        }
//    }
}
