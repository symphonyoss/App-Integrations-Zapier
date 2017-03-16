import React from 'react';
import './styles/warning.css';

export default class Warning extends React.Component {
	constructor(props) {
		super(props);
		this.onClose = this.onClose.bind(this);
	}

	onClose() {
		this.props.onclose();
		return false;
	}

	render() {
		var _class = "";
		switch(this.props.category) {
			case "ERROR": _class = "error";
			break;
			case "WARNING": _class = "warning";
			break;
			case "REQUIRED": _class = "required";
			break;
			default: _class = "success";
			break;
		}
		return(
			<div className={"warning-box "+ _class} id="warning-box">
				<div>
					<p>{this.props.message}</p>
				</div>
				<div>
					<a href="javascript:void(null)" onClick={this.onClose} ><i className="fa fa-times"></i></a>
				</div>
			</div>
		);
	}
}