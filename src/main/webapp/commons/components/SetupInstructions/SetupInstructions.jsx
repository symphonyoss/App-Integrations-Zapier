import React from 'react';
import ReactDOM from 'react-dom';
import Setup from '../../../configurator/conf_setup_instructions/Setup';
import ConfService from '../../js/configurator.service'

class SetupInstructions extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hidden: true
    }
    this.showHideInstructions = this.showHideInstructions.bind(this);
  }

  showHideInstructions() {
    if(this.state.hidden) {
      this.refs.setupInstructions.className += " setup-instructions-transition";

    } else {
      this.refs.setupInstructions.className = "setup-instructions";
    }
    this.setState({
      hidden: !this.state.hidden
    })
    return false;
  }

  render() {
    return(
      <div ref="setupInstructions" className="setup-instructions">
        { ConfService.toogleSetup &&
          (
            <div className='setup-instructions-header'>
              <div>
                <h2>Setup Instructions</h2>
                <p>Here are the steps necessary to add the {ConfService.configurationName} integration.</p>
              </div>
              <div>
                <a href='javascript:void(null)'onClick={this.showHideInstructions}>
                  <i className={ this.state.hidden ? "fa fa-chevron-down": "fa fa-chevron-up" } aria-hidden="true"></i>
                </a>
              </div>
            </div>
        )}
        <Setup />
      </div>
    );
  }
}

export default SetupInstructions;
