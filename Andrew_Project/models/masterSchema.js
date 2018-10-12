const mongoose      = require("mongoose");

const masterSchema  = mongoose.Schema({
    master: String
});

module.exports = mongoose.model("master", masterSchema);