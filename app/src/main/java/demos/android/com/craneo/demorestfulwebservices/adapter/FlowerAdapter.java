package demos.android.com.craneo.demorestfulwebservices.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import demos.android.com.craneo.demorestfulwebservices.MainActivity;
import demos.android.com.craneo.demorestfulwebservices.R;
import demos.android.com.craneo.demorestfulwebservices.model.Flower;

/**
 * Created by crane on 10/30/2016.
 */

public class FlowerAdapter extends ArrayAdapter<Flower> {

    private Context context;
    private List<Flower> flowerList;

    private LruCache<Integer, Bitmap> imageCache;

    public FlowerAdapter(Context context, int resource, List<Flower> objects){
        super(context, resource, objects);
        this.context = context;
        this.flowerList = objects;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent){

        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_flower, parent, false);

        Flower flower = flowerList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(flower.getName());

        Bitmap bitmap = imageCache.get(flower.getProductId());
        if (bitmap != null){
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(flower.getBitmap());
        }else{
            FlowerAndView container = new FlowerAndView();
            container.flower = flower;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return  view;
    }

    class FlowerAndView{
        public Flower flower;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<FlowerAndView, Void, FlowerAndView>{

        @Override
        protected FlowerAndView doInBackground(FlowerAndView... params) {
            FlowerAndView container = params[0];
            Flower flower = container.flower;

            try{
                String imageUrl = MainActivity.PHOTOS_BASE_URL + flower.getPhoto();
                InputStream in  = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                flower.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FlowerAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
            image.setImageBitmap(result.bitmap);
//            result.flower.setBitmap(result.bitmap);
            imageCache.put(result.flower.getProductId(), result.bitmap);
        }
    }
}
