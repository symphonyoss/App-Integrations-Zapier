require('../../vendors/font-awesome-4.6.3/css/font-awesome.min.css');
require('../../vendors/jquery-1.7.1/jquery-1.7.1.min.js');
require('../styles/styles.css');
var ConfService = require('./configurator.service.js'); // ConfService      js object that stores instance and user infomration on memory to share info among screens
var instanceList = []; // instanceList        main object. Stores all instance webhook objects.
var configurationId;   // configurator id
var app_id = getParameterByName('id');
var config = require('../../configurator/config.js');

import React from 'react'
import { render } from 'react-dom'
import { Link, Router, Route, hashHistory, IndexRoute } from 'react-router'
import Home from '../views/Home'
import ListView from '../views/ListView'
import CreateView from '../views/CreateView'
import SaveWebHook from '../views/SaveWebHook'
import Success from '../views/Success'
import EditView from '../views/EditView'
import RemoveView from '../views/RemoveView'

/* getParameterByName       get url parameters */
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/* SYMPHONY API */
SYMPHONY.remote.hello().then(function(data) {
    setTheme(data.themeV2);
    function setTheme(theme) {
        document.getElementsByTagName('html')[0].className = 'symphony-external-app '+ theme.name +' '+theme.size;
    }
    function onThemeChange(theme) {
        setTheme(theme);
    }
    SYMPHONY.application.connect(app_id, ['ui', 'modules', 'applications-nav', "extended-user-service", "integration-config", "stream-service"], [app_id+':module']).then(function(response) {
        //set theme
        var uiService = SYMPHONY.services.subscribe('ui');
        uiService.listen('themeChangeV2', onThemeChange.bind(this));
        
        // subscribe services
        // exteded user service
        var extendedUserService = SYMPHONY.services.subscribe("extended-user-service");
        // integration config services
        var integrationConfService = SYMPHONY.services.subscribe("integration-config");

        // controls the warning of empty instances on list view
        var dataResponse
        // get configurationId
        configurationId = getParameterByName('configurationId');
        // get botUserId
        var botUserId = getParameterByName('botUserId');
        // store all user chat rooms
        var userChatRooms = [];
        // userId
        var userId;
        // baseURL
        var baseURL = window.location.protocol + "//" + window.location.hostname + "/integration";
        // user instances list
        var instanceList = [];
        // set timeout error exception
        var tmr;
        // default index route, ListView or CreateView
        var defaultIndexRoute = ListView;
        setTimeoutError(true);

        /** PROMISES **/
        /* promise 1: get user rooms from api
        /* promise 2: get user id from api
        /* promise 3: get user's instances list from api
        */
        //--> (promise 1)
        var promisedRooms = extendedUserService.getRooms();
        //--> (promise 2)
        var promisedUserId = extendedUserService.getUserId();
        //--> (promise 3)
        var promisedList = integrationConfService.getConfigurationInstanceList(configurationId);
        /** END PROMISES **/

        Promise.all([promisedRooms, promisedUserId, promisedList]).then(function(values) {
            getUserRooms(values[0]);
            userId = values[1];
            ConfService.userId = userId.toString();
            ConfService.botUserId = botUserId;
            ConfService.configurationId = configurationId;
            ConfService.baseURL = baseURL;
            ConfService.appId = app_id;
            ConfService.configurationName = config.app_name;
            ConfService.appTitle = config.app_title;
            ConfService.toogleSetup = config.toogleSetupInstructions;
            getUserInstanceList(values[2]);
        });

        /* getUserRooms      retrives all user's rooms
        *  @param            data          data returned from the first promise
        */
        function getUserRooms(data) {
            for(var prop in data) {
                if(data[prop].userIsOwner) {
                    userChatRooms.push(data[prop]);
                }
            }
            var regExp = /\//g;
            // normalize all rooms threadIds
            userChatRooms.map(function(room, idx) {
                room.threadId = room.threadId.replace(regExp,'_').replace("==","");
            }, this);
            ConfService.userChatRooms = userChatRooms.slice();
        }

        /* getUserInstanceList      retrieves all user's webhook instances
        *  @param                   data        data returned from the fourth promise
        */
        function getUserInstanceList(data) {
            dataResponse = data.length || 0;
            if(dataResponse == 0) ConfService.dataResponse = 0;
            // retrieve all webhook instances
            for(var obj in data) {
                var op = data[obj].optionalProperties;
                var obj_op = JSON.parse(op);
                instanceList.push(
                    {
                        streams: obj_op.streams, // rooms threadId's (string) that are posting locations
                        name: data[obj].name,
                        configurationId: data[obj].configurationId,
                        postingLocationsRooms: [], // user rooms (object) that are posting locations
                        notPostingLocationsRooms: [], // user rooms (object) that are NOT posting locations
                        description: '',
                        instanceId: data[obj].instanceId,
                        lastPostedTimestamp: obj_op.lastPostedDate,
                        streamType: obj_op['streamType'],
                        lastPosted: obj_op.lastPostedDate ? timestampToDate(obj_op.lastPostedDate) : 'not available',
                        created: data[obj].createdDate ? timestampToDate(data[obj].createdDate) : 'not available'
                    }
                );
            };
            // stores all posting locations (object) into instanceList
            var aux_rooms = [];
            instanceList.map(function(inst, i) {
                inst.streams.map(function(stream, j) {
                    userChatRooms.map(function(room, k) {
                        if(stream == room.threadId) {
                            inst.postingLocationsRooms.push(clone(room));
                        }
                    });
                });
            });
            // stores all indexes of the rooms (object) that are not posting locations into an array
            var pl, idx, aux;
            instanceList.map(function(inst, i){
                pl=false;
                idx = [];
                aux=userChatRooms.slice();
                inst.streams.map(function(stream, j){
                    for(var k=0,n=aux.length; k<n; k++) {
                      if(aux[k].threadId == stream) {
                         idx.push(k);
                      }
                    }
                })
                // remove from the user rooms array all those are posting locations rooms
                for(var i=0, n=aux.length; i<n; i++) {
                  for(var j=0, l=idx.length; j<l; j++) {
                    if(i == idx[j]) {
                      aux.splice(i,1);
                      idx.splice(j,1);
                      for(var k=0, s=idx.length; k<s; k++) idx[k]--;
                      i--;
                      break;
                    }
                  }
                }
                inst.notPostingLocationsRooms = aux.slice();
            });

            ConfService.instanceList = instanceList.slice();
            
            setTimeoutError(false);
            defaultIndexRoute = instanceList.length > 0 ? ListView : CreateView;
            render((
                <Router history={hashHistory}>
                    <Route path="/" component={Home}>
                        <IndexRoute component={ defaultIndexRoute } />
                        <Route path="/list-view(/:status)" component={ListView} />
                        <Route path="/create-view" component={CreateView} />
                        <Route path="/save-webhook(/:instanceId)" component={SaveWebHook} />
                        <Route path="/edit-view/:instanceId" component={EditView} />
                        <Route path="/success" component={Success} />
                        <Route path="/remove-view/:instanceId/:name" component={RemoveView} />
                    </Route>
                </Router>
            ), document.getElementById('app'))            
        }

        // set timeout error
        function setTimeoutError(val) {
            if(val) {
                tmr = setTimeout(function() {
                    render((
                            <Router history={hashHistory}>
                                <Route path="/" component={Home}>
                                    <IndexRoute component={ListView} />
                                    <Route path="/list-view(/:status)" component={ListView} />
                                    <Route path="/create-view" component={CreateView} />
                                    <Route path="/save-webhook(/:instanceId)" component={SaveWebHook} />
                                    <Route path="/edit-view/:instanceId" component={EditView} />
                                    <Route path="/success" component={Success} />
                                    <Route path="/remove-view/:instanceId/:name" component={RemoveView} />
                                </Route>
                            </Router>
                        ), document.getElementById('app'))
                    clearTimeout(tmr);
                    hashHistory.push('/save-webhook/error');
                }, ConfService.timeout);
            } else {
                clearTimeout(tmr);
            }
        }

        /********** HELPER FUNCTIONS *********/
        
        /* timestampToDate          format unix timestamp in date format */
        function timestampToDate(_ts) {
            var date = new Date(Number(_ts));
            var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            var month = monthNames[date.getMonth()];
            return month  +' '+ date.getDate() +', '+date.getFullYear();
        }
        
        /* javascript clone object function */
        function clone(obj) {
            if (null == obj || "object" != typeof obj) return obj;
            var copy = obj.constructor();
            for (var attr in obj) {
                if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
            }
            return copy;
        }

    }.bind(this))
}.bind(this));
