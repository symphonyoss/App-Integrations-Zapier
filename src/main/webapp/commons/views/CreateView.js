import React from 'react'
import ReactDOM from 'react-dom'
import IntegrationIdentity from '../components/IntegrationIdentity/IntegrationIdentity'
import CreatePostingLocation from '../components/CreatePostingLocation/CreatePostingLocation'
import SubmitWebHook from '../components/SubmitWebHook/SubmitWebHook'
import Warning from '../components/Warning/Warning';
var ConfService = require('../js/configurator.service.js');

var CreateView = React.createClass({
	getInitialState: function() {
		return {
			showWarning: true,
			messages: []
		}
	},
	showMessage: function(_msg) {
		this.setState({
			messages: _msg.slice()
		})
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
	render: function() {
		var that = this;
		return(
			<div className="container-component block">
				{this.state.messages.map(function(item, i) { 
					return <Warning message={item} ref={i} category={"REQUIRED"} onclose={that.onClose.bind(that, item)} key={i} />
				})}
				<IntegrationIdentity renderLogo={true} />
				<CreatePostingLocation />
				<SubmitWebHook showMessage={this.showMessage} />
			</div>
		);
	}
});



module.exports = CreateView;