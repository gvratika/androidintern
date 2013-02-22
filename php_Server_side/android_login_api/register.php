<?php

/**
 * File to handle all API requests
 * Accepts GET and POST
 * 
 * Each request will be identified by TAG
 * Response will be JSON data

  /**
 * check for POST request 
 */
// include db connect class
require_once __DIR__ . '/db_connect.php';
// connecting to db
$db = new DB_CONNECT();
// array for JSON response
$response = array();

$firstName = $_GET['firstName'];
$lastName = $_GET['lastName'];
$email = $_GET['email'];
$age = $_GET['age'];
$city = $_GET['city'];
$state = $_GET['state'];
$phoneNo = $_GET['phoneNo'];
$occupation = $_GET['occupation'];
$educationLevel = $_GET['educationLevel'];
$gender = $_GET['gender'];
$android_id = $_GET['AndroidId'];

/**
* Check user is existed or not
*/
$result = mysql_query("SELECT email from users WHERE email = '$email'");
$no_of_rows = mysql_num_rows($result);
if ($no_of_rows > 0) {
	// user is already existed - error response
    $response["success"] = 0;
    $response["error_msg"] = "Already registered with this Email Id";	
} else {
    // user not existed register one
	$response["success"] = 1;
	$result = mysql_query("INSERT INTO users(email, firstName, lastName, 
	age, city, state, phoneNo, occupation, educationLevel, gender, 
	android_id, created_at, blocked) VALUES ('$email','$firstName','$lastName',
	'$age','$city','$state','$phoneNo','$occupation','$educationLevel','$gender',
	'$android_id',NOW(),'false')");
}
echo json_encode($response);
?>
