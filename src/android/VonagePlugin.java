package com.example.vonage;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import com.opentok.android.Session;
import com.opentok.android.Publisher;
import com.opentok.android.Subscriber;

public class VonagePlugin extends CordovaPlugin {

    private Session session;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("joinSession")) {
            String apiKey = args.getString(0);
            String sessionId = args.getString(1);
            String token = args.getString(2);
            this.joinSession(apiKey, sessionId, token, callbackContext);
            return true;
        }
        return false;
    }

    private void joinSession(String apiKey, String sessionId, String token, CallbackContext callbackContext) {
        // Inicializar sessão
        session = new Session.Builder(cordova.getActivity().getApplicationContext(), apiKey, sessionId).build();
        session.setSessionListener(new Session.SessionListener() {
            @Override
            public void onConnected(Session session) {
                callbackContext.success("Connected to session");
            }

            @Override
            public void onDisconnected(Session session) {
                callbackContext.error("Disconnected from session");
            }

            @Override
            public void onError(Session session, OpentokError opentokError) {
                callbackContext.error(opentokError.getMessage());
            }
        });
        session.connect(token);
    }
}
