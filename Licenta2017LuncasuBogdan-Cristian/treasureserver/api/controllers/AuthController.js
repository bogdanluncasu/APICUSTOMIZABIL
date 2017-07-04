var sha1 = require('sha1');
module.exports = {
  index: function (req, res) {
    var username = req.param('username');
    var password = req.param('password');

    if (!username || !password) {
      return res.json(401, {err: 'Username and Password required'});
    }
	var hashpassword=sha1(password);
    User.findOne({username: username, password:hashpassword}, function (err, user) {
		if (!user) 
			return res.json(401, {err: 'Invalid username or password'});   
	
		if (err) {
			return res.json(403, {err: 'Forbidden'});
        }
		
		return res.json({
            user: user,
            token: jwToken.issue({id : user.id })
          });
    })
  }
};