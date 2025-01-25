var exec = require('cordova/exec');

var Vonage = {
    startSession: function(apiKey, sessionId, token, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "VonagePlugin", "startSession", [apiKey, sessionId, token]);
    }
};

module.exports = Vonage;
