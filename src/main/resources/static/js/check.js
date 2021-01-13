$(function () {
    $('#commitBtn').click(function () {
        //commitOrder();
        $("#checkForm").submit();
    });
});

function commitOrder() {
    var formData = new FormData()
    var size = $("#checkFile")[0].files[0].size;
    if (size > 1048576) {
        alert("文件超过最大限制，请上传小于1M的文件");
        return;
    }
    formData.append('paper_doc', $("#checkFile")[0].files[0]);
    $.ajax({
        url: "./docheck",
        type: "POST",
        timeout: 0,
        cache: false,
        processData: false, //告诉jQuery不要去处理发送的数据
        contentType: false,// 告诉jQuery不要去设置Content-Type请求头
        data: formData,
        success: function (res) {
            alert('检测完成');
        }
    });
}