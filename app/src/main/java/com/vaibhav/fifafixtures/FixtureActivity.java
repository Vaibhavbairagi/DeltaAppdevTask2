package com.vaibhav.fifafixtures;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toolbar;

import java.util.Objects;


public class FixtureActivity extends AppCompatActivity{
    FloatingActionButton floatingActionButton;
    MyDatabase myDB = new MyDatabase(this);
    private Context context;
    public static int logoclickitem=0;
    public static int editor=0;
    public static int editorindex=0;
    android.support.v7.widget.Toolbar toolbar;
    RecyclerView recyclerView;
    CustomAdapter list_adapter;
    RecyclerView.LayoutManager list_manager;
    int undo=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixture);
        myDB.open();
        listTools.allCards = myDB.getData();
        listTools.coordinatorLayout=findViewById(R.id.coordinatelayout);
        context=this;
        SharedPreferences sPrefs = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        listTools.ID = sPrefs.getInt("id", 0);
        editor=0;
        recyclerViewInit();
        UIinit();
        swipeAction();
        if(recyclerView!=null){
        list_adapter.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void OnItemLongClick(matchinfo m) {
                editor=1;
                editorindex=listTools.allCards.indexOf(m);
                EditorActivity.editorinfo=m;
                Intent intent= new Intent(context,EditorActivity.class);
                startActivity(intent);
                finish();
            }
        });
        list_adapter.setImageviewClickListener(new ImageviewClickListener() {
            @Override
            public void OnImageClick(matchinfo m) {
                if(logoclickitem==1)
                    TeamFixtures.team=m.team1;
                if (logoclickitem==2)
                    TeamFixtures.team=m.team2;
                Intent intent=new Intent(context,TeamFixtures.class);
                startActivity(intent);
            }
        });

        }
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fixtures");


    }

    public void swipeAction(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        final matchinfo temp = listTools.allCards.get(viewHolder.getAdapterPosition());
                        final int pos = viewHolder.getAdapterPosition();
                        listTools.allCards.remove(pos);
                        list_adapter.notifyItemRemoved(pos);
                        Snackbar sbar = Snackbar.make(listTools.coordinatorLayout, "Fixture removed", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        undo = 1;
                                        listTools.allCards.add(pos, temp);
                                        list_adapter.notifyItemInserted(pos);
                                        undo = 1;
                                    }
                                })
                                .addCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        if (undo != 1) {
                                            myDB.removeRow(temp);
                                        }
                                        undo = 0;
                                        super.onDismissed(transientBottomBar, event);
                                    }
                                });
                        sbar.show();
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final CharSequence[] chooseitems={"LongClick on Fixture to edit","Swipe a Fixture to delete"};
        AlertDialog.Builder builder=new AlertDialog.Builder(FixtureActivity.this);
        builder.setTitle("App info");
        builder.setItems(chooseitems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
        return super.onOptionsItemSelected(item);
    }

    public final View.OnClickListener openEditorActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, EditorActivity.class);
            startActivity(intent);
        }
    };
    public void recyclerViewInit(){
        recyclerView=findViewById(R.id.list);
        list_manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(list_manager);
        list_adapter=new CustomAdapter(this);
        recyclerView.setAdapter(list_adapter);
        list_adapter.notifyDataSetChanged();
    }
    public void UIinit(){
        floatingActionButton=findViewById(R.id.floatbutton);
        floatingActionButton.setOnClickListener(openEditorActivity);
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
        editor.apply();
        myDB.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
    public interface ItemLongClickListener{
        void OnItemLongClick(matchinfo m);
    }

    public interface ImageviewClickListener{
        void OnImageClick(matchinfo m);
    }

}
