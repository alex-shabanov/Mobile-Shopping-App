<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Registration Form</title>
		<script src='https://www.google.com/recaptcha/api.js'></script>
	</head>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles2.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
	<body>
  		<c:choose>
    		<c:when test="${it.user == 'noregistration'}">
    			<div id="alertInfo" class="alert alert-info">
  					<strong>Info!</strong>  Invalid Employee Id.
  				</div>
    		</c:when>
    		<c:when test="${it.user == 'accountexists'}">
    			<div id="alertInfo" class="alert alert-info">
  					<strong>Info!</strong>  Administrative account already exists.
  				</div>
    		</c:when>
    		<c:when test="${it.user == 'anotheraccount'}">
    			<div id="alertInfo" class="alert alert-info">
  					<strong>Info!</strong>  Administrative account belong to another user.
  				</div>
    		</c:when>
    		<c:when test="${it.user == 'invalidrecaptcha'}">
    			<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> reCAPTCHA: Must verify you're not a robot.
  				</div>
    		</c:when>
    		<c:otherwise>
    			<h1 id="registrationFormText">Administrative Registration Form</h1>
   			</c:otherwise>
		</c:choose>
		<form id="registrationForm" name="registrationForm" action="/ShoppingPointServer/webapi/registration">
					<table border="0">
						<tr>
							<td><p id="firstNameText">First Name</p></td>
							<td><input type="text" placeholder="Enter First Name" value="" id="firstname" size="50" name="firstname" required/></td>
						</tr>
						<tr>
							<td><p id="lastNameText">Last Name</p></td>
							<td><input type="text" name="lastname" placeholder="Enter Last Name" value="" id="lastname" size="50" required/></td>
						</tr>
						<tr>
							<td><p id="employeeIDText">Employee Id</p></td>
							<td><input type="text" name="employeeId" placeholder="Enter Employee Id" value="" id="employeeId" size="50" required/></td>
						</tr>
						<tr>
						<td><p id="usernameText">Username</p></td>
							<td><input type="text" name="username" placeholder="Enter Username" value="" id="username" size="50" required /></td>
						</tr>
						<tr>
							<td><p id="passwordText">Password</p></td>
							<td><input type="password" name="password" placeholder="Enter Password" value="" id="password" size="50" required/></td>
						</tr>
					</table>
					<input type="reset" value="Clear" id="clearButton" name="clear"/>
					<input type="submit" value="Submit" id="registerButton" name="submit" formaction="/ShoppingPointServer/webapi/registration" formmethod="POST"/>
					<div id="recaptchaVerification" class="g-recaptcha" data-sitekey="6Ld_Lx4TAAAAAP8Z93hj2rM2ype0elueMLQzZ9Xd"></div>
			</form>
	</body>
</html>