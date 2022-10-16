import React, {
  requireNativeComponent,
  View,
  PropTypes,
  DeviceEventEmitter,
  NativeModules
} from 'react-native';

import Subscribable from 'Subscribable';

var ArcGISMap = requireNativeComponent(
  'ArcGISMap',
  {
    name: 'ArcGISMap',
    propTypes: {
      ...View.propTypes,
      layers: PropTypes.arrayOf(PropTypes.shape({
        type: PropTypes.oneOf(['ArcGISTiledMapServiceLayer', 'ArcGISFeatureLayer']),
        url: PropTypes.string
      })),
      onExtentChange: PropTypes.func
    }
  }
);

module.exports = React.createClass({
  mixins: [Subscribable.Mixin],
  componentWillMount: function() {
    this.addListenerOn(DeviceEventEmitter, 'onExtentChange', this.onExtentChange);
  },
  onExtentChange(event) {
    if (this.props.onExtentChange)
      this.props.onExtentChange(event);
  },
  setLevel(level) {
    NativeModules.ArcGISMapModule.setLevel(level);
  },
  zoomCurrentPosition() {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        console.log(position);
        NativeModules.ArcGISMapModule.setCenterWGS84(
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
      <ArcGISMap {...this.props} />
    );
  }
});
