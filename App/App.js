'use strict';

import React, {
  AppRegistry,
  Component,
  StyleSheet,
  Text,
  View,
  NativeModules
} from 'react-native';

import Map from './TestMap';
import Button from 'react-native-button';

function reloadApp() {
  // TODO: this is iOS only
  NativeModules.DevMenu.reload();
}

class App extends Component {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native!
        </Text>

        <Map />

        <Text style={styles.instructions}>
          Shake or press menu button for dev menu
        </Text>
        <Button onPress={reloadApp}>
          Reload JavaScript
        </Button>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5
  }
});

module.exports = App;
