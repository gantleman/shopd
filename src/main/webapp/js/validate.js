
$.validator.setDefaults({
	submitHandler: function() {
		form.submit();
	}
});
$(document).ready(function() {
	$('#form').validate({
		rules: {
			username: "required",
            telephone: {
				required: true,
			},
            email: {
				required: true,
				email: true,
			},
            password: {
				required: true,
				minlength: 8,
			},
			confirmPassword: {
				required: true,
				minlength: 8,
				equalTo: "#password",
			}
		},
		messages: {
            username: "User name cannot be empty",
            telephone: "Contact calls cannot be empty",
            email: {
				required: "Mailbox input cannot be empty",
				email: "Please enter a correct mailbox",
			},
            password: {
				required: "Password input cannot be empty",
				minlength: "Password length should not be less than 8 bits",
			},
			confirmPassword: {
				required: "Input cannot be empty",
				minlength: "Password length should not be less than 8 bits",
				equalTo: "Two inconsistent password input",
			}
		}
	});
});