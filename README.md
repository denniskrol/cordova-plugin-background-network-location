# Cordova background network location plugin for Android

Get network based location at set intervals and send it to a server.

### Prerequisites

```
Cordova
```

### Installing

Add plugin to project

```
cordova plugin add <path>/cordova-plugin-background-network-location
```

### Usage

```javascript
var locationService = window.backgroundNetworkLocation;

// Set config options
locationService.configure({
    url: URL, // Url of server to send location data to
    headers: {}, // Optional header
    minDistanceMeters: 0, // 0 to disable distance, and just use time
    minTime: 300, //In seconds
    toasts: false, // Show debug toasts
    restartAlreadyRunning: true // Restart location service when calling start() if the service is already running
});

// Start service
locationService.start();

// Get last location
var lastLocation = locationService.getLastLocation();

// Stop service
locationService.stop();
```

## Authors

* **Dennis Krol** - *Initial work* - [denniskrol](https://github.com/denniskrol)

## License

This project is licensed under the Do What The Fuck You Want To Public License (WTFPL) - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Based on [cordova-plugin-background-geolocation](https://github.com/mauron85/cordova-plugin-background-geolocation) by mauron85
