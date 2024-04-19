/* Giai ma key & iv trong session */
var initSessionStorage = function(privateKey, privateIV) {
	var defaultKey = localStorage.getItem('defaultKey');
	var defaultIV = localStorage.getItem('defaultIV');
	console.log(defaultKey, ', ', defaultIV);
	privateKey = decrypteFunction(privateKey, defaultKey, defaultIV);
	privateIV = decrypteFunction(privateIV, defaultKey, defaultIV);

	localStorage.setItem('private_key', privateKey);
	localStorage.setItem('private_iv', privateIV);
	console.log('This is the first visit.');
	console.log(privateKey + ", " + privateIV);
}

/* Xu ly ma hoa tat ca thu mailbox */
function handleMailBox() {
	let listTitle = document.querySelectorAll('.email_item_title');
	let listContent = document.querySelectorAll('.email_item_content');

	listTitle.forEach(titleDiv => {
		let decryptedTilte = decryptPrivate(titleDiv.innerHTML);
		titleDiv.innerHTML = decryptedTilte == "" ? "(Không có tiêu đề)" : decryptedTilte;
		titleDiv.classList.remove('invisible');
	});

	listContent.forEach(contentDiv => {
		let decryptedContent = decryptPrivate(contentDiv.innerHTML);
		contentDiv.innerHTML = decryptedContent == "" ? "(Không có nội dung)" : decryptedContent;
		contentDiv.classList.remove('invisible');
	})
}

/* Xu ly co thu moi chuyen den */
var sidebarNew = document.getElementById('new-message'); // Sidebar thu den
// Hàm gửi yêu cầu AJAX để cập nhật session thông qua một endpoint của server
function addCountUnread() {
	let xhr = new XMLHttpRequest();
	xhr.open('GET', '/updateSessionData', true);
	xhr.setRequestHeader('Content-Type', 'application/json');

	xhr.onreadystatechange = function() {
		if (xhr.readyState === XMLHttpRequest.DONE) {
			if (xhr.status === 200) {
				// Xử lý phản hồi từ server nếu cần
				var responseData = JSON.parse(xhr.responseText);
				sidebarNew.innerHTML = '(' + responseData.countUnread + ')';
				console.log('Sidebar đã được cập nhật');
			} else {
				console.error('Lỗi khi cập nhật sidebar');
			}
		}
	};

	xhr.send();
}

// Tạo một khung email mới
function createNewEmail(newMail) {
	// Xoa phan tu cu
	const oldElement = document.getElementById("mail-" + newMail.rootID);
	if (oldElement) {
		oldElement.remove();
	} else {
		addCountUnread();
		const totalBox = document.getElementById("totalBox");
		const totalMail = document.getElementById("totalInbox");
		totalBox.innerHTML = Number(totalBox.innerHTML) + 1;
		totalMail.innerHTML = Number(totalMail.innerHTML) + 1;
	}

	// Giai ma content va subject
	let contentMail = decryptPrivate(newMail.content);
	let subjectMail = decryptPrivate(newMail.subject);

	let urlDetail = newMail.mailID;
	let encodedEmail = btoa(urlDetail);
	let encodedAction = btoa('detail');
	let url = '/mail/mail?my_action=' + encodedAction + '&mailId=' + encodedEmail;

	// Tạo phần tử div để chứa khung email
	const newEmailDiv = document.createElement('a');
	newEmailDiv.setAttribute('href', url);
	newEmailDiv.setAttribute('class', 'email_item active');
	newEmailDiv.setAttribute('id', 'mail-' + newMail.rootID);

	// Tạo checkbox
	var checkbox = document.createElement("input");
	checkbox.type = "checkbox";
	checkbox.classList.add("content_header-item");
	checkbox.name = 'checkBox';
	// Set giá trị cho checkbox (tùy thuộc vào logic của bạn)
	checkbox.value = newMail.mailID;

	// Tạo các phần tử span và div khác cho thông tin email
	var emailName = document.createElement("div");
	emailName.classList.add("email_item_name");
	emailName.textContent = newMail.sendName;

	var emailTitle = document.createElement("span");
	emailTitle.classList.add("email_item_title");
	const title = subjectMail !== undefined ? subjectMail : '(Không có nội dung)';
	emailTitle.textContent = title;

	var emailContent = document.createElement("span");
	emailContent.classList.add("email_item_content");
	const content = contentMail !== undefined ? contentMail : '(Không có nội dung)';
	emailContent.textContent = content;

	var emailTime = document.createElement("span");
	emailTime.classList.add("email_item_time");
	emailTime.textContent = newMail.time;

	// Thêm các phần tử vào div chứa email mới
	newEmailDiv.appendChild(checkbox);
	newEmailDiv.appendChild(emailName);
	newEmailDiv.appendChild(emailTitle);
	newEmailDiv.appendChild(emailContent);
	newEmailDiv.appendChild(emailTime);

	var emailContainer = document.getElementById("email-container");
	if (emailContainer) {
		// Lấy phần tử con đầu tiên của phần tử cha
		const no_mail_div = document.querySelector('.no-mail');
		if (no_mail_div) {
			no_mail_div.remove();
		} else {
			const lastChild = emailContainer.lastChild;
			lastChild.remove();
		}
		// Lấy phần tử emailContainer và chèn khung email mới vào đó
		const firstChild = emailContainer.firstChild;

		// Chèn phần tử mới vào trước phần tử con đầu tiên (đưa vào đầu)
		emailContainer.insertBefore(newEmailDiv, firstChild);

	}
}

