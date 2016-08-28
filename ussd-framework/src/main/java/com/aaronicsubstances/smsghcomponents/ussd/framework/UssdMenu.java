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

    public UssdMenu() {
        this.items = new ArrayList<UssdMenuItem>();
    }

    public String getHeader() {
        return header;
    }

    public UssdMenu header(String header) {
        this.header = header;
        return this;
    }

    public String getFooter() {
        return footer;
    }

    public UssdMenu footer(String footer) {
        this.footer = footer;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public UssdMenu message(String message) {
        this.message = message;
        return this;
    }

    public ArrayList<UssdMenuItem> getItems() {
        return items;
    }

    public UssdMenu items(ArrayList<UssdMenuItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("\"items\" argument cannot be "
                    + "null");
        }
        this.items = items;
        return this;
    }
    
    public UssdMenu addItem(UssdMenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("\"items\" argument cannot "
                    + "be null");
        }
        items.add(item);
        return this;
    }
    
    public UssdMenu addItem(String displayName, String action) {
        return addItem(displayName, action, null);
    }
    
    public UssdMenu addItem(String displayName, String action,
            String controller) {
        String index = String.valueOf(items.size() + 1);
        UssdMenuItem item = new UssdMenuItem(index, displayName, action);
        items.add(item);
        return this;
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
    
    // Used during testing.

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.header != null ? this.header.hashCode() : 0);
        hash = 37 * hash + (this.footer != null ? this.footer.hashCode() : 0);
        hash = 37 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 37 * hash + (this.items != null ? this.items.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UssdMenu other = (UssdMenu) obj;
        if ((this.header == null) ? (other.header != null) : 
                !this.header.equals(other.header)) {
            return false;
        }
        if ((this.footer == null) ? (other.footer != null) : 
                !this.footer.equals(other.footer)) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : 
                !this.message.equals(other.message)) {
            return false;
        }
        if (this.items != other.items && (this.items == null || 
                !this.items.equals(other.items))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UssdMenu{" + "header=" + header + ", footer=" + footer +
                ", message=" + message + ", items=" + items + '}';
    }
}
