var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var concat = require('concat-files');
var extfs = require('extfs');
var fs = require('fs');
var os = require('os');

var sniff = require('./routes/sniff');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.get("/active", function(req, res) {
    res.send(crtprocess);
});
app.use('/sniff', sniff);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

// get the active application
var crtprocess = "";
var aw = require('active-win');
setInterval(function() {
    crtprocess = aw.sync().owner.name;
    console.log(aw.sync());
}, 3000);

// file merging process
setInterval(mergeLogs, 60000);  //setInterval(mergeLogs, 600000);     //600000 ms correspond à 10 min
function mergeLogs(callback) {
    console.log("Merging process");
    var blockingDate = new Date();
    if (blockingDate.getHours() === 23 && blockingDate.getMinutes() > 55) {     //Merge lancé par le changement de date et non le setInterval
        return;
    }
    var source1 = os.userInfo().username + "_extension";
    var source2 = os.userInfo().username + "_processes";
    var source;
    if (!extfs.isEmptySync(source1)) {
        source = source1;
    } else if (!extfs.isEmptySync(source2)) {
        source = source2;
    } else {
        return;
    }
    var date = fs.readFileSync(source).toString().split("|")[0];

    var destination = os.userInfo().username + "_log_" + date;
    if (fs.existsSync(destination)) {
        concat([source1, source2, destination], destination, function(err) {
            if (err) throw err;
            console.log('Merge done');
            fs.writeFileSync(source1, '');
            //fs.writeFileSync(source2, '');
            if (callback) callback();
        });
    } else {
        concat([source1, source2], destination, function(err) {
            if (err) throw err;
            console.log('Merge done');
            fs.writeFileSync(source1, '');
            //fs.writeFileSync(source2, '');
            if (callback) callback();
        });
    }
}
module.exports.mergeLogs = mergeLogs;
module.exports = app;
