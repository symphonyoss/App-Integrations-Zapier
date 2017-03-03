//create our own service
var app_id = getParameterByName('id');
var listService = SYMPHONY.services.register(app_id+":controller");
var config = require('../../configurator/config.js');

SYMPHONY.remote.hello().then(function(data) {
    SYMPHONY.application.register(app_id, ["ui", "modules", "applications-nav", "account", "integrationConfigService", "stream-service"], [app_id+":controller"]).then(function(response) {

         //system services
        var navService = SYMPHONY.services.subscribe("applications-nav");
        var userId = response.userReferenceId;
        var uiService = SYMPHONY.services.subscribe("ui");
        var navService = SYMPHONY.services.subscribe("applications-nav");
        var modulesService = SYMPHONY.services.subscribe("modules");
        var ac = SYMPHONY.services.subscribe("account");

        uiService.registerExtension ('app-settings', app_id, app_id+":controller", {label: 'Configure'});
        
        listService.implement({
            trigger: function(id) {
                //invoke the module service to show our own application in the grid
                var confId = getParameterByName('configurationId');
                var botUserId = getParameterByName('botUserId');
                var context = getParameterByName('context') ? "/"+getParameterByName('context') : "";
                var host = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port;
                modulesService.show(app_id, {title: config.app_title}, app_id+":controller", host + context + "/app.html?configurationId="+confId+"&botUserId="+botUserId+"&id="+app_id, {canFloat: true});
            },
        });

    }.bind(this))
}.bind(this));

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
