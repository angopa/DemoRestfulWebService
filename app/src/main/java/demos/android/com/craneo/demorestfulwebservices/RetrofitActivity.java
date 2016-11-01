package demos.android.com.craneo.demorestfulwebservices;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import demos.android.com.craneo.demorestfulwebservices.adapter.FlowerAdapter;
import demos.android.com.craneo.demorestfulwebservices.adapter.FlowerAdapterRetrofit;
import demos.android.com.craneo.demorestfulwebservices.httpclient.HttpManager;
import demos.android.com.craneo.demorestfulwebservices.interfaces.FlowersAPI;
import demos.android.com.craneo.demorestfulwebservices.model.Flower;
import demos.android.com.craneo.demorestfulwebservices.parsers.FlowerJSONParser;
import demos.android.com.craneo.demorestfulwebservices.request.RequestPackage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitActivity extends ListActivity {
    public static final String PHOTOS_BASE_URL =
            "http://services.hanselandpetal.com/photos/";
    
    private static final String ENDPOINT =
            "http://services.hanselandpetal.com";

    ProgressBar pb;
    List<Flower> flowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        AppCompatCallback callback = new AppCompatCallback() {
            @Override
            public void onSupportActionModeStarted(ActionMode mode) {
            }

            @Override
            public void onSupportActionModeFinished(ActionMode mode) {
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
                return null;
            }
        };

        AppCompatDelegate delegate = AppCompatDelegate.create(this, callback);

        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_retrofit);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_do_task){
            if (isOnline()){
                requestData();
            }else{
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void requestData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        FlowersAPI service = retrofit.create(FlowersAPI.class);

        service.getFeed().enqueue(new Callback<List<Flower>>() {
            @Override
            public void onResponse(Call<List<Flower>> call, Response<List<Flower>> response) {
                flowers = response.body();
                updateDisplay();

            }

            @Override
            public void onFailure(Call<List<Flower>> call, Throwable t) {
                Toast.makeText(RetrofitActivity.this, "Cannot connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

   private void updateDisplay() {
        //Use FlowerAdapter to display data
       FlowerAdapterRetrofit adapter = new FlowerAdapterRetrofit(this, R.layout.item_flower, flowers);
        setListAdapter(adapter);
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }
    }
}
