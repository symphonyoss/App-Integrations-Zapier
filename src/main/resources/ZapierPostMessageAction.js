'use strict';

/*

 This script must be deployed at Zapier, as the Post Message action script.

 It is responsible to dispatch the action data to Zapier WHIB instance at Symphony, with the message
 header, content and icon provided by the user.

 */

var Zap = {
    post_message_write: function(bundle) {
        /*
         Arguments:

         bundle.request.url: <string>
         bundle.request.method: <string> # 'POST'
         bundle.request.auth: <array> # [username, password]
         bundle.request.headers: <object>
         bundle.request.params: <object> # this will be mapped into the querystring
         bundle.request.files: <object> # object of keys/arrays
         # * 1st item: filename str or null
         # * 2nd item: zapier.com endpoint that will stream the file
         # * 3rd item: mimetype str or null
         bundle.request.data: <string> # str or null

         bundle.url_raw: <string>
         bundle.auth_fields: <object>
         bundle.action_fields: <object> # pruned and replaced users' fields
         bundle.action_fields_full: <object> # all replaced users' fields
         bundle.action_fields_raw: <object> # before we replace users' variables

         bundle.zap: <object> # info about the zap

         If you include a callback in the arguments, you can also perform async:
         callback(err, response)

         The response will be used to give the user more fields to use
         in the next step of the Zap.  Please return a JSON serializable object.

         return <object>;
         */

        // build the request with the received bundle content
        var request = {
            'method': bundle.request.method,
            'url': bundle.request.url,
            'headers': {
                'Content-Type': 'application/json',
                'Zapier-Event-Type': 'post_message'
            },
            'data': JSON.stringify(bundle)
        };

        // perform synchronously
        var response = z.request(request);

        // log some data
        console.log('Status: ' + response.status_code);
        console.log('Headers: ' + JSON.stringify(response.headers));
        console.log('Content/Body: ' + response.content);
    }
};
