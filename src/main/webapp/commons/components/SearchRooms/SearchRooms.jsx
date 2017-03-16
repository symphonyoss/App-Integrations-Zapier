import React from 'react'
import ReactDOM from 'react-dom'
var ConfService = require('../../js/configurator.service.js');

var SearchRooms = React.createClass({
	propTypes: {
		filteredRooms: React.PropTypes.arrayOf(React.PropTypes.object).isRequired,
		rooms: React.PropTypes.arrayOf(React.PropTypes.object).isRequired,
		filters: React.PropTypes.arrayOf(React.PropTypes.object).isRequired,
		onChangeInput: React.PropTypes.func.isRequired,
		callAddFilter: React.PropTypes.func.isRequired,
		callRemoveFilter: React.PropTypes.func.isRequired,
		callClearInput: React.PropTypes.func.isRequired
	},
	getInitialState: function() {
		var __filters = [];
		var that = this;
		var __obj = {};
		this.props.filters.map(function(elem, idx) {
			__filters.push(elem);
		});
		return {
			filters: __filters, //filters: [], //array of objects <FilterBox />
			disabled: false,
			saved: ConfService.newInstanceCreated,
			filled: false,
			required: true,
			focused: -1,
			listening: false
		};
	},
	componentWillMount: function() {
		if(this.props.filters.length > 0) {
			this.setState({
				filled: true,
				required: false
			})
			ConfService.required.rooms = false;
		} else {
			ConfService.required.rooms = true;
		}
	},
	componentDidMount: function() {
		var input = this.refs.inputSearch;
		input.focus();
		//input.addEventListener('keydown', this.inputListener);
	},
	inputListener: function() {
		var idx = this.state.focused;
		var input = this.refs.inputSearch;
		if(document.getElementsByTagName("ul")[0] !== undefined) {
			if(event.keyCode == "40") { //down
				input.blur();
				input.removeEventListener('keydown', this.inputListener);
				if(this.props.filteredRooms.length > 0 && idx < this.props.filteredRooms.length) {
					idx++;
					document.getElementsByTagName("ul")[0].focus();
					document.getElementsByTagName("ul")[0].addEventListener('keydown', this.listListener);
				}
			}
			this.setState({
				focused: idx
			})
		}
	},
	listListener: function() {
		var idx = this.state.focused;
		var input = this.refs.inputSearch;
		if(event.keyCode == "40") { //down
			if(this.props.filteredRooms.length > 0 && idx < this.props.filteredRooms.length-1) {
				idx++;
			}
		} else if(event.keyCode == "38") { //up
			if(idx > 0) {
				idx--;
			} else {
				idx = -1;
				document.getElementsByTagName("ul")[0].removeEventListener('keydown', this.listListener);
				var _tmr = setInterval(function(){
					if(input.value != "") {
						clearInterval(_tmr);
						input.focus();	
					}
				},50);
				input.addEventListener('keydown', this.inputListener);
			}
		}
		this.setState({
			focused: idx
		})
	},
	addFilterBox: function(elem, event) { //expects a object room
		var input = this.refs.inputSearch;
		this.setState({
			filters: this.state.filters.concat([elem])
		});
		this.props.callAddFilter(elem, event);
		input.value = "";
		input.focus();
		this.setState({
			filled: true,
			required: false,
			focused: -1
		})
		ConfService.required.rooms = false;
		input.addEventListener('keydown', this.inputListener);
	},
	removeFilterBox: function(elem) {
		var _filters = this.state.filters.slice();
		_filters.map(function(item, i) {
			if(item['threadId'] === elem['threadId']) {
				_filters.splice(i,1);
			}
		});
		this.setState({
			filters: _filters
		});
		if (_filters.length == 0) {
			this.setState({
				filled: false,
				required: true
			})
			ConfService.required.rooms = true;
		};
		this.refs.inputSearch.focus();
		this.props.callRemoveFilter(elem, this.refs.inputSearch);
	},
	onChangeInput: function(e) {
		var input = e.target;
		var that = this;
		this.props.onChangeInput(e);
		if(input.value != "") {
			if(!this.state.listening) {
				this.setState({
					listening: true
				})
				input.addEventListener('keydown', this.inputListener);
			}
		} else if(input.value == "") {
			this.setState({
				listening: false
			})
			input.removeEventListener('keydown', this.inputListener);
		}

	},
	clearInputSearch: function() {
		this.refs.inputSearch.value = "";
		this.refs.inputSearch.focus();
		this.props.callClearInput();
		return false;
	},
	render: function() {
		var that = this;
		return(
			<div className="posting-location-container">
				<div className="input-container">
					<div className="input-search-rooms-container">
						<a href="javascript:void(null)" className="clear" onClick={this.clearInputSearch}><i className="fa fa-times"></i></a>
						<input type="text" className="input-posting-location" placeholder="Search rooms" ref="inputSearch" onChange={this.onChangeInput} disabled={this.state.saved} autoFocus />
					</div>
					<div className="list-container">
						<div className="list-rooms-container">
							{this.props.filteredRooms.length > 0 && (<SugestionsList items={this.props.filteredRooms} callAddFilter={this.addFilterBox} focusItem={this.state.focused} />)}
						</div>
						<div className={this.props.filteredRooms.length > 0 ? "filter-box-container-hide" : "filter-box-container"}>
							{this.state.filters.map(function(item, idx) {
								return <FilterBox room={item} key={idx} callRemoveFilter={that.removeFilterBox} />
							})}
						</div>
					</div>
				</div>
				{/*this.state.filled ? <span className="checked"><i className="fa fa-check" aria-hidden="true"></i></span> : <span className="required"><i className="fa fa-asterisk" aria-hidden="true"></i></span>*/}
				{!this.state.filled && (<span className="required"><i className="fa fa-asterisk" aria-hidden="true"></i></span>)}
			</div>
		);
	}
});

