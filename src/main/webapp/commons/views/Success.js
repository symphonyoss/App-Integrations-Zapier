import React from 'react'
import ReactDOM from 'react-dom'
import { hashHistory } from 'react-router'
import IntegrationIdentity from '../components/IntegrationIdentity/IntegrationIdentity'
import SuccessPostingLocation from '../components/SuccessPostingLocation/SuccessPostingLocation'
import WebHookURL from '../components/WebHookURL/WebHookURL'
import SetupInstructions from '../components/SetupInstructions/SetupInstructions'
var ConfService = require('../js/configurator.service.js');
require('../styles/styles.css');
var Success = React.createClass({
	onClick: function() {
		ConfService.resetProtoInstance();
		hashHistory.push('/list-view/created');
	},
	render: function() {
		return(
			<div className="block">
				<IntegrationIdentity renderLogo={true} instanceName={ConfService.protoInstance.name} disabled={true} />
				<SuccessPostingLocation />
				<WebHookURL instanceId={ConfService.protoInstance.instanceId} configurationId={ConfService.configurationId} baseUrl={ConfService.baseURL} appId={ConfService.appId} />
				<div>
					<SetupInstructions />
					<div className="success-view">
						<button className="button" onClick={this.onClick} >Done</button>
					</div>
				</div>
			</div>
		);
	}
})
module.exports = Success;
