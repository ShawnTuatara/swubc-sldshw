var get_page_id = function (){
    page = {'v': Reveal.getIndices().v,
            'h': Reveal.getIndices().h,
            'f': Reveal.getIndices().f || 0};
            
    page_id = "".concat(Reveal.getIndices().v, ":", Reveal.getIndices().h);
    return page_id;}

function Client(pres_id){ 
    
    var pres_id = pres_id
    
    $('h1').click(function(data){
        
        /*
        stompClient.send("/", {}, JSON.stringify({ 'body': page }));
        */
        
    });
    
    /* endpoints */
    var ep = "".concat("/presentation/", pres_id)
    var ep_page = "".concat(ep, "/page")
    var ep_summary = "".concat(ep, "/page")
    var topic_ep_page = "".concat('/topic', ep_page)
    var topic_ep_summary = "".concat('/topic', ep_summary)

    /*
    initialize host and establish subscriptions
    */
       
    var recieve_id = function (data){console.log(data)};
    var recieve_id_page = function(data){
        console.log("page")
        console.log(data);};
        
    var recieve_topic_id_page = function (data){console.log(data)};
    var recieve_id_summary = function(data){console.log(data)};
    var recieve_topic_id_summary = function (data){console.log(data)};
    
    var do_subscriptions = function(){
    
        stompClient.subscribe(ep, recieve_id );
        stompClient.subscribe(ep_page, recieve_id_page );
        stompClient.subscribe(ep_summary, recieve_id_summary);
        stompClient.subscribe(topic_ep_page, recieve_topic_id_page );
        stompClient.subscribe(topic_ep_summary, recieve_topic_id_summary);
        
    }
    
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, do_subscriptions);
    
    
    $('#Heart').click(function () {
        stompClient.send(ep, {}, { pageannotation: {heart: true, pageId: get_page_id()} });
    });
    
    $('#Comment').click(function () {
        stompClient.send(ep, {}, { pageannotation: {question: true, pageId: get_page_id()} });
    });
    
    $('#Question').click(function () 
    {
        stompClient.send(ep, {}, { pageannotation: {comment: true, pageId: get_page_id()} });
    });
    
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

c = Client(1);