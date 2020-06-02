package com.example.redco.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class Shopping_Result extends AppCompatActivity {

    RecyclerView bought;
    RecyclerView not_bought;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_result);

        LinearLayoutManager layout1 = new LinearLayoutManager(this);
        LinearLayoutManager layout2 = new LinearLayoutManager(this);


        bought = findViewById(R.id.bought);
        not_bought = findViewById(R.id.not_bought);

        bought.setHasFixedSize(true);
        bought.setLayoutManager(layout1);

        not_bought.setHasFixedSize(true);
        not_bought.setLayoutManager(layout2);



        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        ArrayList<String> items = intent.getStringArrayListExtra("items");
        ArrayList<String> items_not = intent.getStringArrayListExtra("items_not");

        listAdapter boughtAD = new listAdapter(items);
        listAdapter not_boughtAD = new listAdapter(items_not);

        bought.setAdapter(boughtAD);
        not_bought.setAdapter((not_boughtAD));

    }
}
