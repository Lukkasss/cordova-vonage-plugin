package com.example.vonage;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import com.opentok.android.Session;
import com.opentok.android.Publisher;
import com.opentok.android.Subscriber;
import com.opentok.android.Stream;
import com.opentok.android.OpentokError;

public class VonagePlugin extends CordovaPlugin implements Session.SessionListener {

    private Session session;
    private Publisher publisher;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("startSession".equals(action)) {
            String apiKey = args.getString(0);
            String sessionId = args.getString(1);
            String token = args.getString(2);
            String publisherDivId = args.getString(3); // ID da div para o Publisher
            String subscriberDivId = args.getString(4); // ID da div para o Subscriber

            this.startSession(apiKey, sessionId, token, publisherDivId, subscriberDivId, callbackContext);
            return true;
        }
        return false;
    }

    private void startSession(String apiKey, String sessionId, String token, String publisherDivId, String subscriberDivId, CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(() -> {
            try {
                // Inicializa a sessão
                session = new Session.Builder(cordova.getActivity(), apiKey, sessionId).build();
                session.setSessionListener(this);

                // Localiza as divs pelo ID
                android.view.ViewGroup publisherContainer = (android.view.ViewGroup) webView.getView().findViewById(
                    cordova.getActivity().getResources().getIdentifier(publisherDivId, "id", cordova.getActivity().getPackageName())
                );

                android.view.ViewGroup subscriberContainer = (android.view.ViewGroup) webView.getView().findViewById(
                    cordova.getActivity().getResources().getIdentifier(subscriberDivId, "id", cordova.getActivity().getPackageName())
                );

                if (publisherContainer != null) {
                    // Configura e adiciona o Publisher à div correspondente
                    publisher = new Publisher.Builder(cordova.getActivity()).build();
                    publisherContainer.addView(publisher.getView());
                }

                // Conecta à sessão
                session.connect(token);
                callbackContext.success("Sessão iniciada!");
            } catch (Exception e) {
                callbackContext.error("Erro ao iniciar sessão: " + e.getMessage());
            }
        });
    }

    @Override
    public void onConnected(Session session) {
        // Publica o stream do Publisher na sessão
        if (publisher != null) {
            session.publish(publisher);
        }
    }

    @Override
    public void onDisconnected(Session session) {
        // Lógica para quando a sessão for desconectada
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        // Cria um Subscriber para o stream recebido
        Subscriber subscriber = new Subscriber.Builder(cordova.getActivity(), stream).build();

        cordova.getActivity().runOnUiThread(() -> {
            android.view.ViewGroup subscriberContainer = (android.view.ViewGroup) webView.getView().findViewById(
                cordova.getActivity().getResources().getIdentifier("subscriber-container", "id", cordova.getActivity().getPackageName())
            );

            if (subscriberContainer != null) {
                // Adiciona o Subscriber à div correspondente
                subscriberContainer.addView(subscriber.getView());
            }
        });

        // Inscreve-se no stream recebido
        session.subscribe(subscriber);
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        // Lógica para quando um stream for removido da sessão
    }

    @Override
    public void onError(Session session, OpentokError error) {
        // Trata erros relacionados à sessão
        System.err.println("Erro na sessão: " + error.getMessage());
    }
}
