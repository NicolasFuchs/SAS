var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/sendInfo', function(req, res, next) {
    console.log('SendInfo Text');
    res.render('index', { title: 'Express' });
});

module.exports = router;