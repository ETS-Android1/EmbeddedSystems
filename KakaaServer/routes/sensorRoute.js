const express     = require('express');
const controller  = require('../controllers/controller');

const router      = express.Router();

router.get('/', controller.getState);
router.put('/', controller.setState);
router.post('/', controller.initiateState);

module.exports = router;