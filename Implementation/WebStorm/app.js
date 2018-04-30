var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var concat = require('concat-files');
var fs = require('fs');
var os = require('os');

var index = require('./routes/index');
var users = require('./routes/users');
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

app.use('/', index);
app.use('/users', users);
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

// file merging process
//setInterval(mergeLogs, 600000);     //600000 ms correspond à 10 min
setInterval(mergeLogs, 60000);
function mergeLogs() {
    console.log("Merging process");
    var date = new Date();
    var source1 = os.userInfo().username + "_extension";
    var source2 = os.userInfo().username + "_processes";
    var destination = os.userInfo().username + "_log_" + date.getUTCDate() + "." + (date.getUTCMonth() + 1) + "." + date.getUTCFullYear();
    if (fs.existsSync(destination)) {
        concat([source1, source2, destination], destination, function(err) {
            if (err) throw err;
            console.log('Merge done');
            fs.writeFileSync(source1, '');
            //fs.writeFileSync(source2, '');
        });
    } else {
        concat([source1, source2], destination, function(err) {
            if (err) throw err;
            console.log('Merge done');
            fs.writeFileSync(source1, '');
            //fs.writeFileSync(source2, '');
        });
    }
}

module.exports = app;
