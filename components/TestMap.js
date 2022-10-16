import React, {
  View,
  Text,
  StyleSheet
} from 'react-native';

import ArcGISMap from './ArcGIS/ArcGISMap';
import Button from 'react-native-button';

var LAYERS = [
  {
    type: 'ArcGISTiledMapServiceLayer',
    url: 'http://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer'
  },
  {
    type: 'ArcGISFeatureLayer',
    url: 'https://services.arcgis.com/nzS0F0zdNLvs7nc8/arcgis/rest/services/MTBENCHMARK2DATA2/FeatureServer/0'
  }
];

module.exports = React.createClass({
  getInitialState: function() {
    return {
      extent: "Initial extent"
    };
  },

  onExtentChange: function(event) {
    //console.log('extent change', event);

    var label = "";

    // iOS
    if (event.label) {
      label = event.label;
    }
    // Android
    else {
      label = "x: "
        + parseInt(event.x, 10)
        + ", y: "
        + parseInt(event.y, 10)
        + ", scale: 1/"
        + parseInt(event.scale);
    }

    this.setState({
      extent: label
    });
  },

  onChangeLevel: function(level) {
    this.refs.map.setLevel(level);
  },

  onZoomCurrentPosition: function() {
    this.refs.map.zoomCurrentPosition();
  },

  render: function() {
    return (
      <View>
        <ArcGISMap
          ref="map"
          // only implemented on Android
          layers={LAYERS}
          // test on iOS
          layer={LAYERS[0].url}
          onExtentChange={this.onExtentChange}
          style={styles.map} />
        <Text style={styles.extent}>
          {this.state.extent}
        </Text>
        <View style={styles.buttons}>
          <Button style={styles.button} onPress={() => { this.onChangeLevel(0.5) }}>
            Zoom +
          </Button>
          <Button style={styles.button} onPress={() => { this.onChangeLevel(2) }}>
            Zoom -
          </Button>
          <Button style={styles.button} onPress={() => { this.onZoomCurrentPosition() }}>
            Locate me
          </Button>
        </View>
      </View>
    );
  }
});

const styles = StyleSheet.create({
  map: {
    flex: 1,
    height: 300,
    width: 300
  },
  extent: {
    textAlign: 'center',
    color: '#000',
    marginBottom: 5
  },
  buttons: {
    flexDirection: 'row',
    marginBottom: 10
  },
  button: {
    marginRight: 10,
    fontSize: 24
  }
});
