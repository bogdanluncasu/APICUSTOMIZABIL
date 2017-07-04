
module.exports.http = {


  middleware: {

     order: [
       'startRequestTimer',
       'cookieParser',
       'session',
       'myRequestLogger',
       'bodyParser',
       'handleBodyParserError',
       'compress',
       'methodOverride',
       'poweredBy',
       '$custom',
       'router',
       'www',
       'favicon',
       '404',
       '500'
     ],

    myRequestLogger: function (req, res, next) {
         console.log("Requested :: ", req.method, req.url);
         return next();
     },


     bodyParser: require('skipper')({limit: 52428800})
	  //bodyParser: skipper

  },

  /***************************************************************************
  *                                                                          *
  * The number of seconds to cache flat files on disk being served by        *
  * Express static middleware (by default, these files are in `.tmp/public`) *
  *                                                                          *
  * The HTTP static cache is only active in a 'production' environment,      *
  * since that's the only time Express will cache flat-files.                *
  *                                                                          *
  ***************************************************************************/

  // cache: 31557600000
};
