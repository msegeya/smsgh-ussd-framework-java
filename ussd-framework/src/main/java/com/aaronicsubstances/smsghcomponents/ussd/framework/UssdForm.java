/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author aaron
 */
public class UssdForm {
    private ArrayList<UssdInput> inputs;
    private int processingPosition;
    private String controller;
    private String action;
    private Map<String, String> data;

    public UssdForm(ArrayList<UssdInput> inputs,
            String controller, String action,
            Map<String, String> data) {
        if (inputs == null) {
            throw new IllegalArgumentException("\"inputs\" argument cannot be "
                    + "null");
        }
        if (controller == null) {
            throw new IllegalArgumentException("\"controller\" argument cannot "
                    + "be null");
        }
        if (action == null) {
            throw new IllegalArgumentException("\"action\" argument cannot be "
                    + "null");
        }
        if (data == null) {
            throw new IllegalArgumentException("\"data\" argument cannot be "
                    + "null");
        }
        this.inputs = inputs;
        this.controller = controller;
        this.action = action;
        this.data = data;
    }

    public ArrayList<UssdInput> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<UssdInput> inputs) {
        if (inputs == null) {
            throw new IllegalArgumentException("\"inputs\" argument cannot "
                    + "be null");
        }
        this.inputs = inputs;
    }

    public int getProcessingPosition() {
        return processingPosition;
    }

    public void setProcessingPosition(int processingPosition) {
        this.processingPosition = processingPosition;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        if (controller == null) {
            throw new IllegalArgumentException("\"controller\" argument "
                    + "cannot be null");
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

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        if (data == null) {
            throw new IllegalArgumentException("\"data\" argument cannot "
                    + "be null");
        }
        this.data = data;
    }
    
    public String render() {
        if (processingPosition < 0 || processingPosition >=
                inputs.size()) {
            throw new RuntimeException(String.format("Invalid processing "
                    + "position (%d) for inputs of size %d",
                    processingPosition, inputs.size()));
        }
        UssdInput currentInput = inputs.get(processingPosition);
        if (currentInput == null) {
            throw new RuntimeException("Encountered null form input at index " +
                    processingPosition);
        }
        return currentInput.render();
    }
}
