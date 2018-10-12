const mongoose      = require("mongoose");

const timeSchema    = mongoose.Schema({
    outlet1: String,
    outlet2: String
});

module.exports = mongoose.model("time", timeSchema);