const button_list_users = document.querySelectorAll('.button_list_users');
for (var i = 0; i < button_list_users.length; i++) {
	button_list_users[i].addEventListener('click', function(event) {
		var container = event.target.parentElement.parentElement.querySelector(".list_users");
		container.style.display = (container.style.display === "block") ? "none" : "block";

		document.addEventListener("click", function(sub_event) {
			if (!event.target.contains(sub_event.target) && !container.contains(sub_event.target)) {
				container.style.display = "none";
			}
		});
	});
}
window.addEventListener('DOMContentLoaded', function() {
	event.preventDefault();
	var urlParams = new URLSearchParams(window.location.search);
	var encodedUrl = urlParams.get('encodedUrl');
	if (encodedUrl) {
		var decodedUrl = decodeURIComponent(encodedUrl);
		console.log("Decoded URL: " + decodedUrl);
		// Sử dụng decodedUrl cho mục đích của bạn
	}
});

$(document).ready(function() {
	handleContent();
	// Xử lý sự kiện khi nhấn vào thẻ a
	$("a[name='check-status']").click(function(event) {
		event.preventDefault(); // Ngăn chặn hành vi mặc định của thẻ a
		const action = document.getElementById('my_action').value;
		const new_action = $(this).attr("value");

		// Lấy giá trị của các checkbox đã được chọn
		var selectedValues = [];
		selectedValues.push($("input[name='mailID']").val());
		// Gửi yêu cầu Ajax đến controller servlet
		//alert(selectedValues);
		$.ajax({
			//url: "../mail/mail",
			url: "http://localhost:8080/mail/mail",
			method: "POST",
			data: {
				selectedValues: selectedValues,
				my_action: "",
				my_new_action: new_action
			},
			success: function(response) {
				// Xử lý phản hồi từ server (nếu cần)
				//location.reload();
				window.location.href = "http://localhost:8080/mail/mail?my_action=" + btoa("incoming-mail-box");
			},
			error: function(xhr, status, error) {
				// Xử lý lỗi (nếu có)
			}
		});
	});

});

/* Xu ly ma hoa tat ca thu mailbox */
function handleContent() {
	let mailTitle = document.getElementById('mail-title');
	let listTitle = document.querySelectorAll('.mail-title-div');
	let listContent = document.querySelectorAll('.mail_content');

	let decryptedTilte = decryptPrivate(mailTitle.innerHTML);
	mailTitle.innerHTML = decryptedTilte == "" ? "(Không có)" : decryptedTilte;
	mailTitle.classList.remove('invisible');

	listContent.forEach(contentDiv => {
		let decryptedContent = decryptPrivate(contentDiv.innerHTML);
		contentDiv.innerHTML = decryptedContent == "" ? "(Không có nội dung)" : decryptedContent;
		contentDiv.classList.remove('invisible');
	})
	
	listTitle.forEach(titleDiv => {
		let decryptedContent = decryptPrivate(titleDiv.innerHTML);
		titleDiv.innerHTML = decryptedContent == "" ? "(Không có nội dung)" : decryptedContent;
	})
}

var sidebarNew = document.getElementById('new-message'); // Sidebar thu den
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

function createNewEmail(newRootID, oldRootID) {
	console.log(newRootID);
	console.log(oldRootID);
	if (newRootID == oldRootID) {
		let alertDiv = document.createElement('div');
		alertDiv.innerHTML = 
    	'<div class="alert alert-success" role="alert">' +
    	'Bạn có tin nhắn mới. <a href="#" onclick="window.location.reload();" class="alert-link">Tải lại trang</a>.' +
    	'</div>';
		
		// Thêm thẻ div vào body của trang
		document.body.appendChild(alertDiv);
	}
}
