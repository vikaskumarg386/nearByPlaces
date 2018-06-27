package com.valuecomvikaskumar.searchplaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.valuecomvikaskumar.searchplaces.model.MyPlaceDetail;
import com.valuecomvikaskumar.searchplaces.model.Photos;
import com.valuecomvikaskumar.searchplaces.model.Results;
import com.valuecomvikaskumar.searchplaces.remote.IGoogleService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button button;
    private ImageView imageView;
    private IGoogleService service;
    private TextView placeName;
    private TextView openingH;
    private TextView closingHr;
    private TextView place_address;
    private RatingBar ratingBar;

    MyPlaceDetail detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        service=Common.getGoogleService();
        placeName=findViewById(R.id.placeName);
        ratingBar=findViewById(R.id.ratingBar);
        openingH=findViewById(R.id.openingH);
        place_address=findViewById(R.id.place_detail);
        placeName=findViewById(R.id.placeName);


        button=findViewById(R.id.button);
        imageView=findViewById(R.id.imageView);
        ProgressDialog dialog=new ProgressDialog(MainActivity.this);
        dialog.setMessage("wait");
        dialog.show();
        if(Common.currentResult!=null&&!TextUtils.isEmpty(String.valueOf(Common.currentResult))&&Common.currentResult.getPhotos()!=null){
            Picasso.with(MainActivity.this).load(getUrl(Common.currentResult.getPhotos()[0].getPhoto_reference())).placeholder(R.drawable.ic_image_black_24dp).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(MainActivity.this).load(getUrl(Common.currentResult.getPhotos()[0].getPhoto_reference())).placeholder(R.drawable.ic_image_black_24dp).into(imageView);

                }
            });
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if(Common.currentResult.getRating()!=null&&!TextUtils.isEmpty(Common.currentResult.getRating())){
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        }
        else {
            ratingBar.setVisibility(View.INVISIBLE);
        }
        if(Common.currentResult.getOpening_hours()!=null){

            openingH.setText("open now: "+String.valueOf(Common.currentResult.getOpening_hours().getOpen_now()));
        }

        service.getPlaceDetail(getPlaceDetailUrl(Common.currentResult.getPlace_id())).enqueue(new Callback<MyPlaceDetail>() {
            @Override
            public void onResponse(Call<MyPlaceDetail> call, Response<MyPlaceDetail> response) {

                detail=response.body();
                placeName.setText(detail.getResult().getName());
                place_address.setText(detail.getResult().getFormatted_address());

            }

            @Override
            public void onFailure(Call<MyPlaceDetail> call, Throwable t) {

            }
        });
        dialog.dismiss();
    }

    private String getPlaceDetailUrl(String place_id) {

        StringBuilder builder=new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        builder.append("placeid="+place_id);
        builder.append("&key=AIzaSyDIsZdb4NvUnv1er3SenoAKvi9kcSDMci8");
        return builder.toString();
    }

    private String getUrl(String ref) {

        StringBuilder builder=new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        builder.append("maxwidth=1000");
        builder.append("&photoreference="+ref);
        builder.append("&key=AIzaSyDIsZdb4NvUnv1er3SenoAKvi9kcSDMci8");
        return builder.toString();
    }




}
