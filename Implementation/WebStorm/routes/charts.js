var fs = require('fs');
var ci = require('case-insensitive');
var express = require('express');
var router = express.Router();

var moment = require('moment-timezone');
moment().tz("Europe/Paris");

var igdb = require('igdb-api-node').default;
var client = igdb('77559b27b45172d9aedc8b330a535cae');

var socialKeywords = ["partager", "share", "photo", "picture", "video", "vidéo", "friend", "ami", "réseau social", "réseaux sociaux", "social network"];
var gameKeywords = ["joue", "play", "jeu", "jeux", "game"];
var networkList = fs.readFileSync("checkFiles/SocialNetworksList.txt", "utf8").split("\r\n");
var gameDownloadSitesList = fs.readFileSync("checkFiles/GamesDownloadSitesList.txt", "utf8").split("\r\n");
var gameSitesList = fs.readFileSync("checkFiles/GameSitesList.txt", "utf8").split("\r\n");
var normalEntries = fs.readFileSync("checkFiles/normalEntries.txt", "utf8").split("\r\n");
var socialEntries = (fs.existsSync("checkFiles/socialEntries.txt"))?fs.readFileSync("checkFiles/socialEntries.txt", "utf8").split("\r\n"):[];
var gameEntries = (fs.existsSync("checkFiles/gameEntries.txt"))?fs.readFileSync("checkFiles/gameEntries.txt", "utf8").split("\r\n"):[];
var entries = [];

var activityName = [];
var activityTime = [];
var activityCategory = [];
var lastSite = "";
var lastCategory = "";
var accumulatedTime = 0;
var date;

router.get("/pieChart", function(req, res) {
    date = getDate();
    var filename = req.query.user + "_log_" + date;
    entries = fs.readFileSync(filename, "utf8").split("\n");
    entries = entries.sort();

    getCategory(entries, 0, function () {
        var index = activityName.indexOf(lastSite);
        if (index === -1) {
            activityName.push(lastSite);
            activityTime.push(accumulatedTime);
            activityCategory.push(lastCategory);
        } else {
            activityTime[index] += accumulatedTime;
        }

        var piejson = {date: date, games: [], networks: []};
        for (var i = 0; i < activityName.length; i++) {
            if (activityCategory[i] === "socialNetwork") {
                piejson.networks.push({name: activityName[i], totalTime: activityTime[i]});
            } else if (activityCategory[i] === "game") {
                piejson.games.push({name: activityName[i], totalTime: activityTime[i]});
            }
        }

        console.log(piejson);
        res.send(piejson);
    });
});