/* Xu ly cac su kien thay doi trang thai */
const checkAllCheckbox = document.getElementById('checked_all');
const checkboxes = document.querySelectorAll('input[type="checkbox"]');
// Lắng nghe sự kiện khi checkbox "checkAll" được thay đổi
checkAllCheckbox.addEventListener('change', () => {
	// Lặp qua tất cả các checkbox và đặt trạng thái checked của chúng bằng với trạng thái của checkbox "checkAll"
	checkboxes.forEach(checkbox => {
		checkbox.checked = checkAllCheckbox.checked;
	});
});

// Lắng nghe sự kiện khi checkbox được chọn hoặc bỏ chọn
checkboxes.forEach(checkbox => {
	checkbox.addEventListener('change', () => {
		// Kiểm tra xem có checkbox nào được chọn hay không
		const anyChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
		const action = document.getElementsByName('my_action')[1].value;
		// Lấy biểu tượng
		const checkIcon = document.getElementsByName('check-status');
		const deleteIcon = document.getElementById('delete-icon');
		const spamIcon = document.getElementById('spam-icon');
		//deleteIcon.forEach(icon => icon.style.display = 'block');
		// Hiển thị hoặc ẩn biểu tượng xóa tùy thuộc vào trạng thái của checkbox
		if (anyChecked) {
			//deleteIcon.style.display = 'block';
			checkIcon.forEach(icon => icon.style.display = 'block');
			if (action == 'deleted-mail-box') {
				deleteIcon.style.display = 'none';
			}
			else if (action == 'spam-mail-box') {
				spamIcon.style.display = 'none';
			}
		} else {
			//deleteIcon.style.display = 'none';
			checkIcon.forEach(icon => icon.style.display = 'none');
		}
	});
});


$(document).ready(function() {
	// Xử lý sự kiện khi nhấn vào thẻ a
	$("a[name='check-status']").click(function(event) {
		event.preventDefault(); // Ngăn chặn hành vi mặc định của thẻ a
		const action = document.getElementsByName('my_action')[1].value;
		const new_action = $(this).attr("value");

		// Lấy giá trị của các checkbox đã được chọn
		var selectedValues = [];
		$("input[name='checkBox']:checked").each(function() {
			selectedValues.push($(this).val());
		});
		// Gửi yêu cầu Ajax đến controller servlet
		$.ajax({
			//url: "../mail/mail",
			url: "http://meowmail.vn/mail/mail",
			method: "POST",
			data: {
				selectedValues: selectedValues,
				my_action: action,
				my_new_action: new_action
			},
			success: function(response) {
				// Xử lý phản hồi từ server (nếu cần)
				location.reload();
			},
			error: function(xhr, status, error) {
				// Xử lý lỗi (nếu có)
			}
		});
	});
});

function changePageLink(currentPage, event){
		const url = window.location.search;
		const urlParam = new URLSearchParams(url);
		const my_action = urlParam.get('my_action');
		const input = urlParam.get('search_input');
		const startDate = urlParam.get('startDate');
		const endDate = urlParam.get('endDate');
		const seach_type = urlParam.get('search_type');
		
		var newurl = "/mail/mail?my_action=" + my_action;
		if(input!= null && input != "") 
			newurl += "&search_input=" + input;
		if(startDate!= null && startDate != "")
			newurl += "&startDate=" + startDate;
		if(endDate!= null && endDate != "")
			newurl += "&endDate=" + endDate;
		if(seach_type!= null && seach_type != "")
			newurl += "&search_type=" + seach_type;
		
		newurl += "&currentPage=" + currentPage;
		event.currentTarget.setAttribute('href', newurl);
		
}


