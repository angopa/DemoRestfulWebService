package demos.android.com.craneo.demorestfulwebservices;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import demos.android.com.craneo.demorestfulwebservices.adapter.FlowerAdapter;
import demos.android.com.craneo.demorestfulwebservices.httpclient.HttpManager;
import demos.android.com.craneo.demorestfulwebservices.model.Flower;
import demos.android.com.craneo.demorestfulwebservices.parsers.FlowerJSONParser;
import demos.android.com.craneo.demorestfulwebservices.request.RequestPackage;

public class MainActivity extends ListActivity {

    public static final String PHOTOS_BASE_URL =
            "http://services.hanselandpetal.com/photos/";
    ProgressBar pb;
    List<MyTask> tasks;
    List<MySimpleTask> tasksS;
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
        delegate.setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
        tasksS = new ArrayList<>();
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
//                requestData("http://services.hanselandpetal.com/secure/flowers.json");
                requestSimpleData("http://services.hanselandpetal.com/secure/flowers.json");
            }else{
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void requestData(String uri) {
        RequestPackage p = new RequestPackage();
        p.setMethod("GET");
        p.setUri(uri);
        p.setParams("param1", "value1");
        p.setParams("param2", "value2");
        p.setParams("param3", "value3");
        MyTask task = new MyTask();
        task.execute(p);
    }

    private void requestSimpleData(String uri) {
        MySimpleTask task = new MySimpleTask();
        task.execute(uri);
    }

    private void updateDisplay() {
        //Use FlowerAdapter to display data
        FlowerAdapter adapter = new FlowerAdapter(this, R.layout.item_flower, flowers);
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


    private class MyTask extends AsyncTask<RequestPackage, String, List<Flower>>{

        @Override
        protected void onPreExecute() {
            if (tasks.size()==0){
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Flower> doInBackground(RequestPackage... strings) {
            //Send the request to public static String getData(String uri, String userName, String password){
            String content = HttpManager.getData(strings[0]);
            flowers = FlowerJSONParser.parseFeed(content);

            return flowers;
        }

        @Override
        protected void onPostExecute(List<Flower> result) {

            tasks.remove(this);
            if (tasks.size()==0){
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null){
                Toast.makeText(MainActivity.this, "Can't connect to the service", Toast.LENGTH_LONG).show();
                return;
            }
            updateDisplay();
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }
    }

    private class MySimpleTask extends AsyncTask<String, String, List<Flower>>{

        @Override
        protected void onPreExecute() {
            if (tasksS.size()==0){
                pb.setVisibility(View.VISIBLE);
            }
            tasksS.add(this);
        }

        @Override
        protected List<Flower> doInBackground(String... strings) {
            //Send the request to public static String getData(String uri, String userName, String password){
            String content = HttpManager.getDataOkHttpClient(strings[0], "feeduser", "feedpassword");
            Log.d("MySimpleTask", content);
            flowers = FlowerJSONParser.parseFeed(content);

            return flowers;
        }

        @Override
        protected void onPostExecute(List<Flower> result) {

            tasksS.remove(this);
            if (tasksS.size()==0){
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null){
                Toast.makeText(MainActivity.this, "Can't connect to the service", Toast.LENGTH_LONG).show();
                return;
            }
            updateDisplay();
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }
    }
}
