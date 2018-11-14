function() {
var env = karate.env;
if (!env) {
    env = 'dev';
  }
var config = {
    env: env,
	myVarName: 'someValue'
  }
  if (env == 'dev') {
    // customize
    // e.g. config.foo = 'bar';
  } else if (env == 'e2e') {
    // customize
  }

karate.configure('connectTimeout', 120000);
karate.configure('readTimeout', 120000);
return config;

}
