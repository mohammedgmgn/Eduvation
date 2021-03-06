package org.mat.eduvation;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mat.eduvation.LocaL_Database.DatabaseConnector;


public class Signupfrag extends Fragment {

    private TextView name, email, passwoard, confirmpass, company, birthday;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private UserModel userModel;
    private DatabaseReference users;
    private DatabaseConnector databaseConnector;
    private TextInputLayout inputLayoutmail, inputLayoutpassword, inputLayoutUsername;
    private ProgressBar progressBar;
    public static String keyGenerator(String[] s) {
        String key = "edu";
        for (String value : s) {
            key += value;
        }
        return key;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseConnector = new DatabaseConnector(getActivity());
        // databaseConnector.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_signupfrag, container, false);
        name=(TextView) root.findViewById(R.id.signupusername);
        email= (TextView) root.findViewById(R.id.signupemailid);
        passwoard=(TextView)root.findViewById(R.id.signuppassid);
        confirmpass=(TextView)root.findViewById(R.id.signupconfirmpassid);
        company = (TextView) root.findViewById(R.id.signupCompany);
        birthday = (TextView) root.findViewById(R.id.birthday);
        Button register = (Button) root.findViewById(R.id.signupbtnid);
        inputLayoutmail = (TextInputLayout) root.findViewById(R.id.TextInputLayoutEmail);
        inputLayoutpassword = (TextInputLayout) root.findViewById(R.id.TextInputLayoutPass);
        inputLayoutUsername = (TextInputLayout) root.findViewById(R.id.TextInputLayoutUsername);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar2);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");

        userModel = new UserModel();

        auth=FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwoard.getText().toString().equals(confirmpass.getText().toString()))
                {
                    if (isNetworkAvailable())
                        signUp();
                    else
                        Toast.makeText(getActivity(), "Please connect to internet", Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(getActivity(), "password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    public  void signUp(){

        String mail=email.getText().toString();
        String pass=passwoard.getText().toString();
        final String username=name.getText().toString();
        if (TextUtils.isEmpty(username)) {
            inputLayoutUsername.setError("username cannot be blank");
            return;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(mail)) {
            inputLayoutmail.setError("email cannot be blank");
            return;
        } else {
            inputLayoutmail.setErrorEnabled(false);

        }

        if (TextUtils.isEmpty(pass)) {
            inputLayoutUsername.setError("password cannot be blank");
            return;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }
        if (pass.length() < 6) {
            inputLayoutpassword.setError("Password too short, enter minimum 6 characters!");
            return;
        } else {
            inputLayoutpassword.setErrorEnabled(false);

        }
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();


                    writeNewUser(name.getText().toString(), email.getText().toString().toLowerCase(),
                            company.getText().toString(), String.valueOf(birthday.getText()));
                    databaseConnector.open();
                    databaseConnector.insertUser(name.getText().toString(), company.getText().toString(), String.valueOf(birthday.getText())
                            , email.getText().toString().toLowerCase(), UserModel.KEY);
                    databaseConnector.close();
                    SaveSharedPreference.setUserName(getContext(), email.getText().toString().toLowerCase());

                    startActivity(new Intent(getContext(), navigation.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    getActivity().finish();
                }
                else {
                    // if email already registerd
                    if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                        Toast.makeText(getActivity(), "The email address is already exist", Toast.LENGTH_SHORT).show();

                    }

                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    private void writeNewUser(String name, String email, String company, String birthday) {
        userModel = new UserModel(name, company, birthday, email.toLowerCase());
        String[] fields = email.split("\\.");
        UserModel.KEY = keyGenerator(fields);
        users.child(UserModel.KEY).setValue(userModel);
    }
}