function createP(text, classname = '') {
    let p = document.createElement('p');
    p.innerText = text;
    if (classname != '') {
        p.className = classname;
    }
    return p;
} 