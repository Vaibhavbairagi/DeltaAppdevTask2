package com.vaibhav.fifafixtures;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import jp.wasabeef.blurry.Blurry;

public class EditorActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public static final int REQUEST_CAMERA_CODE = 10;
    public static final int EXTERNAL_REQUEST_CODE = 13;
    public static final int INTERNAL_REQUEST_CODE = 19;
    Spinner team1spinner,team2spinner,groupnamespinner;
    MyDatabase myDB = new MyDatabase(this);
    Button done;
    int gallerycheck=0;
    Context context;
    Bitmap bmp1=null,bmp2=null;
    Toolbar toolbar;
    ImageView team1logo,team2logo,calender,ediotrback,clock,venueselect;
    ArrayAdapter team1adapter,team2adapter,groupadapter;
    RelativeLayout relativeLayout;
    EditText datetext,timetext,venuetext;
    View grpview,team1view,team2view;
    int REQUEST_CAMERA=1,REQUEST_GALLERY=0,REQUEST_CROP=2,t;
    String mgrp,mteam1,mteam2,mdate=null,mtime=null,mvenue=null;
    byte[] img1,img2;
    ArrayList<String> team;
    ArrayList<matchinfo> editorlist;
    String[] team1array,team2array;
    public static matchinfo editorinfo,new_info;
    int groupposition,hour,minute,fhour,fminute;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listTools.ID=0;
        myDB.open();
        context=this;
        setContentView(R.layout.activity_editor);
        SharedPreferences sPrefs = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        listTools.ID = sPrefs.getInt("id", 0);
        initializeUI();
        setImageClickListeners();
        spinnerActivities();
        done.setOnClickListener(DoneClick);
        calender.setOnClickListener(datepickerlistener);
        clock.setOnClickListener(timepicker);
        venueselect.setOnClickListener(venuelistopener);
        DateDialogue.view= datetext;
        if (FixtureActivity.editor==1){
            datetext.setText(editorinfo.cdate);
            timetext.setText(editorinfo.ctime);
            venuetext.setText(editorinfo.cvenue);
            team1logo.setImageBitmap(BitmapFactory.decodeByteArray(editorinfo.logo1,0,editorinfo.logo1.length));
            img1=editorinfo.logo1;
            img2=editorinfo.logo2;
            bmp1=BitmapFactory.decodeByteArray(editorinfo.logo1,0,editorinfo.logo1.length);
            team2logo.setImageBitmap(BitmapFactory.decodeByteArray(editorinfo.logo2,0,editorinfo.logo2.length));
            bmp2=BitmapFactory.decodeByteArray(editorinfo.logo2,0,editorinfo.logo2.length);
        }
        toolbar= findViewById(R.id.editortoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Fixture");
    }

    public View.OnClickListener timepicker=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar c=Calendar.getInstance();
            hour=c.get(Calendar.HOUR_OF_DAY);
            minute=c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog= new TimePickerDialog(EditorActivity.this,EditorActivity.this,hour,minute, true);
            timePickerDialog.show();
        }
    };

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        fhour=hourOfDay;
        fminute=minute;
        String str;
        if (minute<10){
            str=fhour+" : 0"+fminute;}
        else{
        str=fhour+" : "+fminute;}
        timetext.setText(str);
    }

    public View.OnClickListener venuelistopener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CharSequence[] chooseitems={"Samara Arena","Nizhny Novgorod Stadium","Volgograd Arena","Ekaterinburg Arena","Mordovia Arena",
                    "Rostov Arena","Kaliningrad Stadium","Kazan Arena","Spartak Stadium, Moscow","Fisht Stadium, Sochi","Saint Petersburg Stadium","Luzhniki Stadium, Moscow"};
            AlertDialog.Builder builder=new AlertDialog.Builder(EditorActivity.this);
            builder.setTitle("Select Venue");
            builder.setItems(chooseitems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    venuetext.setText(chooseitems[which]);
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    };

    public View.OnClickListener datepickerlistener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DateDialogue dateDialogue=new DateDialogue();
            FragmentTransaction ft= getFragmentManager().beginTransaction();
            dateDialogue.show(ft,"DatePicker");
        }
    };

    public View.OnClickListener DoneClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mdate=datetext.getText().toString();
            mtime=timetext.getText().toString();
            mvenue=venuetext.getText().toString();
            if(bmp1==null || bmp2==null || mdate.equals("") || mtime.equals("") || mvenue.equals("")){
                showmsgs();
            }
            else {
                if(FixtureActivity.editor==1){
                    new_info=new matchinfo(img1,img2,mgrp,mteam1,mteam2,mdate,mtime,mvenue,FixtureActivity.editorindex);
                    listTools.allCards.add(FixtureActivity.editorindex,new_info);
                    myDB.removeRow(myDB.getData().get(FixtureActivity.editorindex));
                    myDB.createEntry(new_info);
                }
                else{
                    new_info=new matchinfo(img1,img2,mgrp,mteam1,mteam2,mdate,mtime,mvenue,listTools.ID);
                    listTools.allCards.add(new_info);
                    myDB.createEntry(new_info);
                    listTools.ID++;
                }
                updateDatabase();
                Intent intent=new Intent(context,FixtureActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    public void updateDatabase(){
        editorlist=myDB.getData();
        for(int i=0;i<editorlist.size();i++){
            String eteam1=editorlist.get(i).team1;
            String eteam2=editorlist.get(i).team2;
            String egroup=editorlist.get(i).groupname;
            String edate=editorlist.get(i).cdate;
            String etime=editorlist.get(i).ctime;
            String evenue=editorlist.get(i).cvenue;
            int epos=editorlist.get(i).place;
            byte[] eimg1=editorlist.get(i).logo1;
            byte[] eimg2=editorlist.get(i).logo2;
            if((eteam1.equals(new_info.team1) && !eteam2.equals(new_info.team2))|| (eteam1.equals(new_info.team2) && !eteam2.equals(new_info.team1))){
                myDB.removeRow(editorlist.get(i));
                matchinfo einfo=new matchinfo(img1,eimg2,egroup,eteam1,eteam2,edate,etime,evenue,epos);
                myDB.createEntry(einfo);
            }
            else if ((eteam2.equals(new_info.team2) && !eteam1.equals(new_info.team1)) || (eteam2.equals(new_info.team1) && !eteam1.equals(new_info.team2))){
                myDB.removeRow(editorlist.get(i));
                matchinfo einfo=new matchinfo(eimg1,img2,egroup,eteam1,eteam2,edate,etime,evenue,epos);
                myDB.createEntry(einfo);
            }
            else if (((eteam1.equals(new_info.team1)) && (eteam2.equals(new_info.team2))) || ((eteam1.equals(new_info.team2)) && (eteam2.equals(new_info.team1)))){
                myDB.removeRow(editorlist.get(i));
                matchinfo einfo=new matchinfo(img1,img2,egroup,eteam1,eteam2,edate,etime,evenue,epos);
                myDB.createEntry(einfo);
            }
        }
    }

    public void showmsgs(){
        if(bmp1==null)
            Toast.makeText(context,"Set Team1 Logo",Toast.LENGTH_SHORT).show();
        if(bmp2==null)
            Toast.makeText(context,"Set Team2 Logo",Toast.LENGTH_SHORT).show();
        if(mdate.equals(""))
            Toast.makeText(context,"Set Date",Toast.LENGTH_SHORT).show();
        if(mtime.equals(""))
            Toast.makeText(context,"Set Time",Toast.LENGTH_SHORT).show();
        if(mvenue.equals(""))
            Toast.makeText(context,"Set Venue",Toast.LENGTH_SHORT).show();
    }

    public View.OnClickListener imageclick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectimage();
            if(v==team1logo){
               t=1;
            }
            else if(v==team2logo){
                t=2;
            }
        }
    };
    public void selectimage(){
        final CharSequence[] chooseitems={"Take photo","Choose from Gallery","Cancel"};
        AlertDialog.Builder builder=new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(chooseitems, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(chooseitems[which].equals("Take photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            String[] permissionsRequested = {Manifest.permission.CAMERA};
                            ActivityCompat.requestPermissions(EditorActivity.this, permissionsRequested, REQUEST_CAMERA_CODE);
                        }
                        else
                            callCamera();
                    } else
                        callCamera();
                }
                else if(chooseitems[which].equals("Choose from Gallery")){
                    callGallery();
                }
                else if (chooseitems[which].equals("Cancel"))
                    dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_CAMERA_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                        ContextCompat.checkSelfPermission(EditorActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                    String[] permissionsRequested = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(EditorActivity.this, permissionsRequested, EXTERNAL_REQUEST_CODE);
                }
            }
            else{
                Toast.makeText(context,"Camera Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==EXTERNAL_REQUEST_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED)
                if (ContextCompat.checkSelfPermission(EditorActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            callCamera();
            }
            else
                Toast.makeText(context,"Permission Denied",Toast.LENGTH_SHORT).show();

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void callCamera(){
        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CAMERA);
    }
    public void callGallery(){
        gallerycheck=1;
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select File"),REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle=data.getExtras();
                Bitmap bmp=(Bitmap)Objects.requireNonNull(bundle).get("data");
                try {
                    uri=BitmaptoUri(bmp);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Uri dest=Uri.fromFile(new File(getCacheDir(),"cropped"));
                Crop.of(uri,dest).asSquare().start(this);
            } else if (requestCode == REQUEST_GALLERY) {
                if (data!=null){
                    uri=data.getData();
                    Uri dest=Uri.fromFile(new File(getCacheDir(),"cropped"));
                    Crop.of(uri,dest).asSquare().start(this);
                }
            }
            else if (requestCode== Crop.REQUEST_CROP && resultCode==RESULT_OK){
                if (t == 1) {
                    team1logo.setImageURI(Crop.getOutput(data));
                    bmp1=((BitmapDrawable)team1logo.getDrawable()).getBitmap();
                    img1 = ImageViewToByte(team1logo);
                }
                if (t == 2) {
                    team2logo.setImageURI(Crop.getOutput(data));
                    bmp2=((BitmapDrawable)team2logo.getDrawable()).getBitmap();
                    img2 = ImageViewToByte(team2logo);
                }
            }
            }
        }
    public Uri BitmaptoUri(Bitmap inImage) throws IOException {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/FifaLogos");
        String pre="tempImage";String suf=".jpg";
        File tempFile = File.createTempFile(pre, suf, tempDir);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] bitmapData = bytes.toByteArray();
        FileOutputStream fos =  new FileOutputStream(tempFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        return FileProvider.getUriForFile(getApplicationContext(), "com.vaibhav.fifafixtures.provider",tempFile);
    }

    public byte[] ImageViewToByte(ImageView imageView){
        Bitmap bmp= ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100,stream);
        return stream.toByteArray();
    }

    public AdapterView.OnItemSelectedListener team1selcted=(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            assignteamlist();

            mteam1= parent.getSelectedItem().toString();
            int p=parent.getSelectedItemPosition();
            team.remove(p);
            team2adapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,team);
            team2adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            team2spinner.setAdapter(team2adapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mteam1=null;
            if (FixtureActivity.editor==1){
                onItemSelected(parent,team1view,geteditorteam1position(),parent.getItemIdAtPosition(geteditorteam1position()));
                mteam1=editorinfo.team1;
            }
        }
    });
    public AdapterView.OnItemSelectedListener team2selcted=(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            mteam2=parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mteam2=null;
            if (FixtureActivity.editor==1){
                onItemSelected(parent,team2view,geteditorteam2position(),parent.getItemIdAtPosition(geteditorteam2position()));
                mteam2=editorinfo.team2;
            }
        }
    });

    public AdapterView.OnItemSelectedListener groupselected=(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            if(position == 0) {
                groupposition=0;
                mgrp= "Group A";
                team1adapter=ArrayAdapter.createFromResource(EditorActivity.this,R.array.GroupAteams,android.R.layout.simple_spinner_item);
                team1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                team1spinner.setAdapter(team1adapter);
                team1array=getResources().getStringArray(R.array.GroupAteams);
                team2array=team1array;
            }
            if(position == 1) {
                mgrp= "Group B";
                team1adapter=ArrayAdapter.createFromResource(EditorActivity.this,R.array.GroupBteams,android.R.layout.simple_spinner_item);
                team1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                team1spinner.setAdapter(team1adapter);
                groupposition=1;
                team1array=getResources().getStringArray(R.array.GroupBteams);
                team2array=team1array;
            }
            if(position == 2) {
                mgrp= "Group C";
                team1adapter=ArrayAdapter.createFromResource(EditorActivity.this,R.array.GroupCteams,android.R.layout.simple_spinner_item);
                team1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                team1spinner.setAdapter(team1adapter);
                groupposition=2;
                team1array=getResources().getStringArray(R.array.GroupCteams);
                team2array=team1array;
            }
            if(position == 3) {
                mgrp= "Group D";
                team1adapter=ArrayAdapter.createFromResource(EditorActivity.this,R.array.GroupDteams,android.R.layout.simple_spinner_item);
                team1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                team1spinner.setAdapter(team1adapter);
                groupposition=3;
                team1array=getResources().getStringArray(R.array.GroupDteams);
                team2array=team1array;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (FixtureActivity.editor==1)
                onItemSelected(parent,grpview,geteditorgroupposition(),parent.getItemIdAtPosition(geteditorgroupposition()));
        }
    });

    public int geteditorgroupposition(){
            int i;
            String[] grp = getResources().getStringArray(R.array.Group);
            for (i = 0; !Objects.equals(grp[i], editorinfo.groupname);i++);
        return i;
    }

    public int geteditorteam1position(){
        int i;
        String[] str = team1array;
        for (i = 0; !Objects.equals(str[i], editorinfo.team1);i++);
        return i;
    }

    public int geteditorteam2position(){
        int i;
        String[] str = team2array;
        for (i = 0; !Objects.equals(str[i], editorinfo.team1);i++);
        return i;
    }

    public void initializeUI(){
        team1spinner=findViewById(R.id.team1namespinner);
        team2spinner=findViewById(R.id.team2namespinner);
        groupnamespinner=findViewById(R.id.groupnamespinner);
        datetext=findViewById(R.id.dateText);
        timetext=findViewById(R.id.timetext);
        venuetext=findViewById(R.id.venuetext);
        team1logo=findViewById(R.id.team1ImageEditor);
        team2logo=findViewById(R.id.team2ImageEditor);
        done=findViewById(R.id.DONE);
        ediotrback=findViewById(R.id.editorback);
        calender=findViewById(R.id.calender);
        relativeLayout=findViewById(R.id.editorlayout);
        clock=findViewById(R.id.clockview);
        venueselect=findViewById(R.id.dropdownview);
    }
    public void setImageClickListeners(){
        team1logo.setOnClickListener(imageclick);
        team2logo.setOnClickListener(imageclick);
    }
    public void spinnerActivities(){
        groupadapter=ArrayAdapter.createFromResource(this,R.array.Group,android.R.layout.simple_spinner_item);
        groupadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupnamespinner.setAdapter(groupadapter);
        groupnamespinner.setOnItemSelectedListener(groupselected);
        team1spinner.setOnItemSelectedListener(team1selcted);
        team2spinner.setOnItemSelectedListener(team2selcted);
    }

    @Override
    protected void onPause() {
        SharedPreferences sPrefs = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt("id", listTools.ID);
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onStop() {
        SharedPreferences sPrefs = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt("id", listTools.ID);
        editor.apply();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sPrefs = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt("id", listTools.ID);
        myDB.close();
        editor.apply();
        super.onDestroy();
    }
    public void assignteamlist(){
        if (groupposition==0)
            team= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.GroupAteams)));
        if (groupposition==1)
            team= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.GroupBteams)));
        if (groupposition==2)
            team= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.GroupCteams)));
        if (groupposition==3)
            team= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.GroupDteams)));
    }

    @Override
    public void onBackPressed() {
        if (FixtureActivity.editor==1){
            Toast.makeText(this,"Click Done if you've edited",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(this, FixtureActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }
}
