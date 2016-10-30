package demos.android.com.craneo.demorestfulwebservices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import demos.android.com.craneo.demorestfulwebservices.httpclient.HttpManager;
import demos.android.com.craneo.demorestfulwebservices.model.Flower;
import demos.android.com.craneo.demorestfulwebservices.parsers.FlowerJSONParser;

public class MainActivity extends AppCompatActivity {
    TextView output;
    ProgressBar pb;
    List<MyTask> tasks;

    List<Flower> flowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.textView);
        output.setMovementMethod(new ScrollingMovementMethod());

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
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
                requestData("http://services.hanselandpetal.com/secure/flowers.json");
            }else{
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    private void updateDisplay() {
        if(flowers != null){
            for (Flower flower: flowers){
                output.append(flower.getName() + "\n");
            }
        }
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


    private class MyTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
//            updateDisplay("Starting task");
            if (tasks.size()==0){
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... strings) {
            //Send the request to public static String getData(String uri, String userName, String password){
            String content = HttpManager.getData(strings[0], "feeduser", "feedpassword");
            return content;
        }

        @Override
        protected void onPostExecute(String s) {

            tasks.remove(this);
            if (tasks.size()==0){
                pb.setVisibility(View.INVISIBLE);
            }

            if (s == null){
                Toast.makeText(MainActivity.this, "Can't connect to we service", Toast.LENGTH_LONG).show();
                return;
            }

            flowers = new FlowerJSONParser().parseFeed(s);
            updateDisplay();


        }

        @Override
        protected void onProgressUpdate(String... values) {

        }//            updateDisplay(values[0]);
    }
}
