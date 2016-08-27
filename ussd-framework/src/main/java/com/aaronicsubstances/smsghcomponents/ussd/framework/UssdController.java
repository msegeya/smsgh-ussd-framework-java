/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
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
}
