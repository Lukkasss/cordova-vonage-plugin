package com.example.vonage;

import com.opentok.android.Session;
import com.opentok.android.Publisher;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import com.opentok.android.Stream;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

public class VonagePlugin extends CordovaPlugin {
    private Session session;
    private Publisher publisher;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("connectToSession")) {
            String apiKey = args.getString(0);
            String sessionId = args.getString(1);
            String token = args.getString(2);
            String subscriberElementId = args.getString(3); // ID do elemento para Subscriber
            this.connectToSession(apiKey, sessionId, token, subscriberElementId, callbackContext);
            return true;
        }
        return false;
    }

    private void connectToSession(String apiKey, String sessionId, String token, String subscriberElementId, CallbackContext callbackContext) {
        session = new Session.Builder(cordova.getActivity().getApplicationContext(), apiKey, sessionId).build();
        session.setSessionListener(new Session.SessionListener() {
            @Override
            public void onConnected(Session session) {
                // Publicar stream de vídeo
                publisher = new Publisher.Builder(cordova.getActivity()).build();
                session.publish(publisher);
                callbackContext.success("Connected to session");
            }

            @Override
            public void onDisconnected(Session session) {
                callbackContext.error("Disconnected from session");
            }

            @Override
            public void onStreamReceived(Session session, Stream stream) {
                // Cria o Subscriber e associa ao elemento HTML pelo ID
                Subscriber subscriber = new Subscriber.Builder(cordova.getActivity(), stream).build();
                cordova.getActivity().runOnUiThread(() -> {
                    int subscriberViewId = cordova.getActivity().getResources().getIdentifier(subscriberElementId, "id", cordova.getActivity().getPackageName());
                    if (subscriberViewId != 0) {
                        cordova.getActivity().findViewById(subscriberViewId).setVisibility(android.view.View.VISIBLE);
                        ((android.widget.FrameLayout) cordova.getActivity().findViewById(subscriberViewId)).addView(subscriber.getView());
                    }
                });
                session.subscribe(subscriber);
            }

            @Override
            public void onStreamDropped(Session session, Stream stream) {
                // Gerenciar quando um stream é removido
            }

            @Override
            public void onError(Session session, OpentokError opentokError) {
                callbackContext.error(opentokError.getMessage());
            }
        });
        session.connect(token);
    }
}
