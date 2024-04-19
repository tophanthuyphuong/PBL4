// Lưu Key & IV default khi người dùng vào trang đăng ký / đăng nhập

// Xóa vùng localStorage đệm
localStorage.clear();
localStorage.setItem('defaultKey', '6qHEkpDx0EYcuujeHbt3sw==');
localStorage.setItem('defaultIV', 'GirdtFgZudk8z0Lm/mOn3w==');
console.log('This is the first visit.');

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
var email_input = document.getElementById('email');
var pwd_input = document.getElementById('pwd');
var email_error = document.getElementById('email_error');
var password_error = document.getElementById('password_error');


for (var i = 0; i < inputs.length; i++) {
    inputs[i].addEventListener("blur", handleInputBlur)
}
if(btn_submit)
btn_submit.addEventListener('click', handleButtonSubmit)

function handleInputBlur (event) {
    var input_box = event.target.parentElement.parentElement
        var error_span = input_box.getElementsByClassName('error')[0];

        var value = event.target.value.trim();
        if (value === "") {
            error_span.innerHTML = "* Vui lòng nhập trường này"
        } else {
            error_span.innerHTML = ""
        }
}

function handleButtonSubmit (event) {
    event.preventDefault();
    var validEmailError = validateEmail();
    var validPwdError = validatePwd();

    var isError = validEmailError || validPwdError;
    if (!isError) {
        let hiddenPass = document.querySelector("input[name='password']");
		hiddenPass.value = encryptDefault(pwd_input.value);
        document.login_form.submit();
    }
}

function validateEmail() {
    var email = email_input.value.trim();
    var pattern = /^\w+@meowmail.vn$/; 
    var isError = false;

    if (email === "") {
        isError = true;
        email_error.innerHTML = "* Vui lòng nhập email"
    } else if (!email.match(pattern)) {
        isError = true;
        email_error.innerHTML = "* Vui lòng nhập đúng email"
    } else {
        email_error.innerHTML = ""
    }

    return isError;
}

function validatePwd () {
    var pwd = pwd_input.value;
    var isError = false;
	var pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$/;
    if (pwd === "") {
        isError = true;
        password_error.innerHTML = "* Vui lòng nhập mật khẩu"
    } else if (!pwd.match(pattern)) {
		isError = true;
		password_error.innerHTML = "* Mật khẩu không đúng"
	} else {
        password_error.innerHTML = ""
    }
    
    return isError;
}