// Has to be called after pieChart
router.get("/barChart", function(req, res) {
    for (var i = 0; i < entries.length; i++) {
        if (entries[i] !== "") {
            var splittedEntry = entries[i].split("|");
            var indexOrder;
            if (splittedEntry[1].startsWith("http")) {
                indexOrder = [0,2,3,4,1,5];
            } else {
                indexOrder = [0,4,5,6,1,2,3,7];
            }
            var reorderedEntry = "";
            for (var j = 0; j < indexOrder.length; j++) {
                reorderedEntry += splittedEntry[indexOrder[j]] + "|";
            }
            entries[i] = reorderedEntry;
        }
    }
    entries = entries.sort();
    var barjson = { date: date,
                    "0": {"games": 0, "networks": 0}, "1": {"games": 0, "networks": 0}, "2": {"games": 0, "networks": 0}, "3": {"games": 0, "networks": 0},
                    "4": {"games": 0, "networks": 0}, "5": {"games": 0, "networks": 0}, "6": {"games": 0, "networks": 0}, "7": {"games": 0, "networks": 0},
                    "8": {"games": 0, "networks": 0}, "9": {"games": 0, "networks": 0}, "10": {"games": 0, "networks": 0}, "11": {"games": 0, "networks": 0},
                    "12": {"games": 0, "networks": 0}, "13": {"games": 0, "networks": 0}, "14": {"games": 0, "networks": 0}, "15": {"games": 0, "networks": 0},
                    "16": {"games": 0, "networks": 0}, "17": {"games": 0, "networks": 0}, "18": {"games": 0, "networks": 0}, "19": {"games": 0, "networks": 0},
                    "20": {"games": 0, "networks": 0}, "21": {"games": 0, "networks": 0}, "22": {"games": 0, "networks": 0}, "23": {"games": 0, "networks": 0}};
    var currentHour = NaN;
    var accumulatedSocialTime = 0;
    var accumulatedGameTime = 0;
    for (var i = 0; i < entries.length; i++) {
        var splittedEntry = entries[i].split("|");
        var begin = new Date(splittedEntry[1].replace(/\+0.:00/g, "+00:00"));
        var end = new Date(splittedEntry[2].replace(/\+0.:00/g, "+00:00"));
        if (end.getUTCHours() === begin.getUTCHours()) {
            if (begin.getUTCHours() !== currentHour && !isNaN(currentHour)) {
                barjson[currentHour] = {"games": accumulatedGameTime, "networks": accumulatedSocialTime};
                accumulatedGameTime = 0;
                accumulatedSocialTime = 0;
            }
            if (socialEntries.includes(splittedEntry[4])) {
                accumulatedSocialTime += parseFloat(splittedEntry[3]);
                currentHour = end.getUTCHours();
            } else if (gameEntries.includes(splittedEntry[4])) {
                accumulatedGameTime += parseFloat(splittedEntry[3]);
                currentHour = end.getUTCHours();
            }
        } else {
            if (begin.getUTCHours() !== currentHour && !isNaN(currentHour)) {
                barjson[currentHour] = {"games": accumulatedGameTime, "networks": accumulatedSocialTime};
                currentHour = begin.getUTCHours();
                accumulatedGameTime = 0;
                accumulatedSocialTime = 0;
            }
            if (socialEntries.includes(splittedEntry[4])) {
                var splitDate = new Date(begin);
                splitDate.setUTCHours(begin.getUTCHours()+1); splitDate.setUTCMinutes(0); splitDate.setUTCSeconds(0); splitDate.setUTCMilliseconds(0);
                accumulatedSocialTime += (splitDate.getTime() - begin.getTime())/1000;
                barjson[currentHour++] = {"games": accumulatedGameTime, "networks": accumulatedSocialTime};
                for (var j = 0; j < end.getUTCHours() - begin.getUTCHours() - 1; j++) {
                    barjson[currentHour++] = {"games": 0, "networks": 3600};
                }
                if (i < entries.length-1) {
                    splitDate = new Date(end);
                    splitDate.setUTCHours(end.getUTCHours()); splitDate.setUTCMinutes(0); splitDate.setUTCSeconds(0); splitDate.setUTCMilliseconds(0);
                    accumulatedSocialTime = (end.getTime() - splitDate.getTime())/1000;
                    accumulatedGameTime = 0;
                }
                currentHour = end.getUTCHours();
            } else if (gameEntries.includes(splittedEntry[4])) {
                var splitDate = new Date(begin);
                splitDate.setUTCHours(begin.getUTCHours()+1); splitDate.setUTCMinutes(0); splitDate.setUTCSeconds(0); splitDate.setUTCMilliseconds(0);
                accumulatedGameTime += (splitDate.getTime() - begin.getTime())/1000;
                barjson[currentHour++] = {"games": accumulatedGameTime, "networks": accumulatedSocialTime};
                for (var j = 0; j < end.getUTCHours() - begin.getUTCHours() - 1; j++) {
                    barjson[currentHour++] = {"games": 3600, "networks": 0};
                }
                if (i < entries.length-1) {
                    splitDate = new Date(end);
                    splitDate.setUTCHours(end.getUTCHours()); splitDate.setUTCMinutes(0); splitDate.setUTCSeconds(0); splitDate.setUTCMilliseconds(0);
                    accumulatedGameTime = (end.getTime() - splitDate.getTime())/1000;
                    accumulatedSocialTime = 0;
                }
                currentHour = end.getUTCHours();
            }
        }
    }
    barjson[currentHour] = {"games": accumulatedGameTime, "networks": accumulatedSocialTime};
    console.log(barjson);
    res.send(barjson);
});

