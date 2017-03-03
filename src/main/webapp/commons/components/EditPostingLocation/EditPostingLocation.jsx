import React from 'react'
import ReactDOM from 'react-dom'
import SearchRooms from '../SearchRooms/SearchRooms'
import ConfService from '../../js/configurator.service';
import Utils from '../../js/utils.service';

var extendedUserService;

var EditPostingLocation = React.createClass({
	propTypes: {
		instanceId: React.PropTypes.string.isRequired
	},
	getInitialState: function() {
		var oneOneLabel = 'New one-on-one chat';
		for(var instance of ConfService.instanceList){
			if(instance.streamType === "IM"){
				oneOneLabel = `Existing one-on-one chat with ${ConfService.configurationName}`;
				break;
			}
		}
		return {
			checked: false,
			showSearch: false,
			rooms: [],
			filteredRooms: [],
			filters: [],
			oneOneLabel: oneOneLabel,
		}
	},
	componentWillMount: function() {
		extendedUserService = SYMPHONY.services.subscribe("extended-user-service");
	},
	componentDidMount: function() {
		var that = this;
		var _rooms = ConfService.instanceList.filter(function(item){
			return item.instanceId == that.props.instanceId
		});
		ConfService.protoInstance.instanceId = _rooms[0].instanceId;
		ConfService.protoInstance.name = _rooms[0].name;
		ConfService.protoInstance.description = _rooms[0].description;
		ConfService.protoInstance.creatorId = ConfService.userId;
		ConfService.protoInstance.postingLocationsRooms = _rooms[0].postingLocationsRooms.slice();
		ConfService.protoInstance.created = _rooms[0].created;
		ConfService.protoInstance.lastPostedDate = _rooms[0].lastPostedTimestamp;
		ConfService.protoInstance.notPostingLocationsRooms = _rooms[0].notPostingLocationsRooms.slice();
		ConfService.protoInstance.streamType = _rooms[0].streamType;
		if(ConfService.protoInstance.streamType == "IM") {
			this.refs.oneOne.checked = true;
			ConfService.protoInstance.streamType = "IM";
		} else if(_rooms[0].postingLocationsRooms.length > 0) {
			this.refs.chatRoom.checked = true;
			this.setState({
				showSearch: true,
				rooms: _rooms[0].notPostingLocationsRooms.slice(), //rooms: SYMPHONY.getRooms
				filters: _rooms[0].postingLocationsRooms.slice(),
			});
		} else {
			this.setState({
				rooms: ConfService.userChatRooms.slice(),
				showSearch: true
			})
			this.refs.chatRoom.checked = true;
			ConfService.required.rooms = true;
		}
	},
	onChange: function(e) {
		this.setState({
			checked: e.target.checked
		})
		if(e.target.id === 'chat-room') {
			ConfService.protoInstance.streamType = "CHATROOM";
			this.setState({
				showSearch: true,
				rooms: ConfService.userChatRooms.slice()//rooms: SYMPHONY.getRooms
			});
		} else if(e.target.id == 'one-one') {
			ConfService.protoInstance.streamType = "IM";
			ConfService.required.rooms = false;
			this.setState({
				showSearch: false,
				filteredRooms: [],
				filters: [],
			});
			ConfService.protoInstance.postingLocationsRooms = [];
			ConfService.protoInstance.notPostingLocationsRooms = ConfService.userChatRooms.slice();
		}
	},
	onChangeSearch: function(e) {
		var that = this;
		Utils.getUpdatedRooms(extendedUserService, function() {
			that.setState({
	        	rooms: ConfService.userChatRooms
	        });
	    });
		var suggestionList = this.state.rooms.slice();
		this.setState({
			filteredRooms: suggestionList
		})
		if(e.target.value === "") {
			this.setState({
				filteredRooms: []
			});
			return;
		}
		var _filters = this.state.filters.slice();
		_filters.map(function(item, i) {
			suggestionList.map(function(_item, j) {
				if(item['threadId'] === _item['threadId'])
					suggestionList.splice(j,1);
			})
		})
		suggestionList = suggestionList.filter(function(item) {
			return item['name'].toLowerCase().search(e.target.value.toLowerCase()) !== -1;
		});
		this.setState({
			filteredRooms: suggestionList
		});
	},
	addFilter: function(elem, event) {
		var that = this;
		var _filters = this.state.filters.concat([elem]);
		
		this.setState({
			filters: _filters,
			filteredRooms: []
		})
		ConfService.protoInstance.newPostingLocationsRooms.push(elem.threadId);
		var _postingRooms = ConfService.protoInstance.postingLocationsRooms.length > 0 ? ConfService.protoInstance.postingLocationsRooms.slice() : [] ;
		var _notPostingRooms = ConfService.protoInstance.notPostingLocationsRooms.length > 0 ? ConfService.protoInstance.notPostingLocationsRooms.slice() : ConfService.userChatRooms.slice();
		_postingRooms.push(elem);
		_notPostingRooms.map(function(item, i) {
			if(item['threadId'] == elem['threadId']) {
				_notPostingRooms.splice(i,1);
				that.setState({
					rooms: _notPostingRooms.slice()
				})
				return;
			}
		});

		ConfService.protoInstance.postingLocationsRooms = _postingRooms.slice();
		ConfService.protoInstance.notPostingLocationsRooms = _notPostingRooms.slice();
		return false;
	},
	removeFilter: function(elem, target) {
		var that = this;
		var suggestionsList = ConfService.protoInstance.notPostingLocationsRooms.slice();
		var _filteredRooms = this.state.filteredRooms.slice();
		var _filters = this.state.filters.slice();
		var _postingRooms = ConfService.protoInstance.postingLocationsRooms.length > 0 ? ConfService.protoInstance.postingLocationsRooms.slice() : [] ;
		var _notPostingRooms = ConfService.protoInstance.notPostingLocationsRooms.slice();
		var _newPostingRooms = ConfService.protoInstance.newPostingLocationsRooms.slice();
		_postingRooms.map(function(item, i) {
			if(item['threadId'] == elem['threadId']) {
				_notPostingRooms.push(item);
				_postingRooms.splice(i,1);
				ConfService.protoInstance.postingLocationsRooms = _postingRooms.slice();
				ConfService.protoInstance.notPostingLocationsRooms = _notPostingRooms.slice();
				that.setState({
					rooms: _notPostingRooms.slice()
				})
				return;
			}
		})
		_filters.map(function(item, i) {
			if(item['threadId'] == elem['threadId']) {
				_filters.splice(i,1);
				_filteredRooms.push(elem);
				that.setState({
					filters: _filters,
					filteredRooms: _filteredRooms
				})
				return;
			}
		})
		if(target.value === "") {
			this.setState({
				filters: _filters,
				filteredRooms: []
			});
			return;
		}
		_filteredRooms = _filteredRooms.filter(function(item) {
			return item['name'].toLowerCase().search(target.value.toLowerCase()) !== -1;
		});
		_newPostingRooms.some(function(item) {
			if (item == elem['threadId']) {
				_newPostingRooms.splice(i,1);
				ConfService.protoInstance.newPostingLocationsRooms = _newPostingRooms.slice();
			}
		});
		this.setState({
			filteredRooms: _filteredRooms
		})
	},
	clearInput: function() {
		this.setState({
			filteredRooms: []
		})
	},
	render: function() {
		return(
			<div className='posting-location-container'>
				<h5>Posting Location</h5>
				<div className="radio-group">
					<div className="radio">
						<input id="one-one" type="radio" onChange={this.onChange} className="with-font" value="one-one" name="posting" ref="oneOne" />
						<label htmlFor="one-one">{this.state.oneOneLabel}</label>
					</div>
					<div className="radio top">
						<input id="chat-room" ref="chatRoom" type="radio" onChange={this.onChange} value="chat-room" name="posting" ref="chatRoom" />
						<div className="posting-location-warning"><label htmlFor="chat-room">Existing chat room</label><h6>You can only add this integration to a room of which you are an <strong>owner</strong>. You can choose one or more rooms.</h6></div>
					</div>
				</div>
				{this.state.showSearch && (<SearchRooms filters={this.state.filters} filteredRooms={this.state.filteredRooms} rooms={this.state.rooms} onChangeInput={this.onChangeSearch} callAddFilter={this.addFilter} callRemoveFilter={this.removeFilter} callClearInput={this.clearInput} />)}
			</div>
		);
	}
});
module.exports = EditPostingLocation;
