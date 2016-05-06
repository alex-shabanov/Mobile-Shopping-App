package com.shoppingpoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.File;

public class ViewProductItem extends AppCompatActivity {

    private ImageView itemImage;
    private Bundle productInfoBundle;
    private Intent viewProductIntent;
    private CartItem productItem;
    private TextView productNameView, productPriceView, productShortView, productDetailView;
    public static final String TAG = Constants.VIEW_PRODUCT_ITEM_ACTIVITY;
    void setProductItem(CartItem item){ this.productItem = item; }
    CartItem getProductItem(){ return this.productItem; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null){
            viewProductIntent = getIntent();
            if(viewProductIntent != null) {
                /* Retrieving information from intent and bundle */
                productInfoBundle = viewProductIntent.getBundleExtra(Constants.PRODUCT_BUNDLE);
                CartItem item = productInfoBundle.getParcelable(Constants.PRODUCT_INFO);
                setProductItem(item);
            }
            else {

            }
        }
        else if(savedInstanceState != null){
            CartItem item = savedInstanceState.getParcelable(Constants.PRODUCT_INFO);
            setProductItem(item);
        }

        itemImage = (ImageView) findViewById(R.id.pictureView);
        productNameView = (TextView) findViewById(R.id.product_name);
        productPriceView = (TextView) findViewById(R.id.product_price);
        productShortView = (TextView) findViewById(R.id.product_short_description);
        productDetailView= (TextView) findViewById(R.id.product_detail_description);

        CartItem item = getProductItem();
        String itemName = item.getSpecificname();
        String itemPrice = item.getPrice();
        String shortDescription = item.getShortdescription();
        String longDescription = item.getLongdescription();
        String imageName = item.getImagename();

         /* LOADING IMAGES FROM INTERNAL STORAGE USING PICASSO LIBRARY*/
        Picasso.with(this).load(new File(Constants.IMAGE_DIRECTORY_PATH + "/" + imageName)).into(itemImage);

        productNameView.setText(itemName);
        productPriceView.setText(itemPrice);
        productShortView.setText(shortDescription);
        productDetailView.setText(longDescription);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        CartItem item = getProductItem();
        savedInstanceState.putParcelable(Constants.PRODUCT_INFO, item);
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
        menu.findItem(R.id.viewCart).setVisible(false);
        menu.findItem(R.id.checkOut).setVisible(false);
        menu.findItem(R.id.log_in).setVisible(false);
        return true;
    }
}
