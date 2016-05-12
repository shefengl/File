package com.example.q.filescanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.yalantis.phoenix.PullToRefreshView;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Load Settings
    private Context context;
    private Activity activity;

    //    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView freRecyclerView;
    private PullToRefreshView pullToRefreshView;
    private PullToRefreshView frePullToRefreshView;
    private ProgressWheel progressWheel;
    private ProgressWheel freProgressWheel;

    final String loadingLabel = "...";
    private AppAdapter appAdapter;
    private AppAdapter freAppAdapter;

    private static VerticalRecyclerViewFastScroller fastScroller;
    private static VerticalRecyclerViewFastScroller frefastScroller;
    private static LinearLayout noResults;
    private static LinearLayout freNoResults;
    // General variables
    private List<File> appList;
    String apps;

    private File file;
    private List<String> myList;
    private List<Long> myListSize;
    private List<String> mFreList;
    private List<Long> mFreNumList;
    private Long avgSize;
    private HashMap mExtMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.activity = this;
        this.context = this;

        checkAndAddPermissions(activity);

        recyclerView = (RecyclerView) findViewById(R.id.appList);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        progressWheel = (ProgressWheel) findViewById(R.id.progress);
        noResults = (LinearLayout) findViewById(R.id.noResults);

        pullToRefreshView.setEnabled(false);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        freRecyclerView = (RecyclerView) findViewById(R.id.appList2);
        frePullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh2);
        frefastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller2);
        freProgressWheel = (ProgressWheel) findViewById(R.id.progress2);
        freNoResults = (LinearLayout) findViewById(R.id.noResults2);

        pullToRefreshView.setEnabled(false);

        freRecyclerView.setHasFixedSize(true);
        LinearLayoutManager frelinearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        freRecyclerView.setLayoutManager(frelinearLayoutManager);


        mExtMap = new HashMap();

        //init listview


        if (appAdapter != null) {
            apps = Integer.toString(appAdapter.getItemCount());
        } else {
            apps = loadingLabel;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, "Share");
                intent.setType("application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, String.format(getResources().getString(R.string.send_to), "File Scanner")));
            }
        });

        FloatingActionButton start = (FloatingActionButton) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotification();
                progressWheel.setVisibility(View.VISIBLE);

                new getScannedFiles().execute();

            }
        });
        FloatingActionButton stop = (FloatingActionButton) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               progressWheel.setVisibility(View.GONE);

                new getScannedFiles().cancel(true);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mFreNumList != null && myListSize !=null && mFreList != null && avgSize != null && myList != null) {
            List<Integer> newList = new ArrayList<Integer>(myListSize.size());
            for (Long myInt : myListSize) {
                newList.add((int) (long) myInt);
            }

            List<Integer> newPreList = new ArrayList<>(mFreNumList.size());
            for (Long myInt : mFreNumList) {
                newPreList.add((int) (long) myInt);
            }
            outState.putStringArrayList("largeFile", (ArrayList<String>) myList);
            outState.putIntegerArrayList("size", (ArrayList<Integer>) newList);
            outState.putIntegerArrayList("FreNum", (ArrayList<Integer>) newPreList);
            outState.putStringArrayList("FreFile", (ArrayList<String>) mFreList);
            outState.putLong("avgSize", avgSize);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


            myList = savedInstanceState.getStringArrayList("largeFile");
            mFreList= savedInstanceState.getStringArrayList("FreFile");
            ArrayList<Integer> list = savedInstanceState.getIntegerArrayList("size");
            ArrayList<Integer> freList = savedInstanceState.getIntegerArrayList("FreNum");
            myListSize = new ArrayList<Long>();
            mFreNumList = new ArrayList<Long>();
            if(list != null && freList != null) {
                for (Integer frenum : freList) {
                    mFreNumList.add((long) frenum);
                }

                for (Integer size : list) {
                    myListSize.add((long) size);
                }
            }


            avgSize = savedInstanceState.getLong("avgSize");

        if(mFreNumList != null && myListSize !=null && mFreList != null && avgSize != null && myList != null) {
            showview();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new getScannedFiles().cancel(true);
    }

    private void checkAndAddPermissions(Activity activity) {
        UtilsApp.checkPermissions(activity);
    }

    class getScannedFiles extends AsyncTask<Void, String, Void> {
        private Integer totalFiles;
        private Integer actualApps;

        public getScannedFiles() {
            actualApps = 0;
            avgSize = 0l;

            appList = new ArrayList<>();
            myList = new ArrayList<String>();
            myListSize = new ArrayList<Long>();
            mFreList = new ArrayList<>();
            mFreNumList = new ArrayList<>();
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(Void... params) {


            String root_sd = Environment.getExternalStorageDirectory().toString();
            Log.e("file directory is : ", root_sd);
            file = new File(root_sd);
            File list[] = file.listFiles();

            String ext = "";
//            String s = file.getName();

            Log.e("extension is : ", ext);


            for( int i=0; i< list.length; i++)
            {
                appList.add(list[i]);
                myList.add(list[i].getName());
                avgSize += list[i].length();


                int num = list[i].getName().lastIndexOf('.');

                if (num > 0 && num < list[i].getName().length() - 1) {
                    ext = list[i].getName().substring(num + 1).toLowerCase();
                }

                if (mExtMap.containsKey(ext)) {
                    mExtMap.put(ext,(int)mExtMap.get(ext) + 1);
                } else {
                    mExtMap.put(ext, 1);
                }

                Log.e("file size is : ", list[i].getName() + list[i].length());
                Log.e("The average size is : ", ""+avgSize);
                actualApps++;
            }


            List<Map.Entry<String,Integer>> sort = new ArrayList<>(mExtMap.entrySet());


            Collections.sort(sort,new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> a,
                                   Map.Entry<String, Integer> b) {
                    return b.getValue() - a.getValue();
                }
            });

            int len = sort.size()>5 ? 5:sort.size();
            for(int i = 0; i <len;i++){
                mFreList.add("."+sort.get(i).getKey());
                mFreNumList.add(Long.valueOf(sort.get(i).getValue()));

            }

            Log.e("Sorted extension map is : ", ""+ sort);

            Collections.sort(appList, new Comparator<File>() {
                @Override
                public int compare(File p1, File p2) {
                    Long size1 = p1.length();
                    Long size2 = p2.length();
                    return size2.compareTo(size1);
                }
            });
//            mExtMap

            avgSize = avgSize/myList.size();
            myList.clear();
            for(int count = 0; count<10; count++){
                myList.add(appList.get(count).getName());
                myListSize.add(appList.get(count).length());
            }

            totalFiles = myList.size();
            Log.e("total files is : ", ""+totalFiles);
            publishProgress(Double.toString((actualApps * 100) / totalFiles));

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);

           progressWheel.setProgress(Float.parseFloat(progress[0]));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            showview();
        }

        private String getFileExtension(String file){
            int dot = 0;
            for(int i = 0;i<file.length()-1;i++){
                if(file.charAt(file.length()-(1+i)) == '.'){
                    dot = i;
                    break;
                }
            }
            return file.substring(file.length()-(dot+1),file.length());
        }
    }

    private void setPullToRefreshView(final PullToRefreshView pullToRefreshView) {
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                appAdapter.clear();
                recyclerView.setAdapter(null);
                new getScannedFiles().execute();

                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void setFrePullToRefreshView(final PullToRefreshView pullToRefreshView) {
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                freAppAdapter.clear();
                freRecyclerView.setAdapter(null);
                new getScannedFiles().execute();

                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void showview(){
        appAdapter = new AppAdapter(myList,myListSize, avgSize, context);

        freAppAdapter = new AppAdapter(mFreList,mFreNumList,avgSize,context);

        fastScroller.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(appAdapter);
        pullToRefreshView.setEnabled(true);
        progressWheel.setVisibility(View.GONE);

        frefastScroller.setVisibility(View.VISIBLE);
        freRecyclerView.setAdapter(freAppAdapter);
        frePullToRefreshView.setEnabled(true);
        freProgressWheel.setVisibility(View.GONE);

        fastScroller.setRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(fastScroller.getOnScrollListener());

        frefastScroller.setRecyclerView(freRecyclerView);
        freRecyclerView.setOnScrollListener(frefastScroller.getOnScrollListener());

        setPullToRefreshView(pullToRefreshView);

        setFrePullToRefreshView(frePullToRefreshView);
        if (appAdapter != null) {
            apps = Integer.toString(appAdapter.getItemCount());
        } else {
            apps = loadingLabel;
        }
        recyclerView.setAdapter(appAdapter);
    }

    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }

    private void setNotification() {
        Intent notificationIntent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this)
                        .setContentTitle(getResources().getString(R.string.notification))
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getResources().getString(R.string.notification)))
                        .setContentText(getResources().getString(R.string.notification));
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

}
