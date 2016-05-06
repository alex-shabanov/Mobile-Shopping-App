<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>User Log In</title>
		<script src='https://www.google.com/recaptcha/api.js'></script>
	</head>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles3.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
	<body>
		<c:choose>
			<c:when test="${it.user == 'emptyusernamepassword'}">
				<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> User name and password cannot be blank.
  				</div>
			</c:when>
			<c:when test="${it.user == 'emptyusername'}">
				<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> User name is a required field.
  				</div>
			</c:when>
			<c:when test="${it.user == 'emptypassword'}">
				<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> Password is a required field.
  				</div>
			</c:when>
    		<c:when test="${it.user == 'yesregistration'}">
    			<div id="alertRegistrationSuccess" class="alert alert-success">
      				<strong>Success!</strong> Registration successful.
    			</div>
    		</c:when>
    		<c:when test="${it.user == 'mustregister'}">
    			<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Warning!</strong> Invalid user name. Need to register first.
  				</div>
    		</c:when>
    		<c:when test="${it.user == 'invalidpswd'}">
    			<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Warning!</strong> Invalid password.
  				</div>
    		</c:when>
    		<c:when test="${it.user == 'mustsignin'}">
    			<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> This page requires to sign in first.
  				</div>
    		</c:when>
    		<c:when test="${it.user == 'alreadyloggedin'}">
    			<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> User already logged in.
  				</div>
    		</c:when>
    		<c:when test="${it.user == 'sessiontimeout'}">
    			<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> Session has expired.
  				</div>
    		</c:when>
    		<c:when test="${it.user == 'invalidrecaptcha'}">
    			<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> reCAPTCHA: Must verify you're not a robot.
  				</div>
    		</c:when>
    		<c:otherwise>
    			<div id="alertRegistrationInfo" class="alert alert-info">
  					<strong>Info!</strong> This page requires to sign in first.
  				</div>
   			</c:otherwise>
		</c:choose>
		<h1 id="loginTitlePageText">Welcome to Shopping Point</h1>
		<form name="loginForm" action="/ShoppingPointServer/webapi/login" method="POST">
				<table border="0">
					<tr>
						<td><p id="loginUsernameText">Username</p></td>
						<td><input type="text" name="username" placeholder="Enter username" value="" id="username" size="50" /></td>
					</tr>
					<tr>
						<td><p id="loginPasswordText">Password</p></td>
						<td><input type="password" name="password" placeholder="Enter password" value="" id="password" size="50"  /></td>
					</tr>
				</table>
				<input type="submit" value="Sign In" id="loginButton" name="submit"/>
				<input type="submit" value="Register" id="registerButton" name="submit" formaction="/ShoppingPointServer/webapi/registration/adminregistration" formmethod="POST" />
				<div id="recaptchaVerification" class="g-recaptcha" data-sitekey="6Ld_Lx4TAAAAAP8Z93hj2rM2ype0elueMLQzZ9Xd"></div>
		</form>
	</body>
</html>
