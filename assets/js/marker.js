var kMarker_AnimationDuration_ChangeDrawable = 500;
var kMarker_AnimationDuration_Resize = 1000;

function Marker(poiData, isSelected) {

    this.poiData = poiData;
    this.isSelected = isSelected;


    // New: Two animation groups managing the animations used during selection/deselection 
    this.animationGroup_idle = null;
    this.animationGroup_selected = null;


    var markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);
    
    switch (poiData.title){
	    case "Datastream":
	    	this.radarScale = 0.03;
	    	this.radarOpacity = 0.0;
	    	this.radarColor = "#ffffff";
	    	this.markerDraawble = World.markerDrawable_datastream;
			this.indicator = true;
			break;
			
		case "Guard":
	    	this.radarScale = 0.03;
	    	this.radarOpacity = 0.8;
	    	this.radarColor = "#ffffff";
	    	this.markerDraawble = World.markerDrawable_guard;
			this.indicator = false;
			break;
		
		case "Dog":
	    	this.radarScale = 0.03;
	    	this.radarOpacity = 0.8;
	    	this.radarColor = "#ffffff";
	    	this.markerDraawble = World.markerDrawable_dog;
			this.indicator = false;
			break;

	    case "MotionDetector":
	    	this.radarScale = 0.03;
	    	this.radarOpacity = 0.8;
	    	this.radarColor = "#ffffff";
	    	this.markerDraawble = World.markerDrawable_motionDetector;
	    	this.indicator = false;
			break;
			
		case "DetectionPlate":
	    	this.radarScale = 0.03;
	    	this.radarOpacity = 0.8;
	    	this.radarColor = "#ffffff";
	    	this.markerDraawble = World.markerDrawable_detectionplate;
	    	this.indicator = false;
			break;
			
		default:
			this.radarScale = 0.03;
			this.radarOpacity = 0.8;
			this.radarColor = "#ffffff";
			this.markerDraawble = World.markerDrawable_idle;
			this.indicator = false;
			break;
    }


    this.markerDrawable_idle = new AR.ImageDrawable(this.markerDraawble, 1.5, {
        zOrder: 0,
        opacity: 0.8,
        onClick: null
    });


    this.markerDrawable_selected = new AR.ImageDrawable(this.markerDraawble, 2.5, {
        zOrder: 0,
        opacity: 0.0,
        onClick: null
    });
    
    
    this.titleLabel = new AR.Label(poiData.title.trunc(15), 0.5, {
        zOrder: 2,
        offsetY: 1.2,
        style: {
            textColor: '#ff8a00',
            //fontStyle: AR.CONST.FONT_STYLE.BOLD
        }
    });


    this.descriptionLabel = new AR.Label(poiData.description.trunc(0), 0, {
        zOrder: 1,
        offsetY: -0.55,
        style: {
            textColor: '#ff8a00'
        }
    });


    this.radarCircle = new AR.Circle(this.radarScale, {
        horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
        opacity: this.radarOpacity,
        style: {
            fillColor: this.radarColor
        }
    });


    this.radardrawables = [];
    this.radardrawables.push(this.radarCircle);
	
	
	// New: Direction Indicator
    this.directionIndicatorDrawable = new AR.ImageDrawable(World.markerDrawable_directionIndicator, 0.6, {
        enabled: this.indicator
    });


    // Changed: 
    var markerObject = new AR.GeoObject(markerLocation, {
        drawables: {
            cam: [this.markerDrawable_idle, this.markerDrawable_selected, this.titleLabel, this.descriptionLabel],
            indicator: this.directionIndicatorDrawable,
            radar: this.radardrawables
        }
    });


    return this;
    
}
/*
Marker.prototype.getOnClickTrigger = function(marker) {

    return function() {

        if (!Marker.prototype.isAnyAnimationRunning(marker)) {
            if (marker.isSelected) {

                Marker.prototype.setDeselected(marker);

            } else {

                Marker.prototype.setSelected(marker);
            }
        } else {
            AR.logger.debug('a animation is already running');
        }

        return true;
    };
};
*/
Marker.prototype.setSelected = function(marker) {

    marker.isSelected = true;

    // New: 
    if (marker.animationGroup_selected === null) {

        var hideIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 0.0, kMarker_AnimationDuration_ChangeDrawable);
        //var showSelectedDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, "opacity", null, 0.8, kMarker_AnimationDuration_ChangeDrawable);

        var idleDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        var selectedDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        var titleLabelResizeAnimation = new AR.PropertyAnimation(marker.titleLabel, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        var descriptionLabelResizeAnimation = new AR.PropertyAnimation(marker.descriptionLabel, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        marker.animationGroup_selected = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [hideIdleDrawableAnimation, showSelectedDrawableAnimation, idleDrawableResizeAnimation, selectedDrawableResizeAnimation, titleLabelResizeAnimation, descriptionLabelResizeAnimation]);
    }


    // New: 
    // AR.logger.debug('selected isRunning: ' + marker.animationGroup_selected.isRunning());
    // if (!marker.animationGroup_selected.isRunning()) {

    marker.markerDrawable_idle.onClick = null;
    marker.markerDrawable_selected.onClick = Marker.prototype.getOnClickTrigger(marker);

    marker.directionIndicatorDrawable.enabled = true;


    marker.animationGroup_selected.start();
    // }
};

Marker.prototype.setDeselected = function(marker) {

    marker.isSelected = false;

    // New: 
    if (marker.animationGroup_idle === null) {

        var showIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 0.8, kMarker_AnimationDuration_ChangeDrawable);
        var hideSelectedDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, "opacity", null, 0, kMarker_AnimationDuration_ChangeDrawable);

        var idleDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        var selectedDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        var titleLabelResizeAnimation = new AR.PropertyAnimation(marker.titleLabel, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        var descriptionLabelResizeAnimation = new AR.PropertyAnimation(marker.descriptionLabel, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        marker.animationGroup_idle = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [showIdleDrawableAnimation, hideSelectedDrawableAnimation, idleDrawableResizeAnimation, selectedDrawableResizeAnimation, titleLabelResizeAnimation, descriptionLabelResizeAnimation]);
    }


    // New: 
    // AR.logger.debug('idle isRunning: ' + marker.animationGroup_idle.isRunning());
    // if (!marker.animationGroup_idle.isRunning()) {

    marker.markerDrawable_idle.onClick = Marker.prototype.getOnClickTrigger(marker);
    marker.markerDrawable_selected.onClick = null;

    marker.directionIndicatorDrawable.enabled = false;


    marker.animationGroup_idle.start();
    // }
};

Marker.prototype.isAnyAnimationRunning = function(marker) {

    if (marker.animationGroup_idle === null || marker.animationGroup_selected === null) {
        return false;
    } else {
        if ((marker.animationGroup_idle.isRunning() === true) || (marker.animationGroup_selected.isRunning() === true)) {
            return true;
        } else {
            return false;
        }
    }
};

// will truncate all strings longer than given max-length "n". e.g. "foobar".trunc(3) -> "foo..."
String.prototype.trunc = function(n) {
    return this.substr(0, n - 1) + (this.length > n ? '...' : '');
};
