package com.example.vonage;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import com.opentok.android.Session;
import com.opentok.android.Publisher;
import com.opentok.android.Subscriber;

public class VonagePlugin extends CordovaPlugin {
    private Session session;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startSession")) {
            String apiKey = args.getString(0);
            String sessionId = args.getString(1);
            String token = args.getString(2);

            this.startSession(apiKey, sessionId, token, callbackContext);
            return true;
        }
        return false;
    }

    private void startSession(String apiKey, String sessionId, String token, CallbackContext callbackContext) {
        // Inicialize a sessão da Vonage
        session = new Session.Builder(cordova.getActivity(), apiKey, sessionId).build();
        session.connect(token);
        callbackContext.success("Sessão iniciada!");
    }
}
