const controller = require("../controllers/controller");
const express    = require("express");

const router     = express.Router();

router.get('/', controller.getBtnControl);
router.post("/", controller.initializeBtnControl);
router.put("/", controller.setBtnControl);

router.post("/signup", controller.signup);
router.post("/login", controller.login);

module.exports = router;