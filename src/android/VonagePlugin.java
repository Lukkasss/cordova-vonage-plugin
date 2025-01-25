package com.example.vonagevideo;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.opentok.OpenTok;
import com.opentok.Session;

public class VonagePlugin extends CordovaPlugin {
    // Substitua pelas suas credenciais da Vonage
    private static final String API_KEY = "f486e7cd";
    private static final String API_SECRET = "XijjBy5Uf9eDnZYM";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("createSession")) {
            this.createSession(callbackContext);
            return true;
        }
        return false;
    }

    private void createSession(CallbackContext callbackContext) {
        try {
            // Instancia OpenTok com as credenciais
            OpenTok opentok = new OpenTok(API_KEY, API_SECRET);

            // Cria uma nova sessão
            Session session = opentok.createSession();

            // Obtém o sessionId e retorna ao JavaScript
            String sessionId = session.getSessionId();
            callbackContext.success(sessionId);
        } catch (Exception e) {
            // Retorna erro caso algo dê errado
            callbackContext.error("Erro ao criar sessão: " + e.getMessage());
        }
    }
}
