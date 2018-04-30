var port = 3000;
var previousURL = "";
var startTime = new Date();
var endTime = new Date();

// Evénement capturé lorsqu'un onglet est créé ou mis à jour
chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
    endTime = new Date();
    if (typeof tab !== undefined && changeInfo.url !== undefined) {
    	if (previousURL !== "") {
    		sendTimedURL();
    	}
		previousURL = changeInfo.url;
		startTime = endTime;
    }
});

// Evénement capturé lorsqu'un onglet est fermé
chrome.tabs.onRemoved.addListener(function(tabId, removeInfo){
    if (previousURL !== "") {
        endTime = new Date();
        sendTimedURL();
    }
    chrome.tabs.getSelected(null, function(tab) {
        previousURL = tab.url;
    });
    startTime = endTime;
});

// Evénement capturé lorsque l'utilisateur change d'onglet
chrome.tabs.onActivated.addListener(function(activeInfo) {
    endTime = new Date();
    chrome.tabs.get(activeInfo.tabId, function(tab) {
		if (previousURL !== "") {
            sendTimedURL();
		}
		previousURL = tab.url;
		startTime = endTime;
    });
});

function sendTimedURL() {
	var http = new XMLHttpRequest();
	//var description = getMetaContentByName('description');
    var date = startTime.getUTCDate() + "." + (startTime.getUTCMonth() + 1) + "." + startTime.getUTCFullYear();
    var time = (endTime.getTime() - startTime.getTime()) / 1000;
    var TimedUrl = {url:previousURL, date:date, start:startTime, end:endTime, time:time, description:"Metadata"};
    http.open("POST", "http://localhost:" + port + "/sniff", false); // false for synchronous request
    http.setRequestHeader("Content-type", "application/json");
    http.send(JSON.stringify(TimedUrl));
}






















/*chrome.tabs.onUpdated.addListener(function(tab) {
    alert("addListener");
	getMetaContentByName('description');
});*/

function getMetaContentByName(mn) {
	returnContent = "";
	var m = document.getElementsByTagName('meta');
	
	var metadata = "Metadata\n";
	alert(document.querySelector('meta[property="description"]').getAttribute('content'));
	for (var i = 0; i < m.length; m++) {
		try {
			alert("Content : " + m[i].getAttribute("content"));
			/*if (m[i].name.toLowerCase() == mn.toLowerCase()) {
				returnContent = m[i].content;
				break;
			}*/
		} catch (err) {
			continue;
		}
	}
	//alert(metadata);
	return returnContent.trim();
}