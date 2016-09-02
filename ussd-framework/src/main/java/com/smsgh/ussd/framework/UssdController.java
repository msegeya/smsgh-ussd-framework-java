/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import java.util.ArrayList;
import java.util.Map;

/**
 * Base class for classes which handle USSD requests.
 * <p>
 * Provide convenience methods for displaying menus and forms in
 * ussd apps.
 * 
 * @author Aaron Baffour-Awuah
 */
public abstract class UssdController {
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

    /**
     * Does nothing aside instance creation.
     */
    public UssdController() {
    }
    
    /**
     * Called as the final step in initialising a controller. Subclasses 
     * must call this version or else important initialisation  
     * will be skipped.
     */
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

    /**
     * Gets the request to be handled.
     * 
     * @return request to be handled.
     */
    public UssdRequest getRequest() {
        return request;
    }

    /**
     * Sets the request to be handled. Called by the framework
     * during controller initialisation.
     * 
     * @param request the request to be handled.
     */
    void setRequest(UssdRequest request) {
        this.request = request;
    }

    /**
     * Gets the custom data provided by the framework client.
     * 
     * @return custom data from framework client.
     */
    public Map<String, Object> getControllerData() {
        return controllerData;
    }

    /**
     * Sets the custom data provided by the framework client. Called by
     * the framework during controller initialisation.
     * @param controllerData 
     */
    void setControllerData(Map<String, Object> controllerData) {
        this.controllerData = controllerData;
    }

    /**
     * Gets a {@link UssdDataBag} instance which can be used by
     * controller to persist data across requests.
     * 
     * @return {@link UssdDataBag} for persisting requests across requests.
     */
    public UssdDataBag getDataBag() {
        return dataBag;
    }

    /**
     * Sets a {@link UssdDataBag} instance for controllers to use to
     * persist data across requests. Called by the framework during 
     * controller initialisation.
     * 
     * @param dataBag {@link UssdDataBag} instance for persisting requests
     * across requests.
     */
    void setDataBag(UssdDataBag dataBag) {
        this.dataBag = dataBag;
    }

    /**
     * Gets the data collected from ussd app user in previous form
     * screens
     * <p>
     * When a {@link UssdForm} is rendered, it results in the user being
     * asked for a number of inputs, the number being equal to the 
     * number of {@link UssdInput} instances in the {@link UssdForm}.
     * All inputs are saved into a map and made available after the form
     * display is complete.
     * 
     * @return form data from previous form input screens.
     */
    public Map<String, String> getFormData() {
        return formData;
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
    
    /**
     * Asks framework to continue processing by calling a different
     * action on this controller.
     * 
     * @param action action to call to continue ussd processing.
     * 
     * @return the response which informs framework to call the
     * "action" argument and continue the ussd request processing (<b>not</b>
     * the result of calling "action").
     */
    public UssdResponse redirect(String action) {
        return redirect(action, null);
    }
    
    /**
     * Asks framework to continue processing by calling an action on a
     * different controller.
     * 
     * @param action action to call to continue processing.
     * @param controller controller in which action resides.
     * @return the response which informs framework to call the
     * "action" argument in the "controller" class and continue
     * the ussd request processing (<b>not</b> the result of calling
     * "action").
     */
    public UssdResponse redirect(String action, String controller) {
        return UssdResponse.redirect(route(action, controller));
    }
    
    /**
     * Constructs a response which sends a session-terminating
     * message to the app user.
     * 
     * @param message the session-terminating message.
     * 
     * @return response to end session.
     */
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
    
    /**
     * 
     * @param message
     * @param action
     * @param controller
     * @param autoDialOn
     * @return 
     */
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
    
    /**
     * Processes menus. Handles invalid menu choices.
     * 
     * @return appropriate response depending on selected menu
     * choice. Redisplays menu if selected menu choice is invalid.
     */
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
    
    /**
     * Hook for subclasses to override how invalid menu choices are
     * handled. By default invalid menu choices cause a redisplay of
     * the menu, and any auto dial session is ended.
     * 
     * @param menu the menu in which the invalid input was received.
     * @param invalidMenuChoice the invalid choice the app user texted.
     * 
     * @return the response to send to app user in response to his/her
     * invalid input.
     */
    protected UssdResponse handleInvalidMenuChoice(UssdMenu menu,
            String invalidMenuChoice) {
        // Redisplay menu, but turn off auto dial mechanism.
        return renderMenu(menu, false);
    }
    
    /**
     * Processes forms. Handles invalid form options.
     * 
     * @return appropriate response depending on stage of form processing.
     * If form processing is done, returns response that calls the action
     * to process the form data. Redisplays any stage in which an invalid
     * input option is received.
     */
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
    
    /**
     * Hook for subclasses to override how invalid form options are
     * handled. By default, invalid form options cause the form to
     * be redisplayed, and any auto dial session running is ended.
     * 
     * @param form the form in which an invalid option was received.
     * @param invalidOption the invalid option texted by app user.
     * @return the response to send to app user in response to his/her invalid
     * input.
     */
    protected UssdResponse handleInvalidFormInputOption(UssdForm form,
            String invalidOption) {
        // Redisplay form at current input, but turn off auto dial mechanism.
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
