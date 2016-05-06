package com.shoppingpoint;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

public class CustomerCart extends AppCompatActivity implements SignInDialog.Communicator, CheckOutDialog.Communicator{

    private Bundle cartBundle;
    private ListView productItemListView;
    private ItemAdapter itemAdapter;
    private static ArrayList<CartItem> productItemsList;
    private String username;
    private boolean loginState; // false if customer is not signed in true otherwise
    public static final String TAG = Constants.CUSTOMER_CART_ACTIVITY;
    public void setUsername(String name){ this.username = name; }
    public void setLoginState(boolean state){ this.loginState = state; }
    public String getUsername(){ return this.username; }
    public boolean getLoginState(){ return this.loginState; }
    public static void setCartItemsList(ArrayList<CartItem> list){productItemsList = list;}
    public static ArrayList<CartItem> getCartItemsList(){return productItemsList;}
    public void addToCartList(CartItem item){
        if(productItemsList == null) setCartItemsList(new ArrayList<CartItem>());
        productItemsList.add(item);
    }
    public void removeItemFromList(int index){
        productItemsList.remove(index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null){
            productItemsList = new ArrayList<>();
            cartBundle = getIntent().getExtras();
            if(cartBundle != null && !cartBundle.isEmpty()) {
                String name = cartBundle.getString(Constants.CUSTOMER_USERNAME);
                boolean state = cartBundle.getBoolean(Constants.CUSTOMER_LOGIN_STATE);
                setUsername(name);
                setLoginState(state);
                ArrayList<CartItem> list = cartBundle.getParcelableArrayList(Constants.PARSELABLE_CART_ITEM_LIST);
                setCartItemsList(list);
            }
            if(productItemsList != null){
                if(productItemsList.size() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), Constants.EMPTY_SHOPPING_CART, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        else if(savedInstanceState != null){
            String name = savedInstanceState.getString(Constants.CUSTOMER_USERNAME);
            boolean state = savedInstanceState.getBoolean(Constants.CUSTOMER_LOGIN_STATE);
            setUsername(name);
            setLoginState(state);
            productItemsList = savedInstanceState.getParcelableArrayList(Constants.PARCELABLE_LIST);
            if(productItemsList.size() == 0){
                Toast toast = Toast.makeText(getApplicationContext(), Constants.EMPTY_SHOPPING_CART, Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        /* Creating List View with custom item adapter */
        productItemListView = (ListView) findViewById(R.id.cartItemsListView);
        if(productItemsList.size() == 0) {
            itemAdapter = new ItemAdapter(CustomerCart.this, new ArrayList<CartItem>());
        }
        else {
            itemAdapter = new ItemAdapter(CustomerCart.this, productItemsList);
        }
        productItemListView.setAdapter(itemAdapter);
        productItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    public void showSignInDialog(View v){
        FragmentManager manager = getFragmentManager();
        SignInDialog signInDialog = new SignInDialog();
        signInDialog.show(manager, Constants.SIGN_IN_DIALOG);
    }

    public void showCheckOutDialog(View v){
        FragmentManager manager = getFragmentManager();
        CheckOutDialog checkOutDialog = new CheckOutDialog();
        checkOutDialog.show(manager, Constants.CHECK_OUT_DIALOG);
    }

    /* This is where we fetch out the username and password from out Sign In Dialog class */
    @Override
    public void onSignInDialogMessage(String name, String pswd) {
        if(name.equalsIgnoreCase("")){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.blank_user_name, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        else if(pswd.equalsIgnoreCase("")){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.blank_password, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        SignInWebTask wst = new SignInWebTask(SignInWebTask.POST_TASK, this, Constants.SIGN_IN_MESSAGE);
        wst.addNameValuePair(Constants.CUSTOMER_USERNAME, name);
        wst.addNameValuePair(Constants.CUSTOMER_PASSWORD, pswd);
        String sampleURL = Constants.SERVICE_URL + "/customeraccount";
        sampleURL += "/customersignin";
        wst.execute(new String[]{sampleURL});
        hideKeyboard();
    }

    @Override
    public void onCheckOutDialogMessage(String name, String cardNumber, String cardExpiryDate, String cvvNumber, String cardType) {
        int cardCvvNumber;
        long creditCardNumber;
        if(name.equalsIgnoreCase("")){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.blank_user_name, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        else if(cardNumber.equalsIgnoreCase("")){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.blank_credit_card_number, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        else if(cardExpiryDate.equalsIgnoreCase("")){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.blank_expiry_date, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        else if(cvvNumber.equalsIgnoreCase("")){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.blank_cvv_number, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        else if(cardType.equalsIgnoreCase("")){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.card_select_type, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        int cardNumLength = cardNumber.length();
        if(cardNumLength != 16){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.credit_card_number_length, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        /* CHECKING IF CREDIT CARD NUMBER CONTAINS ANY LETTERS */
        try {
            creditCardNumber = Long.parseLong(cardNumber);
        } catch (NumberFormatException e) {
            hideKeyboard();
            Toast toast = Toast.makeText(getApplicationContext(), R.string.credit_card_invalid_characters, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        /* CHECKING IF CREDIT CARD NUMBER CONTAINS ANY LETTERS */
        try {
            cardCvvNumber = Integer.parseInt(cvvNumber);
        } catch (NumberFormatException e) {
            hideKeyboard();
            Toast toast = Toast.makeText(getApplicationContext(), R.string.cvv_invalid_characters, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        /* CHECKING IF CREDIT CARD NUMBER FIRST DIGIT IS NOT ZERO */
        int firstDigit = Integer.parseInt(cardNumber.substring(0, 1));
        if(firstDigit == 0){
            hideKeyboard();
            Toast toast = Toast.makeText(getApplicationContext(), R.string.card_number_first_digit_zero, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        /* CHECKING IF CREDIT CARD CVV NUMBER FIRST DIGIT IS NOT ZERO */
        int firstNum = Integer.parseInt(cvvNumber.substring(0, 1));
        if(firstNum == 0){
            hideKeyboard();
            Toast toast = Toast.makeText(getApplicationContext(), R.string.cvv_number_first_digit_zero, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        String[] expiryDateArray = cardExpiryDate.split("/");
        for(int i = 0; i < expiryDateArray.length; i++){
            String str = expiryDateArray[i];
            if(!str.matches(".*\\d.*")){
                hideKeyboard();
                Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_card_date_entry, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        hideKeyboard();

        String userName = getUsername();
        SignInWebTask wst = new SignInWebTask(SignInWebTask.POST_TASK, this, Constants.VALIDATING_CREDIT_CARD_MESSAGE);
        wst.addNameValuePair(Constants.CUSTOMER_USERNAME, userName);
        wst.addNameValuePair(Constants.CREDIT_CARD_NAME, name);
        wst.addNameValuePair(Constants.CREDIT_CARD_NUMBER, cardNumber);
        wst.addNameValuePair(Constants.CREDIT_CARD_EXPIRY_DATE, cardExpiryDate);
        wst.addNameValuePair(Constants.CREDIT_CARD_CVV_NUMBER, cvvNumber);
        wst.addNameValuePair(Constants.CREDIT_CARD_TYPE, cardType);

        CartItem tempCartItem;
        ArrayList<CartItem> list = getCartItemsList();
        String listSize = String.valueOf(list.size());
        wst.addNameValuePair(Constants.ORDERED_LIST_SIZE, listSize);
        if(list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                tempCartItem = list.get(i);
                String str = String.valueOf(i);
                wst.addNameValuePair(Constants.PRODUCT_PRODUCT_TYPE + str, tempCartItem.getProducttype());
                wst.addNameValuePair(Constants.PRODUCT_GENERAL_NAME + str, tempCartItem.getGeneralname());
                wst.addNameValuePair(Constants.PRODUCT_SPECIFIC_NAME + str, tempCartItem.getSpecificname());
                wst.addNameValuePair(Constants.PRODUCT_NUMBER + str, tempCartItem.getProductnumber());
                wst.addNameValuePair(Constants.PRODUCT_QUANTITY_ORDERED + str, String.valueOf(tempCartItem.getQuantityOrdered()));
                wst.addNameValuePair(Constants.PRODUCT_PRICE + str, tempCartItem.getPrice());
            }
        }
        String sampleURL = Constants.SERVICE_URL + "/customeraccount";
        sampleURL += "/creditcardvalidation";
        wst.execute(new String[]{sampleURL});
    }

    public void handleCheckOutResponse(String response){
        try {
            JSONObject jso = new JSONObject(response);
            boolean cardStatus = jso.getBoolean(Constants.CREDIT_CARD_STATUS);
            String statusMessage = jso.getString(Constants.CREDIT_CARD_MESSAGE);
            if(cardStatus == true){
                Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(cardStatus == false){
                Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void handleSignInResponse(String response) {
        try {
            JSONObject jso = new JSONObject(response);
            String userName = jso.getString(Constants.CUSTOMER_USERNAME);
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
            }
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage(), e);
        }
    }

    private class SignInWebTask extends AsyncTask<String, Integer, String> {

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
        public SignInWebTask(int taskType, Context mContext, String processMessage) {
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
            if(this.processMessage.equalsIgnoreCase(Constants.SIGN_IN_MESSAGE)){
                handleSignInResponse(response);
            }
            if(this.processMessage.equalsIgnoreCase(Constants.VALIDATING_CREDIT_CARD_MESSAGE)){
                handleCheckOutResponse(response);
            }
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
        InputMethodManager inputManager = (InputMethodManager) CustomerCart.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                CustomerCart.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private Bitmap loadImageFromStorage(String directoryPath, String imageName) {
        Bitmap bitMap = null;
        try {
            File file = new File(directoryPath, imageName);
            bitMap = BitmapFactory.decodeStream(new FileInputStream(file));
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return bitMap;
    }

    public class ItemAdapter extends BaseAdapter {
        private List<CartItem> itemsList;
        private Activity context;
        public ItemAdapter(Activity context, List<CartItem> list) {
            this.context = context;
            this.itemsList = list;
        }
        public List<CartItem> getProductList(){ return this.itemsList; }
        @Override
        public int getCount() {
            if (itemsList != null) {
                return itemsList.size();
            } else {
                return 0;
            }
        }
        @Override
        public CartItem getItem(int position) {
            if (itemsList != null) {
                return itemsList.get(position);
            } else {
                return null;
            }
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final CartItem productItem = getItem(position); // gets the client message from the list
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null) {
                convertView = vi.inflate(R.layout.cart_row, null);
                holder = createViewHolder(convertView);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.itemNameView.setTextColor(ContextCompat.getColor(holder.itemNameView.getContext(), R.color.OrangeRed));
            holder.itemPriceView.setTextColor(ContextCompat.getColor(holder.itemPriceView.getContext(), R.color.OrangeRed));
            holder.itemOrderQuantity.setTextColor(ContextCompat.getColor(holder.itemPriceView.getContext(), R.color.OrangeRed));

            /* LOADING IMAGES FROM INTERNAL STORAGE USING AWESOME PICASSO LIBRARY*/
            Picasso.with(context).load(new File(Constants.IMAGE_DIRECTORY_PATH + "/" + productItem.getImagename())).into(holder.itemImage);

            holder.itemNameView.setText(productItem.getSpecificname());
            holder.itemPriceView.setText(productItem.getPrice());
            holder.itemOrderQuantity.setText(Constants.QUANTITY_ORDERED + String.valueOf(productItem.getQuantityOrdered()));
            holder.removeItemButton.setTextAppearance(getApplicationContext(),R.style.ButtonFontStyle);
            holder.removeItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeProductItemFromListView(position);
                    Toast toast = Toast.makeText(getApplicationContext(), "Remove Item " + String.valueOf(position), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            return convertView;
        }

        public void add(CartItem item){
            itemsList.add(item);
        }

        public void removeItem(int n){
            itemsList.remove(n);
        }

        public void deleteItems(){
            if(itemsList.size() != 0){
                itemsList = new ArrayList<>();
            }
        }
        private ViewHolder createViewHolder(View v) {
            ViewHolder holder = new ViewHolder();
            holder.itemImage = (ImageView) v.findViewById(R.id.imageView);
            holder.itemNameView = (TextView) v.findViewById(R.id.textView);
            holder.itemPriceView = (TextView) v.findViewById(R.id.textView2);
            holder.itemOrderQuantity = (TextView) v.findViewById(R.id.textView3);
            holder.removeItemButton = (Button) v.findViewById(R.id.removeProductButton);
            return holder;
        }

        private class ViewHolder {
            ImageView itemImage;
            public TextView itemNameView;
            public TextView itemPriceView;
            public TextView itemOrderQuantity;
            Button removeItemButton;
        }
    }

    public void addProductItemToListView(CartItem item) {
        itemAdapter.add(item);
        itemAdapter.notifyDataSetChanged();
        scroll();
    }

    public void removeProductItemFromListView(int n){
        itemAdapter.removeItem(n);
        itemAdapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        productItemListView.setSelection(productItemListView.getCount() - 1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String name = getUsername();
        boolean state = getLoginState();
        ArrayList<CartItem> list = (ArrayList<CartItem>) itemAdapter.itemsList;
        savedInstanceState.putString(Constants.CUSTOMER_USERNAME, name);
        savedInstanceState.putBoolean(Constants.CUSTOMER_LOGIN_STATE, state);
        savedInstanceState.putParcelableArrayList(Constants.PARCELABLE_LIST, list);
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
        if(id == R.id.log_in){
            String name = getUsername();
            boolean state = getLoginState();
            if(name.equalsIgnoreCase("") && state == false){
                showSignInDialog(this.findViewById(android.R.id.content));
            }
            else if(!name.equalsIgnoreCase("") && state == true) {
                setUsername("");
                setLoginState(false);
                MainActivity.setUsername("");
                MainActivity.setLoginState(false);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.customer_signed_out, Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
            return true;
        }
        if(id == R.id.checkOut) {
            String name = getUsername();
            boolean state = getLoginState();
            if(productItemsList != null){
                if(productItemsList.size() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), Constants.EMPTY_SHOPPING_CART, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }
            }
            if(!name.equalsIgnoreCase("") && state != false){ // that means user is logged in and can proceed with checking out
                showCheckOutDialog(this.findViewById(android.R.id.content));
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), Constants.CUSTOMER_NEED_TO_SIGN_IN, Toast.LENGTH_SHORT);
                toast.show();
            }
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String name = getUsername();
        boolean state = getLoginState();
        ArrayList<CartItem> list = (ArrayList<CartItem>) itemAdapter.itemsList;
        Intent intent = new Intent();
        intent.putExtra(Constants.CUSTOMER_USERNAME, name);
        intent.putExtra(Constants.CUSTOMER_LOGIN_STATE, state);
        intent.putExtra(Constants.ADD_TO_CART_BUTTON_PRESSED, false);
        intent.putExtra(Constants.VIEW_CART_BUTTON_PRESSED, false);
        intent.putExtra(Constants.PARSELABLE_CART_ITEM_LIST, list);
        setResult(RESULT_OK, intent);
        finish(); // needs to be here in order for the calling intent to start
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.viewCart).setVisible(false);
        MenuItem item = menu.findItem(R.id.log_in);
        boolean state = getLoginState();
        if(state == false){
            item.setTitle(R.string.log_in);
        }
        else if(state == true){
            item.setTitle(R.string.log_out);
        }
        return true;
    }
}
