package demos.android.com.craneo.demorestfulwebservices.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import demos.android.com.craneo.demorestfulwebservices.MainActivity;
import demos.android.com.craneo.demorestfulwebservices.R;
import demos.android.com.craneo.demorestfulwebservices.model.Flower;

/**
 * Created by crane on 10/31/2016.
 */

public class FlowerAdapterRetrofit extends ArrayAdapter<Flower> {

    private Context context;
    private List<Flower> flowerList;
    private LruCache<Integer, Bitmap> imageCache;
    //Volley code
    private RequestQueue queue;

    public FlowerAdapterRetrofit(Context context, int resource, List<Flower> objects){
        super(context, resource, objects);
        this.context = context;
        this.flowerList = objects;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent){

        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_flower, parent, false);

        final Flower flower = flowerList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(flower.getName());

        Bitmap bitmap = imageCache.get(flower.getProductId());
        final ImageView image = (ImageView) view.findViewById(R.id.imageView1);;
        if (bitmap != null){
            image.setImageBitmap(flower.getBitmap());
        }else{
            String imageUrl = MainActivity.PHOTOS_BASE_URL + flower.getPhoto();
            ImageRequest request = new ImageRequest(imageUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            image.setImageBitmap(response);
                            imageCache.put(flower.getProductId(), response);
                        }
                    },
                    80,
                    80,
                    Bitmap.Config.ARGB_4444,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("FlowerAdapter", error.getMessage());
                        }
                    });
            queue.add(request);

        }
        return  view;
    }

    class FlowerAndView{
        public Flower flower;
        public View view;
        public Bitmap bitmap;
    }
}
