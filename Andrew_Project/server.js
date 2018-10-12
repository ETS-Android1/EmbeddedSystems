const express   = require('express');
const mongoose  = require('mongoose');
const bodyParser= require('body-parser');
const morgan    = require('morgan');

const route     = require("./routes/route");
const masterRoute =require("./routes/masterRoute");
const timerRoute= require("./routes/timerRoute");

mongoose.connect(process.env.mongoUri);
mongoose.Promise = global.Promise;

const app       = express();

app.use(morgan("dev"));
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

app.use('/', route);
app.use('/master', masterRoute);
app.use('/timer', timerRoute);

app.set("port", process.env.PORT || 5005);

app.listen(app.get("port"), ()=>{
    console.log(`Server up and running at port ${app.get("port")}`);
});