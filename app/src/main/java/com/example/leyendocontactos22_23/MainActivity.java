package com.example.leyendocontactos22_23;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISO_CONTACTOS = 100;
    private static final int PERMISO_SMSS = 101;
    ListView listaContactos;
    Button buttonContactos, buttonSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listaContactos = findViewById(R.id.listaContactos);
        buttonContactos = findViewById(R.id.buttonContactos);
        buttonSMS = findViewById(R.id.buttonSMS);

        buttonContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permisoChequeado = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
                if (permisoChequeado == PackageManager.PERMISSION_GRANTED) {
                    mostrarContactos();
                }else{
                    //Debo pedir los permisos
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},
                            PERMISO_CONTACTOS);
                }
            }
        });
        buttonSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permisoChequeado = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS);
                if (permisoChequeado == PackageManager.PERMISSION_GRANTED) {
                    mostrarSMSoMMS();
                }else{
                    //Debo pedir los permisos
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.READ_CONTACTS,Manifest.permission.READ_SMS},
                            PERMISO_SMSS);
                }
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode== PERMISO_CONTACTOS){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                mostrarContactos();
            }else{
                Toast.makeText(this, "Si no me das permisos no te puedo mostrar los contactos", Toast.LENGTH_SHORT).show();
            }

        }
        if (requestCode== PERMISO_SMSS){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                mostrarSMSoMMS();
            }else{
                Toast.makeText(this, "Si no me das permisos no te puedo mostrar los SMSs", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void mostrarContactos() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        ArrayList contactos = new ArrayList<String>();

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String telefono = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactos.add("Nombre: " + nombre + "\nTeléfono: " + telefono);

        }
        cursor.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, contactos);
        listaContactos.setAdapter(arrayAdapter);
    }
    private void mostrarSMSoMMS(){

        Uri uri = Uri.parse("content://mms-sms/conversations");
        Cursor cursor = getContentResolver().query(uri,
                null, null, null,null);
        ArrayList smss = new ArrayList<String>();

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex("address"));
            @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex("body"));
            smss.add("Número: " + number + "\nMensaje: " + body);

        }
        cursor.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, smss);
        listaContactos.setAdapter(arrayAdapter);
    }


}