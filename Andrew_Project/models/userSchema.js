const mongoose  = require("mongoose");

const userSchema = mongoose.Schema({
    username: {type: String, unique: true, require: true},
    password: {type: String, require: true}
});

module.exports = mongoose.model("users", userSchema);