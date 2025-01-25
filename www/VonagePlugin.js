var exec = require('cordova/exec');

exports.createSession = function(apiKey, apiSecret, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "VonagePlugin", "createSession", [apiKey, apiSecret]);
};
