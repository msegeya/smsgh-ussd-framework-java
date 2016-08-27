/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author aaron
 */
public class UssdController {
    private static final String MENU_PROCESSOR_DATA_KEY = "MenuProcessorData";
    private static final String FORM_PROCESSOR_DATA_KEY = "FormProcessorData";
    private static final String FORM_DATA_KEY = "FormData";
        
    private UssdRequest request;
    private Map<String, String> data;
    private UssdDataBag dataBag;
    private Map<String, String> formData;
    
    public void init() {
        String json = dataBag.get(FORM_DATA_KEY);
        if (json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            formData = gson.fromJson(json, type);
        }
    }

    public UssdRequest getRequest() {
        return request;
    }

    public void setRequest(UssdRequest request) {
        this.request = request;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public UssdDataBag getDataBag() {
        return dataBag;
    }

    public void setDataBag(UssdDataBag dataBag) {
        this.dataBag = dataBag;
    }

    public Map<String, String> getFormData() {
        return formData;
    }

    public void setFormData(Map<String, String> formData) {
        this.formData = formData;
    }
    
    private String route(String action) {
        return route(action, null);
    }
    
    private String route(String action, String controller) {
        if (controller == null) {
            controller = getClass().getName();
        }
        return String.format("%s.%s", action, controller);
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
            String controller) {
        if (message == null) message = "";
        String route = null;
        if (action != null) {
            route = route(action, controller);
        }
        return UssdResponse.render(message, route);
    }
    
    public UssdResponse renderMenu(UssdMenu ussdMenu) {
        Gson gson = new Gson();
        String json = gson.toJson(ussdMenu);
        dataBag.set(MENU_PROCESSOR_DATA_KEY, json);
        String message = ussdMenu.render();
        return render(message, "menuProcessor");
    }
    
    public UssdResponse renderForm(UssdForm form) {
        Gson gson = new Gson();
        String json = gson.toJson(form);
        dataBag.set(FORM_PROCESSOR_DATA_KEY, json);
        return redirect("formInputDisplay");
    }
    
    public UssdResponse menuProcessor() {
        String json = dataBag.get(MENU_PROCESSOR_DATA_KEY);
        Gson gson = new Gson();
        UssdMenu menu = gson.fromJson(json, UssdMenu.class);
        UssdMenuItem item;
        try {
            int choice = Integer.parseInt(request.getTrimmedMessage());
            item = menu.getItems().get(choice - 1);
        }
        catch (Exception ex) {
            return render(String.format("Menu choice %s does not exist.", 
                    request.getTrimmedMessage()));
        }
        dataBag.delete(MENU_PROCESSOR_DATA_KEY);
        return redirect(item.getAction(), item.getController());
    }
    
    public UssdResponse formInputDisplay() {
        UssdForm form = getForm();
        String message = form.render();
        return render(message, "formInputProcessor");
    }
    
    public UssdResponse formInputProcessor() {
        UssdForm form = getForm();
        ArrayList<UssdInput> inputs = form.getInputs();
        UssdInput input = inputs.get(form.getProcessingPosition());
        String key = input.getName();
        String value;
        if (!input.hasOptions())
        {
            value = request.getTrimmedMessage();
        }
        else
        {
            try
            {
                int choice = Integer.parseInt(request.getTrimmedMessage());
                value = input.getOptions().get(choice - 1).value;
            }
            catch (Exception ex)
            {
                return render(String.format("Option %s does not exist.", 
                    request.getTrimmedMessage()));
            }
        }
        form.getData().put(key, value);
        Gson gson = new Gson();
        if (form.getProcessingPosition() == (inputs.size() - 1))
        {
            dataBag.delete(FORM_PROCESSOR_DATA_KEY);
            String jsonData = gson.toJson(form.getData());
            dataBag.set(FORM_DATA_KEY, jsonData);
            return redirect(form.getAction(), form.getController());
        }
        form.setProcessingPosition(form.getProcessingPosition()+1);
        String json = gson.toJson(form);
        dataBag.set(FORM_PROCESSOR_DATA_KEY, json);
        return redirect("formInputDisplay");
    }
    
    private UssdForm getForm()
    {
        String json = dataBag.get(FORM_PROCESSOR_DATA_KEY);
        Gson gson = new Gson();
        UssdForm form = gson.fromJson(json, UssdForm.class);
        return form;
    }
}
