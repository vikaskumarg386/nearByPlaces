package com.valuecomvikaskumar.searchplaces.remote;

import com.valuecomvikaskumar.searchplaces.model.MyPlaceDetail;
import com.valuecomvikaskumar.searchplaces.model.MyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by vikas on 23/6/18.
 */

public interface IGoogleService {

    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<MyPlaceDetail> getPlaceDetail(@Url String url);
}
