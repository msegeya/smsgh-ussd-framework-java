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
}
