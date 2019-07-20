package com.example.bilal.movieslist.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilal.movieslist.Adapter.ListAdapter;
import com.example.bilal.movieslist.GlobalClass;
import com.example.bilal.movieslist.Models.MovieDetails;
import com.example.bilal.movieslist.Models.ResponseData;
import com.example.bilal.movieslist.R;
import com.example.bilal.movieslist.rest.ApiClient;
import com.example.bilal.movieslist.rest.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ListAdapter.OnLongItemClickListener {
    public RecyclerView recyclerView;
   public ListAdapter adapter;
   ArrayList<MovieDetails> list=new ArrayList<>();
   public Context mContext;
   ListAdapter.OnLongItemClickListener listener;
   public ProgressDialog progressDialog;
LinearLayoutManager mLayoutManager;
public boolean isLoading,isLastPage=false;
public int PAGE_SIZE=10;
    private int currentPage = 1;
    private int TOTAL_PAGES = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        listener = this;
        recyclerView = (RecyclerView) findViewById(R.id.rv_movies_list);


        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ListAdapter(mContext,listener);
        recyclerView.setAdapter(adapter);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                if (GlobalClass.checkConnection(mContext)) {

                    getData();
                } else {
                    GlobalClass.showDialog(mContext);
                }
            }
            recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading = true;
                    currentPage += 1;

                    // mocking network delay for API call
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextPage();
                        }
                    }, 1000);
                }

                @Override
                public int getTotalPageCount() {
                    return TOTAL_PAGES;
                }

                @Override
                public boolean isLastPage() {
                    return isLastPage;
                }

                @Override
                public boolean isLoading() {
                    return isLoading;
                }
            });


        }
    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE}, 101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    if (GlobalClass.checkConnection(mContext)) {

                        getData();
                    } else {
                        GlobalClass.showDialog(mContext);
                    }
                } else {
                    //not granted
                    AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                    builder.setMessage("Permissions are not granted !!");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getData(){
        progressDialog = new ProgressDialog(MainActivity.this);

        progressDialog.setMessage("Loading....");
        if ((!isFinishing()) && (progressDialog != null)) {
            progressDialog.show();
        }
        ApiInterface apiInterface = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ResponseData> getMoviesDetailsCall;

        getMoviesDetailsCall = apiInterface.getMoviesData("2010","vote_average.desc","e95c5c33d38a6be5f25c91d64c8d80e0",currentPage);
        getMoviesDetailsCall.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if ((!isFinishing()) && (progressDialog != null)) {
                    progressDialog.dismiss();
                }
                if(response.isSuccessful())
                {

                    ArrayList<MovieDetails> results =response.body().getMovieDetails();

                    adapter.addAll(results);

                    if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;


                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                if ((!isFinishing()) && (progressDialog != null)) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext,t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    public void loadNextPage(){
        ApiInterface apiInterface = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ResponseData> getMoviesDetailsCall;

        getMoviesDetailsCall = apiInterface.getMoviesData("2010","vote_average.desc","e95c5c33d38a6be5f25c91d64c8d80e0",currentPage);
        getMoviesDetailsCall.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if ((!isFinishing()) && (progressDialog != null)) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    adapter.removeLoadingFooter();
                    isLoading = false;

                    ArrayList<MovieDetails> list = response.body().getMovieDetails();
                    adapter.addAll(list);

                    if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                if ((!isFinishing()) && (progressDialog != null)) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext,t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onLongItemClick(String item) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_layout, null);
        dialog.setContentView(view);
        ImageView image = (ImageView) dialog.findViewById(R.id.imgenlarged);
        Picasso.get().load(GlobalClass.imageUrl+item). error(R.drawable.sample).into(image);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

        LinearLayoutManager layoutManager;

        /**
         * Supporting only LinearLayoutManager for now.
         *
         * @param layoutManager
         */
        public PaginationScrollListener(LinearLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading() && !isLastPage()) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= getTotalPageCount()) {
                    loadMoreItems();
                }
            }

        }

        protected abstract void loadMoreItems();

        public abstract int getTotalPageCount();

        public abstract boolean isLastPage();

        public abstract boolean isLoading();
    }





    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    loadMoreItems();
                }
            }
        }

    };
    public void loadMoreItems(){
        isLoading = true;

        currentPage += 1;
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
