package com.shoppingpoint;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SignInDialog extends DialogFragment implements View.OnClickListener {

    Communicator communicator;
    public EditText usernameText, passwordText;
    private Button signInButton, cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.signin_dialog, null);
        usernameText = (EditText) view.findViewById(R.id.usernameEditText);
        passwordText = (EditText) view.findViewById(R.id.passwordEditText);
        signInButton = (Button) view.findViewById(R.id.logInButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        /* Applying Style to buttons */
        signInButton.setTextAppearance(getActivity(), R.style.dialogButtonsFontStyle);
        cancelButton.setTextAppearance(getActivity(), R.style.dialogButtonsFontStyle);
        signInButton.setOnClickListener(this); // this allows buttons to be clicked
        cancelButton.setOnClickListener(this); // this allows buttons to be clicked
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.logInButton){
            String name = usernameText.getText().toString();
            String pswd = passwordText.getText().toString();
            communicator.onSignInDialogMessage(name, pswd);
            dismiss();
        }
        if(v.getId() == R.id.cancelButton){
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
        void onSignInDialogMessage(String name, String password);
    }
}
