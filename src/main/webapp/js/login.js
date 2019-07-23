
$.validator.setDefaults({
	submitHandler: function() {
		form.submit();
	}
});
$(document).ready(function() {
	$('#form2').validate({
		rules: {
			username: "required",
			
			password: {
				required: true,
			},
			confirmlogo: "required",
		},
		messages: {
			username: "User name input cannot be empty",
			
			password: {
				required: "Password input cannot be empty",
			},
			confirmlogo: "Validation code input cannot be empty",
		}
	});
});