var exec = require('cordova/exec');

var VonagePlugin = {
    connectToSession: function(apiKey, sessionId, token, publisherElementId, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "VonagePlugin", "connectToSession", [apiKey, sessionId, token, publisherElementId]);
    }
};

module.exports = VonagePlugin;
