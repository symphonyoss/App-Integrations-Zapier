/* eslint-disable global-require */
import React from 'react';

const Instructions = () => (
  <div className='setup-instructions-content'>
    <h4>Step 1</h4>
    <p>Create a <a href='https://zapier.com/'>Zapier</a> account if you do not already have one.</p>
    <p>Copy the URL provided above (Example image provided below). You will need this later to
    configure the integration correctly.</p>
    <figure>
      <img src={require('./img/zapier_settings_step1.png')} alt="Webhook configuration" />
    </figure>
    <h4>Step 2</h4>
    <p>Go to <a href='https://zapier.com/'>Zapier</a> and press &quot;Make a Zap.&quot; Follow the
    instructions provided by Zapier to choose your trigger app.</p>
    <figure>
      <img src={require('./img/zapier_settings_step2.png')} alt="Webhook configuration" />
    </figure>
    <h4>Step 3</h4>
    <p>Follow the steps provided by Zapier to setup the trigger app (This is an example using
    Trello).</p>
    <figure>
      <img src={require('./img/zapier_settings_step3.png')} alt="Webhook configuration" />
    </figure>
    <h4>Step 4</h4>
    <p>Choose <strong>Symphony</strong> as your action app. Follow the steps on Zapier to finish
    setting up your zap.</p>
    <p>You will need the URL you copied from Symphony previously. Place this URL into the Webhook
    URL field.</p>
    <figure>
      <img src={require('./img/zapier_settings_step4.png')} alt="Webhook configuration" />
    </figure>
  </div>
);

export default Instructions;
