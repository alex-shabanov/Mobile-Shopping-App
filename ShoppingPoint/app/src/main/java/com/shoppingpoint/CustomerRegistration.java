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
import android.widget.Button;
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

public class CustomerRegistration extends AppCompatActivity {

    private Bundle customerInfoBundle;
    private Button viewAccounButton, submitAccountButton, clearFieldsButton;
    private String firstname, lastname, address, city, country;
    private String areacode, email, phonenumber, username, password;
    private boolean loginState;
    private ArrayList<CartItem> itemsCartList;
    public static final String TAG = Constants.CUSTOMER_REGISTRATION_ACTIVITY;

    public void setFirstname(String name) {this.firstname = name;}
    public String getFirstname() {return this.firstname;}
    public void setLastname(String name) {this.lastname = name;}
    public String getLastname() {return this.lastname;}
    public void setAddress(String addr) {this.address = addr;}
    public String getAddress() {return this.address;}
    public void setCity(String cty){this.city = cty;}
    public String getCity(){return this.city;}
    public void setCountry(String ctry){this.country = ctry;}
    public String getCountry(){return this.country;}
    public void setAreacode(String code){this.areacode = code;}
    public String getAreacode(){return this.areacode;}
    public void setEmail(String mail){this.email = mail;}
    public String getEmail(){return this.email;}
    public void setPhonenumber(String phnum){this.phonenumber = phnum;}
    public String getPhonenumber(){return this.phonenumber;}
    public void setUsername(String name){this.username = name;}
    public String getUsername(){return this.username;}
    public void setPassword(String pswd){this.password = pswd;}
    public String getPassword(){return this.password;}
    public void setLoginState(boolean state){ this.loginState = state; }
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
        setContentView(R.layout.activity_customer_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText firstName = (EditText) findViewById(R.id.first_name);
        EditText lastName = (EditText) findViewById(R.id.last_name);
        EditText customerAddress = (EditText) findViewById(R.id.customer_address);
        EditText customerCity = (EditText) findViewById(R.id.customer_city);
        EditText customerCountry = (EditText) findViewById(R.id.customer_country);
        EditText customerAreaCode = (EditText) findViewById(R.id.customer_areacode);
        EditText customerEmail = (EditText) findViewById(R.id.customer_email);
        EditText customerPhone = (EditText) findViewById(R.id.customer_phone_number);
        EditText customerUsername = (EditText) findViewById(R.id.customer_username);
        EditText customerPassword = (EditText) findViewById(R.id.customer_password);

        if(savedInstanceState == null){
            setItemsCartList(new ArrayList<CartItem>());
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
            String first_name, last_name, addr, cty, ctry;
            String area_code, mail, phonenum, userName, pswd;
            first_name = savedInstanceState.getString(Constants.CUSTOMER_FIRST_NAME);
            last_name = savedInstanceState.getString(Constants.CUSTOMER_LAST_NAME);
            addr = savedInstanceState.getString(Constants.CUSTOMER_ADDRESS);
            cty = savedInstanceState.getString(Constants.CUSTOMER_CITY);
            ctry = savedInstanceState.getString(Constants.CUSTOMER_COUNTRY);
            area_code = savedInstanceState.getString(Constants.CUSTOMER_AREA_CODE);
            mail = savedInstanceState.getString(Constants.CUSTOMER_EMAIL);
            phonenum = savedInstanceState.getString(Constants.CUSTOMER_PHONE_NUMBER);
            userName = savedInstanceState.getString(Constants.CUSTOMER_USERNAME);
            pswd = savedInstanceState.getString(Constants.CUSTOMER_PASSWORD);

            String name = savedInstanceState.getString(Constants.CUSTOMER_USERNAME);
            boolean state = savedInstanceState.getBoolean(Constants.CUSTOMER_LOGIN_STATE);
            setUsername(name);
            setLoginState(state);
            itemsCartList = savedInstanceState.getParcelableArrayList(Constants.CUSTOMER_CART_ITEM_LIST);

            /* Setting customer setters */
            setFirstname(first_name);
            setLastname(last_name);
            setAddress(addr);
            setCity(cty);
            setCountry(ctry);
            setAreacode(area_code);
            setEmail(mail);
            setPhonenumber(phonenum);
            setUsername(userName);
            setPassword(pswd);

            /* Setting the text edit fields back on */
            firstName.setText(first_name);
            lastName.setText(last_name);
            customerAddress.setText(addr);
            customerCity.setText(cty);
            customerCountry.setText(ctry);
            customerAreaCode.setText(area_code);
            customerEmail.setText(mail);
            customerPhone.setText(phonenum);
            customerUsername.setText(userName);
            customerPassword.setText(pswd);
        }

        viewAccounButton = (Button) findViewById(R.id.view_account_button);
        submitAccountButton = (Button) findViewById(R.id.submit_account_button);
        clearFieldsButton = (Button) findViewById(R.id.clear_fields_button);
        viewAccounButton.setTextAppearance(getApplicationContext(), R.style.registerButtonFontStyle);
        submitAccountButton.setTextAppearance(getApplicationContext(),R.style.registerButtonFontStyle);
        clearFieldsButton.setTextAppearance(getApplicationContext(),R.style.registerButtonFontStyle);
    }

