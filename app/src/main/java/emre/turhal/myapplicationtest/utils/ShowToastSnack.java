package emre.turhal.myapplicationtest.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;


public class ShowToastSnack {
    public static void showToast(Context context, String string, int duration) {
        Toast.makeText(context, string, duration).show();
    }

    public static void showSnackBar(CoordinatorLayout coordinatorLayout, String message, int duration) {
        Snackbar.make(coordinatorLayout, message, duration).show();
    }
}
