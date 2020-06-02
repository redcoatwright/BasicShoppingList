package com.example.redco.shoppinglist;

/**
 * @Program Name: MainActivity
 * @author Bertie Wright
 * @created on 4/26/2018
 */


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Inventory {

    DynamoDBMapper dynamoDBMapper;

    Button clear;
    Button delete;
    Button save;
    Button go_shopping;

    EditText budget;

    Spinner item_spin;
    Spinner quant_spin;
    Spinner pri_spin;

    RecyclerView list;
    listAdapter list_ad;

    ArrayList<String> mItems = new ArrayList<>();
    ArrayList<String> spin_items = new ArrayList<>();
    ArrayList<String> quant_items = new ArrayList<>();
    ArrayList<String> pri_items = new ArrayList<>();
    ArrayList<ListItem> items = new ArrayList<>();

    LinearLayoutManager layout;

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing the AWS client singleton
        AWSMobileClient.getInstance().initialize(this, awsStartupResult ->
                Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!"))
                .execute();

        // Instantiate a AmazonDynamoDBMapperClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        go_shopping = findViewById(R.id.go_shopping);
        delete = findViewById(R.id.delete);
        save = findViewById(R.id.save);
        clear = findViewById(R.id.clear);

        item_spin = findViewById(R.id.item_spin);
        quant_spin = findViewById(R.id.quant_spin);
        pri_spin = findViewById(R.id.pri_spin);

        budget = findViewById(R.id.budget);

        list = findViewById(R.id.list);
        list.setHasFixedSize(true);
        layout = new LinearLayoutManager(this);
        list.setLayoutManager(layout);

        //running a thread to grab all the items from the dynamodb using the scan method in mapper class
        new Thread(() -> {
            List<ShoppingListsDO> result = dynamoDBMapper.scan(ShoppingListsDO.class, new DynamoDBScanExpression());

            //looping over the results to put the data into their respective collections
            for(ShoppingListsDO item : result){
                mItems.add(item.getName());
                String[] temp = item.getName().split("\\s+");
                items.add(new ListItem(temp[0].substring(0,temp[0].length()-1),temp[2].substring(0,temp[2].length()-1),temp[temp.length-1],temp[temp.length-2]));
            }
        }).start();



        //Setting all the items in the list from the DB
        list_ad = new listAdapter(mItems);
        list.setAdapter(list_ad);

        //Adding all the items from the Inventory interface to the spinner
        spin_items.addAll(Arrays.asList(Inventory.itemNames));
        ArrayAdapter<String> item_ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spin_items);
        item_spin.setAdapter(item_ad);

        //array to add to spinner
        String[] numbers = new String[20];
        for(int i = 0;i < 20;i++) numbers[i] = Integer.toString(i+1);

        //Adding options to the quantity spinner (max of 20)
        quant_items.addAll(Arrays.asList(numbers));
        ArrayAdapter<String> quant_ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,quant_items);
        quant_spin.setAdapter(quant_ad);

        //array to add to spinner
        String[] numbers2 = new String[10];
        for(int i = 0;i < 10;i++) numbers2[i] = Integer.toString(i+1);

        //Adding the options to the priority spinner (max of 10)
        pri_items.addAll(Arrays.asList(numbers2));
        ArrayAdapter<String> pri_ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pri_items);
        pri_spin.setAdapter(pri_ad);

        //setting the functionality for the save button
        save.setOnClickListener((v) -> {


            final ShoppingListsDO sDO = new ShoppingListsDO(); //ShoppingListsDO is a class created by AWS that's a model for the NoSQL DB
            String item_name = item_spin.getSelectedItem().toString(); //getting the item name from the spinner
            boolean flag = false; //flag in case item exists in list already, don't want to add twice

            for(String item : mItems){ //iterating over the items to check current list

                String[] temp = item.split("\\s+"); //splitting the items

                if(temp[0].equals(item_name + ",")){ //checking each item (first in string split)
                    flag = true; //if there's a match, flag is true
                    break; //if match, break the loop (no point in continuing)
                }

            }

            if(!flag) { //if the item is NOT on the list

                String item_quant = quant_spin.getSelectedItem().toString();
                String item_priority = pri_spin.getSelectedItem().toString();
                String item_price;
                int count = 0;

                for(String item : Inventory.itemNames){ //iterating over inventory items to get price

                    if(item.equals(item_name)) break; //once match is found, break loop
                    count++; //counter
                }

                item_price = Double.toString(Inventory.prices[count]); //using counter to get the correct price

                items.add(new ListItem(item_name,item_quant,item_price,item_priority)); //items array holds list items for easy local storage of items
                sDO.setName(item_name + ", x " + item_quant + ", Priority: " + item_priority + " " + item_price); //set the name in the model from AWS
                new Thread(() -> dynamoDBMapper.save(sDO)).start(); //start the thread that adds an item to the DB

                mItems.add(item_name + ", x " + item_quant + ", Priority: " + item_priority + " " + item_price); //adding item to list for display to recyclerview

                list_ad = new listAdapter(mItems); //creating new list adapter for recycler view

                list.setAdapter(list_ad); //setting adapter
            }else{
                displayError("This item already exists in your list!"); //display error message
            }

        });

        //setting functionality for delete button
        delete.setOnClickListener((v) -> {

            String item_name = item_spin.getSelectedItem().toString();
            String item_to_be_removed = "";
            int count = 0;

            for(String item : mItems){ //iterate over items to find the item to be removed

                String[] temp = item.split("\\s+");

                if(temp[0].equals(item_name + ",")){
                    item_to_be_removed = item;
                    break;
                }
                count++;
            }

            if(!item_to_be_removed.isEmpty()) { //checking that the item is on the list before removing
                String finalItem_to_be_removed = item_to_be_removed;
                new Thread(() ->{ //creating thread to connect to db
                    ShoppingListsDO delDO = new ShoppingListsDO(); //creating new model object
                    delDO.setName(finalItem_to_be_removed); //setting the name of the item for the model
                    dynamoDBMapper.delete(delDO); //deleting the item using the models provided by AWS
                }).start();

                mItems.remove(item_to_be_removed); //removing from the mITems list for the recycler view
                items.remove(count); //removing it from the ListItem list

                list_ad = new listAdapter(mItems); //creating new adapter for the recycler view
                list.setAdapter(list_ad); //setting the adapter
            }else{
                displayError("You can't delete an item from the list that isn't there! Dummy"); //error msg
            }



        });

        //button to clear the shopping list
        clear.setOnClickListener(v -> {

            new Thread(() -> { //thread for scanning the DB for all items on it
                List<ShoppingListsDO> result = dynamoDBMapper.scan(ShoppingListsDO.class, new DynamoDBScanExpression());
                for(ShoppingListsDO item : result){ //looping over items in result to delete them
                    dynamoDBMapper.delete(item); //deleting items
                }
            }).start();

            mItems.clear(); //clearing arraylist for recycler view
            items.clear(); //clearing arraylist for back end logic
            list_ad = new listAdapter(mItems); //creating new adapter
            list.setAdapter(list_ad); //setting the new adapter

        });

        //setting the functionality for the
        go_shopping.setOnClickListener((v) -> {

            String temp_budget = budget.getText().toString();

            try {

                if (!temp_budget.equals("")) {

//                    boolean char_flag = temp_budget.matches(".*[a-zA-Z]+.*");


                    ArrayList<String> item_names = new ArrayList<>();
                    ArrayList<Integer> item_quant = new ArrayList<>();
                    ArrayList<Integer> item_pris = new ArrayList<>();
                    ArrayList<Double> item_prices = new ArrayList<>();

                    for (ListItem item : items) {
                        item_names.add(item.getItem_name());
                        item_quant.add(Integer.valueOf(item.getQuantity()));
                        item_pris.add(item.getPriority());
                        item_prices.add(Double.valueOf(item.getPrice()));
                    }
                    Shopping shopClass = new Shopping(item_names, item_quant, item_pris, item_prices, Double.valueOf(temp_budget));
                    shopClass.goShopping();

                    ArrayList<String> bought_list = new ArrayList<>();
                    ArrayList<String> not_bought_list = new ArrayList<>();

                    for (int i = 0; i < shopClass.getBought().length; i++)
                        if (!shopClass.getBought()[i].equals("hello"))
                            bought_list.add(shopClass.getBought()[i] + ", x " + shopClass.getBoughtQuantity()[i]);
                    for (int i = 0; i < shopClass.getNotBought().length; i++)
                        if (!shopClass.getNotBought()[i].equals("hello"))
                            not_bought_list.add(shopClass.getNotBought()[i] + ", x " + shopClass.getNotBoughtQuantity()[i]);

                    shoppingScreen(bought_list, not_bought_list);

                } else {
                    displayError("Please enter a number for the budget!");
                }
            }catch(NumberFormatException e){
                displayError("Please enter a number without characters!");
            }
            budget.setText("");

        });


    }


    //method for starting new activity with an intent statement
    public void displayError(String msg){
        Intent intent = new Intent(this, Error.class);
        intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
    }

    //method to send intent to new activity window for displaying the items bought/not bought
    public void shoppingScreen(ArrayList<String> bought_list, ArrayList<String> not_bought_list){

        Intent intent = new Intent(this, Shopping_Result.class);
        intent.putStringArrayListExtra("items", bought_list);
        intent.putStringArrayListExtra("items_not", not_bought_list);
        startActivity(intent);


    }





}

