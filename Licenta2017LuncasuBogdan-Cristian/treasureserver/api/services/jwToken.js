var
  jwt = require('jsonwebtoken'),
  tokenSecret = "thisishard";

module.exports.issue = function(payload) {
  return jwt.sign(
    payload,
    tokenSecret,
    {
      expiresIn : 2*60*60
    }
  );
};

module.exports.verify = function(token, callback) {
  return jwt.verify( token, tokenSecret, {}, callback );
};


