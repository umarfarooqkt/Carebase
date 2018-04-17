package com.hci.carebase.ui.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hci.carebase.R;
import com.hci.carebase.data.datasource.CollectionsProvider;
import com.hci.carebase.data.interfaces.PatientSource;
import com.hci.carebase.data.interfaces.PhotoSource;
import com.hci.carebase.domain.Patient;
import com.hci.carebase.util.Faker;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DebugFragment extends Fragment {

    @BindView(R.id.debug_results) TextView results;
    @BindView(R.id.debug_patient_get_uid) EditText patientGet;
    @BindView(R.id.debug_patient_lname) EditText patientLName;
    @BindView(R.id.debug_patient_fname) EditText patientFName;
    @BindView(R.id.debug_patient_healthcard) EditText patientHnum;
    @BindView(R.id.debug_results_image_view)
    ImageView img;

    PatientSource provider = CollectionsProvider.patients();

    public static DebugFragment newInstance() {
        DebugFragment fragment = new DebugFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_debug, container, false);

        ButterKnife.bind(this,rootView);

        return rootView;
    }

    @OnClick(R.id.debug_but_patient_update)
    public void onPatientUpdate(View v) {
        final String newLName = patientLName.getText().toString();
        final String newFName = patientFName.getText().toString();
        final String newHNum= patientHnum.getText().toString();
        final String id = patientGet.getText().toString();

        provider.getPatient(id, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Patient p = dataSnapshot.getValue(Patient.class);
                        p.setFirstName(newFName);
                        p.setLastName(newLName);
                        p.setHealthCardNumber(newHNum);

                        provider.updatePatient(id,p,new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                results.setText("Updated! ");
                                Toast.makeText(getContext(), "Updated",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @OnClick(R.id.debug_but_patient_delete)
    public void onPatientDelete(View v) {
        final String id = patientGet.getText().toString();
        provider.deletePatient(id, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Deleted!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.debug_but_patient_create)
    public void onPatientCreate(View v) {

        final Patient p = Faker.fakePatient();
        Log.d("Patient",p.toString() );
        String fakeUser = UUID.randomUUID().toString();

        provider.createPatient(p, fakeUser, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String result = "Create Patient "+p.getFirstName()+" "+p.getLastName();
                results.setText(result);
            }
        });
    }

    @OnClick(R.id.debug_but_patient_get)
    public void onPatientGet(View v) {
        String id = patientGet.getText().toString();
//        String id = "-L8bwHp6Kr6GoU1DvU4Z";

        provider.getPatient(id, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Patient p = dataSnapshot.getValue(Patient.class);

                if (p!=null) {
                    Log.d("Fetched: ",p.toString());
                    results.setText("Fetched! " + p.toString());
                } else {
                    Log.d("Fetched: ","No user");
                    results.setText("No user");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.debug_but_patient_fake_hc)
    public void onFakeHC(View v) {
        PhotoSource sr  = CollectionsProvider.photos();

        sr.getFakeHealthCardPhoto(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
             Glide.with(getContext())
                        .load(uri) // the uri you got from Firebase
                        .centerCrop()
                        .into(img); //Your imageView variable
                img.invalidate();
            }
        });
    }

    @OnClick(R.id.debug_but_patient_fake_user_image)
    public void onFakeUserImgClick(View v) {
        PhotoSource sr  = CollectionsProvider.photos();
        sr.getRandomPhoto(true, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext())
                        .load(uri) // the uri you got from Firebase
                        .centerCrop()
                        .into(img); //Your imageView variable
                img.invalidate();
            }
        });
    }


}
