import React from 'react';
import ReactDOM from 'react-dom';
import { hashHistory } from 'react-router';
import IntegrationIdentity from '../components/IntegrationIdentity/IntegrationIdentity';
import EditPostingLocation from '../components/EditPostingLocation/EditPostingLocation';
import SubmitWebHook from '../components/SubmitWebHook/SubmitWebHook';
import SetupInstructions from '../components/SetupInstructions/SetupInstructions';
import WebHookURL from '../components/WebHookURL/WebHookURL';
import Warning from '../components/Warning/Warning';
var ConfService = require('../js/configurator.service.js');

var EditView = React.createClass({
	propTypes: {
		params: React.PropTypes.object.isRequired
	},
	getInitialState: function() {
		var that = this;
		var _instance = ConfService.instanceList.filter(function(item){
			return item.instanceId == that.props.params.instanceId;
		})
		return{
			instance: _instance[0],
			messages: []
		}
	},
	onUpdate: function() {
		var _msgs = [], _str = "";
		if(ConfService.required.name) {
			_msgs.push(ConfService.messages.name_required);
		} 
		if(ConfService.required.rooms) {
			_msgs.push(ConfService.messages.rooms_required);
		} 
		if(_msgs.length == 0) {
			hashHistory.push('/save-webhook/'+ this.state.instance.instanceId);
		} else {
			this.setState({
				messages: _msgs.slice()
			})	
		}
	},
	onClose: function(item) {
		var _msgs = this.state.messages.slice();
		_msgs.map( (__item, i) => {
			if(item == __item) {
				_msgs.splice(i, 1);
			}
		} )
		this.setState({
			messages: _msgs.slice()
		})
	},
	onCancel: function() {
		hashHistory.push('/list-view');
	},
	render: function() {
		var that = this;
		return(
			<div className='block'>
				{this.state.messages.map(function(item, i) { 
					return <Warning message={item} ref={i} category={"REQUIRED"} onclose={that.onClose.bind(that, item)} key={i} />
				})}
				<IntegrationIdentity renderLogo={true} instanceName={this.state.instance.name} />
				<EditPostingLocation instanceId={this.state.instance.instanceId} />
				<WebHookURL instanceId={this.state.instance.instanceId} configurationId={ConfService.configurationId} baseUrl={ConfService.baseURL} appId={ConfService.appId} />
				<SetupInstructions />
				<div className="remove-btn-container">
					<button className="button" onClick={this.onUpdate} >Update</button>
					<button className="button cancel-link" onClick={this.onCancel} >Cancel</button>
				</div>
			</div>
		);
	}
});
module.exports = EditView;