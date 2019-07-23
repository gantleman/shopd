/**
 * Created by 文辉 on 2017/7/25.
 */
$(document).ready(function () {
    $("#confirm-orders").click(function () {
        // alert("safd");
        var selectAddr =  $('input:radio[name="addressid"]:checked').val();
        if(selectAddr == null) {
            swal("Please add the address first.");
            return;
        }
        var isPay = $('#pay-select').val();
        var oldPrice = $('#total-old').text();
        var newPrice = $('#total-new').text();
        $.ajax({
            url: "/orderFinish",
            type: "POST",
            data: {
                oldPrice: oldPrice,
                newPrice: newPrice,
                isPay: isPay,
                addressid: selectAddr
            },
            success: function () {
                swal("Successful Purchase", "", "success");
                location.href = "/info/list"
            },
            error: function () {
                swal("Buy failed, unable to connect to server!");
            }
        });
    });
});