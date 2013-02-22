<?php

/*
 * Following code will create a new complaint row
 * All complaint details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['name']) && isset($_POST['issue']) && isset($_POST['description'])&& isset($_POST['user'])) {
    
    $name = $_POST['name'];
    $issue = $_POST['issue'];
    $description = $_POST['description'];
	$image = $_POST['image'];
    $video = $_POST['video'];
	$user = $_POST['user'];
	$permission = "false" ;
    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO complaints(user, name, issue, description, photo, video, permission, created_at) VALUES ('$user','$name','$issue','$description','$image','$video','$permission',NOW())");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
		$id = mysql_insert_id(); // last inserted id
        $response["success"] = 1;
        $response["message"] = $id;

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>