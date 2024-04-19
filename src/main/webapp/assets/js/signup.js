// Password
function toggleReenterPassword() {
	var toggleIcon = event.target;
	var passwordInput = toggleIcon.parentElement.parentElement.querySelector('input');
	if (passwordInput.type === "password") {
		passwordInput.type = "text";
		toggleIcon.classList.remove('ri-key-fill');
		toggleIcon.classList.add('ri-key-2-fill');
	} else {
		passwordInput.type = "password";
		toggleIcon.classList.remove('ri-key-2-fill');
		toggleIcon.classList.add('ri-key-fill');
	}
}

var confirm_pwd_input = document.getElementById('confirm_pwd');
var phone_number_input = document.getElementById('phone_number');
var last_name_input = document.getElementById('last_name');
var first_name_input = document.getElementById('first_name');
var birthday = document.getElementById('birthday');
var confirm_pwd_error = document.getElementById('confirm_pwd_error');
var phone_number_error = document.getElementById('phone_number_error');
var format_pwd_error = document.getElementById('format_pwd_error');
var last_name_error = document.getElementById('last_name_error');
var first_name_error = document.getElementById('first_name_error');
var birthday_error = document.getElementById('birthday_error');
var gmail_input = document.getElementById('gmail');
var gmail_error = document.getElementById('gmail_error');

// handle input birthday
document.getElementById('birthday').addEventListener('focus', function() {
	this.setAttribute('type', 'date');
	this.removeAttribute('placeholder');
});

// remove action blur
birthday.removeEventListener("blur", handleInputBlur);
// Submit form
btn_submit.addEventListener("click", handleSubmitSignupForm);
btn_submit.removeEventListener("click", handleButtonSubmit);

function handleSubmitSignupForm(event) {
	event.preventDefault();
	var validEmailError = validateEmail();
	var validPwdError = validatePassword();
	var validPhoneNumberError = validatePhoneNumber();
	var validConfirmPwdError = validateConfirmPwd();
	var validFirstName = validateFirstName();
	var validLastName = validateLastName();
	var validBirthday = validateDate();
	var validGmailError = validateGmail();

	var isError = validEmailError || validPwdError || validPhoneNumberError || validConfirmPwdError
		|| validFirstName || validLastName || validBirthday || validGmailError;
	if (!isError) {
		let hiddenPass = document.querySelector("input[name='password']");
		hiddenPass.value = encryptDefault(pwd_input.value);
		confirm_pwd_input.value = '';
		document.signup_form.submit();
	}
}

function validateGmail() {
    let email = gmail_input.value.trim();
    let pattern = /^[\w.+\-]+@gmail\.com$/; 
    let isError = false;

    if (email === "") {
        isError = true;
        gmail_error.innerHTML = "* Vui lòng nhập gmail"
    } else if (!email.match(pattern)) {
        isError = true;
        gmail_error.innerHTML = "* Vui lòng nhập đúng gmail"
    } else {
        gmail_error.innerHTML = ""
    }

    return isError;
}

function validateFirstName() {
	var firstName = first_name_input.value.trim();
	var isError = false;
	var pattern = /^[\p{L}\s']+$/u;

	if (firstName === "" || !firstName.match(pattern)) {
		isError = true;
		first_name_error.innerHTML = "* Vui lòng nhập đúng tên"
	} else {
		first_name_error.innerHTML = ""
	}
	return isError;
}

function validateLastName() {
	var lastName = last_name_input.value.trim();
	var isError = false;
	var pattern = /^[\p{L}\s']+$/u;

	if (lastName === "" || !lastName.match(pattern)) {
		isError = true;
		last_name_error.innerHTML = "* Vui lòng nhập đúng họ"
	} else {
		last_name_error.innerHTML = ""
	}
	return isError;
}

function validatePassword() {
	var pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$/;
	var pwd = pwd_input.value;

	var isError = false
	if (pwd === "") {
        isError = true;
        password_error.innerHTML = "* Vui lòng nhập mật khẩu"
    } else if (!pwd.match(pattern)) {
		isError = true;
		format_pwd_error.innerHTML = "* Mật khẩu phải có ít nhất 8 ký tự."
			+ " Gồm ít nhất <br> 1 chữ hoa, 1 chữ thường và 1 chữ số"
	} else {
		format_pwd_error.innerHTML = ""
	}
	return isError;
}

function validatePhoneNumber() {
	var phone_number = phone_number_input.value.trim();
	var pattern = /^(84|0[3|5|7|8|9])+([0-9]{8})$/;
	var isError = false;

	if (!phone_number.match(pattern)) {
		isError = true;
		phone_number_error.innerHTML = "* Vui lòng nhập đúng sđt"
	} else {
		phone_number_error.innerHTML = ""
	}

	return isError;
}

function validateDate() {
	var isError = false;
	var date = birthday.value;
	var dateRegex = /^\d{4}-\d{2}-\d{2}$/; // Định dạng dd/mm/yyyy
	if (date !== "" && !dateRegex.test(date)) {
		isError = true;
		birthday_error.innerHTML = "* Vui lòng nhập ngày sinh"
	} else {
		birthday_error.innerHTML = ""
	}
	return isError; // Đúng định dạng và hợp lệ
}

function validateConfirmPwd() {
	var confirm_pwd = confirm_pwd_input.value;
	var pwd = pwd_input.value;
	var isError = false;

	if (pwd != confirm_pwd) {
		isError = true;
		confirm_pwd_error.innerHTML = "* Mật khẩu không khớp"
	} else {
		confirm_pwd_error.innerHTML = ""
	}

	return isError;
}
