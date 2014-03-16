function Client(pres_id){ 
    
    var pres_id = pres_id
    var pageID = null
    
    var header = {"username": null, "password": null};
      
    /* endpoints */
    var ep = "".concat("/presentation/", pres_id)
    var ep_page = "".concat(ep, "/page")
    var ep_summary = "".concat(ep, "/summary")
    var topic_ep_page = "".concat('/topic', ep_page)
    var topic_ep_summary = "".concat('/topic', ep_summary)

    /* initialize host and establish subscriptions */
    var recieve_id = function (data){console.log(data)};
    var recieve_id_page = function(data){$("#PageNum").text(data);}
    var recieve_topic_id_page = function(data){
        pageID = data.body
        $("#PageID").text(pageID);
        }
    
    var recieve_id_summary = function(data){console.log(data)};
    var recieve_topic_id_summary = function (data){console.log(data)};
    
    var init = function(){
    
        stompClient.subscribe(ep, recieve_id );
        stompClient.subscribe(ep_page, recieve_id_page );
        stompClient.subscribe(ep_summary, recieve_id_summary);
        stompClient.subscribe(topic_ep_page, recieve_topic_id_page );
        stompClient.subscribe(topic_ep_summary, recieve_topic_id_summary);
        
    }
    
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, init);
    
    /* bind client functions */
    
    $(document).ready(function(){
        $('#Heart').click(function () {
            me = $(this)
            
            if (me.hasClass("pressed") == false){
                me.addClass("pressed");
                stompClient.send(ep, {}, JSON.stringify({ pageannotation: {heart: true, pageId: pageID} }));
                }
            else if (me.hasClass("pressed")){
                me.removeClass("pressed");
                stompClient.send(ep, {}, JSON.stringify({ pageannotation: {heart: false, pageId: pageID} }));
                }
                
        });
        
        $('#Question').click(function () {
            me = $(this)
            
            if (me.hasClass("pressed") == false){
                me.addClass("pressed");
                stompClient.send(ep, {}, JSON.stringify({ pageannotation: {question: true, pageId: pageID} }));
                }
            else if (me.hasClass("pressed")){
                me.removeClass("pressed");
                stompClient.send(ep, {}, JSON.stringify({ pageannotation: {question: false, pageId: pageID} }));
                }
                
        });
        
        $('#Poll>.option').click(function () {
            
            var me = $(this);        
            var others = $('#Poll>.option').not($(this));
            others.removeClass("pressed")
            
            if (me.hasClass("pressed")){
                me.removeClass("pressed")
                stompClient.send(ep, {}, JSON.stringify({ pageannotation: {vote: null, pageId: pageID} }));
                }
            else {
                me.addClass("pressed")
                stompClient.send(ep, {}, JSON.stringify({ pageannotation: {vote: me.data("option"), pageId: pageID} }));
                
                }
            });
    });
    
    $('#Comment').click(function () {
        stompClient.send(ep, {}, JSON.stringify({ pageannotation: {question: true, pageId: pageID} }));
    });
    
    var register = function (data) {
        stompClient.send("/register", {}, { body: data});
    }
}

function Host(pres_id){
    
    var pres_id = pres_id
    
    /* endpoints */
    var ep = "".concat("/presentation/", pres_id)
    var ep_hosts = "".concat(ep, "/hosts")
    var ep_page = "".concat(ep, "/page")
    var ep_summary = "".concat(ep, "/summary")
    var topic_ep_page = "".concat('/topic', ep_page)
    var topic_ep_summary = "".concat('/topic', ep_summary)

    /* slideshow control event */
    
    var slidechanged = function(){

        var packIndices = function (){
            page = {'v': Reveal.getIndices().v,
                    'h': Reveal.getIndices().h,
                    'f': Reveal.getIndices().f || 0};
                    
            return page;
        }
        
        var pageId = get_pageID();
        stompClient.send(ep_page, {}, JSON.stringify(pageId));
        stompClient.send(ep_hosts, {}, JSON.stringify(packIndices()));
    }
    
    var get_pageID = function (){

        return "".concat(Reveal.getIndices().v, ",", Reveal.getIndices().h);
        }
    
    Reveal.addEventListener("slidechanged", slidechanged);
    
    /* incoming data processing */
    
    var data_recieved = function(data) {
        console.log(data);
    }

    var remote_hosts = function(data){
        Reveal.slide(data.v, data.h, data.f);
        }
    
    /* initialize host and establish subscriptions */
    
    var init = function(){
        stompClient.subscribe(ep, data_recieved);
        stompClient.subscribe(ep_page, data_recieved);
        stompClient.subscribe(ep_hosts, remote_hosts);
    }
    
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, init);

}