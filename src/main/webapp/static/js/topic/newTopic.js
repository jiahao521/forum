$(function(){

    $("#sendBtn").click(function () {
        $("#topicForm").submit();
    });

    $("#topicForm").validate({
        errorElement:"span",
        errorClass:"text-error",
        rules:{
            title:{
                required:true,
            },
            nodeid:{
                required:true,
            }
        },
        messages:{
            title:{
                required:"请输入标题",
            },
            nodeid:{
                required:"请选择节点",
            }
        },
        submitHandler:function () {
            $.ajax({
                url:"/newTopic",
                type:"post",
                data:$("#topicForm").serialize(),
                beforeSend:function(){
                    $("#sendBtn").text("发布中").attr("disabled","disabled");
                },
                success:function(json){
                    if(json.state == "success"){
                        /*跳转到新建帖子jsp*/
                        window.location.href="/topicDetail?topicid="+json.data.id;
                    }else {
                        swal("新增主题异常");
                    }
                },
                error:function(){
                    swal("服务器异常");
                },
                complete:function () {
                    $("#sendBtn").text("发布主题").removeAttr("disabled");
                }
            })
        }
    });


});