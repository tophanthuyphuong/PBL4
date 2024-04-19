// Password
function togglePassword() {
	var toggleIcon = event.target;
	var passwordInput = toggleIcon.parentElement.parentElement.querySelector('input');
	console.log(passwordInput)
	if (passwordInput.type === "password") {
		passwordInput.type = "text";
		toggleIcon.classList.remove('fa-eye');
		toggleIcon.classList.add('fa-eye-slash');
	} else {
		passwordInput.type = "password";
		toggleIcon.classList.remove('fa-eye-slash');
		toggleIcon.classList.add('fa-eye');
	}
}

// handle btnSubmit
var btn_submit = document.getElementById('btn_submit');
var old_password = document.getElementById('old_password');
var new_password = document.getElementById('new_password');
var reenter_password = document.getElementById('reenter_password');
var old_error_message = document.getElementById('old_error_message');
var new_error_message = document.getElementById('new_error_message');
var reenter_error_message = document.getElementById('reenter_error_message');

old_password.addEventListener('change', function() {
	validatePassword(old_password.value, false)
});

new_password.addEventListener('change', function() {
	isError = validatePassword(new_password.value, true)
	if (!isError)
		validateSamePwd(new_password.value, old_password.value)
});

reenter_password.addEventListener('change', function() {
	validateConfirmPwd(new_password.value, reenter_password.value)
});

function handleButtonSubmit(event) {
	event.preventDefault();
	var isError = validatePassword(old_password.value, false)
	if (!isError) isError = validatePassword(new_password.value, true)
	if (!isError) isError = validateSamePwd(new_password.value, old_password.value)
	if (!isError) isError = validateConfirmPwd(new_password.value, reenter_password.value)
	
	
	if (!isError) {
		let encryptedOldpass = encryptPrivate(old_password.value);
		let encryptNewpass = encryptPrivate(new_password.value);
		/*encryptedOldpass = encryptPrivate(encryptedOldpass);
		encryptNewpass = encryptPrivate(encryptNewpass);*/
		
		document.querySelector("input[name='old_password']").value = encryptedOldpass;
		document.querySelector("input[name='new_password']").value = encryptNewpass;
		//console.log(old_password.value);
		document.update_pass.submit();
	}

}

function validatePassword(password, isNew) {
	var pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$/;
	var isError = false
	if (!password.match(pattern)) {
		isError = true;
		if (isNew == true)
			new_error_message.innerHTML = "Mật khẩu phải có ít nhất 8 ký tự." + "\n Gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số"
		else old_error_message.innerHTML = "Mật khẩu phải có ít nhất 8 ký tự." + "\n Gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số"//"Mật khẩu cũ không chính xác!"
	} else {
		new_error_message.innerHTML = ""
		old_error_message.innerHTML = ""
	}
	return isError;
}
function validateConfirmPwd(pwd1, pwd2) {
	var isError = false;
	if (pwd1 != pwd2) {
		isError = true;
		reenter_error_message.innerHTML = "Mật khẩu nhập lại không đúng"
	} else {
		reenter_error_message.innerHTML = ""
	}
	return isError;
}

function validateSamePwd(pwd1, pwd2) {
	var isError = false;
	if (pwd1 === pwd2) {
		isError = true;
		new_error_message.innerHTML = "Mật khẩu mới không được trùng với mật khẩu ban đầu"
	} else {
		new_error_message.innerHTML = ""
	}

	return isError;
}