var BASE_URL = 'http://www.lanyj.cn/am/api/';

function logout() {
	$.ajax({
		url: BASE_URL + 'user/logout'
	}).done(function() {
		window.location.href = 'login.html'
	})
}

{

	var I;
	var username = "未登录";
	var login = '<a href="login.html">登录</a>';
	var breadcrumb = '<li><a href="#">主页</a></li>'

	$.ajax({
		url : BASE_URL + 'user/me'
	}).done(function(ret) {
		if (ret['success']) {
			I = ret['value']
			username = I['username']
			login = '<a href="#" onclick="logout();">登出</a>';
			
			init();
		}
	});
	$.ajax({
		url: BASE_URL + 'user/count'
	}).done(function(ret) {
		if(ret['success']) {
			$('#index_total_member').html(ret['value'][0]);
			$('#index_new_member').html(ret['value'][1]);
		}
	})
	function init() {
		$('#index_username').html(username);
		$('#index_btn_login').html(login);
		$('#index_breadcrumb').html(breadcrumb);
	}
	init();
}

$('#index_message_show_dialog_read').click(function() {
	var uuid = $(this).parent().parent().parent().parent().attr('index-message-show-dialog-uuid')
	swal({
	  title: "标为已读？",
	  text: "发送方也会得知已读状态",
	  icon: "warning",
	  buttons: true,
	  dangerMode: true,
	})
	.then((willDelete) => {
	  if (willDelete) {
		$.ajax({
			url:BASE_URL + 'message/read/' + uuid,
			async:true
		}).done(function(ret) {
			if(ret['success']) {
			    swal("已成功！", {
			      icon: "success",
			    });
			    afterMsgChanged();
			} else {
			    swal("失败！" + ret['msg'], {
			      icon: "error",
			    });
			}
		})
	  }
	});
})
$('#index_message_to_send').click(function(e) {
	e.preventDefault();
	var data = {}
	data['to'] = $('#index_message_to_username').val()
	data['content'] = $('#index_message_to_content').val()
	$.ajax({
		url:BASE_URL + 'message/write',
		data: JSON.stringify(data),
		method: "POST",
		dataType : 'json',
		contentType:'application/json;charset=UTF-8'
	}).done(function(ret) {
		if(ret['success']) {
			swal({
				text:"发送成功！",
				icon: "success",
			});
			afterMsgChanged();
		} else {
			swal({
				text:"发送失败！" + ret['msg'],
				icon:"error"
			})
		}
	})
})

