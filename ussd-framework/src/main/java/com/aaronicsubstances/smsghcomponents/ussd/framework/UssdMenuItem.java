/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

/**
 *
 * @author aaron
 */
public class UssdMenuItem {
    private String index;
    private String display;
    private String controller;
    private String action;

    public UssdMenuItem(String index, String display, 
            String controller, String action) {
        if (index == null) {
            throw new IllegalArgumentException("\"index\" argument cannot "
                    + "be null");
        }
        if (display == null) {
            throw new IllegalArgumentException("\"display\" argument cannot "
                    + "be null");
        }
        if (controller == null) {
            throw new IllegalArgumentException("\"controller\" argument cannot "
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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        if (index == null) {
            throw new IllegalArgumentException("\"index\" argument cannot "
                    + "be null");
        }
        this.index = index;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        if (display == null) {
            throw new IllegalArgumentException("\"display\" argument cannot "
                    + "be null");
        }
        this.display = display;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        if (controller == null) {
            throw new IllegalArgumentException("\"controller\" argument cannot "
                    + "be null");
        }
        this.controller = controller;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        if (action == null) {
            throw new IllegalArgumentException("\"action\" argument cannot "
                    + "be null");
        }
        this.action = action;
    }
    
    // Used during testing.

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.index != null ? this.index.hashCode() : 0);
        hash = 89 * hash + (this.display != null ? this.display.hashCode() : 0);
        hash = 89 * hash + (this.controller != null ? this.controller.hashCode() : 0);
        hash = 89 * hash + (this.action != null ? this.action.hashCode() : 0);
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

    @Override
    public String toString() {
        return "UssdMenuItem{" + "index=" + index + ", display=" + 
                display + ", controller=" + controller + ", action=" + 
                action + '}';
    }
}
