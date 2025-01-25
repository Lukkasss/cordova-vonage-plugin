var exec = require('cordova/exec');

var Vonage = {
    /**
     * Inicia uma sessão de vídeo com Publisher e Subscriber.
     *
     * @param {string} apiKey - A API Key da Vonage.
     * @param {string} sessionId - O ID da sessão Vonage.
     * @param {string} token - O token de autenticação da Vonage.
     * @param {string} publisherDivId - O ID da div onde o Publisher será exibido.
     * @param {string} subscriberDivId - O ID da div onde o Subscriber será exibido.
     * @param {function} successCallback - Função chamada em caso de sucesso.
     * @param {function} errorCallback - Função chamada em caso de erro.
     */
    startSession: function(apiKey, sessionId, token, publisherDivId, subscriberDivId, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'VonagePlugin', 'startSession', [apiKey, sessionId, token, publisherDivId, subscriberDivId]);
    }
};

module.exports = Vonage;
