const express     = require('express');
const controller  = require('../controllers/controller');

const router      = express.Router();

router.get('/', controller.getOutlet);
router.put('/', controller.setOutlet);
router.post('/', controller.initiateOutlet);

router.post('/signup', controller.signup);
router.post('/login', controller.login);

module.exports = router;