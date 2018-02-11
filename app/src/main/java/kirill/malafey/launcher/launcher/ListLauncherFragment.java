package kirill.malafey.launcher.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kirill.malafey.launcher.App;
import kirill.malafey.launcher.AppStore;
import kirill.malafey.launcher.R;

public class ListLauncherFragment extends Fragment {
    private String TAG = "TAG";
    private AppAdapter appAdapter;
    private RecyclerView recyclerView;

    public static ListLauncherFragment newInstance() {
        ListLauncherFragment fragment = new ListLauncherFragment();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_launcher, container, false);
        List<App> list = AppStore.getInstance().getApps();
        Log.d(TAG, "apps list size(ListLauncherFragment): " + list.size());
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI() {
        AppStore appStore = AppStore.getInstance();
        List<App> appsList = appStore.getApps();
        if (appAdapter == null) {
            appAdapter = new AppAdapter(appsList);
            recyclerView.setAdapter(appAdapter);
        } else {
            appAdapter.setApps(appsList);
            appAdapter.notifyDataSetChanged();
        }
    }

    private class AppHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private App app;
        private TextView appNameTextView;
        private TextView appPackageTextView;
        private ImageView appIconImageView;

        public void bindApp(App app, int position) {
            this.app = app;
            Log.d(TAG, "showing list.");
            appNameTextView.setText(app.getAppName());
            appPackageTextView.setText(app.getPackageName());
            appIconImageView.setImageDrawable(app.getAppIcon());
        }


        public AppHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            appNameTextView = itemView.findViewById(R.id.tv_app_name);
            appPackageTextView = itemView.findViewById(R.id.tv_app_package);
            appIconImageView = itemView.findViewById(R.id.iv_app_icon);
        }

        @Override
        public void onClick(View view) {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            startActivity(intent);
        }
    }

    private class AppAdapter extends RecyclerView.Adapter<AppHolder> {
        private List<App> appsList;

        public AppAdapter(List<App> appsList) {
            this.appsList = appsList;
        }

        @Override
        public AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_app, parent, false);
            return new AppHolder(view);
        }

        @Override
        public void onBindViewHolder(AppHolder holder, int position) {
            App app = appsList.get(position);
            holder.bindApp(app, position);
        }

        public void setApps(List<App> appsList) {
            this.appsList = appsList;
        }

        @Override
        public int getItemCount() {
            return appsList.size();
        }
    }
}