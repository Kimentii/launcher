package kirill.malafey.launcher.launcher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;
import java.util.Observable;

import kirill.malafey.launcher.App;
import kirill.malafey.launcher.AppSettings;
import kirill.malafey.launcher.AppStore;
import kirill.malafey.launcher.R;

public class LauncherActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private PackageManager packageManager;
    private Observable appsListReadyObservable;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, LauncherActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppSettings.getInstance(getApplicationContext()).setFirstStart(false);
        setTheme(AppSettings.getInstance(this).getCurrentThemeResource());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        packageManager = getPackageManager();
        setupDrawerContent(navigationView);
        // Without overriding notifyObserver method will not work.
        appsListReadyObservable = new Observable() {
            @Override
            public void notifyObservers() {
                super.setChanged();
                super.notifyObservers();
            }
        };
        (new AppLoader(appsListReadyObservable)).execute();
        Fragment fragment = GridLauncherFragment.newInstance(appsListReadyObservable);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_launcher_activity, fragment).commit();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nv_item_grid:
                fragmentClass = GridLauncherFragment.class;
                break;
            case R.id.nv_item_list:
                fragmentClass = ListLauncherFragment.class;
                break;
            case R.id.nv_item_profile:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.nv_item_settings:
                fragmentClass = SettingsFragment.class;
                break;
            default:
                fragmentClass = GridLauncherFragment.class;
        }

        try {
            if (fragmentClass.equals(GridLauncherFragment.class)) {
                fragment = GridLauncherFragment.newInstance(appsListReadyObservable);
            } else if (fragmentClass.equals(ListLauncherFragment.class)) {
                fragment = ListLauncherFragment.newInstance(appsListReadyObservable);
            } else {
                fragment = (Fragment) fragmentClass.newInstance();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_launcher_activity, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }

    private class AppLoader extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = null;
        private Observable appsListReadyObservable;

        AppLoader(Observable appsListReadyObservable) {
            this.appsListReadyObservable = appsListReadyObservable;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<ApplicationInfo> appsInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : appsInfo) {
                try {
                    if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                        App app = new App();
                        app.setAppName(info.loadLabel(packageManager).toString());
                        app.setPackageName(info.packageName);
                        app.setAppIcon(info.loadIcon(packageManager));
                        app.setInstallationDateMS(getApplicationContext().getPackageManager()
                                .getPackageInfo(info.packageName, 0).firstInstallTime);
                        AppStore.getInstance().addApp(app);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "Notify observers");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            appsListReadyObservable.notifyObservers();
            progressDialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LauncherActivity.this, null, "Loading apps info...");
            super.onPreExecute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
