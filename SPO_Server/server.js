const express       = require('express');
const mongoose      = require('mongoose');
const bodyParser    = require('body-parser');
const morgan        = require('morgan');

const route         = require('./routes/route');
const outletRoute   = require('./routes/outletRoute');
const masterRoute   = require('./routes/masterRoute');
const statusRoute   = require('./routes/statusRoute');
const userRoute     = require('./routes/userRoute');
const thresholdRoute= require('./routes/thresholdRoute');
const timedRoute    = require('./routes/timedRoute');

const app           = express();
mongoose.connect(process.env.mongoUri);
mongoose.Promise = global.Promise;

app.use(morgan("dev"));
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

app.use('/', route);
app.use('/outlet', outletRoute);
app.use('/master', masterRoute);
app.use('/status', statusRoute);
app.use('/users', userRoute);
app.use('/threshold', thresholdRoute);
app.use('/timed', timedRoute);



app.set('port', process.env.PORT || 5000);

app.listen(app.get('port'), () =>{
    console.log(`SPO server up and running at port ${app.get('port')}`);
});