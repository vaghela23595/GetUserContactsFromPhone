package com.contact.getusercontactsfromphone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.contact.getusercontactsfromphone.R;
import com.contact.getusercontactsfromphone.adapter.CustomAdapter;
import com.contact.getusercontactsfromphone.model.ContactModel;

import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<ContactModel> contactModelArrayList;
    public static int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        //--------------If you android version is marsmellow or above then required to runtime permission---------------//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {  //------------here Checke perrmission granted or not---------------------//
                getContactAndSetContactAdapter();
            } else {
                requestPermission();            //-------------here request permission-------------------------//
            }
        } else {
            getContactAndSetContactAdapter();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, PERMISSION_REQUEST_CODE);

    }

    public void getContactAndSetContactAdapter() {
        contactModelArrayList = new ArrayList<>();
//-------------------Here get contactname nd number using content provider ------------------------//

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            ContactModel contactModel = new ContactModel();
            contactModel.setName(name);
            contactModel.setNumber(phoneNumber);
            contactModelArrayList.add(contactModel);
            Log.d("name>>", name + "  " + phoneNumber);
        }
        phones.close();

        customAdapter = new CustomAdapter(this, contactModelArrayList);
        listView.setAdapter(customAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted) {
                        getContactAndSetContactAdapter();
                    }
                    // Toast.makeText(MainActivity.this, "Permission Granted, Now you can access Contact", Toast.LENGTH_SHORT).show();
                    else {

                        Toast.makeText(MainActivity.this, "Permission Granted, Now you can access Contact", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
                                showMessageOKCancel("You need to allow access to read contact permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_CONTACTS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
