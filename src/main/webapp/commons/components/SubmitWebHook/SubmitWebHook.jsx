import React from 'react'
import ReactDOM from 'react-dom'
import { hashHistory } from 'react-router'
require('./styles/styles.css');
var ConfService = require('../../js/configurator.service.js');

var SubmitWebHook = React.createClass({
	propTypes: {
		showMessage: React.PropTypes.func
	},
	onSubmit: function() {
		var msg = [];
		if(ConfService.required.name) {
			msg.push(ConfService.messages.name_required);
		} 
		if(ConfService.required.rooms) {
			msg.push(ConfService.messages.rooms_required);
		} 
		if(msg.length > 0) {
			this.props.showMessage(msg);	
		}
		else {
			hashHistory.push('/save-webhook');	
		}
		
	},
	onCancel: function() {
		hashHistory.push('/list-view');
	},
	render: function() {
		return(
			<div className="submit-webhook">
				<div className="btn-container">
					<button className="button" onClick={this.onSubmit} type="button" >Add</button>
					<button className="button cancel-link" onClick={this.onCancel}>Cancel</button>
				</div>
			</div>
		)
	}
});
module.exports = SubmitWebHook;