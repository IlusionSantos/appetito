package com.example.appetito;

import android.accounts.Account;
import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

public class GetOAuthToken extends AsyncTask<Void,Void,Void> {
    Activity activity;
    Account account;
    int mRequestCode;
    String mScope;

    GetOAuthToken(Activity activity, Account account, String scope, int requestCode){
        this.activity = activity;
        this.account = account;
        this.mRequestCode = requestCode;
        this.mScope = scope;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            String token = fetchToken();
            if (token != null){
                ((MainActivity)activity).onTokenReceived(token);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    protected String fetchToken()throws IOException{
        String accessToken;
        try{
            accessToken = GoogleAuthUtil.getToken(activity,account,mScope);
            GoogleAuthUtil.clearToken(activity,accessToken);
            accessToken = GoogleAuthUtil.getToken(activity,account,mScope);
            return accessToken;
        }
        catch (UserRecoverableAuthException userRecoverableException){
            activity.startActivityForResult(userRecoverableException.getIntent(),mRequestCode);
        }
        catch (GoogleAuthException fatalException){
            fatalException.printStackTrace();
        }
        return null;
    }
}
