var exec = require('cordova/exec');

var VonagePlugin = {
    connectToSession: function(apiKey, sessionId, token, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "VonagePlugin", "connectToSession", [apiKey, sessionId, token]);
    }
};

module.exports = VonagePlugin;