function getCategory(entries, i, callback) {
    if (i < entries.length) {
        if (entries[i] !== "") {
            var splittedEntry = entries[i].split("|");
            if (splittedEntry[1] === lastSite) {
                accumulatedTime += parseFloat((lastSite.startsWith("http")) ? splittedEntry[4] : splittedEntry[6]);
            } else {
                var index = activityName.indexOf(lastSite);
                if (index === -1) {
                    activityName.push(lastSite);
                    activityTime.push(accumulatedTime);
                    activityCategory.push(lastCategory);
                } else {
                    activityTime[index] += accumulatedTime;
                }

                if (splittedEntry[1].startsWith("http")) {                  // website
                    var splittedSite = splittedEntry[1].split("/");
                    var sitefullname = splittedSite[2];
                    var splittedSitename = sitefullname.split(".");
                    var sitename = (splittedSitename.length === 3) ? splittedSitename[1] : splittedSitename[0];
                    var checkListsResp = checkLists(true, sitename, splittedEntry);
                    if (checkListsResp === "") {
                        var checkKeyworksResp = checkKeywords(true, splittedEntry);
                        if (checkKeyworksResp === "") {
                            lastCategory = "";
                            fs.appendFileSync("checkFiles/normalEntries.txt", splittedEntry[1] + "\r\n");
                        }
                    }
                    accumulatedTime = parseFloat(splittedEntry[4]);
                } else {                                                    // process
                    var process_name = splittedEntry[1].split(".");
                    var checkListsResp = checkLists(false, process_name[0], splittedEntry);
                    if (checkListsResp === "") {
                        checkGameAPI(process_name[0], splittedEntry, function(res) {
                            if (!res) {
                                var checkKeyworksResp = checkKeywords(false, splittedEntry);
                                if (checkKeyworksResp === "") {
                                    lastCategory = "";
                                    fs.appendFileSync("checkFiles/normalEntries.txt", splittedEntry[1] + "\r\n");
                                }
                            }
                        });
                    }
                    accumulatedTime = parseFloat(splittedEntry[6]);
                }
                lastSite = splittedEntry[1];
            }
        }
        getCategory(entries, i+1, callback);
    } else {
        callback();
    }
}

function checkLists(isWebsite, sitename, splittedEntry) {
    var categoryFound = false;
    var response = "";
    if (ci(normalEntries).includes(splittedEntry[1])) {
        categoryFound = true;
    } else if (ci(socialEntries).includes(splittedEntry[1])) {
        lastCategory = "socialNetwork";
        response = lastCategory;
        categoryFound = true;
    } else if (ci(gameEntries).includes(splittedEntry[1])) {
        lastCategory = "game";
        response = lastCategory;
        categoryFound = true;
    } else if (isWebsite) {
        for (var j = 0; j < gameSitesList.length; j++) {                                    // Game Sites list test
            if (splittedEntry[1].toLowerCase().includes(gameSitesList[j].toLowerCase())) {
                fs.appendFileSync("checkFiles/gameEntries.txt", splittedEntry[1] + "\r\n");
                lastCategory = "game";
                response = lastCategory;
                categoryFound = true;
                break;
            }
        }
    } else if (!isWebsite) {
        // check avec le fichier ConcreteGamesList.js
        var splittedPath = splittedEntry[2].split((process.platform === "win32")? "\\" : "/");
        if (splittedPath.length > 2) {
            var parentDirectory = splittedPath[splittedPath.length-3];
            for (var j = 0; j < gameDownloadSitesList.length; j++) {                                    // Game Download Sites list test
                if (parentDirectory.toLowerCase() === gameDownloadSitesList[j].toLowerCase()) {
                    fs.appendFileSync("checkFiles/gameEntries.txt", splittedEntry[1] + "\r\n");
                    lastCategory = "game";
                    response = lastCategory;
                    categoryFound = true;
                    break;
                }
            }
        }
    }
    if (!categoryFound) {
        for (var j = 0; j < networkList.length; j++) {                                      // Social Network list test
            if (networkList[j].toLowerCase() === sitename.toLowerCase()) {
                fs.appendFileSync("checkFiles/socialEntries.txt", splittedEntry[1] + "\r\n");
                lastCategory = "socialNetwork";
                response = lastCategory;
                categoryFound = true;
                break;
            }
        }
    }
    return response;
}

