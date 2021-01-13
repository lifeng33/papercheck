var textMousein = false;
var windowtMousein = false;
var mouseX = 0;
var mouseY = 0;
$(function () {
	$(window).mousemove(function(e){
		mouseX = e.clientX;
		mouseY = e.clientY;
    });
	$("#toTop").hide();
	//检测屏幕高度
	var height = $(window).height();
	//scroll() 方法为滚动事件
	$(window).scroll(function () {
		if ($(window).scrollTop() > height) {
			$("#toTop").fadeIn(500);
		} else {
			$("#toTop").fadeOut(500);
		}
	});
	$("#toTop").click(function () {
		$('body,html').animate({ scrollTop: 0 }, 100);
		return false;
	});
	
	$(".similar").mouseover(function(){
		textMousein = true;
		var title = $(this).attr("data-title");
		var url = $(this).attr("data-url");
		var auth = $(this).attr("data-auth");
		var book = $(this).attr("data-book");
		var sctime = $(this).attr("data-sctime");
		$("#repeatTitle").html(title);
		$("#repeatUrl").html(url);
		$("#repeatAuth").html(auth);
		$("#repeatBook").html(book);
		$("#repeatSctime").html(sctime);
		var scrollTop = $(document).scrollTop();
		var top = $(this).offset().top - scrollTop - 130;
		var left = $(this).offset().left - 50;
		$(".repeat-window").css({"top":top+"px","left":left+"px"});
		$(".repeat-window").show(100);
	});
	
	$(".repeat-window").mouseover(function(){
		windowtMousein = true ;
	});
	
	$(".similar").mouseout(function(){
		textMousein = false;
		setTimeout(closeWindow,300);
	});
	
	$(".repeat-window").mouseout(function(){
		windowtMousein = false;
		setTimeout(closeWindow,300);
	});
});
function closeWindow(){
	if(!textMousein && !windowtMousein){
		$(".repeat-window").hide(100);
	}
}