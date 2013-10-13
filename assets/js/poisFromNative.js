var World = {

	// identify images to use as markers in the AR view
	markerDrawable_idle: new AR.ImageResource("assets/marker_idle.png"),
	markerDrawable_datastream: new AR.ImageResource("assets/marker_datastream.png"),
	markerDrawable_selected: new AR.ImageResource("assets/marker_selected.png"),
	markerDrawable_directionIndicator: new AR.ImageResource("assets/indi.png"), 

	markerList: [],

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
		
		// show radar
		PoiRadar.show();
		AR.context.scene.cullingDistance = 100;


		// message to user whilst loading data for defences
		document.getElementById("statusElementLeft").innerHTML = 'Attack Mode Initialising';
		
		// image for the radar
		var poiImage = new AR.ImageResource("img/marker.png", {
			onError: World.errorLoadingImage
		});
		
		// get data for each defence (poiData) and plot into view
		for (var i = 0; i < poiData.length; i++) {

			var singlePoi = {
				"latitude": parseFloat(poiData[i].latitude),
				"longitude": parseFloat(poiData[i].longitude),
				//"altitude": parseFloat(poiData[i].altitude),
				"title": poiData[i].name,
				"description": poiData[i].description
			};

			World.markerList.push(new Marker(singlePoi));
		}
		
		// message to user on loading of data for defences
		document.getElementById("statusElementLeft").innerHTML = 'Virtual Defences Located';

	},

	//  user's latest known location, accessible via userLocation.latitude, userLocation.longitude, userLocation.altitude
	userLocation: null,

	// location updates
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {
		World.userLocation = {
			'latitude': lat,
			'longitude': lon,
			//'altitude': alt,
			'accuracy': acc
		};
	},
	
	updateStatusMsg: function updateStatusMsgFn(statusMsgText) {
		document.getElementById("statusElementLeft").innerHTML = statusMsgText;
	},
	
	updateEnergyValue: function updateEnergyValueFn(energyval) {
		document.getElementById("energyvalue").innerHTML = energyval;
	},
	
	updateAlertElementLeft: function updateAlertElementLeftFn(alertLeftText) {
		document.getElementById("alertElementLeft").innerHTML = alertLeftText;
	},
	
	updateAlertElementRight: function updateAlertElementRightFn(alertRightText) {
		document.getElementById("alertElementRight").innerHTML = alertRightText;
	}

};

/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;