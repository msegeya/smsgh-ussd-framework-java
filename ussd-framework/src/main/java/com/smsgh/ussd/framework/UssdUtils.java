/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @author aaron
 */
public class UssdUtils {
    
    public static String marshallUssdForm(UssdForm form) {
        Gson gson = new Gson();
        String repr = gson.toJson(form);
        return repr;
    }
    
    public static UssdForm unmarshallUssdForm(String repr) {
        Gson gson = new Gson();
        UssdForm form = gson.fromJson(repr, UssdForm.class);
        return form;
    }
    
    public static String marshallUssdMenu(UssdMenu menu) {
        Gson gson = new Gson();
        String repr = gson.toJson(menu);
        return repr;
    }
    
    public static UssdMenu unmarshallUssdMenu(String repr) {
        Gson gson = new Gson();
        UssdMenu menu = gson.fromJson(repr, UssdMenu.class);
        return menu;
    }
    
    public static String marshallMap(Map<String, String> map) {
        Gson gson = new Gson();
        String repr = gson.toJson(map);
        return repr;
    }
    
    public static Map<String, String> unmarshallMap(String repr) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = gson.fromJson(repr, type);
        return map;
    }
}
