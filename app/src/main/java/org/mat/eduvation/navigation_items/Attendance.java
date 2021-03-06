package org.mat.eduvation.navigation_items;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import org.mat.eduvation.ImageModel;
import org.mat.eduvation.R;
import org.mat.eduvation.UserModel;
import org.mat.eduvation.adapters.attendanceAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 */
public class Attendance extends Fragment {
    private List<UserModel> UserModellist;
    private List<ImageModel> UserImagelist;
    private attendanceAdapter adapter;
    private HashMap<String, Bitmap> hashMapwithImages;
    private Firebase refUsers, refImages;
    ValueEventListener valuelisten1;
    ValueEventListener valuelisten2;

    public Attendance() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getContext());
        UserModellist = new ArrayList<>();
        hashMapwithImages = new HashMap<>();
        adapter = new attendanceAdapter(hashMapwithImages, UserModellist);
        refUsers = new Firebase("https://eduvation-7aff9.firebaseio.com/users");
        refImages = new Firebase("https://eduvation-7aff9.firebaseio.com/images");
        refImages = new Firebase("https://eduvation-7aff9.firebaseio.com/images");


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_attendance, container, false);


        Toast.makeText(getContext(), "Loading data please wait", Toast.LENGTH_LONG).show();
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rectcleattendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Get a reference to our posts
        if (isNetworkAvailable()) {

             valuelisten1=new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, ImageModel> data = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, ImageModel>>() {
                    });
                    if (data != null) {
                        Log.d("ImageModel data", String.valueOf(data.size()));
                        UserImagelist = new ArrayList<>(data.values());
                        hashMapwithImages = mergeUsersListWithImageList(UserModellist, UserImagelist);
                        // bitmapList=convert(UsersWithImagesList);
                        adapter.updateData(hashMapwithImages, UserModellist);
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getContext(), "The read failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
            refImages.addValueEventListener(valuelisten1);

            valuelisten2=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //   Toast.makeText(getContext(),"success",Toast.LENGTH_LONG).show();
                    HashMap<String, UserModel> data = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, UserModel>>() {
                    });
                    if (data != null) {
                        Log.d("UserModel data", String.valueOf(data.size()));
                        UserModellist = new ArrayList<>(data.values());
                        Collections.sort(UserModellist, new Comparator<UserModel>() {
                            public int compare(UserModel u1, UserModel u2) {
                                return u1.getName().toLowerCase().compareTo(u2.getName().toLowerCase());
                            }
                        });
                        hashMapwithImages = mergeUsersListWithImageList(UserModellist, UserImagelist);
                        //bitmapList=convert(UsersWithImagesList);
                        adapter.updateData(hashMapwithImages, UserModellist);
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getContext(), "The read failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

            refUsers.addValueEventListener(valuelisten2);
        } else
            Toast.makeText(getActivity(), "Please connect to internet to get attendees", Toast.LENGTH_LONG).show();
        return root;
    }

    private HashMap<String, Bitmap> mergeUsersListWithImageList(List<UserModel> userModellist, List<ImageModel> userImagelist) {
        HashMap<String, Bitmap> hashMap = new HashMap<>();

        if (userImagelist != null && userModellist != null) {
            for (int i = 0; i < userModellist.size(); i++) {
                for (int j = 0; j < userImagelist.size(); j++) {
                    if (userModellist.get(i).getEmail().toLowerCase().equals(userImagelist.get(j).getEmail().toLowerCase())) {
                        //hashMap.put(userModellist.get(i).getEmail().toLowerCase(), userImagelist.get(j));
                        hashMap.put(userModellist.get(i).getEmail().toLowerCase(), Profile.decodeBase64(userImagelist.get(j).getImagestr()));
                        // Log.d("mergeUsersList", String.valueOf(hashMap.get(userModellist.get(i).getEmail().toLowerCase()).getEmail()));
                        //Log.d("mergeUsersList", String.valueOf(hashMap.get(userModellist.get(i).getEmail().toLowerCase()).getImagestr()));
                    }
                }
            }
        }
        // adapter = new attendanceAdapter(getContext(),convert(hashMap),UserModellist, UsersWithImagesList);
        //recyclerView.setAdapter(adapter);
        return hashMap;
    }
    // public List<Bitmap>convert(HashMap<String, ImageModel> mUsersWithImagesList) {

    //List<Bitmap> mylistimage = new ArrayList<>();
    //Iterator myVeryOwnIterator = mUsersWithImagesList.keySet().iterator();
    //while (myVeryOwnIterator.hasNext()) {
    //String key = (String) myVeryOwnIterator.next();
    //String value = mUsersWithImagesList.get(key).getImagestr();
    //     Bitmap myBitmapAgain = Profile.decodeBase64(value);
    // mylistimage.add(myBitmapAgain);
    // }
    // return mylistimage;
    //}
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onPause() {
        Log.d("onPause", "started");
        //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        super.onPause();

    }

    @Override
    public void onStop() {
        Log.d("onPause", "started");
        //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        super.onStop();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

            refImages.removeEventListener(valuelisten1);
            refUsers.removeEventListener(valuelisten2);


    }
}