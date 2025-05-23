function parseFraction(input) {
    if (!input || input.trim() === '') {
        return { numerator: 0, denominator: 1 };
    }
    
    // Проверяем, содержит ли строка символ "/"
    if (input.includes('/')) {
        const parts = input.split('/');
        console.log(parts);
        if (parts.length === 2) {
            const numerator = parseInt(parts[0].trim());
            const denominatorStr = parts[1].trim();
            
            if (denominatorStr === '0') {
                alert('Знаменатель не может быть равен нулю!');
                return { numerator: 0, denominator: 1 };
            }
            
            const denominator = parseInt(denominatorStr) || 1;
            return { numerator, denominator };
        }
    }
    
    // Если это десятичное число
    if (!isNaN(parseFloat(input))) {
        // Преобразуем десятичное число в дробь
        const decimal = parseFloat(input);
        // Десятичное число как "5" просто станет дробью 5/1
        if (Number.isInteger(decimal)) {
            return { numerator: decimal, denominator: 1 };
        } 
        // Для чисел вроде 0.5 превращаем их в дробь 1/2
        else {
            // Находим количество десятичных знаков
            const strNum = decimal.toString();
            const decimalPart = strNum.includes('.') ? strNum.split('.')[1] : '';
            const multiplier = Math.pow(10, decimalPart.length);
            
            // Умножаем на 10^n, чтобы получить целое число
            const numerator = Math.round(decimal * multiplier);
            const denominator = multiplier;
            
            return { 
                numerator: numerator, 
                denominator: denominator 
            };
        }
    }
    
    // Если не удалось распарсить, возвращаем ноль
    return { numerator: 0, denominator: 1 };
}

function clearInputs() {
    // Находим все input элементы на странице и очищаем их
    if (!confirm("Вы уверены, что хотите очистить все поля?")) return;
    const inputs = document.querySelectorAll('#input-container input[type="text"], #input-container input[type="number"]');
    inputs.forEach(input => {
        if (input.className != 'numTextBox') {
            input.value = '';
        }
    });
    
    // Сбрасываем все select элементы к первому значению
    const selects = document.querySelectorAll('#input-container select');
    selects.forEach(select => {
        if (select.options.length > 0) {
            select.selectedIndex = 0;
        }
    });
}

function printVariablesList() {
    let variablesCount = document.getElementById("variableCount").value;
    let listSpan = document.getElementById("variablesList");
    listSpan.innerHTML = "";
    
    for (let i = 0; i < variablesCount; i++) {
        listSpan.innerHTML += `x<sub>${i + 1}</sub>`;
        if (i != variablesCount - 1) {
            listSpan.innerHTML += ', ';
        }
    }

    listSpan.innerHTML += ' ⩾ 0';
}

function fractionToNumber(input) {
    if(input.denominator == 1) {
        return input.numerator;
    } else {
        return input.numerator + "/" + input.denominator;
    }
}

function printInput(input) {
    document.getElementById('func-dir').innerText = input.objective.direction == "MAX" ? "наибольшее" : "наименьшее";
    // Отрисовка целевой функции
    let objectiveFunc = parseLHS(input.objective.coefficients);
    updateMathFormula('objFuncInput', 'F = ' + objectiveFunc);

    // Отрисовка ограничений
    let constraints = '';
    for (let i = 0; i < input.constraints.length; i++) {
        let constraint = input.constraints[i];
        constraints += parseLHS(constraint.coefficients) + parseRHS(constraint.operator, constraint.rhs) + '\\\\';
    }
    constraints = '\\begin{cases}' + constraints + '\\end{cases}';
    updateMathFormula('constraintsInput', constraints);
    document.getElementById('input-block').hidden = false;
}

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

function checkInput(data) {
    let allZeroRHS = true;
    for(let i = 0; i < data.constraints.length; i++) {
        let constraint = data.constraints[i];
        let allZero = true;
        for (let j = 0; j < constraint.coefficients.length; j++) {
            if (constraint.coefficients[j].numerator != 0) allZero = false;
        }
        if (constraint.rhs.numerator != 0) allZeroRHS = false;
        if (allZero) {
            printError(`Ошибка. Все коэффициенты ограничения $${i + 1}$ равны $0$.`);
            return false;
        }
    }
    if (allZeroRHS) {
        printError(`Ошибка. Все свободные коэффициенты равны $0$.`);
        return false;
    }

    let allZeroObjective = true;
    for (let i = 0; i < data.objective.coefficients.length; i++) {
        if (data.objective.coefficients[i].numerator != 0) allZeroObjective = false;
    }

    if (allZeroObjective) {
        printError(`Ошибка. Все коэффициенты целевой функции равны $0$.`);
        return false;
    }

    document.querySelector('.solve-container').hidden = false;
    document.getElementById("errorContainer").hidden = true;
    return true;
}

function printError(text) {
    let errorContainer = document.getElementById("errorContainer");
    errorContainer.innerHTML = '';
    errorContainer.appendChild(createP(text, "error"));
    renderKatexElement('errorContainer');
    document.querySelector('.solve-container').hidden = true;
    errorContainer.hidden = false;
}

function parseURL() {
    const encodedParams = new URLSearchParams(window.location.search);

    const encodedObjective = encodedParams.get('objective');
    const encodedConstraints = encodedParams.get('constraints');
    const method = encodedParams.get('method');

    const decodedObjective = decodeURIComponent(encodedObjective);
    const decodedConstraints = decodeURIComponent(encodedConstraints);

    const objectiveObj = JSON.parse(decodedObjective);
    const constraintsObj = JSON.parse(decodedConstraints);

    return paramsObj = {
      objective: objectiveObj,
      constraints: constraintsObj,
      method: method
    };
}

function problemToFields(data) {
    const numConstraints = data.constraints.length;
    const numVars = data.objective.coefficients.length;

    document.getElementById("constraintCount").value = numConstraints;
    const changeEvent = new Event("change", {
        bubbles: false,    // событие всплывающее, если это необходимо
        cancelable: false // событие нельзя отменить
    });
    document.getElementById("constraintCount").dispatchEvent(changeEvent);
    if (document.getElementById("variableCount") != null) {
        document.getElementById("variableCount").value = numVars;
        document.getElementById("variableCount").dispatchEvent(changeEvent);
    }

    for (let i = 0; i < numConstraints; i++) {
        for (let j = 0; j < numVars; j++) {
            document.getElementById(`v${i}${j}`).value = data.constraints[i].coefficients[j];
        }

        document.querySelector(`#c${i} select`).value = data.constraints[i].operator;
        document.getElementById(`rhs${i}`).value = data.constraints[i].rhs;
    }

    let objectiveVars = document.querySelectorAll('#objectiveVariables>.objVarDiv');
    for (let i = 0; i < objectiveVars.length; i++) {
        document.getElementById(`obj${i}`).value = data.objective.coefficients[i];
    }
    document.getElementById('directionSelect').value = data.objective.direction;
    if (document.getElementById('method') != null && data.method != null) {
        document.getElementById('method').value = data.method;
    }
}