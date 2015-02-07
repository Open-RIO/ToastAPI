function getPower(id, textBase, callback, object) {
    try {
        var client = new XMLHttpRequest();
        client.open('GET', '../api/power.html?id=' + id);
        client.onreadystatechange = function () {
            if (client.readyState == 4 && client.status == 200) {
                callback(client.responseText, textBase, object);
            }
        }
        client.send();
    } catch (err) {
        callback("0.0:D/C", textBase, object);
    }
}