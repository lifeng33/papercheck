//缩放视图
$(document).ready(function () {
    resizeDiv();
});

function resizeDiv() {
    var defaultWidth = 1920;
    var width = $(window).width();
    if (width < 1200) {
        defaultWidth = 1280;
    }
    var scaleX = width / defaultWidth;
    $('.container').css({
        "width": defaultWidth - 10
    });
    $('body').css({
        "zoom": scaleX
    });
};

function getId() {
    var now = new Date();
    var yyyy = now.getFullYear();
    var mm = now.getMonth() + 1;
    var dd = now.getDate();
    var hh = now.getHours();
    var min = now.getMinutes();
    var ss = now.getSeconds();
    var sss = now.getMilliseconds();

    if (mm < 10) {
        mm = "0" + mm;
    }
    if (dd < 10) {
        dd = "0" + dd;
    }
    if (hh < 10) {
        hh = "0" + hh;
    }
    if (min < 10) {
        min = "0" + min;
    }
    if (ss < 10) {
        ss = "0" + ss;
    }
    if (sss < 10) {
        sss = "00" + sss;
    }
    if (sss >= 10 && sss < 100) {
        sss = "0" + sss;
    }
    var rand = Math.round(Math.random() * 1000000);
    return yyyy + "" + mm + "" + dd + "" + hh + "" + min + "" + ss + "" + sss + "" + rand;
}