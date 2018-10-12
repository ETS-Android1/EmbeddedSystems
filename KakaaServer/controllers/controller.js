const stateModel    = require('../models/sensorState');
const outletModel   = require('../models/outlet');
const userModel     = require('../models/user');

const bcrypt            = require('bcrypt');
const jwt               = require('jsonwebtoken');

module.exports  = {
    getState: (req, res, next) =>{      //Both to arduino and app from database
        stateModel.find({})
        .exec()
        .then(result =>{
            if(result.length < 1){
                return res.status(404).json({
                    message: 'No records found'
                })
            }
            let message = '';
            if(result[0].state == "1"){
                message = "Room is'nt empty";
            }else if(result[0].state == "0"){
                message = "Room is empty";
            }else{
                message = "Invalid info";
            }
            res.status(200).json({
                result: result[0].state,
                message
            });
        })
        .catch(err =>{
            res.status(500).json({
                error: err
            });
        })
    },

    initiateState: (req, res, next) =>{
        const state = new stateModel({
            state: "0"
        });
        state.save()
        .then(resultSave =>{
            res.status(201).json({
                message: 'No previous state exists'
            });
        })
        .catch(err =>{
            res.status(500).json({
                error: err
            });
        })
    },

    setState: (req, res, next) =>{     //Both from arduino and app to database
        const state = req.body.state;

        stateModel.updateMany({}, {$set: {state}})
        .exec()
        .then(result =>{
            let message = '';
            let code = "";
            if(state == "1"){
                message = "No motion....";
                code = "1";

            }else if(state == "0"){
                message = "Motion detected!";
                code = "0";
            }else{
                message = "Invalid info";
            }

            outletModel.updateMany({}, {$set: {outlet1: code, outlet2: code}})
            .exec()
            .then(outlets =>{
                res.status(200).json({
                    code,
                    message
                });
            })
            
        })
        .catch(err =>{
            res.status(500).json({
                error: err
            });
        })
    },

    initiateOutlet: (req, res, next) =>{
        const outlet = new outletModel({
            outlet1: "0",
            outlet2: "0"
        }).save()
        .then(result =>{
            res.status(201).json({
                message: "Outlet state has been initialized!"
            });
        })
        .catch(error =>{
            res.status(500).json({
                error
            });
        });
    },

    setOutlet: (req, res, next) =>{
        let outlet1 = req.body.outlet1;
        let outlet2 = req.body.outlet2;

        outletModel.find({})
        .exec()
        .then(outlets =>{
            if(outlet1 == undefined){
                outlet1 = outlets[0].outlet1;
            }
            if(outlet2 == undefined){
                outlet2 = outlets[0].outlet2;
            }

            outletModel.updateMany({}, {$set: {outlet1, outlet2}})
            .exec()
            .then(result =>{
                res.status(200).json({
                    message: "Outlets state changed"
                });
            })
            .catch(error =>{
                res.status(500).json({
                    error
                });
            });
        });
    },

    getOutlet: (req, res, next) =>{
        outletModel.find({})
        .exec()
        .then(results =>{
            if(results.length < 1){
                return res.status(404).json({
                    message: "No records found!"
                });
            }
            res.status(200).json({
                Outlet1: results[0].outlet1,
                Outlet2: results[0].outlet2
            });
        })
        .catch(error =>{
            res.status(500).json({
                error
            })
        });
    },

    signup: (req, res, next) =>{
        const password      = req.body.password;
        const username      = req.body.username;

        userModel.find({username: username})
        .exec()
        .then(user =>{
            if(user.length >= 1){
                return res.status(409).json({
                    message: 'User with same username already exists'
                });
            }

            bcrypt.hash(password, 10, (err, hash) =>{
                if(err){
                    return res.status(500).json({
                        error: err
                    })
                }
                const newUser = new userModel({
                    username,
                    password: hash
                });
                newUser.save()
                .then(result =>{
                    res.status(201).json({
                        user: result,
                        request: {
                            type: 'GET',
                            url: 'http://localhost:5000/users'
                        }
                    })
                })
                .catch(err =>{
                    console.log(err);
                    res.status(500).json({
                        error: err
                    });
                });
            })
        })
    } ,
    login : (req, res, next) =>{
        const username     = req.body.username;
        const password     = req.body.password;

        userModel.findOne({username: username})
        .exec()
        .then(user =>{
            if(!user){
                return res.status(500).json({
                    message: "No user found"
                })
            }
            bcrypt.compare(password, user.password, (err, result) =>{
                if(err){
                    return res.status(401).json({
                        message: 'Auth failed'
                    })
                }
                if(result){
                    // const token = jwt.sign(
                    //     {
                    //         name: user.username,
                    //         level: user.password
                    //     }, process.env.JWT_KEY,
                    //     {
                    //         expiresIn: '1h'
                    //     });
                        return res.status(200).json({
                            message: 'Auth successful',
                            user
                            // token
                        });
                }
                res.status(401).json({
                    message: 'Auth failed'
                })
            })
        })
    } ,

}