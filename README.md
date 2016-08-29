# SMSGH USSD Framework in Java

Framework for building Ussd applications in Java against the [SMSGH USSD API](http://developers.smsgh.com/documentations/ussd).

It was inspired by the sister framework [Smsgh.UssdFramework](https://github.com/smsgh/Smsgh.UssdFramework) for the .NET framework, and began as a port of it to Java.

## Key Features

* Automated session management.
* MVC-like in its handling of concerns, so that a single servlet can handle all USSD requests. 
* Dynamic routing.
* `DataBag` helper in controllers for caching data across requests.
* Support for auto dialling, i.e. being able to automatically process \*713\*1\*2# as \*713# 
  followed by input of 1, and then followed by input of 2.
* Create menus and forms to collect user input.
* Automated handling of invalid choices for menus and form options.
* Extensible for logging and replacing default in-memory session store

## Dependencies

No external dependencies required. Minimum JDK level is JDK 1.6. To build, some Maven knowledge is required.

This project was built with JDK 8, Apache Maven 3.3 and NetBeans IDE 8, and tested on Apache Tomcat 7/8.

## Run The Demo

Included in this repository is a build of a sample web app demonstrating the basic features provided by the framework. To try it out, download the [ussd-demo WAR file](http://gitlab.smsgh.com/aaron/smsgh-ussd-framework-java/tree/master/ussd-demo-1.0.war) and deploy it to your servlet container of choice (if using Tomcat using the manager app is an easy way to deploy a WAR file).

You can then test it locally using [USSD Simulator](http://apps.smsgh.com/UssdSimulator/) or [USSD Mocker](https://github.com/smsgh/ussd-mocker), by pointing them to the endpoint [http://localhost:8080/ussd-demo-1.0/ussd](http://localhost:8080/ussd-demo-1.0/ussd) (the servlet path is __/ussd__). Modify the port if running locally on a different port from *8080*. Modify the context path also if different from */ussd-demo-1.0*.

## Installation

Download the [zip distribution](http://gitlab.smsgh.com/aaron/smsgh-ussd-framework-java/tree/master/ussd-framework-1.0-dist.zip) and copy the ussd-framework-*.jar (currently ussd-framework-1.0.jar) and its dependent jars in the lib folder to the appropriate
class paths on your system, depending on your build environment.

Alternatively, if your project/workflow is based on Maven, then you can manually install the ussd-framework-*.jar into your local repository, and copy the dependencies listed in the ussd-framework's pom.xml (not in the zip) into your project's pom.xml.

## Quick Start

### Setup

To process USSD requests, create a `Ussd` instance and call its `service` method.

Here is a sample setup in a Java servlet.

```java
...

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

...

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

See [ussd-demo](http://gitlab.smsgh.com/aaron/smsgh-ussd-framework-java/tree/master/ussd-demo) folder in source for full sample source code.