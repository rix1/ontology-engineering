var express = require('express');
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io').listen(server);

var exec = require('child_process').exec, child;

app.set('port', 8880);

server.listen(app.get('port'), function(){
   console.log("Express server listening on port " + app.get('port'));
});

app.use(express.static(__dirname + '/public'));
app.use('/static', express.static(__dirname + '/public'));

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', function(socket){
  console.log('a user connected');
  
  socket.on('disconnect', function(){
    console.log('user disconnected');
  });
  
  socket.on('input fields', function(data){
    console.log('data received: '+ data.owl + " " + data.xml);
    saveFile(data.xml);
    runVerbalizor(data.owl, data.xml);
  })
});


function saveFile(xmlData){
var fs = require('fs');
fs.writeFile(process.env.HOME+ '/Development/ontology-engineering/temp/siste.xml', xmlData, function(err) {
    if(err) {
        return console.log(err);
    }
    console.log("The file was saved!");
});
}


var exec = require('child_process').exec, child;

function runVerbalizor(owlURL, schemaXML){
  var returnData;
  
  // Path to latest build  
	child = exec('/usr/bin/java -jar ' + process.env.HOME+ '/Development/ontology-engineering/out/artifacts/OwlVerbalizor/OWLAPI-4.jar ' +  owlURL, 
		function(error, stdout, stderr){
      io.emit('data response', stdout);
			console.log('stdpout: ' + stdout);
			console.log('stderr: ' + stderr);
			if(error !== null){
				console.log('exec error: ' + error);
        io.emit('error msg', 'Feil: Noe gikk galt! Dobbeltsjekk url til ontologien samt xml skjemaet ditt.')
			}
		});

}