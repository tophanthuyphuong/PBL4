// Password
function togglePassword() {
	var toggleIcon = event.target;
	var passwordInput = toggleIcon.parentElement.parentElement.querySelector('input');
	console.log(passwordInput)
	if (passwordInput.type === "password") {
		passwordInput.type = "text";
		toggleIcon.classList.remove('ri-lock-2-fill');
		toggleIcon.classList.add('ri-lock-unlock-fill');
	} else {
		passwordInput.type = "password";
		toggleIcon.classList.remove('ri-lock-unlock-fill');
		toggleIcon.classList.add('ri-lock-2-fill');
	}
}

var btn_submit = document.getElementById('btn_submit');
var inputs = document.getElementsByClassName('login__input');
var pwd_input = document.getElementById('pwd');
var confirm_pwd_input = document.getElementById('confirm_pwd');
var confirm_passcode = document.getElementById('confirm_passcode');

btn_submit.addEventListener('click', handleButtonSubmit)

function handleButtonSubmit(event) {
	event.preventDefault();
	if (pwd_input) {
		let validPwdError = validatePwd(pwd_input);
		let validConfirmPwdError = validatePwd(confirm_pwd_input);
		let validPasscodeError = validatePasscode(confirm_passcode); 

		var isError = validConfirmPwdError || validPwdError || validPasscodeError;
		if (!isError) {
			let hiddenPass = document.querySelector("input[name='password']");
			hiddenPass.value = encryptDefault(pwd_input.value);
			console.log(hiddenPass.value);
			document.login_form.submit();
		}
	}
}

function validatePasscode (element) {
	var elementValue = element.value;
	var errorElemnt = element.parentElement.parentElement.querySelector('.error');
	var isError = false;
	var pattern = /^\d{6}$/;
	if (elementValue === "") {
		isError = true;
		errorElemnt.innerHTML = "* Vui lòng nhập mã xác nhận"
	} else if (!elementValue.match(pattern)) {
		isError = true;
		errorElemnt.innerHTML = "* Mã xác nhận không đúng"
	} else {
		errorElemnt.innerHTML = ""
	}

	return isError;
}

function validatePwd(element) {
	var elementValue = element.value;
	var errorElemnt = element.parentElement.parentElement.querySelector('.error');
	var isError = false;
	var pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$/;
	if (elementValue === "") {
		isError = true;
		errorElemnt.innerHTML = "* Vui lòng nhập mật khẩu"
	} else if (!elementValue.match(pattern)) {
		isError = true;
		errorElemnt.innerHTML = "* Mật khẩu không đúng định dạng"
	} else {
		errorElemnt.innerHTML = ""
	}

	return isError;
}