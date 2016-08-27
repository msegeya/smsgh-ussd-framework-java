/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import java.util.ArrayList;

/**
 *
 * @author aaron
 */
public class UssdInput {
    private String name;
    private String displayName;
    private String header;
    private String message;
    private ArrayList<Option> options;
    
    public UssdInput(String name, String displayName) {
        this(name, displayName, null);
    }

    public UssdInput(String name, String displayName, 
            ArrayList<Option> options) {
        if (name == null) {
            throw new IllegalArgumentException("\"name\" argument cannot "
                    + "be null");
        }
        this.name = name;
        this.displayName = displayName;
        this.options = options;
    }
    
    public String render() {
        if (message != null) {
            return message;
        }
        StringBuilder messageBuilder = new StringBuilder();
        if (header != null) {
            messageBuilder.append(header).append("\n");
        }
        
        if (displayName == null) {
            displayName = name;
        }
        if (options != null && options.size() > 0) {
            messageBuilder.append("Choose ").append(displayName).append(":\n");
            for (int i = 0; i < options.size(); i++) {
                Option option = options.get(i);
                if (option == null) {
                    throw new RuntimeException("Encountered null "
                            + "form input option at index " + i);
                }
                messageBuilder.append(i+1).append(". ").append(option.display);
                messageBuilder.append("\n");
            }
        }
        else {
            messageBuilder.append("Enter ").append(displayName).append(":\n");
        }
        return messageBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("\"name\" argument cannot be null");
        }
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }
    
    public boolean hasOptions() {
        return options != null && options.size() > 0;
    }
    
    public static class Option {
        public final String display;
        public final String value;

        public Option(String display) {
            this(display, display);
        }

        public Option(String display, String value) {
            if (display == null) {
                throw new IllegalArgumentException("\"display\" argument cannot "
                        + "be null");
            }
            if (value == null) {
                throw new IllegalArgumentException("\"value\" argument cannot "
                        + "be null");
            }
            this.display = display;
            this.value = value;
        }
    }
}
