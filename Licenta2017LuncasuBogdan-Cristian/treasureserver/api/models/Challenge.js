module.exports = {
  attributes: {
    title: {
      type: 'string',
      required: true
    },
    description: {
      type: 'string',
      required: true
    },
    users: {
      collection: 'user',
      via: 'challenges'
    },
    tasks: {
      collection: 'task',
      via:'challenge'
    }
  }
};

