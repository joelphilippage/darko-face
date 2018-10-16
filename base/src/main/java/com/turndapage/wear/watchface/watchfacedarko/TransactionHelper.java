package com.turndapage.wear.watchface.watchfacedarko;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

/**
 * Created by jpage on 11/20/2017.
 */

public class TransactionHelper {
    public static void SlideIn(Context context, Fragment fragment) {
        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // replace content view with the podcastSearchResults fragment
        fragmentTransaction.setCustomAnimations(R.animator.slide_in, 0);
        fragmentTransaction.add(R.id.root_view, fragment);
        fragmentTransaction.commit();
    }
}
