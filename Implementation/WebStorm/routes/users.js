var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/Ach', function(req, res, next) {
    console.log('Ach De diesbach');
    res.send('respond with a resource');
});

module.exports = router;
