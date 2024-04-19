// Xử lý sidebar
$(document).ready(function () {
    $('.side_bar__item').on('click', function () {
        // Xóa lớp 'active' khỏi tất cả các thẻ li trong side bar
        $('.side_bar__item').removeClass('active');

        // Thêm lớp 'active' vào thẻ li được click
        $(this).addClass('active');
    });
});