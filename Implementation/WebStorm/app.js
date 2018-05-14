var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var sniff = require('./routes/sniff');
var processWatcher = require('./routes/processWatcher');
var supervise = require('./routes/supervise');
var charts = require('./routes/charts');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.get("/active", function(req, res) {
    res.send(processWatcher.getCrtProcess());
});

app.use("/charts", charts);
app.use("/supervise", supervise);
app.use('/sniff', sniff);



var request = require('request');
var options = {
    url: 'http://localhost:3000/charts/pieChart',
    method: 'GET',
    qs: {user:'Nicolas'}
};
request(options, function(error, response, body) {
    console.log('request sent!');
});



// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};
  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

// detection process
var pw = require('./routes/processWatcher');
setInterval(pw.setCrtProcess, 1000);

// file merging process
var lm = require('./logMerger');
setInterval(lm.mergeLogs, 600000);   //1000(ms) = 1s

module.exports = app;