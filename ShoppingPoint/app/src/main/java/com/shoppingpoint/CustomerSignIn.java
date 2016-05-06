package com.shoppingpoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;

public class CustomerSignIn extends AppCompatActivity {

    private Bundle customerInfoBundle;
    private String username;
    private boolean loginState; // false if customer is not signed in true otherwise
    private ArrayList<CartItem> itemsCartList;
    public static final String TAG = Constants.CUSTOMER_SIGN_IN_ACTIVITY;
    public void setUsername(String name){ this.username = name; }
    public void setLoginState(boolean state){ this.loginState = state; }
    public String getUsername(){ return this.username; }
    public boolean getLoginState(){ return this.loginState; }
    public void setItemsCartList(ArrayList<CartItem> list){ itemsCartList = list;}
    public ArrayList<CartItem> getItemsCartList(){return itemsCartList;}
    public void addToCustomerCartList(CartItem item){
        if(itemsCartList == null) setItemsCartList(new ArrayList<CartItem>());
        CartItem cartItem;
        for(int i = 0; i < itemsCartList.size(); i++) {
            cartItem = itemsCartList.get(i);
            if (cartItem.getImagename().equalsIgnoreCase(item.getImagename())) {
                return;  // so that the item is not added to the list again, only the number of it is increased by one
            }
        }
        getItemsCartList().add(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null){
            setUsername("");
            setLoginState(false); // user initially is not logged in
            customerInfoBundle = getIntent().getExtras();
            if(customerInfoBundle != null && !customerInfoBundle.isEmpty()) {
                String name = customerInfoBundle.getString(Constants.CUSTOMER_USERNAME);
                boolean state = customerInfoBundle.getBoolean(Constants.CUSTOMER_LOGIN_STATE);
                setUsername(name);
                setLoginState(state);
                ArrayList<CartItem> list = customerInfoBundle.getParcelableArrayList(Constants.PARSELABLE_CART_ITEM_LIST);
                setItemsCartList(list);
            }
        }
        if(savedInstanceState != null) {
            String name = savedInstanceState.getString(Constants.CUSTOMER_USERNAME);
            boolean state = savedInstanceState.getBoolean(Constants.CUSTOMER_LOGIN_STATE);
            setUsername(name);
            setLoginState(state);
            itemsCartList = savedInstanceState.getParcelableArrayList(Constants.CUSTOMER_CART_ITEM_LIST);
        }
    }

