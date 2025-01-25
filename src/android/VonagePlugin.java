package com.example.vonage;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.OpentokError;

/**
 * Cordova Plugin para integrar a Vonage Video API.
 */
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
        // Inicializa a sessão
        session = new Session.Builder(cordova.getActivity().getApplicationContext(), apiKey, sessionId).build();

        // Define o listener para eventos da sessão
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
            public void onStreamReceived(Session session, Stream stream) {
                // Lógica opcional para lidar com streams recebidos
                // Exemplo: Log.d("VonagePlugin", "Stream received: " + stream.getStreamId());
            }

            @Override
            public void onStreamDropped(Session session, Stream stream) {
                // Lógica opcional para lidar com streams encerrados
                // Exemplo: Log.d("VonagePlugin", "Stream dropped: " + stream.getStreamId());
            }

            @Override
            public void onError(Session session, OpentokError opentokError) {
                callbackContext.error("Session error: " + opentokError.getMessage());
            }
        });

        // Conecta-se à sessão usando o token fornecido
        session.connect(token);
    }
}
