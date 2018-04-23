var port = 3000;
var previousURL = "";
var startTime = Date.now() / 1000;
var endTime = Date.now() / 1000;

chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
	endTime = Date.now() / 1000;
    if (typeof tab !== undefined && changeInfo.url !== undefined) {
    	if (previousURL !== "" && previousURL !== "chrome://newtab/") {
    		sendTimedURL();
    	}
		previousURL = changeInfo.url;
		startTime = endTime;
    }
});

chrome.tabs.onActivated.addListener(function(activeInfo) {
	endTime = Date.now() / 1000;
	chrome.tabs.get(activeInfo.tabId, function(tab) {
		if (previousURL !== "" && previousURL !== "chrome://newtab/") {
            sendTimedURL();
		}
		previousURL = tab.url;
		startTime = endTime;
    });
});

/*chrome.tabs.onUpdated.addListener(function(tab) {
    alert("addListener");
	getMetaContentByName('description');
});*/

function sendTimedURL() {
	var http = new XMLHttpRequest();
	var description = getMetaContentByName('description');
    var TimedUrl = previousURL + " " + (endTime - startTime) + " " + description;
    http.open("POST", "http://localhost:" + port + "/sniff", false); // false for synchronous request
    http.setRequestHeader("Content-type", "text/plain; charset=utf-8");
	http.setRequestHeader("Content-length", TimedUrl.length);
	http.setRequestHeader("Connection", "close");
    http.send(TimedUrl);
}

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