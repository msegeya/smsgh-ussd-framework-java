/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import java.util.ArrayList;

/**
 *
 * @author aaron
 */
public class UssdMenu {
    private String header;
    private String footer;
    private String message;
    private ArrayList<UssdMenuItem> items;

    public UssdMenu(ArrayList<UssdMenuItem> items) {
        this(items, null);
    }

    public UssdMenu(ArrayList<UssdMenuItem> items, String header) {
        if (items == null) {
            throw new IllegalArgumentException("\"items\" argument cannot "
                    + "be null");
        }
        this.items = items;
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<UssdMenuItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<UssdMenuItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("\"items\" argument cannot be "
                    + "null");
        }
        this.items = items;
    }
    
    public String render() {
        if (message != null) {
            return message;
        }
        
        StringBuilder messageBuilder = new StringBuilder();
        if (header != null) {
            messageBuilder.append(header).append('\n');
        }
        for (int i = 0; i < items.size(); i++) {
            UssdMenuItem item = items.get(i);
            if (item == null) {
                throw new RuntimeException("Encountered null menu item at "
                        + "index " +i);
            }
            messageBuilder.append(item.getIndex());
            messageBuilder.append(". ");
            messageBuilder.append(item.getDisplay()).append('\n');
        }
        if (footer != null) {
            messageBuilder.append(footer);
        }
        return messageBuilder.toString();
    }
}
