﻿var port = 3000;
var previousURL = "";
var previousDescription = "";
var startTime = new Date();
var endTime = new Date();

// Evénement capturé lorsqu'un onglet est créé ou mis à jour
chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
    endTime = new Date();

    if (typeof tab !== undefined && changeInfo.url !== undefined && changeInfo.url !== "chrome://newtab/") {
        if (previousURL !== "") {
    		sendTimedURL();
    	}
    	getMetaContent();
		previousURL = changeInfo.url;
		startTime = endTime;
    }
});

// Evénement capturé lorsque l'utilisateur change d'onglet
chrome.tabs.onActivated.addListener(function(activeInfo) {
    endTime = new Date();
    chrome.tabs.get(activeInfo.tabId, function(tab) {
		if (previousURL !== "") {
            sendTimedURL();
		}
		getMetaContent();
		previousURL = tab.url;
		startTime = endTime;
    });
});

function sendTimedURL() {
	var http = new XMLHttpRequest();
    var day = startTime.getUTCDate();
    var month = startTime.getUTCMonth() + 1;
    var date = startTime.getUTCFullYear() + "." + ((month < 10)?"0":"") + month + "." + ((day < 10)?"0":"") + day;
    var time = (endTime.getTime() - startTime.getTime()) / 1000;
    var TimedUrl = {date:date, url:previousURL, start:startTime, end:endTime, time:time, description:previousDescription};
    http.open("POST", "http://localhost:" + port + "/sniff", false); // false for synchronous request
    http.setRequestHeader("Content-type", "application/json");
    http.send(JSON.stringify(TimedUrl));
}

// Check si le navigateur est toujours actif
var hasBrowserFocusOut = false;
setInterval(function() {
    var http = new XMLHttpRequest();
    http.open("GET", "http://localhost:" + port + "/active", false); // false for synchronous request
    http.send();
    if (!hasBrowserFocusOut && previousURL !== "" && http.responseText !== "" && http.responseText !== "chrome.exe") {
        hasBrowserFocusOut = true;
        endTime = new Date();
        sendTimedURL();
    } else if (hasBrowserFocusOut && http.responseText === "chrome.exe") {
        hasBrowserFocusOut = false;
        startTime = new Date();
    }
}, 3000);

function getMetaContent() {
    var code =  'var meta = document.querySelector("meta[name=\'description\']");' +
                'if (meta) meta = meta.getAttribute("content");' +
                '({' +
                '   title: document.title,' +
                '   description: meta || ""' +
                '});';
    chrome.tabs.executeScript({
        code: code
    }, function(results) {
        if (!results) {
            return;
        }
        previousDescription = results[0].description;
    });
}