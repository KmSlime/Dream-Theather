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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dream.dreamtheather.Fragment.NowShowingMoviesOfCinema;
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

    private CinemaOnClickListener mListener;

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
        if (cinemas != null) {
            cinemaList.addAll(cinemas);
        }
        notifyDataSetChanged();
    }


    public void addCinema(List<Cinema> cinemas) {
        if (cinemas != null) {
            int posBefore = cinemaList.size();
            cinemaList.addAll(cinemas);
            notifyItemRangeInserted(posBefore, cinemas.size());
        }
    }

    @NonNull
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
        @BindView(R.id.tvCinemaName)
        TextView tvCinemaName;
        @BindView(R.id.tvAddressCinema)
        TextView tvAddressCinema;
        @BindView(R.id.tvHotline)
        TextView tvHotline;
        @BindView(R.id.imageCinema)
        ImageView imageCinema;
        @BindView(R.id.itemCinema)
        CardView itemCinema;


        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.itemCinema)
        void clickPanel() {
            if (mListener != null)
                mListener.onItemClick(cinemaList.get(getBindingAdapterPosition()));
            else if (context instanceof MainActivity) {
                ((MainActivity) context)
                        .loadFragment(
                                NowShowingMoviesOfCinema
                                        .newInstance(
                                                cinemaList.get(getBindingAdapterPosition()).getMovies(),
                                                cinemaList.get(getBindingAdapterPosition()).getName(),
                                                cinemaList.get(getBindingAdapterPosition()).getHotline(),
                                                cinemaList.get(getBindingAdapterPosition()).getAddress()
                                        ));
            }
        }

        public void bind(Cinema cinema) {

            tvCinemaName.setText(cinema.getName());

            if (context instanceof Activity) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            }

            String Address = cinema.getAddress();

            tvAddressCinema.setText(Address);
            tvHotline.setText(cinema.getHotline());

            Glide.with(context)
                    .load(cinema.getImageUrl())
                    .placeholder(R.drawable.img_cinema)
                    .error(R.drawable.img_cinema)
                    .into(imageCinema);
        }
    }
}
