$(document).ready(function(){  

    var stompClient = null;

    function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }
    
    function connect() {
        var socket = new SockJS('/socket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            
            stompClient.subscribe('/topic/presentation/1/page', function(pagenum){
            	console.log('received: ' + pagenum);
            	document.getElementById('results').innerHTML = pagenum.body;
            });
            
            stompClient.subscribe('/topic/presentation/1/summary', function(results){
            	console.log('received: ' + results);
            	document.getElementById('results').innerHTML = results.body;
            });
            
            stompClient.subscribe('/topic/presentation/1/relay', function(results){
            	console.log('received: ' + results);
            	document.getElementById('results').innerHTML = results.body;
            });
            
            stompClient.subscribe('/topic/allData', function(results){
            	console.log('received: ' + results);
            	document.getElementById('results').innerHTML = results.body;
            });            
        });}
    
    function send() {
    	 stompClient.send(document.getElementById('path').value, {}, document.getElementById('data').value);
    }
    $('#send').click(send);
    
    function sendComment() {
        stompClient.send("/comment", {}, JSON.stringify({ 'body': "This is through sockets" }));
        }
    $('#sendName').click(sendComment);
    connect();
    
});
