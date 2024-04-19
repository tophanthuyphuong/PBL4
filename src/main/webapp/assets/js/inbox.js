const submit_btn = document.getElementById("submit_btn");
submit_btn.addEventListener("click", validateForm);

function validateForm(event) {
	event.preventDefault();

	let to = document.getElementById("to").value;
	let cc = document.getElementById("cc").value;
	let bcc = document.getElementById("bcc").value;
	let subject = document.getElementById("subject").value;
	let content = document.getElementById("content").value;

	// Kiểm tra điều kiện 1: Phải nhập ít nhất 1 trong 3 trường to, cc, bcc
	if (to === "" && cc === "" && bcc === "") {
		alert("Vui lòng nhập ít nhất một người nhận.");
		return;
	}

	// Kiểm tra điều kiện 2: Phải nhập content hoặc subject
	if (content.trim() === "" && subject.trim() === "") {
		var confirmResult = confirm("Bạn đang gửi thư mà không có tiêu đề và nội dung. Bạn có muốn tiếp tục không?");
		if (!confirmResult) {
			return;
		}
	}

	// Kiểm tra điều kiện 3: Kiểm tra định dạng email
	var emailRegex = /^\w+@meowmail.vn$/;
	var emails = to.split(/\s+/).concat(cc.split(/\s+/), bcc.split(/\s+/));
	console.log(emails);
	for (var i = 0; i < emails.length; i++) {
		if (!emailRegex.test(emails[i]) && emails[i] !== '') {
			alert("Vui lòng nhập định dạng email hợp lệ.");
			return;
		}
	}
	
	let encrypted_subject = document.getElementById('encrypted_subject');
	let encrypted_content = document.getElementById('encrypted_content');
	encrypted_subject.value = encryptPrivate(subject);
	encrypted_content.value = encryptPrivate(content);
	console.log(encrypted_subject.value);
	console.log(encrypted_content.value);
	document.forms["new_email_form"].submit();
}