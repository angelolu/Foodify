package com.foodify.foodify;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment implements RecognitionActivity.ResultToFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View myView;

    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    CardView cvResults, cvPairings;
    TextView tvResults, tvAnalysis, tvPrimaryPairing, tvSecondaryPairing, tvPrimaryDescription;
    ImageView ivPairing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_result, container, false);
        cvResults = myView.findViewById(R.id.cvResults);
        cvPairings = myView.findViewById(R.id.cvPairings);
        tvResults = myView.findViewById(R.id.tvResult);
        tvAnalysis = myView.findViewById(R.id.tvAnalyzing);
        tvPrimaryPairing = myView.findViewById(R.id.tvPrimaryPairing);
        tvSecondaryPairing = myView.findViewById(R.id.tvSecondaryPairing);
        tvPrimaryDescription = myView.findViewById(R.id.tvPrimaryDescription);
        ivPairing = myView.findViewById(R.id.ivPairing);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Results");
        // Inflate the layout for this fragment
        return myView;
    }


    @Override
    public void sendRecognition(String data) {
        if (data != null) {
            Log.e("Something", (myView == null) + "");
            tvAnalysis.setText("Determining pairings...");
            cvResults.setVisibility(View.VISIBLE);
            tvResults.setText(data);
        }
    }

    @Override
    public void sendPairing(String primary, String url, String data) {
        if (data != null) {
            if (primary.equals("")) {
                tvPrimaryPairing.setText("Water");
                tvPrimaryDescription.setText("We don't have any recommendations for this item. However, hydration is key so drink a glass of water! \n\nHelp us improve our results by suggesting beverages");
                tvSecondaryPairing.setVisibility(View.GONE);
                new ImageLoadTask("https://media.istockphoto.com/photos/water-pitcher-and-glass-picture-id467721386?k=6&m=467721386&s=612x612&w=0&h=wdH22KtWqLEdvN0d4XQi62jQToBbBV6UzDLFQHjFspw=", ivPairing).execute();
            } else {
                tvPrimaryPairing.setText(primary);
                tvSecondaryPairing.setText(data);
                Log.e("URL", url);
                new ImageLoadTask(url, ivPairing).execute();
            }
            cvPairings.setVisibility(View.VISIBLE);
            tvAnalysis.setVisibility(View.GONE);
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}
