package com.example.rytryde.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.R;
import com.example.rytryde.data.model.PlaceAutocomplete;
import com.example.rytryde.utils.LocationHelper;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {

    private static final String TAG = "PlacesAutoAdapter";
    final ArrayList<PlaceAutocomplete> predictionList = new ArrayList<>();
    private final PlacesClient placesClient;
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private ClickListener clickListener;
    private Handler handler = new Handler();
    private AutocompleteSessionToken token;

    public PlacesAutoCompleteAdapter(Context context) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setToken(AutocompleteSessionToken token) {
        this.token = token;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Cancel any previous place prediction requests
                handler.removeCallbacksAndMessages(null);
                mResultList.clear();

                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    // Start a new place prediction request in 300 ms

                    // new AsyncLocationPrediction(mContext,token,constraint.toString(), LocationService.getRecentLocation()).execute();
                    handler.postDelayed(() -> {
                        try {
                            mResultList = LocationHelper.getPrediction(TAG, constraint.toString(), placesClient, token);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }, 300);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();

                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();

                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_place_suggestion, viewGroup, false);
        PredictionHolder vh = new PredictionHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {
        mPredictionHolder.address.setText(mResultList.get(i).getPrimaryText());
        mPredictionHolder.area.setText(mResultList.get(i).getSecondaryText());

    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public interface ClickListener {
        void click(Place place);
    }

    public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView address, area;
        private LinearLayout mRow;

        PredictionHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.tv_place_address);
            area = itemView.findViewById(R.id.tv_place_area);
            mRow = itemView.findViewById(R.id.ll_place_item_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PlaceAutocomplete item = mResultList.get(getAdapterPosition());
            if (v.getId() == R.id.cv_place_item_view) {
                String placeId = String.valueOf(item.getPlaceID());

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse response) {
                        Place place = response.getPlace();
                        clickListener.click(place);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        if (exception instanceof ApiException) {
                            Toast.makeText(mContext, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public class AsyncLocationPrediction extends AsyncTask<Void, Void, Task<FindAutocompletePredictionsResponse>> {


        private Context context;
        private String text;
        private AutocompleteSessionToken token;
        private FindAutocompletePredictionsRequest request;
        private Location lastKnownLocation;
        private PlacesClient placesClient;
        private String TAG = "AsyncLocationPredictionClass";


        public AsyncLocationPrediction(Context mContext, AutocompleteSessionToken token, String text, Location lastKnownLcation) {
            context = mContext;
            this.text = text;
            this.token = token;
            this.lastKnownLocation = lastKnownLcation;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Use the builder to create a FindAutocompletePredictionsRequest.
            request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setLocationBias(LocationHelper.getBounds(lastKnownLocation, 59000))
                    .setQuery(text)
                    .build();

            placesClient = com.google.android.libraries.places.api.Places.createClient(context);
            predictionList.clear();

        }

        @Override
        protected Task<FindAutocompletePredictionsResponse> doInBackground(Void... params) {
            Task<FindAutocompletePredictionsResponse> autocompletePredictions = null;
            try {
                autocompletePredictions = placesClient.findAutocompletePredictions(request);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return autocompletePredictions;
        }


        @Override
        protected void onPostExecute(Task<FindAutocompletePredictionsResponse> autocompletePredictions) {

            autocompletePredictions.addOnSuccessListener(findAutocompletePredictionsResponse -> {
                if (findAutocompletePredictionsResponse != null) {
                    for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                        predictionList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(null).toString(), prediction.getSecondaryText(null).toString()));
                        Log.i(TAG, prediction.getPlaceId());
                        Log.i(TAG, prediction.getPrimaryText(null).toString());

                    }
                }

            });
            autocompletePredictions.addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });

            notifyDataSetChanged();

        }

    }
}
