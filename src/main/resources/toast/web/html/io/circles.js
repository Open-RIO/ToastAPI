var pwm = [];
var analog = [];

function createCircle(type, id, dur) {
    var thing = new ProgressBar.Circle(type + id, {
        color: 'rgba(255, 255, 255, 1)',
        fill: 'rgba(255, 255, 255, 0.05)',
        strokeWidth: 3,
        duration: dur,
        easing: 'easeInOut',
        text: {
            value: id.toString(),
            color: 'rgba(255, 255, 255, 1)'
        },

        step: function(state, thing) {
            thing.path.setAttribute('stroke', state.color)
        }
    });

    return thing;
}

function createPWM(id) {
    var thing = createCircle("#pwm", id, 500);
    pwm[id] = thing;
    return thing;
}

function createAnalog(id) {
    var thing = createCircle("#analog", id, 500);
    analog[id] = thing;
    return thing;
}

function update(object, value) {
    var color = '#FFFFFF';
    if (value < 0) {
        color = '#FF0000';
    } else if (value > 0) {
        color = '#00FF00';
    }

    var c = object.path.getAttribute('stroke');
    if (c == 'undefined')
        c = '#FFFFFF';

    object.animate(value, {
        from: {color: c},
        to: {color: color}
    });
}

for (var i = 0; i < 10; i++) {
    createPWM(i);
}

for (var i = 0; i < 4; i++) {
    createAnalog(i);
}

function loopUpdate() {
    setTimeout(function () {
        for (var i = 0; i < 4; i++) {
            var thing = analog[i];
            getIO('analog' + i, function(val, thing) {
                update(thing, val);
            }, thing);
        }

        for (var i = 0; i < 10; i++) {
            var thing = pwm[i];
            getIO('pwm' + i, function(val, thing) {
                update(thing, val);
            }, thing);
        }

        loopUpdate();
    }, 500);
}

loopUpdate();