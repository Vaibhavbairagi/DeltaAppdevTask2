package com.vaibhav.fifafixtures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SwipeButton swipeButton;
    ImageView imageView;
    Toolbar toolbar;
    Boolean b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        swipeButton=findViewById(R.id.swipebutton);
        b=swipeButton.isActive();
        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                imageView.setVisibility(View.INVISIBLE);
                openFixtureActivity();
            }
        });
        toolbar= findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FIFA Fixtures");

    }
    public void openFixtureActivity(){
        Intent intent=new Intent(this,FixtureActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        super.onBackPressed();
    }
}
