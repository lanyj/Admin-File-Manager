var BASE_URL = "/am/api/";

(function ($) {
    $.fn.serializeFormJSON = function () {

        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };
})(jQuery);


$('#login_btn_login').click(function() {
	$.ajax({
		url: BASE_URL + 'user/login',
		data: $('#login_form').serializeArray()
	})
	.done(function(ret) {
		if(ret['success']) {
			window.location.href = 'index.html'
		} else {
			$('#login_error_msg').html(ret['msg']).addClass('alert alert-danger in')
		}
	})
})
$('#login_btn_register').click(function() {
	window.location.href = 'register.html'
})
$('#register_btn_register').click(function() {
	var username = $('#register_username').val();
	var email = $('#register_email').val();
	var password = $('#register_password').val();
	var password2 = $('#register_password2').val();
	if(username.length < 6) {
		swal("用户名应不少于六位", {
		      icon: "error",
		});
		return;
	}
	if(email.length < 3) {
		swal("邮箱格式不正确", {
		      icon: "error",
		});
		return;
	}
	if(password.length < 6) {
		swal("密码应不少于六位", {
		      icon: "error",
		});
		return;
	}
	if(password != password2) {
		swal("两次密码不一致", {
		      icon: "error",
		});
		return;
	}
	var data = {}
	data['username'] = username;
	data['email'] = email;
	data['password'] = password
	
	$.ajax({
		url: BASE_URL + 'user/register',
		method: "POST",
		dataType : 'json',
		contentType:'application/json;charset=UTF-8',
		data: JSON.stringify(data)
	})
	.done(function(ret) {
		if(ret['success']) {
			window.location.href = 'login.html'
		} else {
			$('#register_error_msg').html(ret['msg']).addClass('alert alert-danger in')
		}
	})
})
