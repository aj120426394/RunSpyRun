var World = {

	// identify images to use as markers in the AR view
	markerDrawable_idle: new AR.ImageResource("assets/obstaclemarker.png"),
	markerDrawable_datastream: new AR.ImageResource("assets/markerdatastream.png"),
	markerDrawable_selected: new AR.ImageResource("assets/marker_selected.png"),
	markerDrawable_motionDetector: new AR.ImageResource("assets/motiondetector.png"), 
	markerDrawable_guard: new AR.ImageResource("assets/markerguard.png"), 
	markerDrawable_dog: new AR.ImageResource("assets/dog2.png"),
	markerDrawable_directionIndicator: new AR.ImageResource("assets/indi_2.png"),
	markerDrawable_detectionplate: new AR.ImageResource("assets/detectionplate.png"),
	
	markerList: [],

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
		
		// show radar
		PoiRadar.show();
		AR.context.scene.cullingDistance = 800;
		AR.context.scene.cutoff_max = 50;


		// message to user whilst loading data for defences
		document.getElementById("statusElement1").innerHTML = 'AttackMode Init';
		
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
		document.getElementById("statusElement1").innerHTML = 'Defences Located';

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
		document.getElementById("locationdata").innerHTML = lat+" : "+lon; 
	},
	
	updateStatusMsg: function updateStatusMsgFn(statusMsgText) {
		document.getElementById("statusElement1").innerHTML = statusMsgText;
	},
	
	updateEnergyValue: function updateEnergyValueFn(energyval) {
		document.getElementById("energyvalue").innerHTML = energyval;
		
		var energy100 = 12000;
		
		if (energyval>energy100) {
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar100.png">';
		} else if (energyval < ((energy100)+1) & energyval > (energy100*0.9)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar90.png">';
		} else if (energyval < ((energy100*0.9)+1) & energyval > (energy100*0.8)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar80.png">';
		} else if (energyval < ((energy100*0.8)+1) & energyval > (energy100*0.7)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar70.png">';
		} else if (energyval < ((energy100*0.7)+1) & energyval > (energy100*0.6)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar60.png">';
		} else if (energyval < ((energy100*0.6)+1) & energyval > (energy100*0.5)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar50.png">';
		} else if (energyval < ((energy100*0.5)+1) & energyval > (energy100*0.4)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar40.png">';
		} else if (energyval < ((energy100*0.4)+1) & energyval > (energy100*0.3)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar30.png">';
		} else if (energyval < ((energy100*0.3)+1) & energyval > (energy100*0.2)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar20.png">';
		} else if (energyval < ((energy100*0.2)+1) & energyval > (energy100*0.1)){
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar10.png">';
		} else {
			document.getElementById("energybar").innerHTML = '<img id="energybarimg" src="img/energybar00.png">';
		};
	},
	
	updateAlertElementRight: function updateAlertElementRightFn(alertRightText) {
		document.getElementById("alertElement1").innerHTML = alertRightText;
		console.log("alert text updated"+alertRightText);
	},
	
	updateAlertGraphic: function updateAlertGraphicFn(alertgraphicflag) {
		if (alertgraphicflag=="on") {
			document.getElementById("alertgraphic").innerHTML = '<img id="alertgraphicimg" src="img/Alert.png">';
			console.log("alert graphic turned on");
		} else {
			document.getElementById("alertgraphic").innerHTML = ' ';
			console.log("alert graphic turned off");
		};
	}


};


/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;