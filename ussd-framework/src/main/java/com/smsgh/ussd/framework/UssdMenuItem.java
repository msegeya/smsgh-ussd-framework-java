/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

/**
 * Represents the choices in ussd menus and the action to take upon their
 * selection.
 * 
 * @see UssdMenu
 * 
 * @author aaron
 */
public class UssdMenuItem {
    private String index;
    private String display;
    private String controller;
    private String action;

    /**
     * Creates a new ussd menu item.
     * @param index the choice that the user texts to select the menu item.
     * This is usually the index of the menu item in the menu item list.
     * @param display the text of the ussd menu item. This text will be 
     * prepended by the index string when {@link UssdMenu#render()} is called.
     * @param action the action to take when the menu item is selected. This
     * action will be called on the controller which renders the menu.
     */
    public UssdMenuItem(String index, String display, String action) {
        this(index, display, action, null);
    }
    
    /**
     * Creates a new ussd menu item.
     * @param index the choice that the user texts to select the menu item.
     * This is usually the index of the menu item in the menu item list.
     * @param display the text of the ussd menu item. This text will be 
     * prepended by the index string when {@link UssdMenu#render()} is called.
     * @param action the action to take when the menu item is selected.
     * @param controller the controller in which the action argument will
     * be called. If null, then the action will be called on the 
     * controller which renders the menu.
     */
    public UssdMenuItem(String index, String display, 
            String action, String controller) {
        if (index == null) {
            throw new IllegalArgumentException("\"index\" argument cannot "
                    + "be null");
        }
        if (display == null) {
            throw new IllegalArgumentException("\"display\" argument cannot "
                    + "be null");
        }
        if (action == null) {
            throw new IllegalArgumentException("\"action\" argument cannot "
                    + "be null");
        }
        this.index = index;
        this.display = display;
        this.controller = controller;
        this.action = action;
    }

    /**
     * Gets the message that should be texted to select the menu item.
     * @return choice string by which menu item will be selected.
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the message that should be texted to select the menu item.
     * @param index the choice string
     * @return this instance to enable chaining of property mutators.
     */
    public UssdMenuItem setIndex(String index) {
        if (index == null) {
            throw new IllegalArgumentException("\"index\" argument cannot "
                    + "be null");
        }
        this.index = index;
        return this;
    }

    /**
     * Gets the text of the menu item. (excluding indices/choice like 1., 2.)
     * @return menu item text
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Sets the text of the menu item (excluding choices/indices like 1., *.)
     * @param display the text of the menu item.
     * @return this instance to enable chaining of property mutators.
     * @exception java.lang.IllegalArgumentException if display is null.
     */
    public UssdMenuItem setDisplay(String display) {
        if (display == null) {
            throw new IllegalArgumentException("\"display\" argument cannot "
                    + "be null");
        }
        this.display = display;
        return this;
    }

    /**
     * Gets the controller whose action will be invoked when the menu item
     * is selected.
     * @return controller to handle menu item selection, or null.
     */
    public String getController() {
        return controller;
    }

    /**
     * Sets the controller whose action will be invoked when the menu item
     * is selected.
     * @param controller name of controller or null to use the controller
     * which will render menu.
     * @return this instance to enable chaining of property mutators.
     */
    public UssdMenuItem setController(String controller) {
        this.controller = controller;
        return this;
    }

    /**
     * Gets the action which will be invoked when the menu item is selected.
     * @return action to call upon selection of menu item.
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action which is invoked when the menu item is selected.
     * @param action action which gets called when menu item is selected.
     * @return this instance to enable chaining of property mutators.
     * @exception java.lang.IllegalArgumentException if action is null.
     */
    public UssdMenuItem setAction(String action) {
        if (action == null) {
            throw new IllegalArgumentException("\"action\" argument cannot "
                    + "be null");
        }
        this.action = action;
        return this;
    }
    
    // The 3 methods below are used during testing.

    /**
     * @inheritDoc 
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.index != null ? this.index.hashCode() : 0);
        hash = 89 * hash + (this.display != null ? this.display.hashCode() : 0);
        hash = 89 * hash + (this.controller != null ? this.controller.hashCode() : 0);
        hash = 89 * hash + (this.action != null ? this.action.hashCode() : 0);
        return hash;
    }

    /**
     * @inheritDoc 
     */
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
        final UssdMenuItem other = (UssdMenuItem) obj;
        if ((this.index == null) ? (other.index != null) : 
                !this.index.equals(other.index)) {
            return false;
        }
        if ((this.display == null) ? (other.display != null) : 
                !this.display.equals(other.display)) {
            return false;
        }
        if ((this.controller == null) ? (other.controller != null) :
                !this.controller.equals(other.controller)) {
            return false;
        }
        if ((this.action == null) ? (other.action != null) : 
                !this.action.equals(other.action)) {
            return false;
        }
        return true;
    }

    /**
     * @inheritDoc 
     */
    @Override
    public String toString() {
        return "UssdMenuItem{" + "index=" + index + ", display=" + 
                display + ", controller=" + controller + ", action=" + 
                action + '}';
    }
}
