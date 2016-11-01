package demos.android.com.craneo.demorestfulwebservices.interfaces;

import java.util.List;

import demos.android.com.craneo.demorestfulwebservices.model.Flower;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by crane on 10/31/2016.
 */

interface FlowersAPI {

    @GET("/feeds/flowers.json")
    public Call<List<Flower>> getFeed();
}
