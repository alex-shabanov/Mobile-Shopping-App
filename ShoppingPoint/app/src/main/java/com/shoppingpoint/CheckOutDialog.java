package com.shoppingpoint;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckOutDialog extends DialogFragment implements View.OnClickListener {

    Communicator communicator;
    private RadioGroup radioGroup;
    public TextView totalPriceTextView;
    public EditText nameText, creditCardNumberText, cardExpiryText, cvvNumberText;
    private Button checkOutButton, checkOutCancelButton;
    protected String creditCardType;
    protected double totalPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.checkout_dialog, null);
        radioGroup = (RadioGroup) view.findViewById(R.id.credit_card_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.visaRadioButton){
                    creditCardType = Constants.VISA_CARD;
                }
                else if(checkedId == R.id.masterCardRadioButton){
                    creditCardType = Constants.MASTER_CARD;
                }
            }
        });
        nameText = (EditText) view.findViewById(R.id.nameEditText);
        creditCardNumberText = (EditText) view.findViewById(R.id.creditCartNumberEditText);
        cardExpiryText = (EditText) view.findViewById(R.id.creditCardExpiryDateText);
        cvvNumberText = (EditText) view.findViewById(R.id.cvvNumberEditText);
        totalPriceTextView = (TextView) view.findViewById(R.id.totalAmountTextView);
        checkOutButton = (Button) view.findViewById(R.id.check_out_button);
        checkOutCancelButton = (Button) view.findViewById(R.id.check_out_cancel_button);
        ArrayList<CartItem> list = CustomerCart.getCartItemsList();
        CartItem tempItem;
        totalPrice = 0.0;
        double price = 0.0;
        /* this extracts only the number from the pricing */
        for(int i = 0; i < list.size(); i++){
            tempItem = list.get(i);
            String str = tempItem.getPrice();
            Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
            Matcher m = p.matcher(str);
            while(m.find()) {
                price = Double.parseDouble(m.group(1));
            }
            totalPrice += tempItem.getQuantityOrdered() * price;
        }
        totalPriceTextView.setText(Constants.TOTAL_AMOUNT_CHARGED + " " + String.format("%.2f", totalPrice));
        /* Applying Style to buttons */
        checkOutButton.setTextAppearance(getActivity(), R.style.dialogButtonsFontStyle);
        checkOutCancelButton.setTextAppearance(getActivity(), R.style.dialogButtonsFontStyle);
        checkOutButton.setOnClickListener(this);       // this allows buttons to be clicked
        checkOutCancelButton.setOnClickListener(this); // this allows buttons to be clicked
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.check_out_button){
            String name = nameText.getText().toString();
            String cardNumber = creditCardNumberText.getText().toString();
            String cardExpiryDate = cardExpiryText.getText().toString();
            String cvvNumber = cvvNumberText.getText().toString();
            communicator.onCheckOutDialogMessage(name, cardNumber, cardExpiryDate, cvvNumber, creditCardType);
            dismiss();
        }
        if(v.getId() == R.id.check_out_cancel_button){
            dismiss();
        }
    }

    /* This method allows out Main Activity to get attached to SignIn Dialog Fragment so that the data can be exchanged
    *  This assigns our Main Activity reference to this Sign In Dialog class
    *  */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    interface Communicator {
        void onCheckOutDialogMessage(String name, String cardNumber, String cardExpiryDate, String cvvNumber, String cardType);
    }
}
