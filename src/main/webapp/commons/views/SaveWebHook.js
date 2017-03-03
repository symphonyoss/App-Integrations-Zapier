import React from 'react'
import ReactDOM from 'react-dom'
import { hashHistory } from 'react-router'
import Utils from '../js/utils.service';
import ConfService from '../js/configurator.service';

var SaveWebHook = React.createClass({
	propTypes: {
		params: React.PropTypes.object
	},
	componentDidMount: function() {
		var that = this;
		// integration config services
        var integrationConfService = SYMPHONY.services.subscribe("integration-config");
        // configurationId
        var configurationId = ConfService.configurationId;
        // IM service
        var streamService = SYMPHONY.services.subscribe('stream-service');
        // Build Optional Properties
        var str_streams = "";
        var postingLocationsRooms = ConfService.protoInstance.postingLocationsRooms.slice();
        postingLocationsRooms.map(function(item,i){
    		str_streams += "\""+ item.threadId +"\"";
			if(i < postingLocationsRooms.length-1) {
				str_streams += ",";
			}	
        })
        var optionalProperties;
		if(ConfService.protoInstance.lastPostedDate) {
			optionalProperties = "{\"owner\":\""+ ConfService.userId +"\",\"streams\":["+ str_streams +"],\"lastPostedDate\":"+ ConfService.protoInstance.lastPostedDate +",\"streamType\":\""+ ConfService.protoInstance.streamType +"\"}";
		} else {
			optionalProperties = "{\"owner\":\""+ ConfService.userId +"\",\"streams\":["+ str_streams +"],\"streamType\":\""+ ConfService.protoInstance.streamType +"\"}";
		}
		var _streams = [];
		ConfService.protoInstance.postingLocationsRooms.map(function(item,i){
			_streams.push(item.threadId);
		})
		// errors...
		var timeout = setTimeout(function(){
			hashHistory.push('/list-view/error');
		},ConfService.timeout);	
		if(this.props.params) {
			if(this.props.params.instanceId == "error") {
				clearTimeout(timeout);
				hashHistory.push('/list-view/error');
			}
		}
		// TYPE STREAM IS IM
        if(ConfService.protoInstance.streamType == "IM") {
        	// UPDATE IM
			if(this.props.params.instanceId !== undefined && ConfService.protoInstance.name !== "") {
				var promisedIM = streamService.createIM([ConfService.botUserId]);
				promisedIM.then(function(data){
					_streams.push(data.id);
					var streamId = data.id;
					optionalProperties = "{\"owner\":\""+ ConfService.userId +"\",\"streams\":[\""+ data.id +"\"],\"streamType\":\""+ ConfService.protoInstance.streamType +"\"}";
					var payload = {
			        	instanceId: ConfService.protoInstance.instanceId,
						configurationId: ConfService.configurationId,
						name: ConfService.protoInstance.name,
						description: ConfService.protoInstance.description,
						optionalProperties: optionalProperties
					}
					var updateInstance = integrationConfService.updateConfigurationInstanceById(ConfService.configurationId, ConfService.protoInstance.instanceId, payload);
					updateInstance.then(function(data){
						if (!checkExistingIM()) {
							that.sendWelcomeMessage(_streams, data.instanceId);
						}
						callUpdateInstance(data);
						
					},function(error){
						console.log('error: ',error);
						clearTimeout(timeout);
						hashHistory.push('/list-view/error');
					})
				},function(error){
					console.log('error: ',error);
					clearTimeout(timeout);
					hashHistory.push('/list-view/error');
				});
			} 
			// REMOVE IM
			else if(this.props.params.instanceId !== undefined) { 
				callRemoveInstance();
			} 
			// CREATE IM
			else {
				// request the IM stream from service
				var promisedIM = streamService.createIM([ConfService.botUserId]);
				promisedIM.then(function(data){
					_streams.push(data.id);
					var streamId = data.id;
					optionalProperties = "{\"owner\":\""+ ConfService.userId +"\",\"streams\":[\""+ data.id +"\"],\"streamType\":\""+ ConfService.protoInstance.streamType +"\"}";
					var payload = {
						configurationId: ConfService.configurationId,
						name: ConfService.protoInstance.name,
						description: ConfService.protoInstance.description,
						creatorId: ConfService.userId,
						optionalProperties: optionalProperties
					}
					var saveInstance = integrationConfService.createConfigurationInstance(ConfService.configurationId, payload);
					saveInstance.then(
					function (data) {
						if (!checkExistingIM()) {
							Utils.sendWelcomeMessage(_streams, data.instanceId);
						}
						callSetNewInstance(data);
						
					}, function (error) {
						console.log('error: ', error);
						clearTimeout(timeout);
						hashHistory.push('/list-view/error');
					})

				},function(error){	
					console.log('error: ',error);
					clearTimeout(timeout);
					hashHistory.push('/list-view/error');
				})
			}
		} 
        // TYPE STREAM IS CHATROOM
        else if(ConfService.protoInstance.streamType == "CHATROOM") {
        	// UPDATE Chat room
        	if(this.props.params.instanceId !== undefined && ConfService.protoInstance.name !== "") {
        		if(_streams.length > 0) {
					var promises = [];
					_streams.map(function(item) {
						promises.push(streamService.addRoomMembership(item,ConfService.botUserId));
					})

					Promise.all(promises).then(function(data) {
						updateRooms(data);
					}, function(err) {
						clearTimeout(timeout);
						hashHistory.push('/list-view/error');
					})
					
				} else {
					updateRooms();
				}
				function updateRooms(dataRooms) {
					// #2 after the bot user was added to all rooms, call the service to update the instance ...
					var payload = {
			        	instanceId: that.props.params.instanceId,
						configurationId: ConfService.configurationId,
						name: ConfService.protoInstance.name,
						description: ConfService.protoInstance.description,
						optionalProperties: optionalProperties
					}
					var updateInstance = integrationConfService.updateConfigurationInstanceById(ConfService.configurationId, that.props.params.instanceId, payload);
					updateInstance.then(function(data){
						Utils.sendWelcomeMessage(ConfService.protoInstance.newPostingLocationsRooms, data.instanceId);
						callUpdateInstance(data);
					},function(error){
						console.log('error: ',error);
						clearTimeout(timeout);
						hashHistory.push('/list-view/error');
					})
				}
			} 
			// REMOVE Chat room
			else if(this.props.params.instanceId !== undefined) {
				callRemoveInstance();
			} 
			// CREATE Chat room
			else {
				if(_streams.length > 0) {
					var promises = [];
					for(var str in _streams) {
						promises.push(streamService.addRoomMembership(_streams[str],ConfService.botUserId));
					}
					Promise.all(promises).then(function(data){
						createRooms(data);
					},function(err){
						console.log('error: ',err);
						clearTimeout(timeout);
						hashHistory.push('/list-view/error');
					})
				} else {
					createRooms();
				}
				function createRooms(dataRooms) {
					// #2 after the bot user was added to all rooms, call the service to create a new instance ...
					var payload = {
						configurationId: ConfService.configurationId,
						name: ConfService.protoInstance.name,
						description: ConfService.protoInstance.description,
						creatorId: ConfService.userId,
						optionalProperties: optionalProperties
					}
					var saveInstance = integrationConfService.createConfigurationInstance(ConfService.configurationId, payload);
					saveInstance.then(
					function (data) {
						Utils.sendWelcomeMessage(_streams, data.instanceId);
						callSetNewInstance(data);
					}, function (error) {
						console.log('error: ', error);
						clearTimeout(timeout);
						hashHistory.push('/list-view/error');
					})
				}
			}
        }
		//------------ HELPER FUNCTIONS -------------//
		// callSetNewInstance 		set new instance on configurator service
		function callSetNewInstance(_data) {
			ConfService.protoInstance.instanceId = _data.instanceId;
			var obj_OP = JSON.parse(_data.optionalProperties);
			ConfService.setNewInstance({
				streams: _streams,
				name: ConfService.protoInstance.name,
				configurationId: ConfService.configurationId,
				postingLocationsRooms: ConfService.protoInstance.postingLocationsRooms.slice(),
				newPostingLocationsRooms: [],
				notPostingLocationsRooms: ConfService.protoInstance.notPostingLocationsRooms.slice(),
				description: ConfService.protoInstance.description,
				instanceId: ConfService.protoInstance.instanceId,
				lastPosted: obj_OP.lastPostedDate ? timestampToDate(obj_OP.lastPostedDate) : 'not available',
				streamType: ConfService.protoInstance.streamType,
				created: _data.createdDate ? timestampToDate(_data.createdDate) : 'not available'
			});
			clearTimeout(timeout);
			hashHistory.push('/success');
		}
		// callUpdateInstance 	update an instance on configurator service
		function callUpdateInstance(_data) {
			ConfService.protoInstance.instanceId = _data.instanceId;
			var obj_OP = JSON.parse(_data.optionalProperties);
			ConfService.updateInstance(ConfService.protoInstance.instanceId ,{
				streams: _streams,
				name: ConfService.protoInstance.name,
				configurationId: ConfService.configurationId,
				postingLocationsRooms: ConfService.protoInstance.postingLocationsRooms.slice(),
				newPostingLocationsRooms: [],
				notPostingLocationsRooms: ConfService.protoInstance.notPostingLocationsRooms.slice(),
				description: ConfService.protoInstance.description,
				instanceId: ConfService.protoInstance.instanceId,
				lastPosted: obj_OP.lastPostedDate ? timestampToDate(obj_OP.lastPostedDate) : 'not available',
				streamType: ConfService.protoInstance.streamType,
				created: ConfService.protoInstance.created ? ConfService.protoInstance.created : 'not available'
			});
			ConfService.resetProtoInstance();
			clearTimeout(timeout);
			hashHistory.push('/list-view/updated');
		}
		// callRemoveInstance 		remove the instance on configurator service
		function callRemoveInstance() {
			var deactivateInstance = integrationConfService.deactivateConfigurationInstanceById(ConfService.configurationId, that.props.params.instanceId);
			deactivateInstance.then(function(data){
				ConfService.resetProtoInstance();
				ConfService.removeInstanceById(that.props.params.instanceId);
				clearTimeout(timeout);
				hashHistory.push('/list-view/deactivated');
			},function(error){
				console.log('error: ',error);
				clearTimeout(timeout);
				hashHistory.push('/list-view/error');
			})
		}
        // timestampToDate          format unix timestamp in date format
    	function timestampToDate(_ts) {
            var date = new Date(Number(_ts));
            var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            var month = monthNames[date.getMonth()];
            return month  +' '+ date.getDate() +', '+date.getFullYear();
        }
        // checkExistingIM			check if there are any IM stream type instance
        function checkExistingIM() {
        	var im = ConfService.instanceList.filter(function(item) { return item.streamType === 'IM' });
        	return im.length > 0 ? true : false;
        }
	},

	render: function() {
		return(
			<div>
				<div className="spinner"><div><i className="fa fa-circle-o-notch fa-spin"></i></div><p>{ConfService.messages.working}</p></div>
			</div>
		);
	}
})
module.exports = SaveWebHook;