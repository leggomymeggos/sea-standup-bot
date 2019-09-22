// this points to standup-selector-test.slack.com; sign in with the account mgaebler+standupbot@pivotal.io

var express = require('express');
var request = require('request');

var clientId = process.env.SLACK_CLIENT_ID;
var clientSecret = process.env.SLACK_CLIENT_SECRET;

var app = express();

var PORT = 4390;

app.listen(PORT, function () {
    console.log("Local slack app listening on port " + PORT);
});

app.get('/', function (req, res) {
    res.send('Ngrok is working! Path Hit: ' + req.url)
});

app.get('/oauth', function (req, res) {
    if (!req.query.code) {
        res.status(500);
        res.send({"Error": "Looks like we're not getting code :<"});
        console.log("Looks like we're not getting code :(");
    } else {
        request({
            url: 'https://slack.com/api/oauth.access',
            qs: {
                code: req.query.code,
                client_id: clientId,
                client_secret: clientSecret
            },
            method: 'GET'
        }, function (error, response, body) {
            if (error) {
                console.log(error);
            } else {
                res.json(body);
            }
        });
    }
});

app.post('/command', function (req, res) {
    res.send('Everything is a-ok! b^.^d');
});

app.post('/local-standup-selector', function (req, res) {
    request({
        url: 'http://127.0.0.1:8080/handle-interaction',
        method: 'POST',
        body: req.body
    }, function (error, response, body) {
        if (error) {
            console.log(error);
        } else {
            console.log(body);
            res.json(body);
        }
    });
});

app.get('/run-app', function (req, res) {
    request.post({
        url: 'https://slack.com/api/chat.postMessage',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + process.env.SLACK_AUTH_TOKEN
        },
        // language=JSON
        body: "{\n  \"text\": \"Hallo\",\n  \"channel\": \"DM8Q9PE7J\",\n  \"attachments\": [\n    {\n      \"text\": \"Glorious standup bot, bot of bots, selector of standuppers, has generously chosen YOU, to run standup for the upcoming week.\",\n      \"fallback\": \"Interactive messages not supported via this client\",\n      \"callback_id\": \"" + process.env.SLACK_CALLBACK_TOKEN + "\",\n      \"color\": \"#3AA3E3\",\n      \"attachment_type\": \"default\",\n      \"actions\": [\n        {\n          \"name\": \"confirmation_option\",\n          \"text\": \"I accept the bot\'s glorious offer\",\n          \"type\": \"button\",\n          \"value\": \"yes\",\n          \"confirm\": {\n            \"title\": \"Are you sure you want to confirm?\",\n            \"text\": \"You will need to reach out to an admin to change this (currently)\",\n            \"ok_text\": \"Yes\",\n            \"dismiss_text\": \"No\"\n          }\n        },\n        {\n          \"name\": \"confirmation_option\",\n          \"text\": \"I defiantly refuse the bot\",\n          \"style\": \"danger\",\n          \"type\": \"button\",\n          \"value\": \"no\",\n          \"confirm\": {\n            \"title\": \"Are you sure you wish to defy Standup Bot?\",\n            \"text\": \"Standup bot may forgive, but it will never forget\",\n            \"ok_text\": \"Yes\",\n            \"dismiss_text\": \"No\"\n          }\n        }\n      ]\n    }\n  ]\n}"
    }, function (error, response, body) {
        if (error) {
            console.log(error);
        } else {
            res.json(body);
        }
    })
});

app.get('/init', function (req, res) {
    request.get({
        url: 'http://127.0.01:8080/populate'
    }, function (error, response, body) {
        if (error) {
            console.log(error);
        } else {
            res.json(body);
        }
    })
});

app.get('/select', function (req, res) {
    request.get({
        url: 'http://127.0.01:8080/trigger-selection'
    }, function (error, response, body) {
        if (error) {
            console.log(error);
        } else {
            res.json(body);
        }
    })
});