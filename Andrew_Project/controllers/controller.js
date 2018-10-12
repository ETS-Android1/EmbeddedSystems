const userModel     = require("../models/userSchema");
const timerModel     = require("../models/timerSchema");
const masterModel   = require("../models/masterSchema");
const buttonModel   = require("../models/buttonSchema");

const bcrypt        = require("bcrypt");

module.exports = {
    getTimer: (req, res, next)=>{
        timerModel.find({}).exec()
        .then(results =>{
            if(results == []){
                return res.status(500).json({
                    message: "No timer has been set yet!"
                });
            } else{
                res.status(200).json({
                    outlet1: results[0].outlet1,
                    outlet2: results[0].outlet2
                });
            }
        })
        .catch(err =>{
            res.status(500).json({
                error: err
            })
        });
    },

    setTimer: (req, res, next) =>{
        let outlet1 = req.body.outlet1;
        let outlet2 = req.body.outlet2;

        timerModel.find({}).exec()
        .then(results =>{
            if(results == []){
                return res.status(500).json({
                    message: "No timer has been set yet!"
                });
            } else{
                if(outlet1 == null){
                    outlet1 = results[0].outlet1;
                }
                if(outlet2 == null){
                    outlet2 = results[0].outlet2;
                }

                timerModel.updateMany({}, {$set: {outlet1, outlet2}})
                .exec()
                .then(result =>{
                    res.status(201).json({
                        message: "Timer set",
                        outlet1,
                        outlet2
                    });
                });
            }
        })
        .catch(err =>{
            res.status(500).json({
                error: err
            })
        });

    },

    initializeTimer: (req, res, next) =>{
        const newTimer = new timerModel({
            outlet1: "0",
            outlet2: "0"
        }).save()
        .then(result =>{
            res.status(201).json({
                message: "Timer initialized"
            });
        })
        .catch(err =>{
            error: err
        });
    },

    getBtnControl: (req, res, next) =>{
        buttonModel.find({}).exec()
        .then(results =>{
            if(results == []){
                res.status(404).json({
                    message: "No button command entered!"
                });
            } else{
                res.status(200).json({
                    outlet1: results[0].outlet1,
                    outlet2: results[0].outlet2
                });
            }
        })
        .catch(err =>{
            res.status(500).json({
                error: err
            })
        });
    },

    setBtnControl: (req, res, next) =>{
        let outlet1 = req.body.outlet1;
        let outlet2 = req.body.outlet2;

        buttonModel.find({}).exec()
        .then(results =>{
            if(results == []){
                res.status(404).json({
                    message: "No button command entered!"
                });
            } else{
                if(outlet1 == null){
                    outlet1 = results[0].outlet1;
                }
                if(outlet2 == null){
                    outlet2 = results[0].outlet2;
                }

                buttonModel.updateMany({}, {$set: {outlet1, outlet2}})
                .exec()
                .then(result =>{
                    res.status(201).json({
                        outlet1,
                        outlet2,
                        message: "Outlet status changed"
                    });
                })
            }
        })
        .catch(err =>{
            res.status(500).json({
                error: err
            })
        });
        
    },

    initializeBtnControl: (req, res, next) =>{
        const newBtnControl = new buttonModel({
            outlet1: "0",
            outlet2: "0"
        }).save()
        .then(result =>{
            res.status(201).json({
                message: "Button control initialized"
            });
        })
        .catch(err =>{
            error: err
        });
    },

    getMaster: (req, res, next) =>{
        masterModel.find({}).exec()
        .then(results =>{
            if(results == []){
                res.status(404).json({
                    message: "No master command set yet!"
                });
            } else{
                res.status(200).json({
                    master: results[0].master
                });
            }
        })
        .catch(error =>{
            res.status(500).json({
                error
            });
        });
    },

    setMaster: (req, res, next) =>{
        const master = req.body.master;

        masterModel.updateMany({}, {$set: {master}})
        .exec()
        .then(result =>{
            buttonModel.updateMany({}, {$set: {outlet1: master, outlet2: master}})
            .exec()
            .then(result =>{
                res.status(201).json({
                    master,
                    message: "Master status changed!"
                });
            })
        })
        .catch(error =>{
            res.status(500).json({
                error
            });
        });
        
    },

    initializeMaster: (req, res, next) =>{
        const newMaster = new masterModel({
            master: "0"
        })
        .save()
        .then(result =>{
            res.status(201).json({
                message: "Master initialized!"
            });
        })
        .catch(error =>{
            res.status(500).json({
                error
            });
        });
    },

    signup: (req, res, next) =>{
        const username = req.body.username;
        const password = req.body.password;

        userModel.findOne({username})
        .exec()
        .then(result =>{
            if(result != null){
                res.status(409).json({
                    message: "User with same username already exists, please choose another one"
                });
            } else{
                bcrypt.hash(password, 10, (error, hash) =>{
                    if(error){
                        return res.status(500).json({
                            error
                        });
                    }
                    const newUser  = new userModel({
                        username,
                        password: hash    
                    })
                    .save()
                    .then(user =>{
                        res.status(201).json({
                            user,
                            request: {
                                type: 'GET',
                                url: 'http://localhost:5005/users'
                            }
                        });
                    })
                    .catch(error =>{
                        res.status(500).json({
                            error
                        });
                    })
                })
            }
        });
        
    },

    login: (req, res, next) =>{
        const username = req.body.username;
        const password = req.body.password;

        userModel.findOne({username: username})
        .exec()
        .then(user =>{
            if(!user){
                return res.status(401).json({
                    message: "Auth failed!"
                });
            }
            bcrypt.compare(password, user.password, (error, result) =>{
                if(error){
                    return res.status(500).json({
                        error
                    });
                }
                if(result){
                    return res.status(200).json({
                        message: 'Auth successful',
                        user
                    });
                }

                return res.status(401).json({
                    message: "Auth failed!"
                });

            });
        })
        .catch(error =>{
            res.status(500).json({
                error
            });
        });
    }
}