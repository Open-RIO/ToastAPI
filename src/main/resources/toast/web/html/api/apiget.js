function getPower(id, callback, object) {
    try {
        var client = new XMLHttpRequest();
        client.open('GET', '../api/power.html?id=' + id);
        client.onreadystatechange = function () {
            if (client.readyState == 4 && client.status == 200) {
                callback(client.responseText, object);
            }
        }
        client.send();
    } catch (err) {}
}

function getIO(id, callback, object) {
    try {
        var client = new XMLHttpRequest();
        client.open('GET', '../api/io.html?id=' + id);
        client.onreadystatechange = function () {
            if (client.readyState == 4 && client.status == 200) {
                callback(client.responseText, object);
            }
        }
        client.send();
    } catch (err) {}
}