const mongoose = require("mongoose");

const outletSchema = mongoose.Schema({
    outlet1: String,
    outlet2: String
});

module.exports = mongoose.model("outlets", outletSchema);