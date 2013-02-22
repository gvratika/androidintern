<?php

/*
 * Following code will check for blocked user and device, will also check if a device is 
   already registered
 */
// include db connect class
require_once __DIR__ . '/db_connect.php';
// connecting to db
$db = new DB_CONNECT();
// array for JSON response
$response = array();
$tag = $_GET["tag"];
$id = $_GET["id"];
$response["success"] = 0;

if ($tag == "user") {
        // Request type is check user
        // check for user
		$result = mysql_query("SELECT blocked from users WHERE email = '$id'");
        $result=mysql_fetch_array($result) ;
		$response["blocked"] = $result[0]; 
    }
else if ($tag == "reg") {
        // Request type is check user
        // check for user
		$result = mysql_query("SELECT email from users WHERE android_id = '$id'");
		$no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // device already registered
			$response["blocked"] = "true";
			$result=mysql_fetch_array($result) ;
			$response["email"] = $result[0];
        } else {
            // device not registered 
			$response["blocked"] = "false";			
        } 
    }	
else  {
       // Request type is check device
		$result = mysql_query("SELECT android_id from block_device WHERE android_id = '$id'");
		$no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // device blocked
			$response["blocked"] = "true";
        } else {
            // device not blocked
			$response["blocked"] = "false";			
        }
	}
echo json_encode($response);
?>