/* SugestionsList								List of sugestions based on the content typed within the input search
*/
var SugestionsList = React.createClass({
	propTypes: {
		items: React.PropTypes.arrayOf(React.PropTypes.object).isRequired,
		callAddFilter: React.PropTypes.func.isRequired,
		focusItem: React.PropTypes.number.isRequired
	},
	//hasListener: false,
	//focusIndex: -1,
	getInitialState: function() {
		return {
			items: this.props.items
		}
	},
	onSetFocus: function(_val) {
		this.setState({
			focusedItem: _val
		})
	},
	addFilter: function(item, event) {
		this.props.callAddFilter(item, event);
		return false;
	},
	render: function() {
		var that = this;
		var members;
		return(
			<ul className="room-box" ref="roomsList">
			{
				this.props.items.map(function(item, idx) {
					members = item['memberCount'] > 1 ? item['memberCount'] +" Members" : item['memberCount'] +" Member";
					return <li key={idx} >
						<a  href="javascript:void(null)"
							id={item['nameKey']}
							onClick={that.addFilter.bind(that, item)}
							ref={function(){
								if(that.props.focusItem == idx) {
									document.getElementsByTagName("ul")[0].childNodes[idx].childNodes[0].focus();
								}
							}}
						>
							<span>{item['name']}</span> {item['publicRoom'] === false && (<i className="fa fa-lock"></i>)}
							<div className="room-info">
								<span>{members +", created by "+ item['creatorPrettyName']}</span>
							</div>
						</a>
					</li>
				})
			}
			</ul>
		);
	}
})

/* FilterBox									Component filter
*/
var FilterBox = React.createClass({
	propTypes: {
		room: React.PropTypes.object.isRequired,
		callRemoveFilter: React.PropTypes.func.isRequired
	},
	removeFilter: function() {
		this.props.callRemoveFilter(this.props.room);
		return false;
	},
	render: function() {
		var members = this.props.room['memberCount'] > 1 ? this.props.room['memberCount'] +" Members" : this.props.room['memberCount'] +" Member";;
		return(
				<div className="filter-box">
					{this.props.room['publicRoom'] == false ? <div><span>{this.props.room['name']}</span><span><i className="fa fa-lock"></i></span><a ref="myLink" href="javascript:void(null)" onClick={this.removeFilter} ><i className="fa fa-times"></i></a></div> : <div><span>{this.props.room['name']}</span><a ref="myLink" href="javascript:void(null)" onClick={this.removeFilter} ><i className="fa fa-times"></i></a></div>}
					<div className="room-info">
						<span>{members +", created by "+ this.props.room['creatorPrettyName']}</span>
					</div>

				</div>
		);
	}
})
module.exports = SearchRooms;
