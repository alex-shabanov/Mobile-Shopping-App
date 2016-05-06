package com.shoppingpoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Bundle intentBundle;
    private static String username;
    private static boolean loginState;
    public static final String TAG = Constants.MAIN_ACTIVITY;
    public static void setUsername(String name){ username = name; }
    public static void setLoginState(boolean state){ loginState = state; }
    public static String getUsername(){ return username; }
    public static boolean getLoginState(){ return loginState; }
    private static ArrayList<CartItem> itemsCartList;
    public static void setItemsCartList(ArrayList<CartItem> list){ itemsCartList = list;}
    public static ArrayList<CartItem> getItemsCartList(){return itemsCartList;}
    public static void addToCustomerCartList(CartItem item){
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            setUsername("");
            setLoginState(false); // user initially is not logged in
            setItemsCartList(new ArrayList<CartItem>());
        }
        else if(savedInstanceState != null) {
            String name = savedInstanceState.getString(Constants.CUSTOMER_USERNAME);
            boolean state = savedInstanceState.getBoolean(Constants.CUSTOMER_LOGIN_STATE);
            setUsername(name);
            setLoginState(state);
            itemsCartList = savedInstanceState.getParcelableArrayList(Constants.CUSTOMER_CART_ITEM_LIST);
        }

    }

    public void viewCustomerProducts(View v){
        String name = getUsername();
        boolean state = getLoginState();
        intentBundle = new Bundle();
        intentBundle.putString(Constants.CUSTOMER_USERNAME, name);
        intentBundle.putBoolean(Constants.CUSTOMER_LOGIN_STATE, state);
        intentBundle.putParcelableArrayList(Constants.PARSELABLE_CART_ITEM_LIST, itemsCartList);
        Intent userAccount = new Intent(getApplicationContext(), ViewItems.class);
        userAccount.putExtras(intentBundle);
        startActivityForResult(userAccount, 2);
    }

    public void customerSignIn(View v){
        String name = getUsername();
        boolean state = getLoginState();
        intentBundle = new Bundle();
        intentBundle.putString(Constants.CUSTOMER_USERNAME, name);
        intentBundle.putBoolean(Constants.CUSTOMER_LOGIN_STATE, state);
        intentBundle.putParcelableArrayList(Constants.PARSELABLE_CART_ITEM_LIST, itemsCartList);
        Intent userAccount = new Intent(getApplicationContext(), CustomerSignIn.class);
        userAccount.putExtras(intentBundle);
        startActivityForResult(userAccount, 3);
    }

    public void customerRegistration(View v){
        String name = getUsername();
        boolean state = getLoginState();
        intentBundle = new Bundle();
        intentBundle.putString(Constants.CUSTOMER_USERNAME, name);
        intentBundle.putBoolean(Constants.CUSTOMER_LOGIN_STATE, state);
        intentBundle.putParcelableArrayList(Constants.PARSELABLE_CART_ITEM_LIST, itemsCartList);
        Intent userAccount = new Intent(getApplicationContext(), CustomerRegistration.class);
        userAccount.putExtras(intentBundle);
        startActivityForResult(userAccount, 4);
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

    /* This method allows any data that we want to save from intent to intent when back button on any given intent is pressed */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2) {  // View Items Intent back button pressed
            if(resultCode == RESULT_OK){
                String name = data.getStringExtra(Constants.CUSTOMER_USERNAME);
                boolean state = data.getBooleanExtra(Constants.CUSTOMER_LOGIN_STATE, false);
                setUsername(name);
                setLoginState(state);
                ArrayList<CartItem> list = data.getParcelableArrayListExtra(Constants.PARSELABLE_CART_ITEM_LIST);
                setItemsCartList(new ArrayList<CartItem>());
                for(int i = 0; i < list.size(); i++){
                    addToCustomerCartList(list.get(i));
                }
            }
        }
        if(requestCode == 3) {  // Customer SignIn Intent back button pressed
            if(resultCode == RESULT_OK){
                String name = data.getStringExtra(Constants.CUSTOMER_USERNAME);
                boolean state = data.getBooleanExtra(Constants.CUSTOMER_LOGIN_STATE, false);
                setUsername(name);
                setLoginState(state);
                ArrayList<CartItem> list = data.getParcelableArrayListExtra(Constants.PARSELABLE_CART_ITEM_LIST);
                setItemsCartList(new ArrayList<CartItem>());
                for(int i = 0; i < list.size(); i++){
                    addToCustomerCartList(list.get(i));
                }
            }
        }
        if(requestCode == 4) {  // Customer Register Intent back button pressed
            if(resultCode == RESULT_OK){
                String name = data.getStringExtra(Constants.CUSTOMER_USERNAME);
                boolean state = data.getBooleanExtra(Constants.CUSTOMER_LOGIN_STATE, false);
                setUsername(name);
                setLoginState(state);
                ArrayList<CartItem> list = data.getParcelableArrayListExtra(Constants.PARSELABLE_CART_ITEM_LIST);
                setItemsCartList(new ArrayList<CartItem>());
                for(int i = 0; i < list.size(); i++){
                    addToCustomerCartList(list.get(i));
                }
            }
        }
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

    /* This ensures that the view cart button and checkout button on the toolbar is not visible in this activity */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.viewCart).setVisible(false);
        menu.findItem(R.id.checkOut).setVisible(false);
        menu.findItem(R.id.log_in).setVisible(false);
        return true;
    }
}
