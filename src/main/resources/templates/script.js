function postData(form, id) {
    var formData = new FormData(form);

    if (id == "registerForm") {
        postJsonObj('http://localhost:8080/api_auth/register', Object.fromEntries(formData));
    } else {
        postJsonObj('http://localhost:8080/api_auth/login', formData, false);
    }

    return Object.fromEntries(formData);
}

document.getElementById("registerForm").addEventListener("submit", function (e) {
    e.preventDefault();
    postData(e.target, "registerForm");
});

document.getElementById("loginForm").addEventListener("submit", function (e) {
    e.preventDefault();
    postData(e.target, "loginForm");
});

function postJsonObj(uri, obj, json) {
    if (json === undefined) {
        json = true;
    }

    var xhr = new XMLHttpRequest();
    xhr.open("POST", uri, true);
    xhr.setRequestHeader('Access-Control-Allow-Origin', '*');
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log(JSON.parse(xhr.responseText))
            document.getElementById("response").innerHTML = document.getElementById("response").innerHTML + '<pre>'+JSON.stringify(JSON.parse(xhr.responseText), undefined, '\t')+'<pre>'
        }
    };
    var data = obj;
    if (json === true) {
        xhr.setRequestHeader("Content-Type", "application/json");
        data = JSON.stringify(obj);
    }

    xhr.send(data);
}