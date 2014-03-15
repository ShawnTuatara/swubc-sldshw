window.alert('Hi fglkajf')
$(document).ready(function(){
    Reveal.initialize({});

    function BaseUser() {
        var stompClient = null;
    };

    BaseUser.prototype.connect() {
        var socket = new SockJS('/socket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            
            stompClient.subscribe('/presentation', function(greeting){
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



MyClass.prototype.aFunction(){ 
}

var instance = new MyClass();
instance.aFunction();


client.send("/comment", {}, comment);


$(document).ready(function(){

    Reveal.initialize({});

    var stompClient = null;

    function connect() {
        var socket = new SockJS('/socket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            
            stompClient.subscribe('/comment', function(greeting){
                console.log(greeting);
            });
            
            stompClient.subscribe('/navigation', function(greeting){
                console.log(greeting);
            });
            
            stompClient.subscribe('/like', function(greeting){
                console.log(greeting);
            });
            
            stompClient.subscribe('/like', function(greeting){
                console.log(greeting);
            });
        });}

    function sendComment() {
        stompClient.send("/comment", {}, JSON.stringify({ 'comment': "This is through sockets" }));
        }

    connect();
    sendComment();
    
});