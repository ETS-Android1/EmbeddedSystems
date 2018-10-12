const mongoose = require('mongoose');

const schema   = mongoose.Schema({
    state:    String
});

module.exports = mongoose.model('sensor_state', schema);