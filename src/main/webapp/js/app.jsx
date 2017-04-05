import 'babel-polyfill';
import React from 'react';
import { connect } from 'symphony-integration-commons';
import routes from '../routes/Routes';
import config from './config.service';
import Instructions from '../components/SetupInstructions/template';

const elem = document.getElementById('app');

/*
* connect                           invokes the "connect" function from commons
* @param          SYMPHONY          Global SYMPHONY object (Required)
* @param          config            custom parameters for each integration. (Required)
* @param          routes            default, or custom, routes file (Required)
* @param          elem              HTML DOM element where to render the configurator (Required)
* @param          Instructions      react dom for custom setup instructions (Optional)
*/
connect(
  SYMPHONY,
  config,
  routes,
  elem,
  <Instructions />
);
