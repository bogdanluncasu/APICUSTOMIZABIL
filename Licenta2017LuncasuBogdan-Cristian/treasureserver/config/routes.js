
module.exports.routes = {

  '/': {
    view: 'homepage'
  },

  'POST /challenge/:challengeId/task' : 'TaskController.create',
  'GET /challenge/:challengeId/task' : 'TaskController.find',
  'POST /challenge' : 'ChallengeController.create',
  'POST /user' : 'UserController.create',
  'GET /user' : 'UserController.find',
  'GET /user/:id' : 'UserController.findOne',
  'POST /auth' : 'AuthController.index',
  'GET /challenge/:challengeId/task/:id' : 'TaskController.findOne',
  'POST /challenge/:challengeId/task/:taskId/solve' : 'TaskController.solve',
  'POST /challenge/:challengeId/accept' : 'UserController.acceptChallenge',
  'GET /challenge' : 'ChallengeController.find',
  'GET /challenge/subscribe' : 'ChallengeController.subscribeChallenge',
  'GET /challenge/subscribeTasks' : 'TaskController.subscribeTask',
  'PUT /challenge/:id' : 'ChallengeController.update'


};
