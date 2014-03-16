var lastPage = 12

function Client(pres_id){ 
    
    var pres_id = pres_id;
    var pageId = null;
    var pageTitle = null;
    
    var header = {"username": null, "password": null};
      
    /* endpoints */
    var ep = "".concat("/presentation/", pres_id)
    var ep_page = "".concat(ep, "/page")
    /* var ep_page = "".concat(ep, "/page/", pageId) */
    var ep_summary = "".concat(ep, "/summary")
    var topic_ep_page = "".concat('/topic', ep_page)
    var topic_ep_summary = "".concat('/topic', ep_summary)

    var recieve_page_annotations = function(data){
        
    }
    
    /* initialize host and establish subscriptions */
    var recieve_id = function (data){console.log(data)};
    
    var recieve_id_page = function(data){
        $("#PageNum").text(data);}
    
    var recieve_topic_id_page = function(data){
        
        data = JSON.parse(data.body);
        
        pageId = data.pageId;
        pageTitle = data.title;
        console.log(data)
        
        $("#pageTitle").text(pageTitle);
 
        if ($("#feedback").hasClass("hidden")){
            $("#feedback").removeClass('hidden');
            $("#register").addClass('hidden');
        }
 
        if (pageId==lastPage){
            $("#feedback").addClass('hidden');
            $("#register").removeClass('hidden');
            var superhackendpoint = "".concat(ep_summary, Math.random());
            stompClient.subscribe("".concat("/topic", superhackendpoint), function(data) {
            	data = JSON.parse(data.body);
            	console.log("fuck1", data);
            	document.getElementById("userHeartCount").innerHTML = data.userStats.heartCount;
            	document.getElementById("userQuestionCount").innerHTML = data.userStats.questionCount;
            	console.log("fuck yeah", data);
            })
            stompClient.send(superhackendpoint, {}, 'null');
        }
        

        
        var topic_ep_page_id = "".concat(topic_ep_page, pageId)
        
        stompClient.subscribe(topic_ep_page_id, recieve_page_annotations);
        
        }
    
    var recieve_id_summary = function(data){
        console.log(data)
    }
    
    var recieve_topic_id_summary = function (data){
        console.log(data)
        
        }
    
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
        $('.heart').click(function () {
            me = $(this)
            
            if (me.hasClass("active") == false){
                me.addClass("active");
                
                stompClient.send("".concat(ep_page, "/", pageId), {}, JSON.stringify({heart: true, pageId: pageId}));
                }
            else if (me.hasClass("active")){
                me.removeClass("active");
                stompClient.send("".concat(ep_page, "/", pageId), {}, JSON.stringify({heart: false, pageId: pageId}));
                }
        });
        
        $('.question').click(function () {
            me = $(this)
            
            if (me.hasClass("active") == false){
                me.addClass("active");
                stompClient.send("".concat(ep_page, "/", pageId), {}, JSON.stringify( {question: true, pageId: pageId} ));
                }
            else if (me.hasClass("active")){
                me.removeClass("active");
                stompClient.send("".concat(ep_page, "/", pageId), {}, JSON.stringify({question: false, pageId: pageId}));
                }
        });
        
        $('#poll>.option').click(function () {
            
            var me = $(this);
            var others = $('#Poll>.option').not($(this));
            others.removeClass("active")
            
            if (me.hasClass("active")){
                me.removeClass("active")
                stompClient.send("".concat(ep_page, "/", pageId), {}, JSON.stringify({vote: null, pageId: pageId}));
                }
                
            else {
                me.addClass("active")
                stompClient.send("".concat(ep_page, "/", pageId), {}, JSON.stringify({vote: me.data("option"), pageId: pageId}));
                
                }
            });
        
        var toggleInput = function(){
            if ($("#noteInput").css("display")=="block"){
                    $("#noteInput").css("display", "none");
                    $(this).removeClass("active");
                }
                else {
                    $("#noteInput").css("display", "block");
                    $(this).addClass("active");
                }
        }
        
        $('.notes').click(toggleInput);
            
        $("#noteSubmit").click(function(){
            console.log($("#note").val());
            stompClient.send(ep, {}, JSON.stringify({ pageannotation: {comment: $("#note").val(), "pageId": pageId} }));
            $("#note").val("");
            toggleInput();
        });
        
   	 
	   	 $('#registerSubmit').click(function() {
	   		 stompClient.send("/register", {}, JSON.stringify($("#registrationEmail").val()));
	   		 setTimeout(function () {
	   			window.location.href = "/confirmation";
	   		 }, 500);
	   		 
	   	 });
            
        });
}

function Host(pres_id){
    
    var pres_id = pres_id
    
    /* endpoints */
    var ep = "".concat("/presentation/", pres_id)
    var ep_page = "".concat(ep, "/page")
    var ep_summary = "".concat(ep, "/summary")
    var ep_relay = "".concat(ep, "/relay")
    var topic_ep_page = "".concat('/topic', ep_page)
    var topic_ep_relay = "".concat('/topic', ep, "/relay")
    var topic_ep_summary = "".concat('/topic', ep_summary)

    /* slideshow control event */
    var slidechanged = function(){

        var packIndices = function (){
            page = {'v': Reveal.getIndices().v,
                    'h': Reveal.getIndices().h,
                    'f': Reveal.getIndices().f || 0};
                    
            return page;
        }
        
        var get_pageData = function (){
            
            page_id = Reveal.getIndices().h.toString(); /*"".concat(Reveal.getIndices().v, ",", Reveal.getIndices().h)*/
            title = $($("section")[Reveal.getIndices().h]).find("h1").text();/*$("section")[Reveal.getIndices().h];*/
            
            return {"title": title, pageId: page_id}
            }
        console.log(get_pageData());
        stompClient.send(ep_page, {}, JSON.stringify(get_pageData()));
        /*stompClient.send(ep_relay, {}, JSON.stringify(packIndices()))*/
    
        stompClient.subscribe(topic_ep_page.concat("/").concat(page_id), stats_received);
    }
    
    Reveal.addEventListener("slidechanged", slidechanged);
    
    /* incoming data processing */
    
    var data_recieved = function(data) {
        console.log(data);
    }
    
    var stats_received = function(data) {
    	data = JSON.parse(data.body);
    	console.log("stats received: ", data);
    	document.getElementById("heartCount").innerHTML = data.heartCount;
    	document.getElementById("questionCount").innerHTML = data.questionCount;
    }

    var go_to_slide = function(data){

        data = JSON.parse(data.body);
        /* This locks hosts together */
        /* Reveal.slide(data.v, data.h, data.f); */
        }

    /* initialize host and establish subscriptions */
    
    var init = function(){
        stompClient.subscribe(ep, data_recieved);
        stompClient.subscribe(ep_page, data_recieved);
        /*stompClient.subscribe(topic_ep_relay, go_to_slide);*/

    }
    
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, init);

}

$(document).ready(function() {
	 $('#loginSignIn').click(function() {$(this).parent().submit();});

 });
