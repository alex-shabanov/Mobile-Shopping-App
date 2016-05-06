package com.shoppingpoint;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

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

public class ViewItems extends AppCompatActivity implements SignInDialog.Communicator {

    private Bundle productInfoBundle;
    private Bundle customerInfoBundle;
    private Bundle customerCartBundle;
    private Button viewItemButton;
    private ListView productItemListView;
    private Spinner itemCategory;
    private EditText viewItemName;
    private ItemAdapter itemAdapter;
    private CartItem productItem;
    private ArrayList<ProductItem> productItemsList;
    private ArrayList<CartItem> itemsCartList;
    private ArrayList<CartItem> productList;
    private String username;
    private boolean loginState, addButtonPressed, viewCartButtonPressed;
    public static final String TAG = Constants.VIEW_ITEMS_ACTIVITY;
    public void setUsername(String name) {this.username = name;}
    public void setLoginState(boolean state) {this.loginState = state;}
    public String getUsername() {return this.username;}
    public boolean getLoginState() {return this.loginState;}
    public void setAddButtonPressed(boolean state){addButtonPressed = state;}
    public boolean getAddButtonPressed(){return addButtonPressed;}
    public void setViewCartButtonPressed(boolean state){viewCartButtonPressed = state;}
    public boolean getViewCartButtonPressed(){return viewCartButtonPressed;}
    public void setItemsCartList(ArrayList<CartItem> list) {itemsCartList = list;}
    public ArrayList<CartItem> getItemsCartList() {return itemsCartList;}
    public void setProductList(ArrayList<CartItem> list) {productList = list;}
    public ArrayList<CartItem> getProductList() {return productList;}
    public void addToCustomerCartList(CartItem item) {
        if (itemsCartList == null) setItemsCartList(new ArrayList<CartItem>());
        /* This is part is done so that when the customer orders many items of same type
        *  only one single item is displayed in the cart but the quantity is increased by one.
        * */
        CartItem cartItem;
        for (int i = 0; i < itemsCartList.size(); i++) {
            cartItem = itemsCartList.get(i);
            if (cartItem.getImagename().equalsIgnoreCase(item.getImagename())) {
                int numItems = cartItem.getQuantityOrdered() + 1;
                cartItem.setQuantityOrdered(numItems);
                return;  // so that the item is not added to the list again, only the number of it is increased by one
            }
        }
        getItemsCartList().add(item);
    }

    public void addToProductList(CartItem item) {
        if (productList == null) setProductList(new ArrayList<CartItem>());
        getProductList().add(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            setItemsCartList(new ArrayList<CartItem>()); // this is the list of cart items that customer chooses
            setProductList(new ArrayList<CartItem>());   // this is the product list of currently displayed products
            customerInfoBundle = getIntent().getExtras();
            setAddButtonPressed(false);
            setViewCartButtonPressed(false);
            if (customerInfoBundle != null && !customerInfoBundle.isEmpty()) {
                String name = customerInfoBundle.getString(Constants.CUSTOMER_USERNAME);
                boolean state = customerInfoBundle.getBoolean(Constants.CUSTOMER_LOGIN_STATE);
                boolean add_to_cart_state = customerInfoBundle.getBoolean(Constants.ADD_TO_CART_BUTTON_PRESSED);
                boolean view_cart_state = customerInfoBundle.getBoolean(Constants.VIEW_CART_BUTTON_PRESSED);
                setUsername(name);
                setLoginState(state);
                setAddButtonPressed(add_to_cart_state);
                setViewCartButtonPressed(view_cart_state);
                ArrayList<CartItem> list = customerInfoBundle.getParcelableArrayList(Constants.PARSELABLE_CART_ITEM_LIST);
                setItemsCartList(list);
            }
        }
        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(Constants.CUSTOMER_USERNAME);
            boolean state = savedInstanceState.getBoolean(Constants.CUSTOMER_LOGIN_STATE);
            setUsername(name);
            setLoginState(state);
            itemsCartList = savedInstanceState.getParcelableArrayList(Constants.CUSTOMER_CART_ITEM_LIST);
            productList = savedInstanceState.getParcelableArrayList(Constants.CUSTOMER_PRODUCT_LIST);
        }

