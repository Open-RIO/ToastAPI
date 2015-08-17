$.command("eval", function() {
    var fcmd = [].slice.call(arguments).join(" ");
    eval(fcmd);
});