var express = require('express');
var router = express.Router();
var WINSupervise = require('../WINSupervise');
var OSXSupervise = require('../OSXSupervise');
//var cp = require('child_process');
//var urlExists = require('url-exists');
//var request = require('request');

var port = 3000;

// Called from the supervisor machine --> written in java
/*router.get("/machines", function(req, res) {
    if(process.platform === "win32") {
        WINSupervise.getIP(function(ip) {
            //scanMachines(ip, function(reachableHosts) {
            var reachableHosts = [{ip:"160.98.126.85", mac:"D0-7E-35-66-A9-54", name:"Nico-HP"}];
            scanUsers(reachableHosts, 0, function(reachableUsers) {
                    res.send(reachableUsers);
                });
            //});
        });
    } else if (process.platform === "darwin") {
        OSXSupervise.getIP(function() {
            res.send("Mac OSX machines");
        });
    } else {
        res.send("Sorry, linux machines are not supported");
    }
});*/

// Called from the user machine
router.get("/users", function(req, res) {
    if (process.platform === "win32") {
        WINSupervise.getUsers(function(users) {
            res.send(users);
        });
    } else if (process.platform === "darwin") {
        OSXSupervise.getUsers(function(users) {
            res.send(users);
        });
    } else {
        res.send("Sorry, linux machines are not supported");
    }
});

// Called from the supervisor machine --> written in java
/*function scanMachines(ip, callback) {
    var cmd = "nmap -sP " + ip + "/24";
    cp.exec(cmd, function(err, stdout) {
        var hosts = [];
        var splittedHosts = stdout.split("\n");
        var splittedGateway = splittedHosts[1].split(" ")[4].split(".");
        var suffix = splittedGateway[splittedGateway.length-1];
        checkReachableOnes(hosts, splittedHosts, suffix, 3, callback);
    });
}*/

// Called from the supervisor machine --> written in java
/*function checkReachableOnes(hosts, splittedHosts, suffix, i, callback) {
    if (i < splittedHosts.length-2) {
        var host = {};
        var ip = splittedHosts[i+1].split(" ")[5];
        host["ip"] = ip.substring(1,ip.length-2);
        urlExists('http://' + host["ip"] + ':' + port + '/active', function(err, exists) {
            if (exists) {
                host["name"] = splittedHosts[i+1].split(" ")[4].replace("."+suffix,"");
                host["mac"] = splittedHosts[i].split(" ")[2];
                hosts.push(host);
            }
            checkReachableOnes(hosts, splittedHosts, suffix, i+3, callback);
        });
    } else {
        callback(hosts);
    }
}*/

// Called from the supervisor machine --> written in java
/*function scanUsers(reachableHosts, i, callback) {
    if (i < reachableHosts.length) {
        var options = {
            url: 'http://' + reachableHosts[i].ip + ':' + port + '/supervise/users',
            method: 'GET',
            headers: {'Accept': 'application/json'}
        };
        request(options, function (error, response, body) {
            var users = JSON.parse(body);
            for (var j = 0; j < users.length; j++) {
                var user = {};
                user["FullName"] = users[j]["FullName"];
                user["Name"] = users[j]["Name"];
                if (!reachableHosts[i].hasOwnProperty('users')) reachableHosts[i]["users"] = [];
                reachableHosts[i]["users"].push(user);
            }
            scanUsers(reachableHosts, i+1, callback);
        });
    } else {
        callback(reachableHosts);
    }
}*/

module.exports = router;