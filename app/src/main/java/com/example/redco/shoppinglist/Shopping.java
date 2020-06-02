package com.example.redco.shoppinglist;

/**
 * @Program Name: Shopping
 * @author Bertie Wright
 * @Revised on: 4/26/2018
 */

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Shopping
{
    //Declaring variables used during the shopping logic

    private String[] bought;
    private String[] notBought;
    private Double[] notBoughtPrice;
    private Double[] boughtPrice;
    private Integer[] sortedQuantityArray;
    private Integer[] notBoughtQuantity;
    private Integer[] boughtQuantity;
    private String name;
    private Double moneyInPocket;
    private Double bankAccount;
    private String[] inputItems;
    private Integer[] inputPriority;
    private ArrayList<Integer> tempPriorityArray;
    private String[] sortedItemArray;
    private Integer[] sortedPriorityArray;
    private Double[] sortedPriceArray;
    private Double[] itemPrices;
    private Integer[] itemQuants;


    //Declaring the default constructor and initializing class fields

    public Shopping(ArrayList<String> items, ArrayList<Integer> quant, ArrayList<Integer> priority, ArrayList<Double> prices, Double budget)
    {


        inputItems = items.toArray(new String[items.size()]);
        inputPriority = priority.toArray(new Integer[items.size()]);
        itemPrices = prices.toArray(new Double[items.size()]);
        itemQuants = quant.toArray(new Integer[items.size()]);
        tempPriorityArray = new ArrayList<Integer>();
        sortedItemArray = new String[items.size()];
        sortedQuantityArray = new Integer[items.size()];
        bought = new String[items.size()];
        notBought = new String[items.size()];
        notBoughtPrice = new Double[items.size()];
        boughtPrice = new Double[items.size()];
        boughtQuantity = new Integer[items.size()];
        notBoughtQuantity = new Integer[items.size()];
        moneyInPocket = budget;
        bankAccount = new Double(budget);
        sortedPriorityArray = new Integer[items.size()];
        sortedPriceArray = new Double[items.size()];
    }

    //Declaring the go shopping method which goes through the logic of sorting priority and purchasing items
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void goShopping()
    {



        //Declaring variables for the goShopping method

        Integer[] tempPriority = inputPriority.clone();


        //cloning the inputPriority so we can sort it without sorting the original reference object
        tempPriority = inputPriority.clone();

        for(int i = 0;i < inputItems.length;i++)
        {
            tempPriorityArray.add(i, -1);
            tempPriorityArray.set(i, tempPriority[i]);
        }

        //using streams to sort the temp priority array
        List result = tempPriorityArray.stream()
                .sorted((x, y) -> Integer.compare(y, x))
                .collect(Collectors.toList());

        //setting the temp priority array to the result of the stream
        tempPriorityArray = (ArrayList<Integer>) result;

        for(int i = 0;i < inputItems.length;i++)
        {
            sortedPriorityArray[i] = tempPriorityArray.get(i);
        }

        //re-arranging the item and price arrays based on the sorted priority array
        for(int i = 0; i < inputItems.length; i++)
        {
            for(int y = 0; y < inputItems.length; y++)
            {
                int temp = 0;

                //checking the sorted priority array against the input priority array
                //to figure out where they match to assign item and price
                if(sortedPriorityArray[i].equals(inputPriority[y]))
                {

                    //Logic for making sure that, since we can have multiple same priorities, the items/quantity/price
                    //sort with the new priority inputs
                    for(int z = 0; z < inputItems.length;z++)
                    {
                        //if(inputItems[y].equals(sortedItemArray.get(z)))
                        if(inputItems[y].equals(sortedItemArray[z]))
                        {
                            temp++;
                        }
                    }
                    if(temp == 0)
                    {
                        //sortedItemArray.add(i, inputItems[y]);
                        sortedItemArray[i] = inputItems[y];
                        sortedPriceArray[i] = itemPrices[y];
                        sortedQuantityArray[i] = itemQuants[y];
                    }
                }
            }
        }


        //for loop to initialize the bought/not bought/bought price/not bought price/quantity arrays for printing later
        for(int i = 0; i < inputItems.length;i++)
        {
            bought[i] = "hello";
            notBought[i] = "hello";
            boughtPrice[i] = -1.00;
            notBoughtPrice[i] = -1.00;
            boughtQuantity[i] = -1;
            notBoughtQuantity[i] = -1;
        }

        for(int i =0;i < inputItems.length;i++)
        {
            System.out.println("sorted Price");
            System.out.println(sortedPriceArray[i]);
            System.out.println("sorted Quantity");
            System.out.println(sortedQuantityArray[i]);

        }

        //running the algorithm which purchases the items startin'g with the highest priority
        //and purchasing as many items as possible if items are too expensive
        for(int i = 0; i < inputItems.length; i++)
        {
            //subtracting the price of an item (thus purchasing it)
            moneyInPocket = moneyInPocket - (sortedPriceArray[i]*sortedQuantityArray[i]);

            //checking to make sure we didn't spend too much money
            if(moneyInPocket < 0.00)
            {
                //adding back what was "bought" if too much was spent (i.e. if our "money" < 0.00)
                moneyInPocket = moneyInPocket + (sortedPriceArray[i]*sortedQuantityArray[i]);

                //assigning what was not bought from the item array and its price
                notBought[i] = sortedItemArray[i];//.get(i);
                notBoughtPrice[i] = sortedPriceArray[i];
                notBoughtQuantity[i] = sortedQuantityArray[i];

            }
            else
            {
                //assigning what was bought and its price
                bought[i] = sortedItemArray[i];//.get(i);
                boughtPrice[i] = sortedPriceArray[i];
                boughtQuantity[i] = sortedQuantityArray[i];
            }
        }

        //displaying the items purchased
        System.out.println("You bought these items!");
        for(int i = 0; i < inputItems.length; i++)
        {
            //checking to make sure the item isn't "null" thus meaning it
            //wasn't purchases and then displaying what was purchased
            if(!bought[i].equals("hello"))
            {
                System.out.println(bought[i]);
            }
            //checking to make sure the item price isn't "0" which
            //allows us to display only the price of the bought items
            if(boughtPrice[i] != -1)
            {
                System.out.print("Price: ");
                System.out.println(boughtPrice[i]);
            }
        }
        System.out.println();

        //Using the same logic as right above where we test the condition of if the item is "null" or
        //the price is "0" then we can go through the not bought / price arrays to display those items
        System.out.println("You didn't buy these items!");
        for(int i = 0; i < inputItems.length; i++)
        {
            if(!notBought[i].equals("hello"))
            {
                System.out.println(notBought[i]);
            }
            if(notBoughtPrice[i] != -1)
            {
                System.out.print("Price: ");
                System.out.println(notBoughtPrice[i]);
            }
        }
    }

    //Declaring the equals method for the Shopping class
    public boolean equal(Shopping newShopping)
    {
        if(!(newShopping instanceof Shopping)){return false;}
        for(int i = 0;i < 7;i++)
        {
            if(!(newShopping.bought[i].equals(bought[i]))){return false;}
            if(!(newShopping.notBought[i].equals(notBought[i]))){return false;}
            if(!(newShopping.boughtPrice[i] == boughtPrice[i])){return false;}
            if(!(newShopping.notBoughtPrice[i] == notBoughtPrice[i])){return false;}
            if(!(newShopping.sortedPriorityArray[i] == sortedPriorityArray[i])){return false;}
            if(!(newShopping.tempPriorityArray.get(i).equals(tempPriorityArray.get(i)))){return false;}
            if(!(newShopping.sortedPriceArray[i] == sortedPriceArray[i])){return false;}
            if(!(newShopping.inputItems[i].equals(inputItems[i]))){return false;}
            if(!(newShopping.inputPriority[i].equals(inputPriority[i]))){return false;}
        }
        return true;
    }

    //Declaring method to copy the object so you can have two objects with different references
    public void copy(Shopping copyClass)
    {
        this.inputItems = copyClass.inputItems;
        this.inputPriority = copyClass.inputPriority;
        this.name = copyClass.name;
        this.bankAccount = copyClass.bankAccount;
        this.bought = copyClass.bought;
        this.notBought = copyClass.notBought;
        this.boughtPrice = copyClass.boughtPrice;
        this.notBoughtPrice = copyClass.notBoughtPrice;
        this.sortedPriorityArray = copyClass.sortedPriorityArray;
        this.sortedItemArray = copyClass.sortedItemArray;
        this.sortedPriceArray = copyClass.sortedPriceArray;
        this.tempPriorityArray = copyClass.tempPriorityArray;
    }

    //Declaring the getter method to retrieve the items bought
    public String[] getBought()
    {
        return this.bought;
    }

    //Declaring the getter method to retrieve the items not bought
    public String[] getNotBought()
    {
        return this.notBought;
    }

    //Declaring the getter method to retrieve the price for the bought items
    public Double[] getBoughtPrice()
    {
        return this.boughtPrice;
    }

    //Declaring the getter method to retrieve the price for the items not bought
    public Double[] getNotBoughtPrice()
    {
        return this.notBoughtPrice;
    }

    //Declaring the getter method to retrieve the quantity for the bought items
    public Integer[] getBoughtQuantity()
    {
        return this.boughtQuantity;
    }

    //Declaring the getter method to retrieve the quantity for the not bought items
    public Integer[] getNotBoughtQuantity()
    {
        return this.notBoughtQuantity;
    }

    //Declaring the getter method to retrieve the name of the user
    public String getName()
    {
        return this.name;
    }

    //Declaring the getter method to retrieve the money used for shopping that gets printed to the file
    public Double getMoneyInPocket()
    {
        return this.moneyInPocket;
    }

    //Declaring the getter method to retrieve the user's total budget
    public Double getBankAccount()
    {
        return this.bankAccount;
    }

    //Declaring the getter method to retrieve the original input items
    public String[] getItems()
    {
        return this.inputItems;
    }

    //Declaring the getter method to retrieve the original priority array
    public Integer[] getPriorities()
    {
        return this.inputPriority;
    }
}
