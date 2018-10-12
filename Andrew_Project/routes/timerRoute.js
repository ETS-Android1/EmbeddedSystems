const controller = require("../controllers/controller");
const express    = require("express");

const router     = express.Router();

router.get('/', controller.getTimer);
router.post('/', controller.initializeTimer);
router.put('/', controller.setTimer);

module.exports = router;