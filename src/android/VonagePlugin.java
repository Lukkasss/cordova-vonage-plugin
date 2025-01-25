package com.example.vonage;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import com.opentok.OpenTok;
import com.opentok.Session;
import com.opentok.exception.OpenTokException;

/**
 * Plugin Cordova para gerar Session ID usando o SDK da Vonage.
 */
public class VonagePlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("generateSessionId")) {
            String apiKey = args.getString(0);
            String apiSecret = args.getString(1);
            this.generateSessionId(apiKey, apiSecret, callbackContext);
            return true;
        }
        return false;
    }

    private void generateSessionId(String apiKey, String apiSecret, CallbackContext callbackContext) {
        try {
            // Inicializa o cliente OpenTok
            OpenTok opentok = new OpenTok(apiKey, apiSecret);

            // Cria uma nova sessão
            Session session = opentok.createSession();

            // Obtém o Session ID gerado
            String sessionId = session.getSessionId();

            // Retorna o Session ID ao JavaScript
            callbackContext.success(sessionId);
        } catch (OpenTokException e) {
            // Retorna erro ao JavaScript em caso de falha
            callbackContext.error("Erro ao gerar Session ID: " + e.getMessage());
        }
    }
}
