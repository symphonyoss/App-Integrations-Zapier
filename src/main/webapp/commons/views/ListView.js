import React from 'react';
import ReactDOM from 'react-dom';
import { hashHistory } from 'react-router';
import SetupInstructions from '../components/SetupInstructions/SetupInstructions';
import Warning from '../components/Warning/Warning';
import config from '../../configurator/config';
var ConfService = require('../js/configurator.service.js');


var ListView = React.createClass({
  propTypes: {
    params: React.PropTypes.object
  },
  onConfigureNew: function() {
    hashHistory.push('/create-view');
  },
  getInitialState: function() {
    return {
      showWarning: true,
      loading: false,
      status: false,
      message: "",
      error: false,
      showIntances: false
    }
  },
  componentWillMount: function() {
    var _msg;

    if(this.props.params) {
      if(this.props.params.status !== undefined){
        switch(this.props.params.status) {
          case "created": _msg = ConfService.messages.created;
          break;
          case "updated": _msg = ConfService.messages.updated;
          break;
          case "deactivated":
            _msg = ConfService.messages.deactivated;
            this.setState({
              showIntances: true
            })
          break;
          case "error":
            _msg = ConfService.messages.error;
            this.setState({
              error: true
            })
          break;
          default:
            this.setState({
              error: false
            })
          break;
        }
        this.setState({
          message: _msg,
          status: true
        })
      }
    }

    if(ConfService.dataResponse == 1) {
      this.setState({
        showWarning: false,
        loading: true,
      })
    } else {
      this.setState({
        showWarning: true,
        loading: false,
        message: ConfService.messages.not_found
      })
    }
  },
  componentDidMount: function() {
    if(this.refs.instanceTable.getElementsByTagName("td").length > 0) {
      this.setState({
        showWarning: false,
        loading: false,
         showIntances: true
      })
    }
    if(this.props.params) {
      if(this.props.params.status == "error") {
        this.setState({
          loading: false
        })
      }
    }

  },
  onCloseStatus: function() {
    this.setState({
      status: false,
      message: ""
    })
  },
  onCopyWebhookUrl: function() {
    var whurl = ConfService.baseURL +"/v1/whi/"+ ConfService.appId +"/"+ this.props.instance.configurationId + '/' + this.props.instance.instanceId;

  },
  render: function() {
    var rows = ConfService.instanceList.slice() || [];
    var _cat;
    if(this.state.error) {
      _cat = "ERROR";
    } else if(this.state.showWarning) {
      _cat = "WARNING";
    } else {
      _cat = "SUCCESS";
    }
    return (
      <div>
        {this.state.status && (<Warning message={this.state.message} category={_cat} onclose={this.onCloseStatus} />)}
        <div className={ this.state.showIntances ? "whi-table block" : "hide"} >
          <SetupInstructions />
          <div id="header">
            <h2>Configured Integrations</h2>
            <button onClick={this.onConfigureNew} className="button">Configure New</button>
          </div>
          <table ref="instanceTable" className={this.state.showWarning ? "hide" : ""}>
            <thead>
            <tr>
              <th>Description</th>
              <th>Active In</th>
              <th>Webhook URL</th>
              <th>Last Posted</th>
              <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            { rows.map(function(item, idx) {
              return <DataRow key={idx} instance={item} copyId={idx} />
            })}
            </tbody>
          </table>
        </div>
        {this.state.loading && (<div className="spinner"><div><i className="fa fa-circle-o-notch fa-spin"></i></div><p>{ConfService.messages.loading}</p></div>)}
      </div>
    )
  }
})

var DataRow = React.createClass ({
  propTypes: {
    instance: React.PropTypes.object,
    copyId: React.PropTypes.number
  },
  getInitialState: function() {
    return {
      enableCopy: true
    }
  },
  onClickRemove: function() {
    hashHistory.push('/remove-view/'+ this.props.instance.instanceId +'/'+ this.props.instance.name);
  },
  onClickEdit: function() {
    hashHistory.push('/edit-view/'+ this.props.instance.instanceId);
  },
  onCopyURL: function(e) {
    var target = e.target;
    this.setState({
      enableCopy: false
    })
    // Copy to clipboard without displaying input
    var textarea = document.createElement('textarea');
    textarea.style.position = 'relative';
    textarea.style.top = 0;
    textarea.style.left = 0;
    textarea.style.width = '1px';
    textarea.style.height = '1px';
    textarea.style.padding = 0;
    textarea.style.border = 0;
    textarea.style.outline = 0;
    textarea.style.boxShadow = 0;
    textarea.style.background = 'transparent';
    textarea.style.fontSize = 0;

    var webhookUrl = document.querySelector(target.dataset.copytarget) ? document.querySelector(target.dataset.copytarget).getAttribute('data-value') : null;
    textarea.value = webhookUrl;
    if (textarea) {
      target.parentNode.appendChild(textarea);
      textarea.select();
      try {
          // copy text
          document.execCommand('copy');
          target.innerHTML = "Copied!";
          var that = this;
          setTimeout(function(){
            target.innerHTML = "Copy URL";
            target.parentNode.removeChild(target.parentNode.getElementsByTagName('textarea')[0]);
            that.setState({
              enableCopy: true
            })
          },2000);
        } catch (err) {
          console.log(err);
        }
    } else {
      console.log('element not found ' + textarea);
    }
  },
  render: function() {
    var posting_locations_names = [];
    this.props.instance.postingLocationsRooms.map(function(item,i){
      posting_locations_names.push(item.name);
    })
    var that =  this;
    var _url = '/crud-view/' + this.props.instance.instanceId;

    var whUrl = ConfService.baseURL +"/v1/whi/"+ ConfService.appId +"/"+ this.props.instance.configurationId + '/' + this.props.instance.instanceId;
    var cropedWebHookUrl = whUrl.substr(0, 35) + '...';
    return(
      <tr>
        <td><span>{this.props.instance.name}</span></td>
        <td>
          <ul>
          {posting_locations_names.map(function(e, i){
            return <li key={i} ><span>{that.props.instance.streamType == "IM" ? ConfService.labelPostinLocations : e} { (i < posting_locations_names.length-1) ? ", " : ""}</span></li>
          })}
        {posting_locations_names.length === 0 && this.props.instance.streamType == "IM" && (<li><span>{ConfService.labelPostinLocations +  config.IM_shorthand}</span></li>)}
          </ul>
        </td>
        <td>
          <div className='url-container'>
            <span id={"webhook-url-"+ this.props.copyId} data-value={whUrl}>{cropedWebHookUrl}</span>
            { this.state.enableCopy ? <a href="javascript:void(null)" data-copytarget={"#webhook-url-"+ this.props.copyId}  onClick={this.onCopyURL}>Copy URL</a> : <a href="javascript:void(null)" data-copytarget={"#webhook-url-"+ this.props.copyId} >Copy URL</a> }
          </div>
        </td>
        <td><span>{this.props.instance.lastPosted}</span></td>
        <td>
          <ul>
            <li><a href="javascript:void(null)" onClick={this.onClickEdit} >Edit</a></li>
            <li><a href="javascript:void(null)" onClick={this.onClickRemove} >Remove</a></li>
          </ul>
        </td>
      </tr>
    )
  }
})
ReactDOM.render(<ListView />, document.getElementById("instance-list"));
module.exports = ListView;
