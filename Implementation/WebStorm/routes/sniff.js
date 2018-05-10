var express = require('express');
var router = express.Router();
var fs = require('fs');
var os = require('os');

var moment = require('moment-timezone');
moment().tz("Europe/Paris");

router.post('/', function(req, res) {
    var filename = os.userInfo().username + "_extension";
    var ws = fs.createWriteStream(filename, {flags:'a'});
    var momentStart = moment(req.body.start).format();
    var momentEnd = moment(req.body.end).format();
    console.log("momentStart : " + momentStart + " momentEnd : " + momentEnd);
    var entry = req.body.date + "|" + req.body.url + "|" + momentStart + "|" + momentEnd + "|" + req.body.time + "|" + req.body.description;
    ws.write(entry + "\n");
    ws.end();
    console.log("date : " + req.body.date + " url : " + req.body.url + " start : " + momentStart + " end : " + momentEnd + " time : " + req.body.time + " description : " + req.body.description);
    res.send('Info display');
});

module.exports = router;