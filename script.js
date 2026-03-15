document.getElementById("touristForm").addEventListener("submit", function(event){

event.preventDefault();

let name = document.getElementById("name").value;
let email = document.getElementById("email").value;
let location = document.getElementById("location").value;

document.getElementById("message").innerText =
"Tourist Registered Successfully!";

});

function sendAlert(){

document.getElementById("message").innerText =
"Emergency Alert Sent to Authorities!";
}