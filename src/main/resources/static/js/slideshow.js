function Client(){ 
    
    
    /*
    client function bindings
    */
    
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
