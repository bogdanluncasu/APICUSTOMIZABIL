module.exports = {
  find: function (req, res, next) {
    userid = req.token.id;
    listofchallenges = [];
    Challenge.query('select title,description,challenge.id from challenge left join challenge_users__user_challenges "c_u"\
    on challenge.id=c_u.challenge_users where c_u.user_challenges=$1;', [userid],
      function (err, acceptedchallenges) {
        if (err) return next(err);
        rows=acceptedchallenges["rows"];
        for (row in rows) {
          rows[row]["accepted"] = true;
          listofchallenges.push(rows[row]);
        }
        Challenge.query('select title,description,id from challenge\
        except select title,description,challenge.id from challenge left join challenge_users__user_challenges "c_u"\
        on challenge.id=c_u.challenge_users where c_u.user_challenges = $1', [userid],
          function (err, activechallenges) {
            if (err) return next(err);
            rows=activechallenges["rows"];
            for (row in rows) {
              rows[row]["accepted"] = false;
              listofchallenges.push(rows[row]);
            }
            return res.json(200, listofchallenges);
          });

      });

  },
  subscribeChallenge: function(req,res){
    if (!req.isSocket) {
      return res.badRequest('Only a client socket can subscribe to challenges.');
    }

    Challenge.find().exec(function (err, challenges) {
		for(var index in challenges){
			Challenge.subscribe(req, "__challenge"+challenges[index]["id"]);
		}
    });
    return res.ok();
  }


};

