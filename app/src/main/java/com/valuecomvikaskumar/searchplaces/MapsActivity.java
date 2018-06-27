package com.valuecomvikaskumar.searchplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.test.mock.MockPackageManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.valuecomvikaskumar.searchplaces.model.MyPlaces;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
                          GoogleApiClient.ConnectionCallbacks,
                          GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private MyPlaces currentPlace;
    private GoogleMap mMap;
    private String lat,lng;
    private Button search;
    private AutoCompleteTextView editText;
    private static final int REQUEST_CODE_PERMISSION=2;
    private String mPermission= Manifest.permission.ACCESS_FINE_LOCATION;
    private double latitude;
    private double longitude;
    private Location lastLocation;
    private Marker marker;
    private String placeType;
    private LocationRequest locationRequest;
    IGoogleService googleService;
    private GoogleApiClient mGoogleApiClient;
    private final int MY_PERMISSION_CODE=1000;

    private String places[]={"accounting",
            "airport",
            "amusement_park",
            "aquarium",
            "art_gallery",
            "atm",
            "bakery",
            "bank",
            "bar",
            "beauty_salon",
            "bicycle_store",
            "book_store",
            "bowling_alley",
            "bus_station",
            "cafe",
            "campground",
            "car_dealer",
            "car_rental",
            "car_repair",
            "car_wash",
            "casino",
            "cemetery",
            "church",
            "city_hall",
            "clothing_store",
            "convenience_store",
            "courthouse",
            "dentist",
            "department_store",
            "doctor",
            "electrician",
            "electronics_store",
            "embassy",
            "fire_station",
            "florist",
            "funeral_home",
            "furniture_store",
            "gas_station",
            "gym",
            "hair_care",
            "hardware_store",
            "hindu_temple",
            "home_goods_store",
            "hospital",
            "insurance_agency",
            "jewelry_store",
            "laundry",
            "lawyer",
            "library",
            "liquor_store",
            "local_government_office",
            "locksmith",
            "lodging",
            "meal_delivery",
            "meal_takeaway",
            "mosque",
            "movie_rental",
            "movie_theater",
            "moving_company",
            "museum",
            "night_club",
            "painter",
            "park",
            "parking",
            "pet_store",
            "pharmacy",
            "physiotherapist",
            "plumber",
            "police",
            "post_office",
            "real_estate_agency",
            "restaurant",
            "roofing_contractor",
            "rv_park",
            "school",
            "shoe_store",
            "shopping_mall",
            "spa",
            "stadium",
            "storage",
            "store",
            "subway_station",
            "supermarket",
            "synagogue",
            "taxi_stand",
            "train_station",
            "transit_station"
            ,"travel_agency"
            ,"veterinary_care"
            ,"zoo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editText=findViewById(R.id.editText);
        search=findViewById(R.id.button2);
        ArrayAdapter adapter=new ArrayAdapter(MapsActivity.this,android.R.layout.simple_list_item_1,places);
        editText.setAdapter(adapter);
        googleService=Common.getGoogleService();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.hospitle:{
                        placeType="hospital";
                        nearByPlace("hospital");
                        break;
                    }
                    case R.id.school:{
                        placeType="school";
                        nearByPlace("school");
                        break;
                    }
                    case R.id.restaurent:{
                        placeType="restaurant";
                        nearByPlace("restaurant");
                        break;
                    }
                    case R.id.tample:{
                        placeType="temple";
                        nearByPlace("hindu_temple");
                        break;
                    }
                    case R.id.hotel:{
                        placeType="hotel";
                        nearByPlace("hotel");
                        break;
                    }


                }

                return true;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(editText.getText())){
                   // sss(editText.getText().toString());
                    placeType=editText.getText().toString();
                    nearByPlace(editText.getText().toString());
                }
            }
        });

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                placeType=editText.getText().toString();
                nearByPlace(editText.getText().toString());
            }
        });



    }





    private void nearByPlace(String place) {
        mMap.clear();
        String url=getUrl(latitude,longitude,place);

        googleService.getNearByPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                currentPlace=response.body();
                if(response.isSuccessful()){
                    for(int i=0;i<response.body().getResults().length;i++){

                        MarkerOptions markerOptions=new MarkerOptions();

                        Results results=response.body().getResults()[i];
                        double lat=Double.parseDouble(results.getGeometry().getLocation().getLat());
                        double lng=Double.parseDouble(results.getGeometry().getLocation().getLng());
                        Log.i("lat",String.valueOf(lat));
                        Log.i("lng",String.valueOf(lng));
                        String placeName=results.getName();
                        String vicinity=results.getVicinity();
                        LatLng latLng=new LatLng(lat,lng);
                        markerOptions.position(latLng)
                                .title(placeName);
                        if(placeType.equals("hospital")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital));
                              }
                        else if(placeType.equals("school")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_school));
                        }else if(placeType.equals("temple")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.temple));
                        }else if(placeType.equals("hotel")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_hotel));
                        }else if(placeType.equals("restaurant")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_restaurant));
                        }
                        else markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                        markerOptions.snippet(String.valueOf(i));
                        mMap.addMarker(markerOptions);


                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });
    }

    private String getUrl(double latitude, double longitude, String place) {

        StringBuilder builder=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        builder.append("location="+latitude+","+longitude);
        builder.append("&radius="+1000);
        builder.append("&type="+place) ;
        builder.append("&sensor=true&key=AIzaSyDIsZdb4NvUnv1er3SenoAKvi9kcSDMci8");
        Log.i("Url",builder.toString());
        return builder.toString();
    }

    private boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                },MY_PERMISSION_CODE);
            }
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case MY_PERMISSION_CODE:{

                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                        if(mGoogleApiClient==null)
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }

                }
                else {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
       {
           if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

               buildGoogleApiClient();
               mMap.setMyLocationEnabled(true);
           }

       }
       else {
           buildGoogleApiClient();
           mMap.setMyLocationEnabled(true);
       }

       mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
           @Override
           public boolean onMarkerClick(Marker marker) {

               Common.currentResult=currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
               startActivity(new Intent(MapsActivity.this,MainActivity.class));
               return true;
           }
       });
    }

    private synchronized void buildGoogleApiClient(){

        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest=new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

         LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest,this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
         mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation=location;
        if(marker!=null){
            marker.remove();
        }
        latitude=location.getLatitude();
        longitude=location.getLongitude();

        LatLng latLng=new LatLng(latitude,longitude);
        MarkerOptions markerOptions=new MarkerOptions()
                .position(latLng)
                .title("Your Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker=mMap.addMarker(markerOptions);
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if(mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);

        }

    }



    void sss(String pin){
        new GetCoordinates().execute(pin.replace(" ","+"));

    }

    private  class GetCoordinates extends AsyncTask<String,Void,String> {

        ProgressDialog dialog=new ProgressDialog(MapsActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait");
            dialog.setCanceledOnTouchOutside(false);
            //dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject= null;
            try {
                jsonObject = new JSONObject(s);
                lat=((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                lng=((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                Log.i("lat",lat);
                Log.i("lng",lng);

                if(marker!=null)
                    marker.remove();
                LatLng latLng=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                MarkerOptions markerOptions=new MarkerOptions()
                        .position(latLng)
                        .title("Your Position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                marker=mMap.addMarker(markerOptions);
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings) {

            String response;
            try {
                String address=strings[0];
                HttpDataHandler http=new HttpDataHandler();
                String url=String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s",address);
                response=http.getHttpData(url);
                return response;
            }catch (Exception e){

            }
            return null;
        }
    }

}
