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
                    int publisherViewId = cordova.getActivity().getResources().getIdentifier(publisherElementId, "id", cordova.getActivity().getPackageName());
		    Log.d("VonagePlugin", "PublisherViewId: " + publisherViewId);			
                    if (publisherViewId != 0) {
                        FrameLayout publisherContainer = cordova.getActivity().findViewById(publisherViewId);
			Log.d("VonagePlugin", "publisherContainer: " + publisherContainer);
                        if (publisherContainer != null) {
                            // Adicionar a View do Publisher ao contêiner
                            publisherContainer.addView(publisher.getView(), new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            ));
                        } else {
                            callbackContext.error("Elemento HTML não encontrado: " + publisherElementId);
                        }
                    } else {
                        callbackContext.error("ID inválido para o elemento HTML: " + publisherElementId);
                    }
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
