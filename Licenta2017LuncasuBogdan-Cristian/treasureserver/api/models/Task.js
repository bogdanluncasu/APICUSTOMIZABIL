module.exports = {

  attributes: {
	  title:{
		  type:'text'
	  },
	  description: {
		  type: 'text',
		  required: true
		},
	  labels: {
	    collection: 'label',
      via: 'task'
    },
    active: {
	    type: 'boolean',
      defaultsTo: true
    },
    challenge:{
	    model:'challenge',
      required:true
    },
    user:{
	    model:'user'
    }
  }
};

