var btn_submit = document.getElementById('btn_submit');
var email_input = document.getElementById('email');
var phone_number_input = document.getElementById('phone_number');
var fullname_input = document.getElementById('fullname');
var birthday = document.getElementById('birthday');
var error_message = document.getElementById('error_message');


function handleButtonSubmit(event) {
	event.preventDefault();
	var validFullnameError = validateFullName();
	var validEmailError = validateEmail();
	var validPhoneNumberError = validatePhoneNumber();
	var validBirthdayError = validateDate();

	var isError = validEmailError || validPhoneNumberError || validFullnameError || validBirthdayError;
	if (!isError) {
		document.update_account.submit();
	} else if (error_message.innerHTML === "") {
		error_message.innerHTML = "Vui lòng nhập đúng định dạng email!";
	}
}

function validateFullName() {
	var fullname = fullname_input.value.trim();
	var pattern = /^[\p{L}\s']+$/u;
	var isError = false;

	if (fullname === "") {
		isError = true;
		error_message.innerHTML = "Vui lòng nhập họ tên!";
	} else if (!fullname.match(pattern)) {
		isError = true;
		error_message.innerHTML = "Vui lòng nhập họ tên đúng định dạng!";
	} else {
		error_message.innerHTML = "";
	}
	return isError;
}

function validateEmail() {
	var email = email_input.value.trim();
	var pattern = /^\w+@meowmail.vn$/;
	var isError = false;

	if (email === "") {
		isError = true;
		error_message.innerHTML = "Vui lòng nhập email!";
	} else if (!email.match(pattern)) {
		isError = true;
		if (error_message.innerHTML === "") error_message.innerHTML = "Vui lòng nhập đúng định dạng email!";
	} 
	return isError;
}

function validatePhoneNumber() {
	var phone_number = phone_number_input.value.trim();
	//var pattern = /^(84) 8(3|5|7|8|9)\d{8}$/;
	var pattern = /^\(84\) (3|5|7|8|9)\d{1}-\d{3}-\d{4}$/;
	var isError = false;
	if (!phone_number.match(pattern)) {
		isError = true;
		if (error_message.innerHTML === "") error_message.innerHTML = "Vui lòng nhập số điện thoại đúng định dạng!";
	} else {
		phone_number_input.value = parsePhoneNumber(phone_number);
	}
	return isError;
}

function validateDate() {
	var isError = false;
	var date = birthday.value;
	console.log("ngaysinh:" + date);
	var dateRegex = /^\d{4}-\d{2}-\d{2}$/; // Định dạng dd/mm/yyyy
	if (date !== "" && !dateRegex.test(date)) {
		isError = true;
		if (error_message.innerHTML === "") error_message.innerHTML = "Vui lòng nhập ngày sinh đúng định dạng!";
	}
	return isError;
}

// handle input birthday
document.getElementById('birthday').addEventListener('focus', function() {
	this.setAttribute('type', 'date');
	this.removeAttribute('placeholder');
});

// Handle phone_number_input
function parsePhoneNumber(phoneNumber) {
	const parseNumber = phoneNumber.replace(/\(84\)/, '0').replace(/[- ]/g, ''); // /g - replaceAll
	return parseNumber;
}

function forrmatPhoneNumber(phoneNumber) {
	const phoneRegex = /^0\d{9}$/;
	if (!phoneNumber.match(phoneRegex)) {
		return "";
	}
	
	const formattedNumber = phoneNumber.replace(/^0/, '84').replace(/(\d{2})(\d{2})(\d{3})(\d{4})/, '($1) $2-$3-$4');
	return formattedNumber;
}

const birthdayValue = phone_number_input.value;
phone_number_input.value = forrmatPhoneNumber(birthdayValue);

