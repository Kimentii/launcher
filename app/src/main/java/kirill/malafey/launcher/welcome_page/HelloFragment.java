package kirill.malafey.launcher.welcome_page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kirill.malafey.launcher.R;

public class HelloFragment extends Fragment {

    public static HelloFragment newInstance() {
        HelloFragment fragment = new HelloFragment();
        return fragment;
    }

    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hello, container, false);
    }
}