var exec = require('cordova/exec');

var VonagePlugin = {
    joinSession: function(apiKey, sessionId, token, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "VonagePlugin", "joinSession", [apiKey, sessionId, token]);
    }
};

module.exports = VonagePlugin;
