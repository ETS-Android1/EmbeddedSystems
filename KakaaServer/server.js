const express       = require('express');
const bodyParser    = require('body-parser');
const mongoose      = require('mongoose');
const morgan        = require('morgan');

const route         = require('./routes/route');
const sensorRoute   = require('./routes/sensorRoute');

const app           = express();

mongoose.connect(process.env.mongoUri);
mongoose.Promise = global.Promise;

app.use(morgan("dev"));
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

app.use('/', route);
app.use('/sensor', sensorRoute);

app.set('port', process.env.PORT || 5050);
app.listen(app.get('port'), () =>{
    console.log(`Server up and running at port ${app.get('port')}`);
});