package com.example.redco.shoppinglist;

/***
 * ListItem class defines an object used for storing the data in a shopping list.
 * @author Bertie Wright
 *
 */
public class ListItem{

    private String item_name;
    private String quantity;
    private String price;
    private int priority;


    public ListItem(String iName, String iQuant, String iPrice, String iPrior) {

        this.setItem_name(iName);
        this.setQuantity(iQuant);
        this.setPrice(iPrice);
        this.setPriority(Integer.valueOf(iPrior));
    }


    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


}
