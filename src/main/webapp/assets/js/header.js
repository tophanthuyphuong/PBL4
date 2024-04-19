// Xử lý header
var startDateInput = document.getElementById("startDate");
var endDateInput = document.getElementById("endDate");

startDateInput.setAttribute("max", new Date().toISOString().split('T')[0]);
endDateInput.setAttribute("max", new Date().toISOString().split('T')[0]);

startDateInput.addEventListener("change", function () {
    var startDateValue = startDateInput.value;
    endDateInput.setAttribute("min", startDateValue);

    if (endDateInput.value && endDateInput.value < startDateValue) {
        endDateInput.value = startDateValue;
    }
});

endDateInput.addEventListener("change", function () {
    var endDateValue = endDateInput.value;

    if (endDateValue < startDateInput.value) {
        endDateInput.value = startDateInput.value;
    }
});

// JavaScript để xử lý sự kiện nút "Bộ lọc"
const filter_button = document.querySelector('.filter-button');
const filter_container = document.querySelector('.filter-container');

document.addEventListener("click", function (event) {
    if (!filter_button.contains(event.target) && !filter_container.contains(event.target)) {
        filter_container.style.display = "none";
    }
});

filter_button.addEventListener('click', function () {
    filter_container.style.display = (filter_container.style.display === "block") ? "none" : "block";
    filter_container.style.animation = 'slideIn 0.3s';
});

function toggleMenu() {
    var menu = document.getElementById('accountMenu');
    menu.classList.toggle('active');
}

function search(){
	const input = document.getElementById('search_input').value;
	const startDate = document.getElementById('startDate').value;
	const endDate = document.getElementById('endDate').value;
	const action = document.getElementById('my_action').value;
	const new_action = document.getElementById('my_new_action').value;
	$.ajax({
			//url: "../mail/mail",
			url: "http://localhost:8080/mail/mail",
			method: "POST",
			data: {
				search_input: input,
				startDate: startDate,
				endDate: endDate,
				my_action: action,
				my_new_action: new_action
			},
			success: function(response) {
				// Xử lý phản hồi từ server (nếu cần)
				//location.reload();
			},
			error: function(xhr, status, error) {
				// Xử lý lỗi (nếu có)
			}
		});
	
}

