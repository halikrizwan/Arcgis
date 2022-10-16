import React, {
  requireNativeComponent,
  View,
  PropTypes,
  NativeModules
} from 'react-native';

var DeviceEventEmitter = require('RCTNativeAppEventEmitter');

var ArcGISMap = React.createClass({
  propTypes: {
    layer: PropTypes.string,
    onExtentChange: PropTypes.func
  },
  componentWillMount: function() {
    DeviceEventEmitter.addListener(
      'onExtentChange',
      this.onExtentChange
    );
  },
  onExtentChange(label) {
    if (this.props.onExtentChange) {
      this.props.onExtentChange(label);
    }
  },
  setLevel(level) {
    NativeModules.ArcGISMapManager.setLevel(
      React.findNodeHandle(this),
      level
    );
  },
  zoomCurrentPosition() {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        console.log(position);
        NativeModules.ArcGISMapManager.setCenterWGS84(
          React.findNodeHandle(this),
          position.coords.longitude,
          position.coords.latitude
        );
      },
      (error) => alert(error.message),
      {
        enableHighAccuracy: true,
        timeout: 20000,
        maximumAge: 1000
      }
    );
  },
  render() {
    return (
      <RCTArcGISMap {...this.props} />
    );
  }
});

// requireNativeComponent automatically resolves this to "RCTArcGISMapManager"
var RCTArcGISMap = requireNativeComponent('RCTArcGISMap', ArcGISMap);

module.exports = ArcGISMap;
