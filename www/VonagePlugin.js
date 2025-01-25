var exec = require('cordova/exec');

var VonagePlugin = {
    connectToSession: function(apiKey, sessionId, token, subscriberElementId, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "VonagePlugin", "connectToSession", [apiKey, sessionId, token, subscriberElementId]);
    }
};

module.exports = VonagePlugin;
