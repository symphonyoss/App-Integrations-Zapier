import 'babel-polyfill';
import { initUnauthenticatedApp } from 'symphony-integration-commons';
import config from './config.service';

initUnauthenticatedApp(config);
