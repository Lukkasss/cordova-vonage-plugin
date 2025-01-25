package com.example.vonagevideo;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import com.opentok.OpenTok;
import com.opentok.exception.OpenTokException;

public class VonagePlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("createSession")) {
            String apiKey = args.getString(0);
            String apiSecret = args.getString(1);
            this.createSession(apiKey, apiSecret, callbackContext);
            return true;
        }
        return false;
    }

    private void createSession(String apiKey, String apiSecret, CallbackContext callbackContext) {
        try {
            OpenTok openTok = new OpenTok(Integer.parseInt(apiKey), apiSecret);
            String sessionId = openTok.createSession().getSessionId();
            callbackContext.success(sessionId);
        } catch (OpenTokException e) {
            callbackContext.error("Error creating session: " + e.getMessage());
        }
    }
}
