<?php

/*
 * Following code will get single complaint details
 * A complaint is identified by complaint id (pid)
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// check for post data
if (isset($_GET["pid"])) {
    $pid = $_GET['pid'];

    // get a complaint from complaints table
    $result = mysql_query("SELECT *FROM complaints WHERE pid = $pid");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {

            $result = mysql_fetch_array($result);

			$complaint = array();
			$complaint["pid"] = $result["pid"];
			$complaint["name"] = $result["name"];
			$complaint["photo"] = $result["photo"];
			$complaint["issue"] = $result["issue"];
			$complaint["description"] = $result["description"];
			$complaint["created_at"] = $result["created_at"];
			$complaint["video"] = $result["video"];
            // success
            $response["success"] = 1;

            // user node
            $response["complaint"] = array();

            array_push($response["complaint"], $complaint);

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no complaint found
            $response["success"] = 0;
            $response["message"] = "No complaint found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no complaint found
        $response["success"] = 0;
        $response["message"] = "No complaint found";

        // echo no users JSON
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