import React from 'react'
import ReactDOM from 'react-dom'
require('./styles/styles.css');
var ConfService = require('../../js/configurator.service.js');

var CreatePostingLocation = React.createClass({
	getInitialState: function() {
		return {
			filters: []
		}
	},
	componentWillMount: function() {
		var _filters = [];
		ConfService.protoInstance.postingLocationsRooms.map(function(item,i){
			_filters.push(item);
		})
		this.setState({
			filters: _filters.slice()
		});
	},
	render: function() {
		return(
			<div className='posting-location-container'>
				{this.state.filters.length > 0 && (<h5>Posting Location</h5>)}
				{this.state.filters.length > 0 && (<SearchRooms filters={this.state.filters} />)}
			</div>
		);
	}
});

var SearchRooms = React.createClass({
	propTypes: {
		filters: React.PropTypes.arrayOf(React.PropTypes.object).isRequired,
	},
	getInitialState: function() {
		var __filters = this.props.filters.slice();
		return {
			filters: __filters //filters: [], //array of objects <FilterBox />
		};
	},
	render: function() {
		return(
			<div className="filter-box-container-success">
				{this.state.filters.map(function(item, idx) {
					return <FilterBox room={item} key={idx} />
				})}
			</div>
		);
	}
});

/* FilterBox									Component filter
*/
var FilterBox = React.createClass({
	propTypes: {
		room: React.PropTypes.object.isRequired,
	},
	render: function() {
		var members = this.props.room['memberCount'] > 1 ? this.props.room['memberCount'] +" Members" : this.props.room['memberCount'] +" Member";;
		return(
			<div className="filter-box">
				{this.props.room['publicRoom'] == false ? <div><span>{this.props.room['name']}</span><span><i className="fa fa-lock"></i></span></div> : <div><span>{this.props.room['name']}</span></div>}
				<div className="room-info">
					<span>{members +", created by "+ this.props.room['creatorPrettyName']}</span>
				</div>
				
			</div>
		);
	}
})
module.exports = SearchRooms;
module.exports = CreatePostingLocation;