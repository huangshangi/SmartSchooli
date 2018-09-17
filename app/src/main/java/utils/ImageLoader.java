package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/*图片加载类,使用方法：ImageLoader.getInstance().loadingImage(path,imageView)
    path代表图片的路径,imageView代表要设置图片的控件
    注意：对于在手机内存中的图片，该类已具备压缩图片功能；对于在网络上的图片，该类并不具备压缩功能，建议再将图片
        上传至网络的时候将图片压缩，以减少下载消耗的流量
*/
public class ImageLoader {
    private Semaphore threadPoolSemaphore;//线程池许可证
    private Semaphore listSemaphore;//队列许可证

    public static ImageLoader imageLoader;
    private LruCache<String,Bitmap> lruCache;
    private ExecutorService mThreadpool;
    private Thread thread_main;//后台轮询线程，一直运行
    private Handler handler_main;
    private LinkedList<Runnable>list;//盛放任务的队列

    private Handler UIhandler;//线程更新专用

    private ImageLoader(){
        threadPoolSemaphore=new Semaphore(3);
        listSemaphore=new Semaphore(0);
        list=new LinkedList<Runnable>();
        //初始化缓冲池
        int memory=(int)Runtime.getRuntime().maxMemory();
        int cachememory=memory/6;
        lruCache=new LruCache<String,Bitmap>(cachememory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };

        //初始化线程池
        mThreadpool= Executors.newFixedThreadPool(3);

        //后台轮询线程
        thread_main=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler_main=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                            mThreadpool.execute(getTask());
                        try {
                            threadPoolSemaphore.acquire();
                        }catch (Exception e){

                        }


                    }
                };
                listSemaphore.release();
                Looper.loop();

            }
        });
        thread_main.start();
    }

    //用于获取ImageLoader实例
    public static ImageLoader getInstance(){
        if(imageLoader==null){
            synchronized (ImageLoader.class){
                if(imageLoader==null){
                    imageLoader=new ImageLoader();
                }
            }
        }
        return imageLoader;
    }

    private  Runnable getTask(){
        if(list.size()==0)
            return new Runnable() {
                @Override
                public void run() {

                }
            };
        return list.removeLast();
    }

    //用于给imageView设置图片
    public void loadImage(final String path,final ImageView imageView){
        imageView.setTag(path);
        if(UIhandler==null){
            UIhandler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    ImageHolder imageHolder=(ImageHolder)msg.obj;
                    Bitmap b=imageHolder.bitmap;
                    ImageView iv=imageHolder.imageView;
                    String p=imageHolder.path;
                    if(iv.getTag().toString().equals(p)){
                        iv.setImageBitmap(b);
                    }
                }
            };
        }
        Bitmap bitmap=getBitmapFromCache(path);
        if(bitmap==null){
            //根据路径获取图片信息
            addTask(new Runnable(){
                @Override
                public void run(){
                    ImageSize imageSize=getImageViewSize(imageView);
                    Bitmap b;

                    //判断图片在手机内存中还是网络上
                    if(path.contains("http")){
                        b=getBitmapFromInet(path);
                    }else {
                       b = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);
                    }
                    addBitmapToCache(path,b);
                    refreshBitmap(path,b,imageView);
                    threadPoolSemaphore.release();
                }
            });

        }else{
            refreshBitmap(path,bitmap,imageView);
        }
    }


    //刷新图片
    private void refreshBitmap(String path,Bitmap b,ImageView imageView){
        Message message=Message.obtain();
        ImageHolder imageHolder=new ImageHolder();
        imageHolder.path=path;
        imageHolder.bitmap=b;
        imageHolder.imageView=imageView;
        message.obj=imageHolder;
        UIhandler.sendMessage(message);

    }

    //将任务添加到线程池
    private synchronized void addTask(Runnable runnable){
        list.add(runnable);
        if(handler_main==null){
            try {

                listSemaphore.acquire();
            }catch (Exception e) {

            }}
        handler_main.sendEmptyMessage(0x110);
    }
    //将照片加载到内存
    private void addBitmapToCache(String path,Bitmap bitmap){
        if(getBitmapFromCache(path)==null)
            if(bitmap!=null)
                lruCache.put(path,bitmap);
    }

    //从手机中得到图片
    private Bitmap decodeSampledBitmapFromPath(String path,int reqwidth,int reqheight){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);

        options.inSampleSize=caculateInSampleSize(options,reqwidth,reqheight);
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFile(path,options);
    }

    //从网络获取图片
    private  Bitmap getBitmapFromInet(String url){
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection=(HttpURLConnection)url1.openConnection();
            connection.setConnectTimeout(5*1000);
            connection.setReadTimeout(5*1000);

            return BitmapFactory.decodeStream(connection.getInputStream());
        }catch (Exception e){

        }
        return null;
    }



    //获取图片的压缩比例
    private int caculateInSampleSize(BitmapFactory.Options options,int reqwidth,int reqheight){
        int width=options.outWidth;
        int height=options.outHeight;
        int inSampleSize=1;
        if(height>reqheight||width>reqwidth){
            int widthRadio=Math.round(width*1.0f/reqwidth);
            int heightRadio=Math.round(height*1.0f/reqheight);
            inSampleSize=Math.max(widthRadio,heightRadio);
        }
        return inSampleSize;
    }
    //获取图片需要的大小
    private ImageSize getImageViewSize(ImageView imageView){
        ImageSize imageSize=new ImageSize();
        DisplayMetrics displayMetrics=imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp=imageView.getLayoutParams();
        int width=imageView.getWidth();
        if(width<=0){
            width=lp.width;
        }
        if(width<=0){
            width=getImageViewFieldValue(imageView,"mMaxWidth");
        }
        if(width<=0){
            width=displayMetrics.widthPixels;
        }

        int height=imageView.getHeight();
        if(height<=0){
            height=lp.height;
        }

        if(height<=0){
            height=getImageViewFieldValue(imageView,"mMaxHeight");

        }
        if(width<=0){
            height=displayMetrics.heightPixels;
        }

        imageSize.width=width;
        imageSize.height=height;
        return imageSize;
    }


    //根据反射取得imageview最大尺寸
    private int getImageViewFieldValue(ImageView imageView,String value){
        int result=0;
        try{
            Field field=ImageView.class.getDeclaredField(value);
            field.setAccessible(true);
            int fieldValue=field.getInt(imageView);
            if(fieldValue>=0&&fieldValue<Integer.MAX_VALUE){
                result=fieldValue;
            }
        }catch (Exception e){

        }
        return result;
    }


    //从内存中取得bitmap
    private Bitmap getBitmapFromCache(String path){
        return lruCache.get(path);
    }

    class ImageSize{
        int width;
        int height;
    }

    class ImageHolder{
        Bitmap bitmap;
        String path;
        ImageView imageView;
    }
}
