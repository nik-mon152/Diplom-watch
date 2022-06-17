package com.example.mycarwatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class List extends AppCompatActivity {

    TextView textView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser user;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userId;
    int probeg_car;
    RecyclerView notificationLists;
    java.util.List<Notification> notificationList;
    AdapterNotification adapterNotification;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().hide();

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = fAuth.getCurrentUser().getUid();

        textView = findViewById(R.id.notif_probeg);

        DocumentReference documentReference = fstore.collection("Cars").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    if(documentSnapshot.exists()){
                        textView.setText("Ваш текуший пробег: " + documentSnapshot.getLong("Mileage").intValue() + "км");
                        probeg_car = documentSnapshot.getLong("Mileage").intValue();
                    }else{
                        Log.d("Сообщение об ошибке", "Ошибка в документе");
                    }
                }
            }
        });
        
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Activity.class));
                finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.update);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        notificationLists = findViewById(R.id.notificationlist);
        notificationLists.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));

        notificationList = new ArrayList<>();
        adapterNotification = new AdapterNotification(notificationList);
        notificationLists.setAdapter(adapterNotification);
        fstore.collection("Notification").document(user.getUid()).collection("myNotifications").orderBy("work_notif", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                String docId = documentSnapshot.getId();
                                Notification notification = documentSnapshot.toObject(Notification.class);
                                assert notification != null;
                                notification.setDocId(docId);
                                notificationList.add(notification);
                                adapterNotification.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fstore.collection("Notification").document(user.getUid()).collection("myNotifications").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                notificationLists.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
                                notificationList = new ArrayList<>();
                                adapterNotification = new AdapterNotification(notificationList);
                                notificationLists.setAdapter(adapterNotification);
                                fstore.collection("Notification").document(user.getUid()).collection("myNotifications").orderBy("work_notif", Query.Direction.DESCENDING).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                                        String docId = documentSnapshot.getId();
                                                        Notification notification = documentSnapshot.toObject(Notification.class);
                                                        assert notification != null;
                                                        notification.setDocId(docId);
                                                        notificationList.add(notification);
                                                        adapterNotification.notifyDataSetChanged();
                                                        swipeRefreshLayout.setRefreshing(false);
                                                    }
                                                }
                                            }
                                        });
                                adapterNotification.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
            }
        });
    }
}