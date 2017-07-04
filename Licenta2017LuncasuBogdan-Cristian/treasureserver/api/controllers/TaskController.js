var request = require('request');
var APIURL="https://objectrecognize.herokuapp.com/";
var DEVKEY="NwmvwpdTilbZlSvszqYt36AQVRrd8S5piD6SmiBZzKaaTZMoafzELAadRoYXstMhTNAFP5VQiY992rTeiUjzUYfXtqpdCqKFf6gFEz7FXUzuc0ebBUkosl5rfSHYvBd9";
module.exports = {

  create: function (req, res, next) {
    challengeId = req.param('challengeId');
    task = req.params.all();
    Challenge.findOne({id: challengeId}, function (err, challenge) {
      if (!challenge)
        return res.json(401, {err: 'Invalid challenge id'});

      if (err) {
        return res.json(403, {err: 'Forbidden'});
      }
      task["challenge"] = challenge;
      Task.create(task, function created(err, createdTask) {
        if (err) return next(err);
        res.redirect('/challenge/' + challengeId + "/task/" + createdTask.id)
      });
    });
  },
  find: function (req, res, next) {
    id = req.param('challengeId');
    Challenge.findOne({id: id}).populate('tasks').exec(function (err, challenge) {
      if (!challenge)
        return res.json(401, {err: 'Invalid challenge id'});

      if (err) {
        return res.json(403, {err: 'Forbidden'});
      }
      return res.json(challenge.tasks);
    });
  },
  solve:function(req,res,next){
      taskId=req.param('taskId');
      image=req.param('image');
      Task.findOne({id:taskId}).populate('labels').exec(function (err,task){
        if(!task) return res.json(401,{err:"Invalid task id"});
        if(err) return res.json(403,{err:"Forbidden"});
        if(!task.active) return res.json(401,{err:"Task already done"});
        request.post({
          url:APIURL+"image/objectrecognition",
          json:{
            "image":image,
            "developer_key":DEVKEY
          }
        }, function (error, response, body) {
          if (!error && response.statusCode === 200) {
            data=body;
            taskDone=true;
            objects=data["objects"];

            for(label_index=0;label_index<task.labels.length;label_index++){
              label=task.labels[label_index].label
              counter=0;
              for(obj in objects){
                objname = objects[obj]['object'];
                if(label===objname)
                  break;
                counter+=1;
              }
              if(counter===objects.length)
                taskDone=false;
            }

            if(taskDone){
              task.active=false;
              task.user=req.token.id;
              task.save(function(err) {});
			  Task.publishUpdate("__task"+task.id,{message:"Task "+task.title+" done."});
			  
            }

            return res.json(200,{'done':taskDone,'info':data["objects"]})
          }
        });



      })
  },
  subscribeTask: function(req,res,next){
	if(!req.isSocket)
		  return res.badRequest('Only a client socket can subscribe to tasks.');
	userid=req.token.id;
	sails.log("Task subscribed -- "+userid);
	
	User.find({id:userid}).populate('challenges').exec(function (err, user) {
		if(err) return next(err);
		if(!user) return res.json(401, {err: 'User does not exists!'});
		challenges=user[0].challenges;
		for(var indexc in challenges){
			challengeid=challenges[indexc].id;
			if(challengeid){
				Challenge.find({id: challengeid}).populate('tasks').exec(function(err, challenge){
					if(err) return next(err);
					if(!challenge) return res.json(401, {err: 'Challenge does not exists!'});
					tasks=challenge[0].tasks;
					for(var indext in tasks){
						taskid=tasks[indext].id;
						if(taskid){
							id="__task"+taskid;
							Task.subscribe(req, id);
						}
					}
				});
			}
		}
    });
    return res.ok();
  }
};

