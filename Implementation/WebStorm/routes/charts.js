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

var lastSite = "";
var lastCategory = "";
var accumulatedTime = 0;
var date = getDate();
var piejson = {date: date, games: [], networks: []};

router.get("/pieChart", function(req, res) {
    var filename = req.query.user + "_log_" + date;
    entries = fs.readFileSync(filename, "utf8").split("\n");
    entries = entries.sort();

    getCategory(entries, 0, function () {
        if (lastCategory === "socialNetwork") {
            piejson.networks.push({name: lastSite, totalTime: accumulatedTime});
        } else if (lastCategory === "game") {
            piejson.games.push({name: lastSite, totalTime: accumulatedTime});
        }

        console.log(piejson);
        res.send("piechart data sent");
    });
});

// Has to be called after pieChart
/*
router.get("/barChart", function(req, res) {
    for(var i = 0; i < entries.length; i++) {
        var splittedEntry = entries[i].split("|");
        var indexOrder;
        if (splittedEntry[1].startsWith("http")) {
            indexOrder = [2,3,4,0,1,5];
        } else {
            indexOrder = [4,5,6,0,3,1,2,7];
        }
        var reorderedEntry = "";
        for (var j = 0; j < indexOrder.length; j++) {
            reorderedEntry += splittedEntry[indexOrder[j]] + "|";
        }
        entries[i] = reorderedEntry;
    }
    entries = entries.sort();

    res.send("barchart data sent");
});
*/

function getCategory(entries, i, callback) {
    if (i < entries.length) {
        if (entries[i] !== "") {
            var splittedEntry = entries[i].split("|");
            if (splittedEntry[1] === lastSite) {
                accumulatedTime += parseFloat((lastSite.startsWith("http")) ? splittedEntry[4] : splittedEntry[6]);
            } else {
                if (lastCategory === "socialNetwork") {
                    piejson.networks.push({name: lastSite, totalTime: accumulatedTime});
                } else if (lastCategory === "game") {
                    piejson.games.push({name: lastSite, totalTime: accumulatedTime});
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
        var parentDirectory = splittedPath[splittedPath.length-3];
        for (var j = 0; j < gameDownloadSitesList.length; j++) {                            // Game Download Sites list test
            if (parentDirectory.toLowerCase() === gameSitesList[j].toLowerCase()) {
                fs.appendFileSync("checkFiles/gameEntries.txt", splittedEntry[1] + "\r\n");
                lastCategory = "game";
                response = lastCategory;
                categoryFound = true;
                break;
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