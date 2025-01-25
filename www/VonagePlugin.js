var exec = require('cordova/exec');

var VonagePlugin = {
    createSession: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "VonagePlugin", "createSession", []);
    }
};

module.exports = VonagePlugin;
