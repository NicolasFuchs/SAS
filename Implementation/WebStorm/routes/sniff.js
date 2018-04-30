var express = require('express');
var fs = require('fs');
var os = require('os');
var app = require('app');
var router = express.Router();

router.post('/', function(req, res, next) {
    var filename = os.userInfo().username + "_extension";
    var ws = fs.createWriteStream(filename, {flags:'a'});
    var entry = req.body.url + "|" + req.body.date + "|" + req.body.start + "|" + req.body.end + "|" + req.body.time + "|metadata";
    ws.write(entry + "\n");
    ws.end();
    console.log("url : " + req.body.url + " date : " + req.body.date + " start : " + req.body.start + " end : " + req.body.end + " time : " + req.body.time + " description : " + req.body.description);
    res.send('Info display');
});

module.exports = router;