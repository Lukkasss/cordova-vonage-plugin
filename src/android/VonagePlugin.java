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
    // Código JavaScript para obter as dimensões e posição do elemento HTML
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
                JSONObject rect = new JSONObject(value);
                int left = rect.getInt("left");
                int top = rect.getInt("top");
                int width = rect.getInt("width");
                int height = rect.getInt("height");

                Log.d("VonagePlugin", "Dimensões do contêiner: left=" + left + ", top=" + top + ", width=" + width + ", height=" + height);

                // Criar um contêiner dinâmico no código nativo
                FrameLayout publisherContainer = new FrameLayout(cordova.getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                params.leftMargin = left;
                params.topMargin = top;

                // Configurar o contêiner com as dimensões e posição do elemento HTML
                publisherContainer.setLayoutParams(params);

                // Adicionar a View do Publisher ao contêiner dinâmico
                publisherContainer.addView(publisher.getView(), new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));

                // Adicionar o contêiner ao layout pai da WebView
                ((FrameLayout) actualWebView.getParent()).addView(publisherContainer);

                Log.d("VonagePlugin", "Publisher View adicionada ao container informado");
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