function checkKeywords(isWebsite, splittedEntry) {
    var categoryFound = false;
    var response = "";
    if ((isWebsite && splittedEntry[5] !== "") || (!isWebsite && (splittedEntry[2] !== "" || splittedEntry[7] !== ""))) {
        for (var j = 0; j < gameKeywords.length; j++) {                                                                     // Game description test
            if (isWebsite && splittedEntry[5].toLowerCase().includes(" " + gameKeywords[j].toLowerCase())) {
                fs.appendFileSync("checkFiles/gameEntries.txt", splittedEntry[1] + "\r\n");
                lastCategory = "game";
                response = lastCategory;
                categoryFound = true;
                break;
            } else if ( !isWebsite && splittedEntry[2] !== "" && splittedEntry[2].toLowerCase().includes(gameKeywords[j].toLowerCase()) ||
                        !isWebsite && splittedEntry[7] !== "" && splittedEntry[7].toLowerCase().includes(" " + gameKeywords[j].toLowerCase())) {
                fs.appendFileSync("checkFiles/gameEntries.txt", splittedEntry[1] + "\r\n");
                lastCategory = "game";
                response = lastCategory;
                categoryFound = true;
                break;
            }
        }
        if (!categoryFound) {
            for (var j = 0; j < socialKeywords.length; j++) {                                                               // Social Network description test
                if (isWebsite && splittedEntry[5].toLowerCase().includes(socialKeywords[j].toLowerCase())) {
                    fs.appendFileSync("checkFiles/socialEntries.txt", splittedEntry[1] + "\r\n");
                    lastCategory = "socialNetwork";
                    response = lastCategory;
                    categoryFound = true;
                    break;
                } else if ( !isWebsite && splittedEntry[2] !== "" && splittedEntry[2].toLowerCase().includes(socialKeywords[j].toLowerCase()) ||
                            !isWebsite && splittedEntry[7] !== "" && splittedEntry[7].toLowerCase().includes(socialKeywords[j].toLowerCase())) {
                    fs.appendFileSync("checkFiles/socialEntries.txt", splittedEntry[1] + "\r\n");
                    lastCategory = "socialNetwork";
                    response = lastCategory;
                    categoryFound = true;
                    break;
                }
            }
        }
    }
    return response;
}

function checkGameAPI(sitename, splittedEntry, callback) {
    var splittedPath = splittedEntry[2].split((process.platform === "win32")? "\\" : "/");
    var parentDirectory = splittedPath[splittedPath.length-2];
    if (splittedPath[2] === "Google" || splittedPath[1] === "Window" || parentDirectory === "Application" || parentDirectory === "bin") {
        callback(false);
    } else {
        client.games({
            limit: 1,
            offset: 0,
            search: parentDirectory
        }, ['name']).then(function (res) {
            if (res.body !== "[]" && res.body.startsWith(parentDirectory)) {
                fs.appendFileSync("checkFiles/gameEntries.txt", splittedEntry[1] + "\r\n");
                lastCategory = "game";
                callback(true);
            } else {
                client.games({
                    limit: 1,
                    offset: 0,
                    search: splittedEntry[7]
                }, ['name']).then(function (res) {
                    if (res.body !== "[]" && res.body.startsWith(splittedEntry[7])) {
                        fs.appendFileSync("checkFiles/gameEntries.txt", splittedEntry[1] + "\r\n");
                        lastCategory = "game";
                        callback(true);
                    } else {
                        client.games({
                            limit: 1,
                            offset: 0,
                            search: sitename
                        }, ['name']).then(function (res) {
                            if (res.body !== "[]" && res.body.startsWith(sitename)) {
                                fs.appendFileSync("checkFiles/gameEntries.txt", splittedEntry[1] + "\r\n");
                                lastCategory = "game";
                                callback(true);
                            } else {
                                callback(false);
                            }
                        }).catch(function (err) {});
                    }
                }).catch(function (err) {});
            }
        }).catch(function (err) {});
    }
}

function getDate() {
    var time = moment(new Date());
    var day = time.date();
    var month = time.month() + 1;
    return time.year() + "." + ((month < 10) ? "0" : "") + month + "." + ((day < 10) ? "0" : "") + day;
}

module.exports = router;