    public void submitCustomerInfo(View v){
        String first_name, last_name, addr, cty, ctry;
        String area_code, mail, phonenum, userName, pswd;

        EditText firstName = (EditText) findViewById(R.id.first_name);
        EditText lastName = (EditText) findViewById(R.id.last_name);
        EditText customerAddress = (EditText) findViewById(R.id.customer_address);
        EditText customerCity = (EditText) findViewById(R.id.customer_city);
        EditText customerCountry = (EditText) findViewById(R.id.customer_country);
        EditText customerAreaCode = (EditText) findViewById(R.id.customer_areacode);
        EditText customerEmail = (EditText) findViewById(R.id.customer_email);
        EditText customerPhone = (EditText) findViewById(R.id.customer_phone_number);
        EditText customerUsername = (EditText) findViewById(R.id.customer_username);
        EditText customerPassword = (EditText) findViewById(R.id.customer_password);

        first_name = firstName.getText().toString();
        last_name = lastName.getText().toString();
        addr = customerAddress.getText().toString();
        cty = customerCity.getText().toString();
        ctry = customerCountry.getText().toString();
        area_code = customerAreaCode.getText().toString();
        mail = customerEmail.getText().toString();
        phonenum = customerPhone.getText().toString();
        userName = customerUsername.getText().toString();
        pswd = customerPassword.getText().toString();

        if(first_name.equals("") || last_name.equals("") || addr.equals("") || cty.equals("") || ctry.equalsIgnoreCase("") ||
                area_code.equalsIgnoreCase("") || mail.equalsIgnoreCase("") || phonenum.equalsIgnoreCase("") ||
                userName.equalsIgnoreCase("") || pswd.equalsIgnoreCase("")){
            Toast.makeText(this, R.string.all_fields_requirement, Toast.LENGTH_LONG).show();
            return;
        }

        WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, Constants.SUBMIT_CUSTOMER_INFO);
        wst.addNameValuePair(Constants.CUSTOMER_FIRST_NAME, first_name);
        wst.addNameValuePair(Constants.CUSTOMER_LAST_NAME, last_name);
        wst.addNameValuePair(Constants.CUSTOMER_ADDRESS, addr);
        wst.addNameValuePair(Constants.CUSTOMER_CITY, cty);
        wst.addNameValuePair(Constants.CUSTOMER_COUNTRY, ctry);
        wst.addNameValuePair(Constants.CUSTOMER_AREA_CODE, area_code);
        wst.addNameValuePair(Constants.CUSTOMER_EMAIL, mail);
        wst.addNameValuePair(Constants.CUSTOMER_PHONE_NUMBER, phonenum);
        wst.addNameValuePair(Constants.CUSTOMER_USERNAME, userName);
        wst.addNameValuePair(Constants.CUSTOMER_PASSWORD, pswd);
        // the passed String is the URL we will POST to
        String sampleURL = Constants.SERVICE_URL + "/customeraccount";
        wst.execute(new String[]{sampleURL}); // must edit url
    }

    public void updateCustomerInfo(View v){
        String first_name, last_name, addr, cty, ctry;
        String area_code, mail, phonenum, userName, pswd;

        EditText firstName = (EditText) findViewById(R.id.first_name);
        EditText lastName = (EditText) findViewById(R.id.last_name);
        EditText customerAddress = (EditText) findViewById(R.id.customer_address);
        EditText customerCity = (EditText) findViewById(R.id.customer_city);
        EditText customerCountry = (EditText) findViewById(R.id.customer_country);
        EditText customerAreaCode = (EditText) findViewById(R.id.customer_areacode);
        EditText customerEmail = (EditText) findViewById(R.id.customer_email);
        EditText customerPhone = (EditText) findViewById(R.id.customer_phone_number);
        EditText customerUsername = (EditText) findViewById(R.id.customer_username);
        EditText customerPassword = (EditText) findViewById(R.id.customer_password);

        first_name = firstName.getText().toString();
        last_name = lastName.getText().toString();
        addr = customerAddress.getText().toString();
        cty = customerCity.getText().toString();
        ctry = customerCountry.getText().toString();
        area_code = customerAreaCode.getText().toString();
        mail = customerEmail.getText().toString();
        phonenum = customerPhone.getText().toString();
        userName = customerUsername.getText().toString();
        pswd = customerPassword.getText().toString();

        if(first_name.equals("") || last_name.equals("") || addr.equals("") || cty.equals("") || ctry.equalsIgnoreCase("") ||
                area_code.equalsIgnoreCase("") || mail.equalsIgnoreCase("") || phonenum.equalsIgnoreCase("") ||
                userName.equalsIgnoreCase("") || pswd.equalsIgnoreCase("")){
            Toast.makeText(this, R.string.all_fields_requirement, Toast.LENGTH_LONG).show();
            return;
        }

        WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, Constants.UPDATING_CUSTOMER_INFO);
        wst.addNameValuePair(Constants.CUSTOMER_FIRST_NAME, first_name);
        wst.addNameValuePair(Constants.CUSTOMER_LAST_NAME, last_name);
        wst.addNameValuePair(Constants.CUSTOMER_ADDRESS, addr);
        wst.addNameValuePair(Constants.CUSTOMER_CITY, cty);
        wst.addNameValuePair(Constants.CUSTOMER_COUNTRY, ctry);
        wst.addNameValuePair(Constants.CUSTOMER_AREA_CODE, area_code);
        wst.addNameValuePair(Constants.CUSTOMER_EMAIL, mail);
        wst.addNameValuePair(Constants.CUSTOMER_PHONE_NUMBER, phonenum);
        wst.addNameValuePair(Constants.CUSTOMER_USERNAME, userName);
        wst.addNameValuePair(Constants.CUSTOMER_PASSWORD, pswd);
        // the passed String is the URL we will POST to
        String sampleURL = Constants.SERVICE_URL + "/customeraccount";
        sampleURL += "/updateaccount";
        wst.execute(new String[]{sampleURL});
    }

    public void getCustomerInfo(View v){
        String userName, pswd;
        EditText customerUsername = (EditText) findViewById(R.id.customer_username);
        EditText customerPassword = (EditText) findViewById(R.id.customer_password);
        userName = customerUsername.getText().toString();
        pswd = customerPassword.getText().toString();
        if(userName.equalsIgnoreCase("") || pswd.equalsIgnoreCase("")){
            Toast.makeText(this, R.string.enter_username_and_password, Toast.LENGTH_LONG).show();
            return;
        }
        else if(pswd.equalsIgnoreCase("")){
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_LONG).show();
            return;
        }
        String sampleURL = Constants.SERVICE_URL;
        sampleURL += "/customeraccount";
        sampleURL += "/getaccountbyusername";
        sampleURL += "?username=" + userName;
        sampleURL += "&password=" + pswd;
        WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, Constants.GETTING_INFO_MESSAGE);
        wst.execute(new String[]{sampleURL});
    }

    public void clearTextFields(){
        EditText firstName = (EditText) findViewById(R.id.first_name);
        EditText lastName = (EditText) findViewById(R.id.last_name);
        EditText customerAddress = (EditText) findViewById(R.id.customer_address);
        EditText customerCity = (EditText) findViewById(R.id.customer_city);
        EditText customerCountry = (EditText) findViewById(R.id.customer_country);
        EditText customerAreaCode = (EditText) findViewById(R.id.customer_areacode);
        EditText customerEmail = (EditText) findViewById(R.id.customer_email);
        EditText customerPhone = (EditText) findViewById(R.id.customer_phone_number);
        EditText customerUsername = (EditText) findViewById(R.id.customer_username);
        EditText customerPassword = (EditText) findViewById(R.id.customer_password);
        firstName.setText("");
        lastName.setText("");
        customerAddress.setText("");
        customerCity.setText("");
        customerCountry.setText("");
        customerAreaCode.setText("");
        customerEmail.setText("");
        customerPhone.setText("");
        customerUsername.setText("");
        customerPassword.setText("");
    }

    public void clearAccountFields(View v){
        clearTextFields();
    }

    public void handleResponse(String response) {
        EditText firstName = (EditText) findViewById(R.id.first_name);
        EditText lastName = (EditText) findViewById(R.id.last_name);
        EditText customerAddress = (EditText) findViewById(R.id.customer_address);
        EditText customerCity = (EditText) findViewById(R.id.customer_city);
        EditText customerCountry = (EditText) findViewById(R.id.customer_country);
        EditText customerAreaCode = (EditText) findViewById(R.id.customer_areacode);
        EditText customerEmail = (EditText) findViewById(R.id.customer_email);
        EditText customerPhone = (EditText) findViewById(R.id.customer_phone_number);
        EditText customerUsername = (EditText) findViewById(R.id.customer_username);
        EditText customerPassword = (EditText) findViewById(R.id.customer_password);
        try {
            JSONObject jso = new JSONObject(response);
            String first_name = jso.getString(Constants.CUSTOMER_FIRST_NAME);
            String last_name = jso.getString(Constants.CUSTOMER_LAST_NAME);
            String addr = jso.getString(Constants.CUSTOMER_ADDRESS);
            String cty = jso.getString(Constants.CUSTOMER_CITY);
            String ctry = jso.getString(Constants.CUSTOMER_COUNTRY);
            String area_code = jso.getString(Constants.CUSTOMER_AREA_CODE);
            String mail = jso.getString(Constants.CUSTOMER_EMAIL);
            String phonenum = jso.getString(Constants.CUSTOMER_PHONE_NUMBER);
            String userName = jso.getString(Constants.CUSTOMER_USERNAME);
            String pswd = jso.getString(Constants.CUSTOMER_PASSWORD);
            long counter = jso.getLong(Constants.CUSTOMER_COUNTER);

            if(userName.equalsIgnoreCase(Constants.ACCOUNT_EXISTS)){
                Toast.makeText(this, R.string.account_exists, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, R.string.different_username, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(userName.equalsIgnoreCase(Constants.ACCOUNT_NOT_EXISTS)){
                Toast.makeText(this, R.string.account_not_exists, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(userName.equalsIgnoreCase(Constants.PASSWORD_NOT_MATCH)){
                Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(counter == -5){ // this is when account exists and the customer can retrieve and view its account info
                /* Setting Text Views */
                firstName.setText(first_name);
                lastName.setText(last_name);
                customerAddress.setText(addr);
                customerCity.setText(cty);
                customerCountry.setText(ctry);
                customerAreaCode.setText(area_code);
                customerEmail.setText(mail);
                customerPhone.setText(phonenum);
                customerUsername.setText(userName);
                customerPassword.setText(pswd);
            }
            else if(counter == -7){
                /* Setting Text Views */
                firstName.setText(first_name);
                lastName.setText(last_name);
                customerAddress.setText(addr);
                customerCity.setText(cty);
                customerCountry.setText(ctry);
                customerAreaCode.setText(area_code);
                customerEmail.setText(mail);
                customerPhone.setText(phonenum);
                customerUsername.setText(userName);
                customerPassword.setText(pswd);
                Toast.makeText(this, R.string.account_updated_successfully, Toast.LENGTH_SHORT).show();
                clearTextFields();
                return;
            }
            else {
                Toast.makeText(this, R.string.account_created, Toast.LENGTH_SHORT).show();
                clearTextFields();
                return;
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
            if (response == null) {
                return result;
            } else {
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
        InputMethodManager inputManager = (InputMethodManager) CustomerRegistration.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                CustomerRegistration.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Constants.CUSTOMER_FIRST_NAME, getFirstname());
        savedInstanceState.putString(Constants.CUSTOMER_LAST_NAME, getLastname());
        savedInstanceState.putString(Constants.CUSTOMER_ADDRESS, getAddress());
        savedInstanceState.putString(Constants.CUSTOMER_CITY, getCity());
        savedInstanceState.putString(Constants.CUSTOMER_COUNTRY, getCountry());
        savedInstanceState.putString(Constants.CUSTOMER_AREA_CODE, getAreacode());
        savedInstanceState.putString(Constants.CUSTOMER_EMAIL, getEmail());
        savedInstanceState.putString(Constants.CUSTOMER_PHONE_NUMBER, getPhonenumber());
        savedInstanceState.putString(Constants.CUSTOMER_USERNAME, getUsername());
        savedInstanceState.putString(Constants.CUSTOMER_PASSWORD, getPassword());
        savedInstanceState.putBoolean(Constants.CUSTOMER_LOGIN_STATE, getLoginState());
        savedInstanceState.putParcelableArrayList(Constants.CUSTOMER_CART_ITEM_LIST, itemsCartList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.viewCart).setVisible(false); // menu button is not visible
        menu.findItem(R.id.checkOut).setVisible(false);
        menu.findItem(R.id.log_in).setVisible(false);
        return true;
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
