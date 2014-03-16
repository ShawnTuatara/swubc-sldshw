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

/*
$(document).ready(function(){
    Reveal.initialize({});

    
    function BaseUser() {
        var stompClient = null;
    };

    BaseUser.prototype.connect = function () {
        var socket = new SockJS('/socket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            
            stompClient.subscribe('/comment', function(greeting){
                console.log(greeting);
            });
        });}

    function Client(BaseUser){

        var base_user = new BaseUser(); 
        base_user.connect();
        
        }

    function Host(BaseUser){

        var base_user = new BaseUser(); 
        base_user.connect();
        
        
        var comment_subscriptions = client.subscribe("/comment", comment_recieved);
        var comment_recieved = function(data){console.log(data)}

        var packIndices = function (){
        page = {'indexv': Reveal.getIndices().v,
            'indexh': Reveal.getIndices().h,
            'indexf': Reveal.getIndices().f || 0};
            console.log(page)

        return page;
    }
    
    Reveal.addEventListener("slidechanged", packIndices);
        
        
        }

*/