package com.example.q.filescanner;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> implements Filterable {
    // Load Settings
    private AppPreferences appPreferences;

    // AppAdapter variables
    private List<String> fileList;
    private List<String> fileListSearch;
    private List<Long> fileSize;
    private Long avgSize;
    private Context context;

    public AppAdapter(List<String> fileList, List<Long> fileSize, Long avgSize, Context context) {
        this.fileList = fileList;
        this.fileSize = fileSize;
        this.context = context;
        this.avgSize = avgSize;
        this.appPreferences = AppManager.getAppPreferences();
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void clear() {
        fileList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(AppViewHolder appViewHolder, int i) {
        if(i<10){
            String appInfo = fileList.get(i).toString();
            appViewHolder.vName.setText(appInfo);
            appViewHolder.vSize.setText("File size : " + fileSize.get(i).toString());
           appViewHolder.vAvgSize.setText("The Average Size : " + avgSize);

        }

    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<String> results = new ArrayList<>();
                if (fileListSearch == null) {
                    fileListSearch = fileList;
                }
                if (charSequence != null) {
                    if (fileListSearch != null && fileListSearch.size() > 0) {
                        for (final String appInfo : fileListSearch) {
                            if (appInfo.toLowerCase().contains(charSequence.toString())) {
                                results.add(appInfo);
                            }
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count > 0) {
                } else {
                }
                fileList = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View appAdapterView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_layout, viewGroup, false);
        if(i<10){
            return new AppViewHolder(appAdapterView);
        }
        return null;
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vSize;
        protected TextView vAvgSize;
        protected CardView vCard;

        public AppViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.txtName);
            vSize = (TextView) v.findViewById(R.id.txtSize);
            vAvgSize = (TextView) v.findViewById(R.id.txtavgsize);
            vCard = (CardView) v.findViewById(R.id.app_card);

        }
    }

}
