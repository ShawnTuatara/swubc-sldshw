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
            
            stompClient.subscribe('/topic/comments', function(greeting){
                console.log(greeting);
            });
            
        });}
    
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