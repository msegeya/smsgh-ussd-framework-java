/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author aaron
 */
public class UssdController {
    public static final String MENU_PROCESSOR_DATA_KEY = 
            UssdController.class.getName() + ".MenuProcessorData";
    public static final String FORM_PROCESSOR_DATA_KEY = 
            UssdController.class.getName() + ".FormProcessorData";
    public static final String FORM_DATA_KEY = 
            UssdController.class.getName() + ".FormData";
        
    private UssdRequest request;
    private Map<String, Object> controllerData;
    private UssdDataBag dataBag;
    private Map<String, String> formData;
    
    public void init() {
        // Retrieve any form data existing from previous ussd screens,
        // for use by current route
        String repr = dataBag.get(FORM_DATA_KEY);
        try {
            formData = UssdUtils.unmarshallMap(repr);
        }
        catch (RuntimeException ex) {
            throw new FrameworkException("An error occured while getting "
                    + "form data.", ex);
        }
    }

    public UssdRequest getRequest() {
        return request;
    }

    void setRequest(UssdRequest request) {
        this.request = request;
    }

    public Map<String, Object> getControllerData() {
        return controllerData;
    }

    void setControllerData(Map<String, Object> controllerData) {
        this.controllerData = controllerData;
    }

    public UssdDataBag getDataBag() {
        return dataBag;
    }

    void setDataBag(UssdDataBag dataBag) {
        this.dataBag = dataBag;
    }

    public Map<String, String> getFormData() {
        return formData;
    }

    void setFormData(Map<String, String> formData) {
        this.formData = formData;
    }
    
    private String route(String action, String controller) {
        if (action == null) {
            throw new IllegalArgumentException("\"action\" argument "
                    + "cannot be null");
        }
        if (controller == null) {
            controller = getClass().getName();
        }
        return String.format("%s.%s", controller, action);
    }
    
    public UssdResponse redirect(String action) {
        return redirect(action, null);
    }
    
    public UssdResponse redirect(String action, String controller) {
        return UssdResponse.redirect(route(action, controller));
    }
    
    public UssdResponse render(String message) {
        return render(message, null, null);
    }
    
    public UssdResponse render(String message, String action) {
        return render(message, action, null);
    }
    
    public UssdResponse render(String message, String action, 
            boolean autoDialOn) {
        return render(message, action, null, autoDialOn);
    }
    
    public UssdResponse render(String message, String action,
            String controller) {
        return render(message, action, controller, true);
    }
    
    public UssdResponse render(String message, String action,
            String controller, boolean autoDialOn) {
        if (message == null) message = "";
        String route = null;
        if (action != null) {
            route = route(action, controller);
        }
        UssdResponse ussdResponse = UssdResponse.render(message, route);
        ussdResponse.setAutoDialOn(autoDialOn);
        return ussdResponse;
    }
    
    public UssdResponse renderMenu(UssdMenu ussdMenu) {
        return renderMenu(ussdMenu, true);
    }
    
    public UssdResponse renderMenu(UssdMenu ussdMenu, boolean autoDialOn) {
        if (ussdMenu == null) {
            throw new IllegalArgumentException("\"ussdMenu\" argument cannot "
                    + "be null");
        }
        String repr = UssdUtils.marshallUssdMenu(ussdMenu);
        dataBag.set(MENU_PROCESSOR_DATA_KEY, repr);
        String message = ussdMenu.render();
        return render(message, "menuProcessor", autoDialOn);
    }
    
    
    public UssdResponse renderForm(UssdForm form) {
        return renderForm(form, true);
    }
    
    public UssdResponse renderForm(UssdForm form, boolean autoDialOn) {
        if (form == null) {
            throw new IllegalArgumentException("\"form\" argument cannot "
                    + "be null");
        }
        String repr = UssdUtils.marshallUssdForm(form);
        dataBag.set(FORM_PROCESSOR_DATA_KEY, repr);
        String message = form.render();
        return render(message, "formProcessor", autoDialOn);
    }
    
    public UssdResponse menuProcessor() {
        UssdMenu menu = getMenu();
        UssdMenuItem chosenItem = null;
        String choice = request.getTrimmedMessage();
        for (UssdMenuItem item : menu.getItems()) {
            if (item == null) {
                throw new FrameworkException("Encountered null "
                        + "ussd menu item.");
            }
            if (item.getIndex().equalsIgnoreCase(choice)) {
                chosenItem = item;
                break;
            }
        }
        if (chosenItem == null) {
            return handleInvalidMenuChoice(menu, choice);
        }
        dataBag.delete(MENU_PROCESSOR_DATA_KEY);
        return redirect(chosenItem.getAction(), chosenItem.getController());
    }
    
    protected UssdResponse handleInvalidMenuChoice(UssdMenu menu,
            String invalidMenuChoice) {
        // Redisplay menu, but turn of auto dial mechanism.
        return renderMenu(menu, false);
    }
    
    public UssdResponse formProcessor() {
        UssdForm form = getForm();
        ArrayList<UssdInput> inputs = form.getInputs();
        UssdInput input = inputs.get(form.getProcessingPosition());
        if (input == null) {
            throw new FrameworkException("Encountered null ussd form input.");
        }
        String key = input.getName();
        String value;
        if (!input.hasOptions())
        {
            value = request.getTrimmedMessage();
        }
        else
        {
            UssdInput.Option option;
            try {
                int choice = Integer.parseInt(request.getTrimmedMessage());
                option = input.getOptions().get(choice - 1);
            }
            catch (Exception ex) {
                return handleInvalidFormInputOption(form,
                        request.getTrimmedMessage());
            }
            if (option == null) {
                throw new FrameworkException("Encountered null ussd input "
                        + "option");
            }
            value = option.value;
        }
        form.getData().put(key, value);
        if (form.getProcessingPosition() == (inputs.size() - 1))
        {
            dataBag.delete(FORM_PROCESSOR_DATA_KEY);
            String formDataRepr = UssdUtils.marshallMap(form.getData());
            dataBag.set(FORM_DATA_KEY, formDataRepr);
            return redirect(form.getAction(), form.getController());
        }
        form.processingPosition(form.getProcessingPosition()+1);
        String formRepr = UssdUtils.marshallUssdForm(form);
        dataBag.set(FORM_PROCESSOR_DATA_KEY, formRepr);
        String message = form.render();
        return render(message, "formProcessor");
    }
    
    protected UssdResponse handleInvalidFormInputOption(UssdForm form,
            String invalidOption) {
        // Redisplay form at current input, but turn of auto dial mechanism.
        return renderForm(form, false);
    }
    
    private UssdMenu getMenu() {        
        String repr = dataBag.get(MENU_PROCESSOR_DATA_KEY);
        UssdMenu menu;
        try {
            menu = UssdUtils.unmarshallUssdMenu(repr);
        }
        catch (RuntimeException ex) {
            throw new FrameworkException("An error occured while getting "
                    + "UssdMenu object.", ex);
        }
        if (menu == null) {
            throw new FrameworkException("UssdMenu object could not be found.");
        }
        return menu;
    }
    
    private UssdForm getForm() {
        String repr = dataBag.get(FORM_PROCESSOR_DATA_KEY);
        UssdForm form;
        try {
            form = UssdUtils.unmarshallUssdForm(repr);            
        }
        catch (RuntimeException ex) {
            throw new FrameworkException("An error occured while getting "
                    + "UssdForm object.", ex);
        }
        if (form == null) {
            throw new FrameworkException("UssdForm object could not be found.");
        }
        return form;
    }
}
