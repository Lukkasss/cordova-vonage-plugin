package com.example.vonage;

import com.opentok.android.Session;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
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
    // Código JavaScript para localizar o elemento HTML e preparar o contêiner
    final String jsCode =
        "(function() {" +
        "   var container = document.getElementById('" + publisherElementId + "');" +
        "   if (container) {" +
        "       container.innerHTML = ''; " + // Limpa qualquer conteúdo existente na div
        "       var videoElement = document.createElement('video');" +
        "       videoElement.setAttribute('autoplay', 'true');" +
        "       videoElement.setAttribute('playsinline', 'true');" +
        "       videoElement.style.width = '100%';" +
        "       videoElement.style.height = '100%';" +
        "       container.appendChild(videoElement);" +
        "       return 'success';" +
        "   } else {" +
        "       return 'not found';" +
        "   }" +
        "})();";

    // Executar o JavaScript na WebView
    WebView actualWebView = (WebView) webView.getEngine().getView();
    actualWebView.evaluateJavascript(jsCode, value -> {
        if ("\"success\"".equals(value)) {
            Log.d("VonagePlugin", "Publisher injetado com sucesso na div: " + publisherElementId);

            // Associar o stream do Publisher ao elemento <video> criado
            cordova.getActivity().runOnUiThread(() -> {
                // Aqui você pode configurar a associação do stream ao <video>
                publisher.setPublisherListener(new PublisherKit.PublisherListener() {
                    @Override
                    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
                        Log.d("VonagePlugin", "Stream criado: associando ao elemento de vídeo.");
                        // Configurar associação do stream ao vídeo (se necessário)
                    }

                    @Override
                    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
                        Log.d("VonagePlugin", "Stream destruído.");
                    }

                    @Override
                    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
                        Log.e("VonagePlugin", "Erro no Publisher: " + opentokError.getMessage());
                    }
                });
            });
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
