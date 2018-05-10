var cp = require('child_process');

function getIP(callback) {
    console.log("Machines on windows");
    cp.exec('tracert -h 1 www.google.ch', function(err, stdout) {
        var ip = stdout.match(/\[.*\]/g)[1];
        ip = ip.substring(1,ip.length-1);
        callback(ip);
    });
}

function getUsers(callback) {
    cp.exec('wmic useraccount list full', function (err, stdout) {
        var userInfo = ["AccountType", "Description", "Disabled", "Domain", "FullName", "InstallDate", "LocalAccount", "Lockout", "Name", "PasswordChangeable", "PasswordExpires", "PasswordRequired", "SID", "SIDType", "Status"];
        var users = stdout.trim().split("\r\r\n\r\r\n\r\r\n");
        var realUsers = [];
        for (var i = 0; i < users.length; i++) {
            var user = users[i].split("\r\r\n");
            var json = {};
            for (var j = 0; j < user.length; j++) {
                json[userInfo[j]] = user[j].replace(userInfo[j] + "=", "");
            }
            if (json["Description"] === "" && json["Disabled"] === "FALSE" && json["Status"] === "OK") {
                realUsers.push(json);
            }
        }
        callback(realUsers);
    });
}

module.exports.getIP = getIP;
module.exports.getUsers = getUsers;