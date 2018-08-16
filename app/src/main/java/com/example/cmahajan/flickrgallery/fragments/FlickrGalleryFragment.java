package com.example.cmahajan.flickrgallery.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cmahajan.flickrgallery.R;
import com.example.cmahajan.flickrgallery.models.DataHolder;
import com.example.cmahajan.flickrgallery.models.FlickerItem;
import com.example.cmahajan.flickrgallery.utilities.FlickrItemsFetcher;
import com.example.cmahajan.flickrgallery.utilities.Paginator;
import com.example.cmahajan.flickrgallery.utilities.PersistanceManager;
import com.squareup.picasso.Picasso;

import java.util.List;


public class FlickrGalleryFragment extends Fragment {

    private DataHolder dataHolder;
    private GridView gridView;
    private Button loadMore;

    private Paginator p;
    private int currentPage = 0;
    private ImageGridViewAdapter imageGridViewAdapter;
    private int totalPages;
    private ConnectivityManager cm;
    private NetworkInfo activeNetwork;
    private boolean networkConnected;
    private PersistanceManager persistanceManager;

    public static FlickrGalleryFragment newInstance() {
        return new FlickrGalleryFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        persistanceManager = new PersistanceManager(getContext());

        // This retains the state of the fragment across activity recreation.
        setRetainInstance(true);

        cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        networkConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!networkConnected) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Could Not Load - No Internet.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            if (currentPage == 0) {
                dataHolder = new DataHolder();
                new FetchFlickrItemsTask().execute();
            }
        }


    }

    private void toggleButtons() {
        //SINGLE PAGE DATA // LAST PAGE

        if (totalPages <= 1 || currentPage == totalPages || !networkConnected) {
            loadMore.setVisibility(View.GONE);
        } else {
            loadMore.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery_flickr, container, false);

        gridView = v.findViewById(R.id.gridView);
        loadMore = v.findViewById(R.id.button_id);

        //NAVIGATE
        if (networkConnected) {
            loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentPage += 1;
                    List<FlickerItem> tmp = p.getCurrentFlickerItems(currentPage);
                    imageGridViewAdapter.getDataHolder().getFlickerItems().addAll(tmp);
                    imageGridViewAdapter.notifyDataSetChanged();
                    toggleButtons();
                }
            });
        }
        return v;
    }

    private void setupAdapter(DataHolder dataHolder) {
        if (isAdded()) {
            imageGridViewAdapter = new ImageGridViewAdapter(dataHolder);
            gridView.setAdapter(imageGridViewAdapter);
            toggleButtons();
        }
    }

    private class FetchFlickrItemsTask extends AsyncTask<Void, Integer, List<FlickerItem>> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading images from Flickr. Please wait...");
            progressDialog.show();
        }

        @Override
        protected List<FlickerItem> doInBackground(Void... params) {

            List<FlickerItem> flickerItemList = persistanceManager.fetchItems().getFlickerItems();
            List<FlickerItem> flickerItems;

            if (flickerItemList == null || flickerItemList.size() == 0) {
                flickerItems = new FlickrItemsFetcher().fetchItems(persistanceManager);
            } else {
                flickerItems = flickerItemList;
            }

            return flickerItems;
        }

        @Override
        protected void onPostExecute(List<FlickerItem> items) {
            progressDialog.dismiss();

            //Items contains the original data.
            dataHolder.setFlickerItems(items);
            p = new Paginator(dataHolder);
            totalPages = p.getTotalPages();

            List<FlickerItem> flickerItems = p.getCurrentFlickerItems(currentPage);

            DataHolder currDataHolder = new DataHolder();
            currDataHolder.setFlickerItems(flickerItems);

            setupAdapter(currDataHolder);

        }

    }

    private class ImageGridViewAdapter extends BaseAdapter {

        private DataHolder dataHolder;

        public ImageGridViewAdapter(DataHolder dataHolder) {
            this.dataHolder = dataHolder;
        }

        public DataHolder getDataHolder() {
            return dataHolder;
        }

        @Override
        public int getCount() {
            return dataHolder.getFlickerItems().size();
        }

        @Override
        public Object getItem(int position) {
            return dataHolder.getFlickerItems().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView result;
            if (convertView == null) {

                result = new ImageView(getContext());
                result.setLayoutParams(new ViewGroup.LayoutParams(285, 285));
                result.setScaleType(ImageView.ScaleType.CENTER_CROP);
                result.setPadding(32, 32, 32, 32);
            } else {
                result = (ImageView) convertView;
            }

            Log.i("POSITION", String.valueOf(position));
            String url = dataHolder.getFlickerItems().get(position).getUrl();

            Picasso.get().load(url).into(result);
            return result;
        }
    }

}
