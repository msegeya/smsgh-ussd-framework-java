/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import java.util.ArrayList;

/**
 * Used to display menus in ussd apps.
 * <p>
 * A menu presents the ussd app user with choices, usually numbered choices.
 * E.g.
 * <p>
 * Select fruit:
 * <ol>
 *  <li>Apple
 *  <li>Banana
 *  <li>Pawpaw
 * </ol>
 * To act on the user's choice, one supplies {@link UssdMenuItem} instances
 * which contain the controller and action to call for a given choice.
 * 
 * @author Aaron Baffour-Awuah
 */
public class UssdMenu {
    private String header;
    private String footer;
    private String message;
    private ArrayList<UssdMenuItem> items;

    /**
     * Creates a new instance with an empty list of
     * {@link UssdMenuItem} instances.
     */
    public UssdMenu() {
        this.items = new ArrayList<UssdMenuItem>();
    }

    /**
     * Gets the menus' header.
     * @return menu's header or null if menu will be displayed without a 
     * header.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the menu's header.
     * @param header menu's header. Can be null.
     * @return this instance to enable chaining of property mutators.
     */
    public UssdMenu header(String header) {
        this.header = header;
        return this;
    }

    /**
     * Gets the menu's footer.
     * @return menu's footer or null if menu will be displayed without a 
     * footer.
     */
    public String getFooter() {
        return footer;
    }

    /**
     * Sets the menu's footer.
     * @param footer  menu's footer. Can be null.
     * @return this instance to enable chaining of property mutators.
     */
    public UssdMenu footer(String footer) {
        this.footer = footer;
        return this;
    }

    /**
     * Gets the message which will be used to render the entire menu.
     * @return entire menu's representation or null to use the default
     * way of rendering menus.
     * @see #message(java.lang.String) 
     */
    public String getMessage() {
        return message;
    }

    /**
     * Hook for clients to override how a menus is displayed.
     * @param message the message which will be used to render the entire 
     * menu and skip the default of using headers, menu items and footers. Can
     * be null to indicate that the default way be used.
     * @return this instance to enable chaining of property mutators.
     */
    public UssdMenu message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Gets the list of menu items.
     * @return menu items.
     */
    public ArrayList<UssdMenuItem> getItems() {
        return items;
    }

    /**
     * Sets the list of menu items.
     * @param items menu items
     * @return this to enable chaining of mutator methods.
     * @exception java.lang.IllegalArgumentException if items is null.
     */
    public UssdMenu items(ArrayList<UssdMenuItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("\"items\" argument cannot be "
                    + "null");
        }
        this.items = items;
        return this;
    }
    
    /**
     * 
     * @param item
     * @return 
     */
    public UssdMenu addItem(UssdMenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("\"item\" argument cannot "
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
    
    // The three methods below help with testing.

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
