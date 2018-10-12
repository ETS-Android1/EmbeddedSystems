const controller = require("../controllers/controller");
const express    = require("express");

const router     = express.Router();

router.get('/', controller.getMaster);
router.post("/", controller.initializeMaster);
router.put("/", controller.setMaster);

module.exports = router;