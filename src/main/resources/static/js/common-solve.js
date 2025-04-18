
function parseValueWithIndex(value, index) {
    return value + '<sub>' + index + '</sub>';
}

function activateAccordions() {
    document.querySelectorAll('.accordion-trigger').forEach(trigger => {
        trigger.addEventListener('click', () => {
            const content = trigger.nextElementSibling;
            
            trigger.classList.toggle('open');
            
            if (content.style.maxHeight) {
                content.style.maxHeight = null;
            } else {
                content.style.maxHeight = content.scrollHeight + "px";
            }
        });
    });
}

function removeAnswerContainers() {
    const answerContainers = document.querySelectorAll('div.answerContainer');

    answerContainers.forEach(container => {
    container.remove();
    });
}

function createP(text, classname = '') {
    let p = document.createElement('p');
    p.innerText = text;
    if (classname != '') {
        p.className = classname;
    }
    return p;
} 

function createTextInnerHTML(text, classname = '') {
    let p = document.createElement('p');
    p.innerHTML = text;
    if (classname != '') {
        p.className = classname;
    }
    return p;
}