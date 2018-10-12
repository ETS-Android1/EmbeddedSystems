const mongoose  = require("mongoose");

const buttonSchema = mongoose.Schema({
    outlet1: String,
    outlet2: String
});

module.exports = mongoose.model("outlets", buttonSchema);