package com.example.roshan.personalassistant;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {

    private EditText txt;
    private ImageButton btn;
    Cursor cursor;
    String name,phonenumber;
    ArrayList<String> phonenumberl ;
    ArrayList<String> namel ;
    String[] st,kt;
    int j,k;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt=(EditText) findViewById(R.id.editText);
        btn=(ImageButton)findViewById(R.id.imageButton);
        namel = new ArrayList<String>();
        phonenumberl = new ArrayList<String>();
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vote();
            }
        });
    }
    public void contact()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, 10011);
    }
    public void alarm()
    {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "New Alarm");
        i.putExtra(AlarmClock.EXTRA_HOUR, j);
        i.putExtra(AlarmClock.EXTRA_MINUTES, k);
        startActivity(i);
    }
    public void call()
    {
        j=0;
        try
        {
            NumberFormat.getInstance().parse(st[1]);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+st[1]+st[2]+st[3]));
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
            return;
        }
        catch(ParseException e)
        {
        }

        st[1]=st[1].substring(0,1).toUpperCase()+st[1].substring(1);
        if(k==3)
        {
            st[2]=st[2].substring(0,1).toUpperCase()+st[2].substring(1);
            st[1]=st[1]+" "+st[2];
        }
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(name.equals(st[1]))
            {
//                namel.add(name);
//                phonenumberl.add(phonenumber);
                j++;
                break;
            }
        }
        cursor.close();
        if(j==0)
        {
            Toast.makeText(getApplicationContext(),"Contact Not Found",Toast.LENGTH_SHORT).show();
        }
        else if(j>0)
        {
            Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phonenumber));
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
        }
    }
    public void vote() {
        txt.setText("");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Listening");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Sorry! Your device doesn't support speech input",Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        st = result.get(0).split(" ");
                        k = st.length;
                        txt.setText(result.get(0));
                 //  Toast.makeText(getApplicationContext(),String.valueOf(k),Toast.LENGTH_SHORT).show();
                    if(result.get(0).equals("open contact"))
                    {
                        contact();
                    }
                    else if(st[0].equals("call"))
                    {
                        call();
                    }
                    else if(st[0].equals("set")&&st[1].equals("alarm"))
                    {
                        if(k!=4)
                        {
                            Toast.makeText(getApplicationContext(),"Wrong Instruction",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        int u=st[2].indexOf(":");
                        try {
                            if(u==-1)
                            {
                                j = Integer.parseInt(st[2]);
                                k=0;
                            }
                            else {
                                j = Integer.parseInt(st[2].substring(0, u));
                                k = Integer.parseInt(st[2].substring(u + 1));
                            }
                        }catch (NumberFormatException e)
                        {
                            Toast.makeText(getApplicationContext(),"Wrong Instruction",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if(j>12 || k>59)
                        {
                            Toast.makeText(getApplicationContext(),"Wrong Instruction",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if(st[3].equals("p.m"))
                        {
                            j=j+12;
                        }
                        alarm();
                    }
                    else if(st[1].equals("Play")&&st[2].equals("Store"))
                    {
                        String url = "https://play.google.com/store/apps";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                    }
                    else if(st[0].equals("download"))
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://search?q="+st[1]+"&c=apps"));
                        startActivity(intent);
                    }
                    else if(st[0].equals("open") && st[1].equals("gallery"))
                    {
                        Intent galleryIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent,100 );
                    }
                    else if(st[0].equals("open") && st[1].equals("Facebook"))
                    {
                        Intent intent = new Intent("android.intent.category.LAUNCHER");
                        intent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");
                        startActivity(intent);
                    }
                    else if(st[0].equals("open") && st[1].equals("settings"))
                    {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                    else if(st[0].equals("open") && (st[1].equals("Map")||st[1].equals("map")))
                    {
                        for(int i=4;i<k;i++)
                        {
                            st[3]=st[3]+" "+st[i];
                        }
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+st[3]);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                    else
                    {
                     //   txt.setText("Wrong Instruction");
                        Toast.makeText(getApplicationContext(),"Wrong Instruction",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                break;
            }

        }
    }
}