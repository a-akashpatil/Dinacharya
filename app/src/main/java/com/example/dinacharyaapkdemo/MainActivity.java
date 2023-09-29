package com.example.dinacharyaapkdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dinacharyaapkdemo.Adapter.ToDoAdapter;
import com.example.dinacharyaapkdemo.Model.ToDoModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener{

FirebaseAuth auth;
FirebaseUser mUser;
FirebaseFirestore firestore;
private TextView userName;




private RecyclerView recyclerView;
private FloatingActionButton floatingActionButton;

private ToDoAdapter toDoAdapter;
private List<ToDoModel> mList;
private Query query;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
private ListenerRegistration listenerRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.floatingActionButton);

       auth = FirebaseAuth.getInstance();

        mUser = auth.getCurrentUser();


//// Assuming you have access to the navigation header's View
//        View headerView = navigationView.getHeaderView(0); // 0 assumes it's the first header view
//        // Find the TextView
//        TextView navHeaderEmail = headerView.findViewById(R.id.user_img);
//
//        // Set User Email
//        if (mUser != null) {
//            String userEmail = mUser.getEmail();
//            navHeaderEmail.setText(userEmail);
//        }





        firestore = FirebaseFirestore.getInstance();

     //bottomNavigation view




        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {

                    return true;
                } else if (item.getItemId() == R.id.nav_articles) {
                    startActivity(new Intent(getApplicationContext(), Articles.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.nav_meditation) {
                    startActivity(new Intent(getApplicationContext(), Meditation.class));
                    overridePendingTransition(0, 0);
                    return true;

                }else if (item.getItemId() == R.id.nav_water) {
                    startActivity(new Intent(getApplicationContext(), WaterReminder.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle item clicks here
                int itemId = menuItem.getItemId();
                if(itemId==R.id.nav_profile){
                    startActivity(new Intent(MainActivity.this, UserProfile.class));

                } else if (itemId==R.id.nav_about) {
                    startActivity(new Intent(MainActivity.this, About.class));

                } else if (itemId == R.id.nav_share) {
                    startActivity(new Intent(MainActivity.this, Share.class));

                } else if (itemId == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();

                    // Again open login page
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();


                }

                // Close the drawer after handling the click
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });




        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewTask addNewTask = AddNewTask.newInstance();
                addNewTask.show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        mList = new ArrayList<>();
        toDoAdapter = new ToDoAdapter(MainActivity.this, mList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(toDoAdapter);

        showData();


    }
    private void showData() {
       query= firestore.collection("task")
                .orderBy("time", Query.Direction.DESCENDING)
                .whereEqualTo("userId", mUser.getUid());

         listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle any errors that occur during data retrieval
                            Toast.makeText(MainActivity.this, "" , Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mList.clear();
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                String id = documentChange.getDocument().getId();
                                ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                                mList.add(0, toDoModel); // Add tasks at the beginning to display newly entered tasks on top
                                toDoAdapter.notifyDataSetChanged();
                            }
                        }

                        listenerRegistration.remove();
                    }
                });
    }


    @Override
    public void onDialogClose(DialogInterface dialog) {
        mList.clear();
        showData();
        toDoAdapter.notifyDataSetChanged();

    }
}