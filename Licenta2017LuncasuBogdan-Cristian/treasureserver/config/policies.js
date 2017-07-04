module.exports.policies = {

  '*': ['isAuthorized'],
  'UserController': {
    'create' : true,
	'findOne' :true
  },

  'AuthController': {
    '*': true
  },
  'ChallengeController': {
    'subscribeChallenge': true
  }

};
