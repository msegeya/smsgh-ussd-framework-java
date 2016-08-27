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

    public UssdInput name(String name) {
        if (name == null) {
            throw new IllegalArgumentException("\"name\" argument cannot be null");
        }
        this.name = name;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UssdInput displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public UssdInput header(String header) {
        this.header = header;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public UssdInput message(String message) {
        this.message = message;
        return this;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public UssdInput options(ArrayList<Option> options) {
        this.options = options;
        return this;
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
        
        // Used during testing.

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 47 * hash + (this.display != null ? this.display.hashCode() : 0);
            hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
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
            final Option other = (Option) obj;
            if ((this.display == null) ? (other.display != null) : 
                    !this.display.equals(other.display)) {
                return false;
            }
            if ((this.value == null) ? (other.value != null) : 
                    !this.value.equals(other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Option{" + "display=" + display + ", value=" + value + '}';
        }
    }
    
    // Used during testing.

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.displayName != null ? this.displayName.hashCode() : 0);
        hash = 37 * hash + (this.header != null ? this.header.hashCode() : 0);
        hash = 37 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 37 * hash + (this.options != null ? this.options.hashCode() : 0);
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
        final UssdInput other = (UssdInput) obj;
        if ((this.name == null) ? (other.name != null) : 
                !this.name.equals(other.name)) {
            return false;
        }
        if ((this.displayName == null) ? (other.displayName != null) : 
                !this.displayName.equals(other.displayName)) {
            return false;
        }
        if ((this.header == null) ? (other.header != null) : 
                !this.header.equals(other.header)) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : 
                !this.message.equals(other.message)) {
            return false;
        }
        if (this.options != other.options && (this.options == null || 
                !this.options.equals(other.options))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UssdInput{" + "name=" + name + ", displayName=" + 
                displayName + ", header=" + header + ", message=" + 
                message + ", options=" + options + '}';
    }    
}
