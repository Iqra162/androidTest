package com.example.bilal.movieslist.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.bilal.movieslist.GlobalClass;
import com.example.bilal.movieslist.Models.MovieDetails;
import com.example.bilal.movieslist.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    public Context mContext;

    public interface OnLongItemClickListener {
        void onLongItemClick(String item);
    }

    public ArrayList<MovieDetails> items;
    private final OnLongItemClickListener listener;

    public ListAdapter(Context context,OnLongItemClickListener listener) {
        items = new ArrayList<>();
        this.listener = listener;
        mContext=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {

            case ITEM:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_adapter, parent, false);

                viewHolder = new MovieDetailsHolder(v);

                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(v2);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        //viewHolder.bind(items.get(position), listener);
        switch (getItemViewType(position)) {
            case ITEM:


                    final MovieDetailsHolder dataHolder = (MovieDetailsHolder) viewHolder;
                    dataHolder.name.setText(items.get(position).getOriginalTitle());
                    dataHolder.overview.setText(items.get(position).getOverview());
                    dataHolder.releasedDate.setText(items.get(position).getReleaseDate());
                    Picasso.get().load(GlobalClass.imageUrl + items.get(position).getPosterPath()).error(R.drawable.sample).into(dataHolder.image);
                Picasso.get()
                        .load(GlobalClass.imageUrl + items.get(position).getPosterPath())
                        .transform(new BlurTransformation(mContext, 20, 1))
                        .into(dataHolder.mBackgroundLAyout);
                    dataHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onLongItemClick(items.get(position).getPosterPath());
                        }
                    });

                break;
            case LOADING:
                break;
        }

    }


    class MovieDetailsHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView overview;
        private TextView releasedDate;
        private ImageView image;
        private ProgressBar mProgessbar;
        public ImageView mBackgroundLAyout;

        public MovieDetailsHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.movie_title);
            overview = (TextView) itemView.findViewById(R.id.overView);
            releasedDate = (TextView) itemView.findViewById(R.id.release_date);
            image = (ImageView) itemView.findViewById(R.id.thumbnail);
            mProgessbar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mBackgroundLAyout=(ImageView)itemView.findViewById(R.id.bg);


        }
//
//            public void bind(final MovieDetails item, final OnItemClickListener listener) {
//                name.setText(item.getOriginalTitle());
//                overview.setText(item.getOverview());
//                releasedDate.setText(item.getReleaseDate());
//                Picasso.get().load(GlobalClass.imageUrl + item.getPosterPath()).error(R.drawable.sample).into(image);
//
//                image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        listener.onItemClick(item.getPosterPath());
//                    }
//                });
//            }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == items.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(MovieDetails movie) {
        if(movie.getPosterPath()!= null) {
            items.add(movie);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void addAll(ArrayList<MovieDetails> moveResults) {
        for (MovieDetails result : moveResults) {
            if(result.getPosterPath() != null)
            add(result);
        }
    }

    public void remove(MovieDetails r) {
        int position = items.indexOf(r);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new MovieDetails());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = items.size() - 1;
        MovieDetails result = getItem(position);

        if (result != null) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public MovieDetails getItem(int position) {
        return items.get(position);
    }

    protected class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}