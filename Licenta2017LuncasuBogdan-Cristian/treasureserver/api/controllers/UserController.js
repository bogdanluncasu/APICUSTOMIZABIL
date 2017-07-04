module.exports = {
  findOne: function(req,res,next){
    id=req.param("id");
    User.findOne({id:id}).populate('challenges').exec(function(err,user){
      if(err) return next(err);
      return res.json(200,user);
    });
  },
  create: function (req, res, next) {
    user = req.params.all();

    User.create(user, function created(err, createdUser) {
      if (err) return next(err);
      res.redirect('/user/' + createdUser.id)
    });
  },
  acceptChallenge: function (req,res,next) {
    challengeId=req.param("challengeId");
    userid=req.token.id;
      User.findOne({id:userid}).exec(
        function(err,user){
          if(!user) return res.json(401, {err: 'User does not exists!'});
          if(err) return next(err);
          user.challenges.add(challengeId);
          user.save(function(err) {});
		  Challenge.findOne({id: challengeId}).exec(
			function(err,challenge){
				if(err) return next(err);
				if(!challenge) return res.json(401, {err: 'Challenge does not exists!'});
				Challenge.publishUpdate("__challenge"+challengeId,{
					  message:"User "+user.username+" has accepted challenge "+challenge.title
				});
			}
		  );
          res.redirect("/user/"+user.id);
        }
      );


  }
};

