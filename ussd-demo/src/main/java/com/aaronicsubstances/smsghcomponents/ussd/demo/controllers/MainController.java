/*
 *  (c) 2016. Aaronic Substances
 */
package com.aaronicsubstances.smsghcomponents.ussd.demo.controllers;

import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdController;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdForm;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdInput;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdMenu;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdMenuItem;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdResponse;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author aaron
 */
public class MainController extends UssdController {
    
    public UssdResponse start() {
        UssdMenu menu = new UssdMenu().header("Welcome")
                .addItem("Greet me", "greetingForm")
                .addItem("What's the time?", "time")
                .addItem(new UssdMenuItem("0", "Exit", "exit"))
                .footer("\nPowered by SMSGH");
        return renderMenu(menu);
    }
    
    public UssdResponse greetingForm() {
        String formHeader = "Greet Me!";
        UssdForm form = new UssdForm("greeting")
                .addInput(new UssdInput("Name").header(formHeader))
                .addInput(new UssdInput("Sex").header(formHeader)
                        .addOption(new UssdInput.Option("Male", "M"))
                        .addOption(new UssdInput.Option("Female", "F")));
        return renderForm(form);
    }
    
    public UssdResponse greeting() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting = "";
        if (hour < 12) {
            greeting = "Good morning";
        }
        if (hour >= 12) {
            greeting = "Good afternoon";
        }
        if (hour >= 16) {
            greeting = "Good evening";
        }
        if (hour >= 21) {
            greeting = "Good night";
        }
        String name = getFormData().get("Name");
        String prefix = getFormData().get("Sex").equals("M") ? "Master" : 
                "Madam";
        return render(String.format("%s, %s %s", greeting, prefix, name));
    }
    
    public UssdResponse time() {
        return render(DateFormat.getTimeInstance(DateFormat.SHORT)
                .format(new Date()));
    }
    
    public UssdResponse exit() {
        return render("Bye bye!");
    }
}
