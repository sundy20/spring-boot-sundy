var count = 0;
var max = 2000;

for (var i = 0; i < max; i++) {
    socketClient(i);
}

function socketClient(id) {

    var message = 'message' + id;

    var io = require('socket.io-client');

    var socket = io.connect('http://localhost:9527', {'force new connection': true});

    socket.on('connect', function () {

        count++;

        console.log('connect count:' + count);

        var user = {

            token: "ssdsdsds",

            exchange: "",

            metal: ""
        };

        socket.emit("joinRoom", user);

        var jsonObject = {

            messageContent: message
        };

        socket.emit('message', jsonObject);
    });
}
