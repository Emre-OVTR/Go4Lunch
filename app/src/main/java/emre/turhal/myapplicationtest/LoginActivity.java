package emre.turhal.myapplicationtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

import emre.turhal.myapplicationtest.databinding.ActivityLoginBinding;
import emre.turhal.myapplicationtest.manager.UserManager;


public class LoginActivity extends Activity {

    private final int RC_SIGN_IN = 123;
    private ActivityLoginBinding mBinding;
    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        this.startSignInActivity();


    }


    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.logo_loggin)
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().build()
                ))
                .setIsSmartLockEnabled(false, true)
                .build(), RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void showSnackBar(String message){
        Snackbar.make(mBinding.logActivityCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show();


    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                mUserManager.createUser();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            } else {
                // ERRORS
                if (response == null) {
                    showSnackBar("authentification canceled");
                } else if (response.getError()!= null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                        showSnackBar("error no internet");
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar("unknown error");
                    }
                }
            }
        }
    }




}