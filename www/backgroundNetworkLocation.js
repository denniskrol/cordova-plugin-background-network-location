var exec = require('cordova/exec');
var emptyFnc = function(){};

module.exports = {

    configure: function(config, success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundNetworkLocation',
            'configure',
            [config]
        );
    },

    start: function(success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundNetworkLocation',
            'start', []);
    },

    stop: function(success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundNetworkLocation',
            'stop', []);
    },

    finish: function(success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundNetworkLocation',
            'finish', []);
    },
	
	getLastLocation: function(success, failure) {
        if (typeof(success) !== 'function') {
            throw 'BackgroundNetworkLocation#getLastLocation requires a success callback';
        }
        exec(success,
            failure || emptyFnc,
            'BackgroundNetworkLocation',
            'getLastLocation', []);
    },

    getLocationProviders: function(success, failure) {
        if (typeof(success) !== 'function') {
            throw 'BackgroundNetworkLocation#getLocationProviders requires a success callback';
        }
        exec(success,
            failure || emptyFnc,
            'BackgroundNetworkLocation',
            'getLocationProviders', []);
    },
};