/* eslint-disable no-unused-vars */
import React from 'react';
import { Router, Route, hashHistory } from 'react-router';
import {
  Home,
  CreateView,
  EditView,
  RemoveView,
  InstanceCreated,
 } from 'symphony-integration-commons';

const Routes = () => (
  <Router history={hashHistory}>
    <Route path='/' component={Home} />
    <Route path='/create-view' component={CreateView} />
    <Route path='/edit-view' component={EditView} />
    <Route path='/remove-view' component={RemoveView} />
    <Route path='/instance-created' component={InstanceCreated} />
  </Router>
);

export default Routes;
