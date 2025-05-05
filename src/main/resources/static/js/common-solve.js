function createP(text, classname = '') {
    let p = document.createElement('p');
    p.innerText = text;
    if (classname != '') {
        p.className = classname;
    }
    return p;
} 

function formatDoubleNumber(number) {
    let rounded = Math.round(number * 100) / 100;

    if (rounded === Math.floor(rounded)) {
        return String(rounded);
    }

    let str = rounded.toFixed(2);

    str = str.replace(/0+$/, '').replace(/\.$/, '');

    return str;
}

function removeAnswerContainers() {
    const answerContainers = document.querySelectorAll('div.answerContainer');

    answerContainers.forEach(container => {
        container.remove();
    });
}