        viewItemButton = (Button) findViewById(R.id.view_items_button);
        viewItemButton.setTextAppearance(getApplicationContext(), R.style.viewItemButtonFontStyle);
        viewItemName = (EditText) findViewById(R.id.item_name_editText);
        itemCategory = (Spinner) findViewById(R.id.onlineStatusSpinner);
        itemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.product_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCategory.setAdapter(adapter);

        /* Creating List View with custom item adapter */
        productItemListView = (ListView) findViewById(R.id.productItemsListView);
        if (productList.size() == 0) {
            itemAdapter = new ItemAdapter(ViewItems.this, new ArrayList<CartItem>());
        } else {
            itemAdapter = new ItemAdapter(ViewItems.this, productList);
        }
        productItemListView.setAdapter(itemAdapter);
        productItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemAdapter itemAdapter = (ItemAdapter) parent.getAdapter();
                CartItem productItem = itemAdapter.getItem(position);
                productInfoBundle = new Bundle();
                productInfoBundle.putParcelable(Constants.PRODUCT_INFO, productItem);
                Intent viewProductItem = new Intent(getApplicationContext(), ViewProductItem.class);
                viewProductItem.putExtra(Constants.PRODUCT_BUNDLE, productInfoBundle);
                startActivity(viewProductItem); // viewing the product item in more details in another activity
            }
        });
    }

    public void getProductItems(View v) {
        Spinner categoryItem = (Spinner) findViewById(R.id.onlineStatusSpinner);
        String itemCategory = categoryItem.getSelectedItem().toString();
        String itemType = viewItemName.getText().toString();
        if (itemType.equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.enter_product_type, Toast.LENGTH_SHORT).show();
            return;
        }
        /* Here I'm deleting the items currently stored inside Item Adapter */
        if (productList.size() != 0) {
            productList = new ArrayList<>();
            itemAdapter.deleteItems();
            itemAdapter.notifyDataSetChanged();
        }
        String sampleURL = Constants.SERVICE_URL + "/getitems";
        sampleURL += "/getitembycategorytype";
        sampleURL += "?itemcategory=" + itemCategory;
        sampleURL += "&itemtype=" + itemType;
        hideKeyboard();
        WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, Constants.GETTING_DATA_MESSAGE);
        wst.execute(new String[]{sampleURL});
    }

    /* This is where we fetch out the username and password from out Sign In Dialog class */
    @Override
    public void onSignInDialogMessage(String name, String pswd) {
        if (name.equalsIgnoreCase("")) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.blank_user_name, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        } else if (pswd.equalsIgnoreCase("")) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.blank_password, Toast.LENGTH_SHORT);
            toast.show();
            hideKeyboard();
            return;
        }
        hideKeyboard();
        SignInWebTask wst = new SignInWebTask(SignInWebTask.POST_TASK, this, Constants.SIGN_IN_MESSAGE);
        wst.addNameValuePair(Constants.CUSTOMER_USERNAME, name);
        wst.addNameValuePair(Constants.CUSTOMER_PASSWORD, pswd);
        String sampleURL = Constants.SERVICE_URL + "/customeraccount";
        sampleURL += "/customersignin";
        wst.execute(new String[]{sampleURL});
    }

    public void showSignInDialog(View v) {
        FragmentManager manager = getFragmentManager();
        SignInDialog signInDialog = new SignInDialog();
        signInDialog.show(manager, Constants.SIGN_IN_DIALOG);
    }

    public void handleSignInResponse(String response) {
        try {
            JSONObject jso = new JSONObject(response);
            String userName = jso.getString(Constants.CUSTOMER_USERNAME);
            long counter = jso.getLong(Constants.CUSTOMER_COUNTER);
            if (userName.equalsIgnoreCase(Constants.USERNAME_NOT_EXISTS)) {
                Toast.makeText(this, R.string.incorrect_username, Toast.LENGTH_SHORT).show();
                return;
            } else if (userName.equalsIgnoreCase(Constants.INCORRECT_PASSWORD)) {
                Toast.makeText(this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                return;
            } else if (counter == 100) {
                /* Login is successful so put customer info into bundle and ship it to View Items Activity */
                Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                setUsername(userName);
                setLoginState(true); // means that customer is logged in
            }
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage(), e);
        }
    }

    public void handleResponse(String response) {
        try {
            JSONObject jsonObj;
            JSONArray jsonArray = new JSONArray(response);
            productItemsList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                String itemCategory = jsonObj.getString(Constants.PRODUCT_CATEGORY);
                String itemType = jsonObj.getString(Constants.PRODUCT_PRODUCT_TYPE);
                String itemGenName = jsonObj.getString(Constants.PRODUCT_GENERAL_NAME);
                String itemSpecificName = jsonObj.getString(Constants.PRODUCT_SPECIFIC_NAME);
                String itemNumber = jsonObj.getString(Constants.PRODUCT_NUMBER);
                String itemShortDescr = jsonObj.getString(Constants.PRODUCT_SHORT_DESCR);
                String itemLongDescr = jsonObj.getString(Constants.PRODUCT_LONG_DESCR);
                String itemQuantity = jsonObj.getString(Constants.PRODUCT_QUANTITY);
                String itemPrice = jsonObj.getString(Constants.PRODUCT_PRICE);
                String itemImageName = jsonObj.getString(Constants.PRODUCT_IMAGE_NAME);
                String itemImageStr = jsonObj.getString(Constants.PRODUCT_IMAGE);
                Long itemCounter = jsonObj.getLong(Constants.CUSTOMER_COUNTER);

                /* ***** SAVING IMAGE TO INTERNAL STORAGE ***** */
                SaveImageToInternalStorage saveImage = new SaveImageToInternalStorage(itemImageName);
                saveImage.execute(new String[]{itemImageStr});

                ProductItem product = new ProductItem(itemCategory, itemType, itemGenName, itemSpecificName, itemNumber, itemShortDescr,
                        itemLongDescr, itemQuantity, itemPrice, itemImageName, itemImageStr, 0);
                productItemsList.add(product);

                productItem = new CartItem(itemCategory, itemType, itemGenName, itemSpecificName, itemNumber, itemShortDescr,
                        itemLongDescr, itemQuantity, itemPrice, itemImageName, 0);

                productList.add(productItem);
                addProductItemToListView(productItem); // adds items to the list view
            }
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage(), e);
        }
        if (productList.size() == 0) {
            Toast toast = Toast.makeText(productItemListView.getContext(), R.string.no_items, Toast.LENGTH_SHORT);
            toast.show();
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
            HttpParams http = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(http, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(http, SOCKET_TIMEOUT);
            return http;
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
            handleSignInResponse(response);
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

    public class ItemAdapter extends BaseAdapter {
        private List<CartItem> itemsList;
        private Activity context;
        public ItemAdapter(Activity context, List<CartItem> list) {
            this.context = context;
            this.itemsList = list;
        }
        public List<CartItem> getProductList() {
            return this.itemsList;
        }
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
            final ViewHolder holder;
            final CartItem productItem = getItem(position);
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = vi.inflate(R.layout.item_row, null);
                holder = createViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.itemNameView.setTextColor(ContextCompat.getColor(holder.itemNameView.getContext(), R.color.OrangeRed));
            holder.itemPriceView.setTextColor(ContextCompat.getColor(holder.itemPriceView.getContext(), R.color.OrangeRed));

            /* LOADING IMAGES FROM INTERNAL STORAGE USING AWESOME PICASSO LIBRARY */
            if(productItemsList != null){
                ProductItem product;
                product = productItemsList.get(position);
                Bitmap map = getImageFromBase64(product.getProductimage());
                holder.itemImage.setImageBitmap(map);
            }
            else{
                Picasso.with(getApplicationContext()).load(new File(Constants.IMAGE_DIRECTORY_PATH + "/" + productItem.getImagename())).into(holder.itemImage);
            }
            holder.itemNameView.setText(productItem.getSpecificname());
            holder.itemPriceView.setText(productItem.getPrice());
            holder.toCartButton.setTextAppearance(getApplicationContext(), R.style.ButtonFontStyle);
            holder.toCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAddButtonPressed(true);
                    CartItem productItem = getItem(position);
                    String category = productItem.getCategory();
                    String productType = productItem.getProducttype();
                    String generalName = productItem.getGeneralname();
                    String specificName = productItem.getSpecificname();
                    String productNumber = productItem.getProductnumber();
                    String shortDescription = productItem.getShortdescription();
                    String longDescription = productItem.getLongdescription();
                    String productQuantity = productItem.getQuantity();
                    String productPrice = productItem.getPrice();
                    String imageName = productItem.getImagename();
                    CartItem cartItem = new CartItem(category, productType, generalName, specificName, productNumber, shortDescription,
                            longDescription, productQuantity, productPrice, imageName, 1);
                    // ADDING PRODUCT ITEMS TO CART LIST
                    addToCustomerCartList(cartItem);
                    MainActivity.addToCustomerCartList(cartItem);
                    Toast toast = Toast.makeText(getApplicationContext(), Constants.ITEM_ADDED_TO_CART, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            return convertView;
        }

        public void add(CartItem item) {
            itemsList.add(item);
        }

        public void deleteItems() {
            if (itemsList.size() != 0) {
                itemsList = new ArrayList<>();
            }
        }

        private ViewHolder createViewHolder(View v) {
            ViewHolder holder = new ViewHolder();
            holder.itemImage = (ImageView) v.findViewById(R.id.imageView);
            holder.itemNameView = (TextView) v.findViewById(R.id.textView);
            holder.itemPriceView = (TextView) v.findViewById(R.id.textView2);
            holder.toCartButton = (Button) v.findViewById(R.id.addToCartButton);
            return holder;
        }

        private class ViewHolder {
            ImageView itemImage;
            public TextView itemNameView;
            public TextView itemPriceView;
            Button toCartButton;
        }
    }

    /* This method adds a product item to the list view */
    public void addProductItemToListView(CartItem item) {
        itemAdapter.add(item);
        itemAdapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        productItemListView.setSelection(productItemListView.getCount() - 1);
    }

    private Bitmap getImageFromBase64(String strBase64) {
        byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
        Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitMap;
    }

    public class SaveImageToInternalStorage extends AsyncTask<String, String, String> {
        private String imageName;

        public SaveImageToInternalStorage(String pictureName) {
            imageName = pictureName;
        }

        private Bitmap getImageFromBase64(String strBase64) {
            byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
            Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return bitMap;
        }

        private String saveImageToInternalStorage(Bitmap bitmapImage, String imageName) {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File dirPath = cw.getDir(Constants.IMAGE_DIRECTORY, Context.MODE_PRIVATE);
            boolean imageExists = isFileExists(imageName);
            if (imageExists) {
                return Constants.IMAGE_ALREADY_EXISTS;
            }
            File file = new File(dirPath, imageName);  // Create image directory
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return dirPath.getAbsolutePath();
        }
        @Override
        protected String doInBackground(String... params) {
            Bitmap map = getImageFromBase64(params[0]);
            return saveImageToInternalStorage(map, imageName);
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) ViewItems.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                ViewItems.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public boolean isFileExists(String fileName) {
        String path = Constants.IMAGE_DIRECTORY_PATH + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String name = getUsername();
        boolean state = getLoginState();
        boolean add_to_cart_state = getAddButtonPressed();
        boolean view_cart_state = getViewCartButtonPressed();
        savedInstanceState.putString(Constants.CUSTOMER_USERNAME, name);
        savedInstanceState.putBoolean(Constants.CUSTOMER_LOGIN_STATE, state);
        savedInstanceState.putString(Constants.ADD_TO_CART_BUTTON_PRESSED, name);
        savedInstanceState.putBoolean(Constants.VIEW_CART_BUTTON_PRESSED, state);

        savedInstanceState.putParcelableArrayList(Constants.CUSTOMER_CART_ITEM_LIST, itemsCartList);
        savedInstanceState.putParcelableArrayList(Constants.CUSTOMER_PRODUCT_LIST, productList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra(Constants.CUSTOMER_USERNAME);
                boolean state = data.getBooleanExtra(Constants.CUSTOMER_LOGIN_STATE, false);
                boolean add_to_cart_state = data.getBooleanExtra(Constants.ADD_TO_CART_BUTTON_PRESSED, false);
                boolean view_cart_state = data.getBooleanExtra(Constants.VIEW_CART_BUTTON_PRESSED, false);
                ArrayList<CartItem> list = data.getParcelableArrayListExtra(Constants.PARSELABLE_CART_ITEM_LIST);
                setItemsCartList(new ArrayList<CartItem>());
                for (int i = 0; i < list.size(); i++) {
                    addToCustomerCartList(list.get(i));
                }
                setUsername(name);
                setLoginState(state);
                setAddButtonPressed(add_to_cart_state);
                setViewCartButtonPressed(view_cart_state);
            }
        }
    }

    /* This method ensures that the current state of the cart with all its item is preserved throughout all activities */
    @Override
    public void onBackPressed() {
        String name = getUsername();
        boolean state = getLoginState();
        Intent intent = new Intent();
        intent.putExtra(Constants.CUSTOMER_USERNAME, name);
        intent.putExtra(Constants.CUSTOMER_LOGIN_STATE, state);
        intent.putExtra(Constants.PARSELABLE_CART_ITEM_LIST, itemsCartList);
        setResult(RESULT_OK, intent);
        if(getAddButtonPressed() == true && getViewCartButtonPressed() == true){
            deleteFilesFromInternalStorage();
        }
        else if(getAddButtonPressed() == false && getViewCartButtonPressed() == false){
            deleteFilesFromInternalStorage();
        }
        finish(); // needs to be here in order for the calling intent to start
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
        if (id == R.id.log_in) {
            String name = getUsername();
            boolean state = getLoginState();
            if (name.equalsIgnoreCase("") && state == false) {
                showSignInDialog(this.findViewById(android.R.id.content));
            } else if (!name.equalsIgnoreCase("") && state == true) {
                setUsername("");
                setLoginState(false);
                MainActivity.setUsername("");
                MainActivity.setLoginState(false);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.customer_signed_out, Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        }
        if (id == R.id.viewCart) {
            setViewCartButtonPressed(true);
            String name = getUsername();
            boolean state = getLoginState();
            customerCartBundle = new Bundle();
            customerCartBundle.putString(Constants.CUSTOMER_USERNAME, name);
            customerCartBundle.putBoolean(Constants.CUSTOMER_LOGIN_STATE, state);
            customerCartBundle.putParcelableArrayList(Constants.PARSELABLE_CART_ITEM_LIST, itemsCartList);
            Intent customerCart = new Intent(getApplicationContext(), CustomerCart.class);
            customerCart.putExtras(customerCartBundle);
            startActivityForResult(customerCart, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.checkOut).setVisible(false);
        MenuItem item = menu.findItem(R.id.log_in);
        boolean state = getLoginState();
        if (state == false) {
            item.setTitle(R.string.log_in);
        } else if (state == true) {
            item.setTitle(R.string.log_out);
        }
        return true;
    }

    /*
    * This method deletes all bit maps image files currently saved in the
    * internal storage for the given session when user presses the back button
    */
    public void deleteFilesFromInternalStorage() {
        File file1;
        File[] filesArray;
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dirPath = cw.getDir(Constants.IMAGE_DIRECTORY, Context.MODE_PRIVATE);
        filesArray = dirPath.listFiles();
        for(int i = 0; i < filesArray.length; i++) {
            file1 = filesArray[i];
            File file2 = new File(dirPath, file1.getName());
            file2.delete();
        }
    }
}
