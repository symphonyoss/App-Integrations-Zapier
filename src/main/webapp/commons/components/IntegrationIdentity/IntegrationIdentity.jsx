import React from 'react'
import ReactDOM from 'react-dom'
var ConfService = require('../../js/configurator.service.js');

var IntegrationIdentity = React.createClass({ 
    propTypes: {
      renderLogo: React.PropTypes.bool,
      instanceName: React.PropTypes.string,
      disabled: React.PropTypes.bool
    },
    getDefaultProps: function() {
      return {
        renderLogo: false,
        instanceName: "",
        disabled: false
      };
    },
    getInitialState: function() {
      return {
        name: "",
        filled: false,
        required: true
      };
    },
    componentWillMount: function() {
      if(this.props.instanceName !== "") {
        ConfService.required.name = false;
        this.setState({
          name: this.props.instanceName,
          filled: true
        })
      } else {
        ConfService.required.name = true;
      }
      this.setState({
        required: ConfService.required.name
      })
    },
    componentDidMount: function() {
      this.refs.instanceName.focus();
    },
    handleNameChange: function(e) {
      if(e.target.value !== "") {
        ConfService.required.name = false;
        this.setState({
          filled: true,
          required: false
        })
      } else { 
        ConfService.required.name = true;
        this.setState({
          filled: false,
          required: true
        })
      }
      this.setState({
        name: e.target.value
      });
      ConfService.protoInstance.name = e.target.value;
    },
    render: function() {
      const displayName = ConfService.configurationName.replace(new RegExp('(webHook)', 'gi'), '');
      return (
        <div className='integration-identity-container'>
          <div className="integration-identity-logo-div">
            <figure>
               <img src={require('../../../configurator/img/logo.png')} alt={ConfService.configurationName} />
            </figure>
            <h5>
              {ConfService.configurationName}
            </h5>
          </div>
          <h3>{displayName} Webhook Integration</h3>
          <h5><label htmlFor="ii-name">Description</label></h5>
          <div className="integration-identity-input">
            <input type="text" className="text-input" ref="instanceName" id="ii-name" placeholder="Add a short description here" value={this.state.name} onChange={this.handleNameChange} disabled={this.props.disabled} />{/*!this.props.disabled && (this.state.filled ? <span className="checked"><i className="fa fa-check" aria-hidden="true"></i></span> : <span className="required"><i className="fa fa-asterisk" aria-hidden="true"></i></span>)*/}
            {!this.props.disabled && !this.state.filled && (<span className="required"><i className="fa fa-asterisk" aria-hidden="true"></i></span>)}
          </div>
        </div>
      )
    }
});
module.exports = IntegrationIdentity;