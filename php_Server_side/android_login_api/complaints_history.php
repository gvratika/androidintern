<?php

/*
 * Following code will list all the complaints
 */

// array for JSON response
$response = array();

$searchBy = $_GET['searchBy'];
$keyword = $_GET['keyword'];
$user = $_GET['user'] ;
// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// get all complaints submitted by user from complaints table
	
$result = mysql_query("SELECT pid, $searchBy FROM complaints WHERE user = '$user' AND $searchBy LIKE '%$keyword%' ORDER BY created_at DESC") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // complaints node
    $response["complaints"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $complaint = array();
        $complaint["pid"] = $row["pid"];
        $complaint["name"] = $row[$searchBy];
        // push single complaint into final response array
        array_push($response["complaints"], $complaint);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no complaints found
    $response["success"] = 0;
    $response["message"] = "No complaints found";

    // echo no users JSON
    echo json_encode($response);
}
?>
