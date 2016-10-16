<?php
function println($string)
 {
  print($string . PHP_EOL);
 }
 
 
function insert($value)
 {
  $uservername = 'localhost';
  $username    = 'mauri879_admin' ;
  $password    = 'mauri879_admin';
  $dbname      = 'mauri879_hackathon';

  global $message;
  $message .= 'inserting ' . $value;
  try 
   {
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $sql = "INSERT INTO data (value)
    VALUES ('" . $value . "')";
    
    $conn->exec($sql);
   }
  catch(PDOException $e)
    {
     $message .= ', ' . $e->getMessage();
     //echo $sql . "<br>" . $e->getMessage();
    }

  $conn = null;
 }
 
 
$message = ''; 
 
$raw = file_get_contents('php://input');
$message .= strlen($raw) . ',' . $raw;
$data = json_decode($raw); 
insert($data["value"]); 
 
println('{');
println(' result    : OK');
println(' timestamp : ' . time());
println(' message   : \'' . $message . '\'');
println('}');




?>