    public void customerLogIn(View v){
        EditText customerUsername = (EditText) findViewById(R.id.customerUsernameEditText);
        EditText customerPassword = (EditText) findViewById(R.id.customerPasswordEditText);
        String userName = customerUsername.getText().toString();
        String pswd = customerPassword.getText().toString();
        if(userName.equalsIgnoreCase("")){
            Toast.makeText(this, R.string.blank_username, Toast.LENGTH_LONG).show();
            return;
        }
        if(pswd.equalsIgnoreCase("")){
            Toast.makeText(this, R.string.blank_password, Toast.LENGTH_LONG).show();
            return;
        }
        String name = getUsername();
        boolean state = getLoginState();
        if(!name.equalsIgnoreCase("") && state != false){
            Toast.makeText(this, R.string.customer_already_signed_in, Toast.LENGTH_LONG).show();
            return;
        }
        WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, Constants.SIGN_IN_MESSAGE);
        wst.addNameValuePair(Constants.CUSTOMER_USERNAME, userName);
        wst.addNameValuePair(Constants.CUSTOMER_PASSWORD, pswd);
        String sampleURL = Constants.SERVICE_URL + "/customeraccount";
        sampleURL += "/customersignin";
        wst.execute(new String[]{sampleURL});
    }

    public void customerRegistration(View v){
        Intent userAccount = new Intent(getApplicationContext(), CustomerRegistration.class);
        startActivity(userAccount);
    }

    public void handleResponse(String response) {
        try {
            JSONObject jso = new JSONObject(response);
            String userName = jso.getString(Constants.CUSTOMER_USERNAME);
            String pswd = jso.getString(Constants.CUSTOMER_PASSWORD);
            long counter = jso.getLong(Constants.CUSTOMER_COUNTER);

            if(userName.equalsIgnoreCase(Constants.USERNAME_NOT_EXISTS)){
                Toast.makeText(this, R.string.incorrect_username, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(userName.equalsIgnoreCase(Constants.INCORRECT_PASSWORD)){
                Toast.makeText(this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(counter == 100){
                /* Login is successful so put customer info into bundle and ship it to View Items Activity */
                Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                setUsername(userName);
                setLoginState(true); // means that customer is logged in
                String name = getUsername();
                boolean state = getLoginState();
                customerInfoBundle = new Bundle();
                customerInfoBundle.putString(Constants.CUSTOMER_USERNAME, name);
                customerInfoBundle.putBoolean(Constants.CUSTOMER_LOGIN_STATE, state);
                customerInfoBundle.putParcelableArrayList(Constants.PARSELABLE_CART_ITEM_LIST, itemsCartList);
                Intent userAccount = new Intent(getApplicationContext(), ViewItems.class);
                userAccount.putExtras(customerInfoBundle);
                MainActivity.setUsername(name);
                MainActivity.setLoginState(state);
                startActivityForResult(userAccount, 3);
                finish(); // this makes sures that the SignIn Activity is removed from activity stack
                               // so that whenever back is pressed we always will end up going to Main Activity
            }
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage(), e);
        }
    }

    private class WebServiceTask extends AsyncTask<String, Integer, String> {

        public static final int POST_TASK = 1;
        public static final int GET_TASK = 2;
        private static final String TAG = Constants.WEB_SERVICE_TASK;
        private static final int CONN_TIMEOUT = 3000;
        private static final int SOCKET_TIMEOUT = 5000;
        private int taskType = GET_TASK;
        private Context mContext = null;
        private String processMessage = Constants.PROCESSING_MESSAGE;
        private ArrayList<NameValuePair> params = new ArrayList<>();
        private ProgressDialog pDlg = null;
        public WebServiceTask(int taskType, Context mContext, String processMessage) {
            this.taskType = taskType;
            this.mContext = mContext;
            this.processMessage = processMessage;
        }

        public void addNameValuePair(String name, String value) {
            params.add(new BasicNameValuePair(name, value));
        }

        private void showProgressDialog() {
            pDlg = new ProgressDialog(mContext);
            pDlg.setMessage(processMessage);
            pDlg.setProgressDrawable(mContext.getWallpaper());
            pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDlg.setCancelable(false);
            pDlg.show();
        }

        @Override
        protected void onPreExecute() {
            hideKeyboard();
            showProgressDialog();
        }

        protected String doInBackground(String... urls) {
            String url = urls[0];
            String result = "";
            HttpResponse response = doResponse(url);
            if(response == null) {
                return result;
            }
            else {
                try {
                    result = inputStreamToString(response.getEntity().getContent());
                } catch (IllegalStateException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            handleResponse(response);
            pDlg.dismiss();
        }

        // Establish connection and socket (data retrieval) timeouts
        private HttpParams getHttpParams() {
            HttpParams htpp = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);
            return htpp;
        }

        private HttpResponse doResponse(String url) {
            HttpClient httpclient = new DefaultHttpClient(getHttpParams());
            HttpResponse response = null;
            try {
                switch (taskType) {
                    case POST_TASK:
                        HttpPost httppost = new HttpPost(url);
                        httppost.setEntity(new UrlEncodedFormEntity(params));
                        response = httpclient.execute(httppost);
                        break;
                    case GET_TASK:
                        HttpGet httpget = new HttpGet(url);
                        response = httpclient.execute(httpget);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
            return response;
        }

        private String inputStreamToString(InputStream is) {
            String line = "";
            StringBuilder total = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
            return total.toString();
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) CustomerSignIn.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                CustomerSignIn.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String name = getUsername();
        boolean state = getLoginState();
        savedInstanceState.putString(Constants.CUSTOMER_USERNAME, name);
        savedInstanceState.putBoolean(Constants.CUSTOMER_LOGIN_STATE, state);
        savedInstanceState.putParcelableArrayList(Constants.CUSTOMER_CART_ITEM_LIST, itemsCartList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.viewCart).setVisible(false); // menu button is not visible
        menu.findItem(R.id.checkOut).setVisible(false);
        menu.findItem(R.id.log_in).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /* This method ensures that the current state of the cart with all its item is preserved throughout all activities */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        String name = getUsername();
        boolean state = getLoginState();
        intent.putExtra(Constants.CUSTOMER_USERNAME, name);
        intent.putExtra(Constants.CUSTOMER_LOGIN_STATE, state);
        intent.putExtra(Constants.PARSELABLE_CART_ITEM_LIST, itemsCartList);
        setResult(RESULT_OK, intent);
        finish(); // needs to be here in order for the calling intent to start
    }
}
