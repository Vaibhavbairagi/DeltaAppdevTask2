package com.vaibhav.fifafixtures;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.blurry.Blurry;

public class TeamFixtures extends AppCompatActivity {
    ArrayList<matchinfo> teamcard=new ArrayList<>();
    MyDatabase myDB=new MyDatabase(this);
    public static String team;
    matchinfo m;
    teamMatchAdapter adapter;
    android.support.v7.widget.Toolbar toolbar;
    RecyclerView recyclerView;
    byte[] img;
    RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_fixtures);
        myDB.open();
        teamcard = myDB.getData();
        myDB.close();
        for(int i=0;i<teamcard.size();i++){
            m= teamcard.get(i);
            if (!Objects.equals(m.team1, team)&&!Objects.equals(m.team2, team)){
                teamcard.remove(i);
                i--;
            }
            if(Objects.equals(m.team1, team))
                img=m.logo1;
            else if(Objects.equals(m.team2, team))
                img=m.logo2;
        }
        listTools.init_cards();
        listTools.allCards=teamcard;
        recyclerViewInit();
        String str="Team "+team+" Fixtures";
        toolbar=findViewById(R.id.fixturetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(str);

    }
    public void recyclerViewInit(){
        recyclerView=findViewById(R.id.teamMatch);
        manager=new LinearLayoutManager(this);
        adapter=new teamMatchAdapter(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        this.finish();
        super.onStop();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        myDB.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,FixtureActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
