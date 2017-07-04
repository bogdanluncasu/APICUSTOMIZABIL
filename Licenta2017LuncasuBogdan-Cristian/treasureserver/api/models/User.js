var sha1 = require('sha1');
module.exports = {

  attributes: {
    username: {
      type: 'string',
      unique: true,
      required: true
    },
    password: {
      type: 'string',
      required: true
    },
    emailAddress: {
      type: 'string',
      unique: true,
      required: true,
      email: true
    },
    toJSON: function () {
      var obj = this.toObject();
      delete obj.password;
      return obj;
    },
    challenges: {
      collection: 'challenge',
      via: 'users',
      dominant: true
    },
    role: {
      type: 'string'
    }

  },
  beforeCreate: function (user, next) {
    user.password = sha1(user.password);
    next();
  }
};

