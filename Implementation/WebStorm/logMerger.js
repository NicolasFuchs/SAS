var os = require('os');
var fs = require('fs');
var extfs = require('extfs');
var concat = require('concat-files');

var moment = require('moment-timezone');
moment().tz("Europe/Paris");

function mergeLogs() {
    console.log("Merging process");
    var blockingDate = moment(new Date());
    if (blockingDate.hours() === 23 && blockingDate.minutes() > 55) {
        return;
    }

    var source1 = os.userInfo().username + "_extension";
    var source2 = os.userInfo().username + "_processes";

    if (extfs.isEmptySync(source1) && extfs.isEmptySync(source2)) return;
    var date = fs.readFileSync(source2).toString().split("|")[0];

    var destination = os.userInfo().username + "_log_" + date;
    var sources = [];
    if (fs.existsSync(destination)) {
        sources.push(destination);
    }
    if (!extfs.isEmptySync(source1) && !extfs.isEmptySync(source2)) {
        sources.push(source1, source2);
    } else if (!extfs.isEmptySync(source1)) {
        sources.push(source1);
    } else if (!extfs.isEmptySync(source2)) {
        sources.push(source2);
    }
    concat(sources, destination, function(err) {
        if (err) throw err;
        sources.splice(0);
        console.log('Merge done');
        fs.writeFileSync(source1, '');
        fs.writeFileSync(source2, '');
    });
}
module.exports.mergeLogs = mergeLogs;