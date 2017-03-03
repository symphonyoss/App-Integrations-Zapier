import React from 'react'
import ReactDOM from 'react-dom'
import { hashHistory } from 'react-router'
import IntegrationIdentity from '../components/IntegrationIdentity/IntegrationIdentity'
import SuccessPostingLocation from '../components/SuccessPostingLocation/SuccessPostingLocation'
import WebHookURL from '../components/WebHookURL/WebHookURL'
import SetupInstructions from '../components/SetupInstructions/SetupInstructions'
var ConfService = require('../js/configurator.service.js');

var RemoveView = React.createClass({
	propTypes: {
		params: React.PropTypes.object.isRequired
	},
	getInitialState: function() {
		return{
			name: this.props.params.name,
			instanceId: this.props.params.instanceId
		}
	},
	componentWillMount: function() {
		var that = this;
		var _instance = ConfService.instanceList.filter(function(item){
			return item.instanceId == that.props.params.instanceId;
		})
		ConfService.protoInstance.postingLocationsRooms = _instance[0].postingLocationsRooms.slice();
		ConfService.protoInstance.streamType = _instance[0].streamType;
	},
	onRemove: function() {
		hashHistory.push('/save-webhook/'+ this.state.instanceId);
	},
	onCancel: function() {
		hashHistory.push('/list-view');
	},
	render: function() {
		return(
			<div className='block'>
				<IntegrationIdentity renderLogo={true} instanceName={this.state.name} disabled={true} />
				<SuccessPostingLocation />
				<WebHookURL instanceId={this.state.instanceId} configurationId={ConfService.configurationId} baseUrl={ConfService.baseURL} appId={ConfService.appId} />
				<div className="remove-btn-container">
					<button className="button" onClick={this.onRemove} >Remove</button>
					<button className="button cancel-link" onClick={this.onCancel} >Cancel</button>
				</div>
			</div>
		);
	}
});
module.exports = RemoveView;