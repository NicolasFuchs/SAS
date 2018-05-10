var lm = require('../logMerger');
var aw = require('active-win');
var fs = require('fs');
var os = require('os');

var moment = require('moment-timezone');
moment().tz("Europe/Paris");

var lastProcess = "";
var crtProcess = aw.sync();
var startTime = null;
var endTime = moment(new Date());

function setCrtProcess() {
    crtProcess = aw.sync();
    writeProcess();
}

function getCrtProcess() {
    return crtProcess.owner.name;
}

function writeProcess() {
    endTime = moment(new Date());

    if (startTime !== null) {
        var day = startTime.date();
        var month = startTime.month() + 1;
        var date = startTime.year() + "." + ((month < 10) ? "0" : "") + month + "." + ((day < 10) ? "0" : "") + day;
    }
    if (endTime.hours() === 0 && endTime.minutes() === 0 && endTime.seconds() === 0) {
        console.log("new day merge");

        var filename = os.userInfo().username + "_processes";
        var ws = fs.createWriteStream(filename, {flags: 'a'});
        var entry = date + "|" + lastProcess.owner.name + "|" + lastProcess.owner.path + "|" + lastProcess.owner.processId + "|" + startTime.format() + "|" + endTime.format() + "|" + lastProcess.title;
        ws.write(entry + "\n");
        ws.end();
        console.log("date : " + date + " name : " + lastProcess.owner.name + " path : " + lastProcess.owner.path + " processId : " + lastProcess.owner.processId + " start : " + startTime.format() + " end : " + endTime.format() + " title : " + lastProcess.title);
        startTime = endTime;
        lastProcess = crtProcess;

        lm.mergeLogs(function() {
        });
    } else if (lastProcess === "") {
        startTime = endTime;
        lastProcess = crtProcess;
    } else if (crtProcess.owner.name !== lastProcess.owner.name) {
        var filename = os.userInfo().username + "_processes";
        var ws = fs.createWriteStream(filename, {flags: 'a'});
        var entry = date + "|" + lastProcess.owner.name + "|" + lastProcess.owner.path + "|" + lastProcess.owner.processId + "|" + startTime.format() + "|" + endTime.format() + "|" + lastProcess.title;
        ws.write(entry + "\n");
        ws.end();
        console.log("date : " + date + " name : " + lastProcess.owner.name + " path : " + lastProcess.owner.path + " processId : " + lastProcess.owner.processId + " start : " + startTime.format() + " end : " + endTime.format() + " title : " + lastProcess.title);
        startTime = endTime;
        lastProcess = crtProcess;
    }
}

module.exports.setCrtProcess = setCrtProcess;
module.exports.getCrtProcess = getCrtProcess;
