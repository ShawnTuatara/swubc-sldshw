function Client(){ 
    
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

    
    $('h1').click(function(data){
        console.log(this.text());
        /*
        stompClient.send("/", {}, JSON.stringify({ 'body': page }));
        */
    });

    
    /*
    initialize host and establish subscriptions
    */
    
    var recieve_broadcast = function (data){console.log(data)};
    var recieve_data = function(data){console.log(data)};
    
    var do_subscriptions = function(){
    
        stompClient.subscribe('/broadcast', recieve_broadcast);
        stompClient.subscribe('/', recieve_data);
    }
    
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, do_subscriptions);
    
}

function Host(){
    /*
    slideshow control event
    */
    
    var slidechanged = function(){

        var packIndices = function (){
            page = {'v': Reveal.getIndices().v,
                    'h': Reveal.getIndices().h,
                    'f': Reveal.getIndices().f || 0};
                    
            return page;

        }
        stompClient.send("/", {}, JSON.stringify({ 'body': packIndices() }));
    }
    
    Reveal.addEventListener("slidechanged", slidechanged);
    
    /*
    incoming data processing
    */
    
    var data_recieved = function(data) {
        console.log(data);
    }

    /*
    initialize host and establish subscriptions
    */
    
    var do_subscriptions = function(){
        stompClient.subscribe("/", data_recieved);
    }
    
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, do_subscriptions);

}
