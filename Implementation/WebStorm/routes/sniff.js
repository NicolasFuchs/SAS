var express = require('express');
var fs = require('fs');
var os = require('os');
var app = require('../app');
var router = express.Router();
var lastDate = null;

// for test
//var counter = 0;

router.post('/', function(req, res, next) {

    //for test
    /*counter++;
    if (counter >= 5) {
        console.log("Date is changed");
        req.body.date = "2018.05.01";
    }*/

    var d = req.body.date.split(".");
    var crtDate = new Date(d[0],d[1],d[2]);
    if (lastDate !== null && (crtDate.getTime() !== lastDate.getTime())) {
        console.log("new day merge");
        app.mergeLogs(function() {
            lastDate = crtDate;
            var filename = os.userInfo().username + "_extension";
            var ws = fs.createWriteStream(filename, {flags:'a'});
            var entry = req.body.date + "|" + req.body.url + "|" + req.body.start + "|" + req.body.end + "|" + req.body.time + "|" + req.body.description;
            ws.write(entry + "\n");
            ws.end();
            console.log("date : " + req.body.date + " url : " + req.body.url + " start : " + req.body.start + " end : " + req.body.end + " time : " + req.body.time + " description : " + req.body.description);
            res.send('Info display');
        });
    } else {
        lastDate = crtDate;
        var filename = os.userInfo().username + "_extension";
        var ws = fs.createWriteStream(filename, {flags:'a'});
        var entry = req.body.date + "|" + req.body.url + "|" + req.body.start + "|" + req.body.end + "|" + req.body.time + "|" + req.body.description;
        ws.write(entry + "\n");
        ws.end();
        console.log("date : " + req.body.date + " url : " + req.body.url + " start : " + req.body.start + " end : " + req.body.end + " time : " + req.body.time + " description : " + req.body.description);
        res.send('Info display');
    }
});

module.exports = router;