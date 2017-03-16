var config = require('../../configurator/config');

var successCreatedMessage;

if (config.app_name === 'Universal Webhook') {
	successCreatedMessage = 'You have successfully configured a new '+ config.app_name +' integration.';
} else {
	successCreatedMessage = 'You have successfully configured a new '+ config.app_name +' integration. Register your configured integration on '+ config.app_name +' to complete setup.';
}

module.exports = {

	instanceList: [],

	userChatRooms: [],

	userId: "",

	baseURL: "",

	appId: "",

	appTitle: "",

	configurationName: "",

	configurationId: "",

	toogleSetup: true,

	newInstanceCreated: false,

	protoInstance: {
		instanceId: "",
		name: "",
		description: "",
		creatorId: "",
		newPostingLocationsRooms: [],	// Rooms used to check welcome message
		postingLocationsRooms: [],
		notPostingLocationsRooms: [],
		lastPostedDate: null
	},

	dataResponse: 1,

	timeout: 10000, // timeout errors in miliseconds

	botUserId: "",

	required: {
		name: true,
		rooms: false // streamType IM is default
	},

	messages: {
		created: successCreatedMessage,
		updated: 'You have successfully updated your '+ config.app_name +' integration.',
		deactivated: 'Instance removed from Symphony. You can now remove the webhook from '+ config.app_name,
		error: "An error has ocurred and the operation could not be completed. Please try again later.",
		not_found: "No webhook instances were found.",
		loading: "Searching for instances...",
		working: "Working...",
		name_required: "Description is required!",
		rooms_required: "Posting Location is required!"
	},

	labelPostinLocations: "One-on-one with ",

	setPostingLocationsRooms: function(_id, _arr) {
		this.instanceList.map(function(item, idx) {
			if(item.instanceId == _id) {
				item.postingLocationsRooms = _arr.slice();
			}
		})
	},

	setNotPostingLocationsRooms: function(_id, _arr) {
		this.instanceList.map(function(item, idx) {
			if(item.instanceId == _id) {
				item.notPostingLocationsRooms = _arr.slice();
			}
		})
	},

	setNewInstance: function(_elem) {
		this.instanceList.push(_elem);
		this.dataResponse = 1;
		//this.resetProtoInstance();
	},

	updateInstance: function(_id, _item) {
		var that = this;
		this.instanceList.map(function(item, idx){
			if(_id == item.instanceId) {
				that.instanceList[idx] = _item;
				return;
			}
		});
	},

	removeInstanceById: function(_id) {
		var that = this;
		this.instanceList.map(function(item, idx){
			if(_id == item.instanceId) {
				that.instanceList.splice(idx,1);
				return;
			}
		});
		if(this.instanceList.length == 0) {
			this.dataResponse = 0;
		}
	},

	resetProtoInstance: function() {
		this.protoInstance.instanceId = "";
		this.protoInstance.name = "";
		this.protoInstance.description = "";
		this.protoInstance.creatorId = "";
		this.protoInstance.postingLocationsRooms = [];
		this.protoInstance.notPostingLocationsRooms = [];
		this.protoInstance.newPostingLocationsRooms = [];
		this.protoInstance.lastPostedDate = null;
		this.protoInstance.streamType = "IM";
	}
}
