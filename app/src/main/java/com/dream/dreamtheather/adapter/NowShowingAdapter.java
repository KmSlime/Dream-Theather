package com.dream.dreamtheather.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dream.dreamtheather.AdminActivity;
import com.dream.dreamtheather.Fragment.MovieDetail;
import com.dream.dreamtheather.MainActivity;
import com.dream.dreamtheather.Model.Movie;
import com.dream.dreamtheather.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NowShowingAdapter extends RecyclerView.Adapter<NowShowingAdapter.ItemHolder> {
    private List<Movie> mData = new ArrayList<>();
    Context mContext;

    public NowShowingAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Movie> data) {
        mData.clear();
        if (data !=null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(List<Movie> data) {
        if(data!=null) {
            int posBefore = mData.size();
            mData.addAll(data);
            notifyItemRangeInserted(posBefore,data.size());
        }
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_movie_card, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvRating) TextView tvRating;

        @BindView(R.id.type_linear_layout)
        LinearLayout mTypeParent;

        @BindView(R.id.tvDirector) TextView tvDirector;
        @BindView(R.id.txt_actors) TextView tvCast;
        @BindView(R.id.img)
        ImageView image;
        @BindView(R.id.panel) View panel;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        @OnClick(R.id.panel)
        void clickPanel() {
            String curContext = mContext.getClass().getSimpleName();
            Log.d("MovieDetail", "curContext: "+curContext);
            if(curContext.equals("AdminActivity"))
                ((AdminActivity) mContext).loadFragment(MovieDetail.newInstance(mData.get(getBindingAdapterPosition())));
            else
                ((MainActivity) mContext).loadFragment(MovieDetail.newInstance(mData.get(getBindingAdapterPosition())));
        }
        public void bind(Movie movie) {

            tvName.setText(movie.getTitle());

            tvRating.setText(String.valueOf(movie.getRate()));

            List<String> types = movie.getType();
            mTypeParent.removeAllViews();
            if(mContext instanceof Activity) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                if(inflater!=null)
                    for (String type : types) {
                        if(!type.isEmpty()) {
                            TextView tv = (TextView) inflater.inflate(R.layout.type_movie_text_view, mTypeParent, false);
                            tv.setText(type);
                            tv.setBackgroundResource(R.drawable.round_yellow_drawable);
                            mTypeParent.addView(tv);
                        }
                    }
            }

            tvDirector.setText(movie.getDirector());
            tvCast.setText(movie.getCast());

            RequestOptions requestOptions = new RequestOptions().override(image.getWidth());
            Glide.with(mContext)
                    .load(movie.getImageUrl())
                    .apply(requestOptions)
                    .into(image);
        }

    }
}