$(function () {
    // 提示信息
    var lang = {
        "sProcessing": "处理中...",
        "sLengthMenu": "每页 _MENU_ 项",
        "sZeroRecords": "没有匹配结果",
        "sInfo": "当前显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项。",
        "sInfoEmpty": "当前显示第 0 至 0 项，共 0 项",
        "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
        "sInfoPostFix": "",
        "sSearch": "搜索:",
        "sDom": "<'row'<'col-md-6'l><'col-md-6'f>r>t<'row'<'col-md-12'i><'col-md-12 center-block'p>>",
        "sUrl": "",
        "sEmptyTable": "表中数据为空",
        "sLoadingRecords": "载入中...",
        "sInfoThousands": ",",
        "oPaginate": {
            "sFirst": "首页",
            "sPrevious": "上页",
            "sNext": "下页",
            "sLast": "末页",
            "sJump": "跳转"
        },
        "oAria": {
            "sSortAscending": ": 以升序排列此列",
            "sSortDescending": ": 以降序排列此列"
        }
    };
    
	// 初始化表格
	var index_file_table = $("#index_file_table").dataTable({
	    language:lang,  // 提示信息
	    autoWidth: true,  // 禁用自动调整列宽
	    stripeClasses: ["odd", "even"],  // 为奇偶行加上样式，兼容不支持CSS伪类的场合
	    processing: false,  // 隐藏加载提示,自行处理
	    serverSide: true,  // 启用服务器端分页
	    searching: false,
	    orderMulti: false,  // 启用多列排序
	    order: [],  // 取消默认排序查询,否则复选框一列会出现小箭头
	    renderer: "bootstrap",  // 渲染样式：Bootstrap和jquery-ui
	    pagingType: "simple_numbers",  // 分页样式：simple,simple_numbers,full,full_numbers
	    columnDefs: [{
	        "targets": 'nosort',  // 列的样式名
	        "orderable": false    // 包含上样式名‘nosort’的禁止排序
	    }],
	    drawCallback: function(settings, json) {
	    	$('[index-file-btn-download]').unbind();
	    	$('[index-file-btn-download]').click(function(e) {
	    		e.preventDefault();
	    		fileDownload($(this).parent().attr('index-file-list-id'));
	    	})

	    	$('[index-file-btn-delete]').unbind();
	    	$('[index-file-btn-delete]').click(function(e) {
	    		e.preventDefault();
	    		fileDelete($(this).parent().attr('index-file-list-id'));
	    	})
//	    	$('[index-file-btn-edit]').click(function(e) {
//	    		e.preventDefault();
//	    		fileEdit($(this).parent().attr('index-file-list-id'));
//	    	})
	    },
	    ajax: function (data, callback, settings) {
	        // 封装请求参数
	        var param = {};
	        param.pageSize = data.length;// 页面显示记录条数，在页面显示每页显示多少项的时候
	        param.pageNo = (data.start / data.length);// 当前页码
	        // ajax请求数据
	        $.ajax({
	            type: "GET",
	            url: BASE_URL + "file/list",
	            cache: false,  // 禁用缓存
	            data: param,  // 传入组装的参数
	            dataType: "json",
	            success: function (result) {
	            	var returnData = {};
                    returnData.draw++;// 这里直接自行返回了draw计数器,应该由后台返回
                    returnData.recordsTotal = parseInt(result['msg'], 10);// 返回数据全部记录
                    returnData.recordsFiltered = parseInt(result['msg'], 10);// 后台不实现过滤功能，每次查询均视作全部结果
                    var tmp = []
                    var j = 0;
                    for(var i = 0; i < result['value'].length; i++) {
                    	if(result['value'][i]['directory'] == true) {
                    		continue;
                    	} else {
                        	tmp[j] = createFile(result['value'][i])
    						j++;
                    	}
                    }
                    $('#index_total_file').html(tmp.length);
                    if(tmp.length == 0) {
                        returnData.data = [];// 返回的数据列表
                    } else {
                        returnData.data = tmp;// 返回的数据列表
                    }
                    callback(returnData);
	            }
	        });
	    },
	    // 列表表头字段
	    columns: [
	        { "data": "name" },
	        { "data": "uploadTime" },
	        { "data": "username" },
	        { "data": "command" }
	    ]
	}).api();
	
	// 初始化表格
	var index_message_inbox_table = $("#index_message_inbox_table").dataTable({
	    language:lang,  // 提示信息
	    autoWidth: true,  // 禁用自动调整列宽
	    stripeClasses: ["odd", "even"],  // 为奇偶行加上样式，兼容不支持CSS伪类的场合
	    processing: false,  // 隐藏加载提示,自行处理
	    serverSide: true,  // 启用服务器端分页
	    searching: false,
	    orderMulti: false,  // 启用多列排序
	    order: [],  // 取消默认排序查询,否则复选框一列会出现小箭头
	    renderer: "bootstrap",  // 渲染样式：Bootstrap和jquery-ui
	    pagingType: "simple_numbers",  // 分页样式：simple,simple_numbers,full,full_numbers
	    columnDefs: [{
	        "targets": 'nosort',  // 列的样式名
	        "orderable": false    // 包含上样式名‘nosort’的禁止排序
	    }],
	    drawCallback: function(settings, json) {
	    	$('[index-message-btn-read]').unbind();
	    	$('[index-message-btn-read]').click(function(e) {
	    		e.preventDefault();
	    		messageRead($(this).parent().attr('index-message-list-id'));
	    	})
	    },
	    ajax: function (data, callback, settings) {
	        // 封装请求参数
	        var param = {};
	        param.pageSize = data.length;// 页面显示记录条数，在页面显示每页显示多少项的时候
	        param.pageNo = (data.start / data.length);// 当前页码
	        // ajax请求数据
	        $.ajax({
	            type: "GET",
	            url: BASE_URL + "message/inbox",
	            cache: false,  // 禁用缓存
	            data: param,  // 传入组装的参数
	            dataType: "json",
	            success: function (result) {
	            	var returnData = {};
                    returnData.draw++;// 这里直接自行返回了draw计数器,应该由后台返回
                    returnData.recordsTotal = parseInt(result['msg'], 10);// 返回数据全部记录
                    returnData.recordsFiltered = parseInt(result['msg'], 10);// 后台不实现过滤功能，每次查询均视作全部结果
                    var tmp = []
                    var j = 0;
                    for(var i = 0; i < result['value'].length; i++) {
                    	tmp[j] = createInboxMessage(result['value'][i])
						j++;
                    }
	                returnData.data = tmp;// 返回的数据列表
                    callback(returnData);
	            }
	        });
	    },
	    // 列表表头字段
	    columns: [
	        { "data": "sender" },
	        { "data": "createTime" },
	        { "data": "content" },
	        { "data": "status"},
	        { "data": "command"}
	    ]
	}).api();
	
	// 初始化表格
	var index_message_outbox_table = $("#index_message_outbox_table").dataTable({
	    language:lang,  // 提示信息
	    autoWidth: true,  // 禁用自动调整列宽
	    stripeClasses: ["odd", "even"],  // 为奇偶行加上样式，兼容不支持CSS伪类的场合
	    processing: false,  // 隐藏加载提示,自行处理
	    serverSide: true,  // 启用服务器端分页
	    searching: false,
	    orderMulti: false,  // 启用多列排序
	    order: [],  // 取消默认排序查询,否则复选框一列会出现小箭头
	    renderer: "bootstrap",  // 渲染样式：Bootstrap和jquery-ui
	    pagingType: "simple_numbers",  // 分页样式：simple,simple_numbers,full,full_numbers
	    columnDefs: [{
	        "targets": 'nosort',  // 列的样式名
	        "orderable": false    // 包含上样式名‘nosort’的禁止排序
	    }],
	    ajax: function (data, callback, settings) {
	        // 封装请求参数
	        var param = {};
	        param.pageSize = data.length;// 页面显示记录条数，在页面显示每页显示多少项的时候
	        param.pageNo = (data.start / data.length);// 当前页码
	        // ajax请求数据
	        $.ajax({
	            type: "GET",
	            url: BASE_URL + "message/outbox",
	            cache: false,  // 禁用缓存
	            data: param,  // 传入组装的参数
	            dataType: "json",
	            success: function (result) {
	            	var returnData = {};
                    returnData.draw++;// 这里直接自行返回了draw计数器,应该由后台返回
                    returnData.recordsTotal = parseInt(result['msg'], 10);// 返回数据全部记录
                    returnData.recordsFiltered = parseInt(result['msg'], 10);// 后台不实现过滤功能，每次查询均视作全部结果
                    var tmp = []
                    var j = 0;
                    for(var i = 0; i < result['value'].length; i++) {
                    	tmp[j] = createOutboxMessage(result['value'][i])
						j++;
                    }
	                returnData.data = tmp;// 返回的数据列表
                    callback(returnData);
	            }
	        });
	    },
	    // 列表表头字段
	    columns: [
	        { "data": "receiver" },
	        { "data": "createTime" },
	        { "data": "content" },
	        { "data": "status"},
	    ]
	}).api();
});
function createInboxMessage(msg) {
	var read = msg['readTime'] != null;
	var tmp = {}
	tmp['sender'] = msg['from']['username'];
	tmp['createTime'] = msg['createTime'];
	tmp['content'] = msg['content']
	tmp['status'] = read ? "已读" : "未读";
	if(read) {
		tmp['command'] = '';
	} else {
		tmp['command'] = '<div index-message-list-id=' + msg['uuid'] + '>'
		  + '<a class="btn btn-success" index-message-btn-read href="#">'
		  + '<i class="glyphicon glyphicon-eye-open"></i> 标记为已读</a>'
		  + '</div>';
	}
	return tmp;
}
function createOutboxMessage(msg) {
	var read = msg['readTime'] != null;
	var tmp = {}
	tmp['receiver'] = msg['to']['username'];
	tmp['createTime'] = msg['createTime'];
	tmp['content'] = msg['content']
	tmp['status'] = read ? "已读" : "未读";
	return tmp;
}
function createFile(file) {
	var tmp = {}
	tmp['name'] = file['name'];
	tmp['uploadTime'] = file['uploadTime'];
	tmp['username'] = file['uploader']['username']
//	tmp['command'] = 'command'
	tmp['command'] = '<div index-file-list-id=' + file['uuid'] + '>'
				  + '<a class="btn btn-success" index-file-btn-download href="#">'
				  + '<i class="glyphicon glyphicon-arrow-down"></i> 下载</a>'
				  + '<a class="btn btn-danger" index-file-btn-delete href="#">'
				  + '<i class="glyphicon glyphicon-trash icon-white"></i> 删除</a>'
				  + '</div>';
	return tmp;
}
//function fileEdit(uuid) {
//	$.ajax({
//		url:BASE_URL + 'file/detail/' + uuid
//	}).done(function(ret) {
//		if(ret['success']) {
//			var file = ret['value'];
//			$('#index_file_show_dialog_title').html(file.name);
//		}
//	})
//}
function fileDelete(uuid) {
	swal({
	  title: "删除文件？",
	  text: "您将会永久删除该文件！",
	  icon: "warning",
	  buttons: true,
	  dangerMode: true,
	})
	.then((willDelete) => {
	  if (willDelete) {
		$.ajax({
			url:BASE_URL + 'file/delete/' + uuid,
			async:true
		}).done(function(ret) {
			if(ret['success']) {
			    swal("已成功！", {
			      icon: "success",
			    });
			    $('#index_file_table').dataTable().fnReloadAjax();
			} else {
			    swal("失败！" + ret['msg'], {
			      icon: "error",
			    });
			}
		})
	  }
	});
}
function fileDownload(uuid) {
	$('<form></form>')
		.attr('action', BASE_URL + 'file/download/' + uuid)
		.appendTo('body').submit().remove();
}
$('#index_file_btn_upload').click(function() {
	var p = document.getElementById('index_file_control_upload');
	if(p.files.length == 0) {
		swal({
			text: "请选择文件后上传！",
			icon: "error"
		})
		return;
	}
	var form = $('#index_file_form_upload')[0];
    var data = new FormData(form);
	$.ajax({
		url:BASE_URL + 'file/upload',
		type: "POST",
		enctype: 'multipart/form-data',
		processData: false, //prevent jQuery from automatically transforming the data into a query string
		contentType: false,
		cache: false,
		timeout: 600000,
		data: data
	}).done(function(ret) {
		if(ret['success']) {
			swal({
				text: "上传成功！",
				icon: "success"
			})
			$('#index_file_table').dataTable().fnReloadAjax();
		} else {
			swal({
				text: "上传失败！" + ret['msg'],
				icon: "error"
			})
		}
	})
})
function messageRead(uuid) {
	swal({
	  title: "标记为已读？",
	  text: "发送方也会得知您已读状态！",
	  icon: "warning",
	  buttons: true,
	  dangerMode: true,
	})
	.then((willDelete) => {
	  if (willDelete) {
		$.ajax({
			url:BASE_URL + 'message/read/' + uuid,
			async:true
		}).done(function(ret) {
			if(ret['success']) {
			    swal("已成功！", {
			      icon: "success",
			    });
			    afterMsgChanged();
			} else {
			    swal("失败！" + ret['msg'], {
			      icon: "error",
			    });
			}
		})
	  }
	});
}
function afterMsgChanged() {
	$('#index_message_inbox_table').DataTable().ajax.reload();
    $('#index_message_outbox_table').DataTable().ajax.reload();
//    $('#index_message_inbox_table').dataTable().fnReloadAjax();
//    $('#index_message_outbox_table').dataTable().fnReloadAjax();
    $.ajax({
		url: BASE_URL + 'message/count'
	}).done(function(ret) {
		if(ret['success']) {
			$('#index_total_message').html(ret['value'][0]);
			$('#index_new_message').html(ret['value'][1]);
		}
	})
}
$.ajax({
	url: BASE_URL + 'message/count'
}).done(function(ret) {
	if(ret['success']) {
		$('#index_total_message').html(ret['value'][0]);
		$('#index_new_message').html(ret['value'][1]);
	}
})