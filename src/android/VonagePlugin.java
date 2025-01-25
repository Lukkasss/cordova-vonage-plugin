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
import org.json.JSONObject;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.util.Log;
import android.webkit.WebView;

public class VonagePlugin extends CordovaPlugin {
    private Session session;
    private Publisher publisher;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("connectToSession")) {
            String apiKey = args.getString(0);
            String sessionId = args.getString(1);
            String token = args.getString(2);
            String publisherElementId = args.getString(3); // ID do elemento para Subscriber
            this.connectToSession(apiKey, sessionId, token, publisherElementId, callbackContext);
            return true;
        }
        return false;
    }

    private void connectToSession(String apiKey, String sessionId, String token, String publisherElementId, CallbackContext callbackContext) {
        session = new Session.Builder(cordova.getActivity().getApplicationContext(), apiKey, sessionId).build();
        session.setSessionListener(new Session.SessionListener() {
            @Override
            public void onConnected(Session session) {
                // Publicar stream de vídeo
                publisher = new Publisher.Builder(cordova.getActivity()).build();
		// Renderizar o Publisher no elemento HTML especificado
		cordova.getActivity().runOnUiThread(() -> {
    // Código JavaScript para verificar se o elemento HTML existe
    final String jsCode =
        "(function() {" +
        "   var container = document.getElementById('" + publisherElementId + "');" +
        "   if (container) {" +
        "       var rect = container.getBoundingClientRect();" +
        "       return JSON.stringify({" +
        "           left: rect.left, top: rect.top, width: rect.width, height: rect.height" +
        "       });" +
        "   } else {" +
        "       return null;" +
        "   }" +
        "})();";

    // Executar o JavaScript na WebView para obter as dimensões do contêiner
    WebView actualWebView = (WebView) webView.getEngine().getView();
    actualWebView.evaluateJavascript(jsCode, value -> {
        if (value != null && !value.equals("null")) {
            try {
                // Parse das dimensões retornadas pelo JavaScript
                String jsonString = value.trim();
                if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
                    jsonString = jsonString.substring(1, jsonString.length() - 1)
                                           .replace("\\\"", "\""); // Desescapar aspas internas
                }

                JSONObject rect = new JSONObject(jsonString);
                int left = (int) rect.getDouble("left");
                int top = (int) rect.getDouble("top");
                int width = (int) rect.getDouble("width");
                int height = (int) rect.getDouble("height");

                Log.d("VonagePlugin", "Dimensões do contêiner: left=" + left + ", top=" + top + ", width=" + width + ", height=" + height);

                // Configurar a View do Publisher com as dimensões do contêiner HTML
                cordova.getActivity().runOnUiThread(() -> {
                    publisher.getView().setLayoutParams(new FrameLayout.LayoutParams(
                            width,
                            height
                    ));

                    // Adicionar a View do Publisher diretamente à WebView no local correto
                    actualWebView.addView(publisher.getView());

                    Log.d("VonagePlugin", "Publisher View injetada no container existente");
                });
            } catch (JSONException e) {
                Log.e("VonagePlugin", "Erro ao processar as dimensões do contêiner: " + e.getMessage());
            }
        } else {
            Log.e("VonagePlugin", "Elemento não encontrado na WebView: " + publisherElementId);
        }
    });
});








                session.publish(publisher);
                callbackContext.success("Connected to session");
            }

            @Override
            public void onDisconnected(Session session) {
                callbackContext.error("Disconnected from session");
            }

            @Override
            public void onStreamReceived(Session session, Stream stream) {

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
