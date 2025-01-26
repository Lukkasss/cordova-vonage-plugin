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
    // Criar um contêiner dinâmico para o Publisher
    FrameLayout publisherContainer = new FrameLayout(cordova.getActivity());

    // Configurar as dimensões da View
    int width = (int) (150 * cordova.getActivity().getResources().getDisplayMetrics().density); // 150px em dp
    int height = (int) (300 * cordova.getActivity().getResources().getDisplayMetrics().density); // 300px em dp

    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
    params.rightMargin = 0; // Alinhar à borda direita
    params.bottomMargin = 0; // Alinhar à borda inferior
    params.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.END; // Canto inferior direito

    publisherContainer.setLayoutParams(params);

    // Adicionar a View do Publisher ao contêiner
    publisherContainer.addView(publisher.getView(), new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
    ));

    // Adicionar o contêiner ao layout pai da WebView
    FrameLayout rootLayout = (FrameLayout) cordova.getActivity().findViewById(android.R.id.content);
    rootLayout.addView(publisherContainer);

    Log.d("VonagePlugin", "Publisher View adicionada no canto inferior direito.");
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
    Log.d("VonagePlugin", "Stream recebido: criando Subscriber.");

    cordova.getActivity().runOnUiThread(() -> {
        // Criar um contêiner dinâmico para o Subscriber
        FrameLayout subscriberContainer = new FrameLayout(cordova.getActivity());

        // Configurar as dimensões da View
        int width = (int) (200 * cordova.getActivity().getResources().getDisplayMetrics().density); // 150px em dp
        int height = (int) (300 * cordova.getActivity().getResources().getDisplayMetrics().density); // 300px em dp

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = android.view.Gravity.CENTER; // Centralizar na tela

        subscriberContainer.setLayoutParams(params);

        // Criar o Subscriber e adicionar sua View ao contêiner
        Subscriber subscriber = new Subscriber.Builder(cordova.getActivity(), stream).build();
	subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        subscriberContainer.addView(subscriber.getView(), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Adicionar o contêiner ao layout pai da WebView
        FrameLayout rootLayout = (FrameLayout) cordova.getActivity().findViewById(android.R.id.content);
        rootLayout.addView(subscriberContainer);

        Log.d("VonagePlugin", "Subscriber View adicionada no meio da tela.");

        // Inscrever o Subscriber na sessão
        session.subscribe(subscriber);
    });
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
