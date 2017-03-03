import ConfService from './configurator.service';

const Utils = {

	sendWelcomeMessage(streams, instanceId) {
		const query = ConfService.baseURL +'/v1/whi/'+ ConfService.appId +'/'+ ConfService.configurationId +'/'+ instanceId +'/welcome';
		const payload = {
    		streams: streams
    	}
		$.ajax({
			url: query,
			type:'POST',
			data: JSON.stringify(payload),
			dataType: "json",
			contentType: "application/json",
			success: success,
			error: error
		});
    	const success = data => {
    		console.log('success welcome:', data);
    	}
    	const error = err => {
    		console.log('error welcome: ', err);
    	}		
    },

    getUpdatedRooms(_service, _cb) {
    	var promisedRooms = _service.getRooms();
		
		// store all user chat rooms
        var userChatRooms = [];
		var that = this;
		promisedRooms.then(function(data) {
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
	        _cb();
	    }, function(err) {
			console.log('Error retrieving user rooms. ', err);
		});
    }
}


export default Utils;