package vn.com.jvs.updatecontacts;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.com.jvs.updatecontacts.Adapter.AdapterView;
import vn.com.jvs.updatecontacts.Object.Contracts;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSIONS = 100;
    private ProgressBar _progressBar;
    private ArrayList<Contracts> _contractsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
                // Request
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                        REQUEST_PERMISSIONS);
            }
            else {
                getContactList();
            }
        } else {
            getContactList();
        }

        _progressBar = findViewById(R.id.progressbar);
        _progressBar.setVisibility(View.GONE);

        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _progressBar.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < _contractsList.size(); i++) {
                            _progressBar.setMax(_contractsList.size());
                            try {
                                String newNumber = buildNewNulber(_contractsList.get(i).get_phone());
                                updateContactNew(_contractsList.get(i).get_id(), newNumber, _contractsList.get(i).get_type());
                                _progressBar.setProgress(i);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        _progressBar.setVisibility(View.GONE);
                        getContactList();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (!alert.isShowing()) alert.show();
            }
        });
    }

    private void getContactList() {
        _contractsList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {

                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                            String type = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.TYPE));

                            phoneNo = phoneNo.replace("-", "");
                            phoneNo = phoneNo.replace(" ", "");
                            phoneNo = phoneNo.replace("(", "");
                            phoneNo = phoneNo.replace(")", "");
                            String phoneTemp = phoneNo.replace("+84", "0");
                            // Chi lay 11 so
                            if (phoneTemp.length() >= 11) {

                                Contracts contracts = new Contracts(name, phoneNo, id, type);
                                _contractsList.add(contracts);
                            }
                        }
                        pCur.close();
                    }
                }
            }
        }
        if (cur!=null){
            cur.close();
        }

        ListView listView = findViewById(R.id.list_item);
        AdapterView adapterView = new AdapterView(MainActivity.this, _contractsList);
        listView.setAdapter(adapterView);
        TextView textViewNull = findViewById(R.id.list_item_null);
        if (_contractsList.size() == 0) {
            textViewNull.setVisibility(View.VISIBLE);
            listView.setEmptyView(textViewNull);
        } else {
            textViewNull.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                // Do not care about the results returned
                // Switch to the next screen
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Request
                    if (Build.VERSION.SDK_INT >= 23) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                                REQUEST_PERMISSIONS);
                    } else {
                        getContactList();
                    }
                }
            }
        }
    }

    private String buildNewNulber(String input) {
        String newNumber = "";
        if (input.startsWith("+84")) {
            input = input.replace("+84","0");
        }
        String startInput = input.substring(0, 4);
        String endInput = input.substring(4, input.length());

        CheckBox checkBox = findViewById(R.id.check_84);
        boolean check84 = checkBox.isChecked();

        // Viettell
        if (startInput.startsWith("0169")) {
            if (check84) {
                newNumber = "+8439" + endInput;
            } else {
                newNumber = "039" + endInput;
            }
        }
        if (startInput.startsWith("0168")) {
            if (check84) {
                newNumber = "+8438" + endInput;
            } else {
                newNumber = "038" + endInput;
            }
        }
        if (startInput.startsWith("0167")) {
            if (check84) {
                newNumber = "+8437" + endInput;
            } else {
                newNumber = "037" + endInput;
            }
        }
        if (startInput.startsWith(" 0166")) {
            if (check84) {
                newNumber = "+8436" + endInput;
            } else {
                newNumber = "036" + endInput;
            }
        }
        if (startInput.startsWith("0165")) {
            if (check84) {
                newNumber = "+8435" + endInput;
            } else {
                newNumber = "035" + endInput;
            }
        }
        if (startInput.startsWith("0164")) {
            if (check84) {
                newNumber = "+8434" + endInput;
            } else {
                newNumber = "034" + endInput;
            }
        }
        if (startInput.startsWith("0163")) {
            if (check84) {
                newNumber = "+8433" + endInput;
            } else {
                newNumber = "033" + endInput;
            }
        }
        if (startInput.startsWith("0162")) {
            if (check84) {
                newNumber = "+8432" + endInput;
            } else {
                newNumber = "032" + endInput;
            }
        }

        // Mobi
        if (startInput.startsWith(" 0120")) {
            if (check84) {
                newNumber = "+8470" + endInput;
            } else {
                newNumber = "070" + endInput;
            }
        }
        if (startInput.startsWith("0121")) {
            if (check84) {
                newNumber = "+8479" + endInput;
            } else {
                newNumber = "079" + endInput;
            }
        }
        if (startInput.startsWith("0122")) {
            if (check84) {
                newNumber = "+8477" + endInput;
            } else {
                newNumber = "077" + endInput;
            }
        }
        if (startInput.startsWith("0126")) {
            if (check84) {
                newNumber = "+8476" + endInput;
            } else {
                newNumber = "076" + endInput;
            }
        }
        if (startInput.startsWith("0128")) {
            if (check84) {
                newNumber = "+8478" + endInput;
            } else {
                newNumber = "078" + endInput;
            }
        }

        // Vina
        if (startInput.startsWith("0124")) {
            if (check84) {
                newNumber = "+8484" + endInput;
            } else {
                newNumber = "084" + endInput;
            }
        }
        if (startInput.startsWith("0127")) {
            if (check84) {
                newNumber = "+8481" + endInput;
            } else {
                newNumber = "081" + endInput;
            }
        }
        if (startInput.startsWith("0129")) {
            if (check84) {
                newNumber = "+8482" + endInput;
            } else {
                newNumber = "082" + endInput;
            }
        }
        if (startInput.startsWith("0123")) {
            if (check84) {
                newNumber = "+8483" + endInput;
            } else {
                newNumber = "083" + endInput;
            }
        }
        if (startInput.startsWith("0125")) {
            if (check84) {
                newNumber = "+8485" + endInput;
            } else {
                newNumber = "085" + endInput;
            }
        }

        // Vietnam
        if (startInput.startsWith("0186")) {
            if (check84) {
                newNumber = "+8456" + endInput;
            } else {
                newNumber = "056" + endInput;
            }
        }
        if (startInput.startsWith("0188")) {
            if (check84) {
                newNumber = "+8458" + endInput;
            } else {
                newNumber = "058" + endInput;
            }
        }

        // Gtel
        if (startInput.startsWith("0199")) {
            if (check84) {
                newNumber = "+8459" + endInput;
            } else {
                newNumber = "059" + endInput;
            }
        }

        return newNumber;
    }

    public void updateContactNew (String contactId, String newNumber, String phoneType)
            throws RemoteException, OperationApplicationException {

        //ASSERT: @contactId alreay has a work phone number
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='"  +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
        String[] phoneArgs = new String[]{contactId, phoneType};
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectPhone, phoneArgs)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                .build());
        this.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    }
}
