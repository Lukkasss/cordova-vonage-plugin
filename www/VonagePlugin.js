var exec = require('cordova/exec');

var VonagePlugin = {
    generateSessionId: function(apiKey, apiSecret, successCallback, errorCallback) {
        exec(successCallback, errorCallback, "VonagePlugin", "generateSessionId", [apiKey, apiSecret]);
    }
};

module.exports = VonagePlugin;
