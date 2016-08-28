# Smsgh.UssdFramework.Java

Framework for building Ussd applications in Java against the [SMSGH USSD API](http://developers.smsgh.com/documentations/ussd).

It was inspired by the sister framework [Smsgh.UssdFramework](https://github.com/smsgh/Smsgh.UssdFramework) for the .NET framework, and began as a port of it to Java.

## Key Features

* Automated session management.
* Built around concept of controllers so single servlet can handle all USSD requests. 
* Dynamic routing.
* `DataBag` helper in controllers for caching data across requests.
* Support for auto dialling, i.e. being able to automatically process \*713\*1\*2# as \*713# 
  followed by input of 1 and then followed by input of 2.
* Create menus and forms to collect user input.
* Automated handling of invalid choices for menus and form options.
* Extensible for logging and replacing default in-memory session store

## Dependencies

No external dependencies required. Minimum JDK level is JDK 1.5. To build, some Maven knowledge is required.
This project was built with Apache Maven 3.3.9.

## Quick Start

### Installation

Download the [zip distribution](h) and copy the ussd-framework-*.jar and its dependent jars in the lib folder to the appropriate
class paths on your system, depending on your build environment.
```

### Setup

To process USSD requests, create a `Ussd` instance and call its `service` method.

Here is a sample setup in a Java servlet.

```java
public class UssdServlet extends HttpServlet {
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        Ussd ussd = new Ussd()
                .initiationController("Main")
                .initiationAction("start");

        ussd.controllerPackages(new String[]{
            "com.smsgh.ussd.demo.controllers"
        });
        
        boolean handled = ussd.service(req, resp);
        if (!handled) {
            super.service(req, resp);
        }
    }
}
```

This tells the framework to route all initial requests to `MainController`'s `start` action, which is a method with signature `UssdResponse start()`.

### Controller actions

Next we create our `MainController` which extends `UssdController`.

```java
package com.smsgh.ussd.demo.controllers;

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
                .addInput(new UssdInput("Gender").header(formHeader)
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
        
        // formData will be null if previous screen was not a UssdForm
        Map<String, String> formData = getFormData();
        
        String name = formData.get("Name");
        String gender = formData.get("Gender");
        String prefix = gender.equals("M") ? "Master" : "Madam";
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
```

And that's it!

See [ussd-demo](https://bitbucket.org/aaronic/smsghussdframework/src/de179bcaa54123357362d0ceff1e6d6a6b00cff5/ussd-demo/?at=master) folder in source for full sample source code.

You can simulate USSD sessions using [USSD Mocker](https://github.com/smsgh/ussd-mocker) or [USSD Simulator](http://apps.smsgh.com/UssdSimulator/).

