let constraintCountTextbox = document.getElementById("constraintCount");
constraintCountTextbox.addEventListener('change', handleConstraintChange);
window.addEventListener('load', () => handleConstraintChange({target: constraintCountTextbox}));

function handleConstraintChange(e) {
    let constraintsContainer = document.getElementById('constraintsContainer');
    let constraints = constraintsContainer.querySelectorAll(".constraintDiv");

    // Проверка ввода количества ограничений
    let targetConstraintCount = parseInt(e.target.value) || 2;
    targetConstraintCount = Math.max(2, Math.min(10, targetConstraintCount));
    constraintCountTextbox.value = targetConstraintCount;

    let currentCount = constraints.length;

    if (currentCount > targetConstraintCount) {
        removeConstraint(constraints, targetConstraintCount);
    } 
    else if (currentCount < targetConstraintCount) {
        addConstraint(constraintsContainer, currentCount, targetConstraintCount);
    }
}

function removeConstraint(constraints, targetConstraintCount) {
    for (let i = constraints.length; i > targetConstraintCount; i--) {
        constraints[i - 1].remove();
    }
}

function addConstraint(constraintsContainer, currentLength, targetConstraintCount) {
    for (let i = currentLength; i < targetConstraintCount; i++) {
        let constraintDiv = document.createElement('div');
        constraintDiv.className = 'constraintDiv';
        constraintDiv.id = `c${i}`;
        let variablesDiv = document.createElement('div');
        variablesDiv.className = 'variablesDiv';
        for (let j = 0; j < 2; j++) {
            let variableTextbox = document.createElement('input');
            variableTextbox.setAttribute('type', 'text');
            variableTextbox.setAttribute('id', `v${i}${j}`);
            variableTextbox.setAttribute('placeholder', '0');

            let xLabel = document.createElement('span');
            xLabel.innerHTML = `x<sub>${j + 1}</sub>`;

            let variableDiv = document.createElement('div');
            variableDiv.className = 'varDiv';

            if (j != 0) {
                let spanPlus = document.createElement('span');
                spanPlus.innerHTML = '+';
                spanPlus.className = "plus";
                variableDiv.appendChild(spanPlus);
            }

            variableDiv.appendChild(variableTextbox);
            variableDiv.appendChild(xLabel);

            variablesDiv.append(variableDiv);
        }

        constraintDiv.appendChild(variablesDiv);
        let select = document.createElement('select');
        select.className = 'operator-select';
        select.id = `operatorSelect${i}`;

        const options = [
            { value: 'LEQ', symbol: '≤' },
            { value: 'EQ', symbol: '=' },
            { value: 'GEQ', symbol: '≥' },
        ];

        options.forEach(opt => {
            let option = document.createElement('option');
            option.value = opt.value;
            option.textContent = opt.symbol;
            select.appendChild(option);
        });


        let rhsTextbox = document.createElement('input');
        rhsTextbox.setAttribute('type', 'text');
        rhsTextbox.setAttribute('id', `rhs${i}`);
        rhsTextbox.setAttribute('placeholder', '0');

        constraintDiv.appendChild(select);
        constraintDiv.appendChild(rhsTextbox);            
        constraintsContainer.appendChild(constraintDiv);
    }
}

let variableCountTextbox = document.getElementById("variableCount");
variableCountTextbox.addEventListener('change', handleVariableCountChange);
document.getElementById("variableCount").onchange = variablesList;
window.addEventListener('load', () => handleVariableCountChange({target: variableCountTextbox}));
window.addEventListener('load', () => printVariablesList());
constraintCountTextbox.addEventListener('change', handleVariableCountChange);

function handleVariableCountChange() {
    // Проверка ввода количества ограничений
    let targetCount = parseInt(variableCountTextbox.value) || 2;
    targetCount = Math.max(2, Math.min(10, targetCount));
    variableCountTextbox.value = targetCount;

    let currentCount = document.querySelectorAll('#constraintsContainer>#c0>.variablesDiv>div').length;
    if (currentCount > targetCount) {
        removeVariables(targetCount);
        removeObjectiveVariables(targetCount);
    } else {
        addVariables(targetCount);
        addObjectiveVariables(targetCount);
    }
}

function removeVariables(targetCount) {
    let constraintsLength = document.querySelectorAll('#constraintsContainer>div').length;
    for (let i = 0; i < constraintsLength; i++) {
        let variables = document.querySelectorAll(`#c${i} .variablesDiv div`);
        for (let j = variables.length; j > targetCount; j--) {
            variables[j - 1].remove();
        }
    }
}

function addVariables(targetCount) {
    let constraintsLength = document.querySelectorAll('#constraintsContainer>div').length;
    for (let i = 0; i < constraintsLength; i++) {
        let variablesDiv = document.querySelector(`#c${i}>.variablesDiv`);
        let variablesLength = document.querySelectorAll(`#c${i}>.variablesDiv>.varDiv`).length;

        for (let j = variablesLength; j < targetCount; j++) {
            let variableDiv = document.createElement('div');
            variableDiv.className = 'varDiv';
            
            let spanPlus = document.createElement('span');
            spanPlus.innerHTML = '+';

            let input = document.createElement('input');
            input.setAttribute('type', 'text');
            input.setAttribute('placeholder', '0');
            spanPlus.className = "plus";
            input.id = `v${i}${j}`;

            let spanX = document.createElement('span');
            spanX.innerHTML = `x<sub>${j+1}</sub>`;

            variableDiv.appendChild(spanPlus);
            variableDiv.appendChild(input);
            variableDiv.appendChild(spanX);
            variablesDiv.appendChild(variableDiv);
        }
    }
}

function addObjectiveVariables(targetCount) {
    let objectiveContainer = document.getElementById('objectiveVariables');
    let variablesLength = document.querySelectorAll('#objectiveVariables>.objVarDiv').length;

    for (let i = variablesLength; i < targetCount; i++) {
        let variableDiv = document.createElement('div');
        variableDiv.className = 'objVarDiv';

        let input = document.createElement('input');
        input.setAttribute('type', 'text');
        input.setAttribute('placeholder', '0');
        input.id = `obj${i}`;

        if (i != 0) {
            let spanPlus = document.createElement('span');
            spanPlus.innerHTML = '+';
            variableDiv.appendChild(spanPlus);
        }
        variableDiv.appendChild(input);
        objectiveContainer.appendChild(variableDiv);
    }
}

function removeObjectiveVariables(targetCount) {
    let objectiveVars = document.querySelectorAll('#objectiveVariables>.objVarDiv');
    for (let i = objectiveVars.length; i > targetCount; i--) {
        objectiveVars[i - 1].remove();
    }
}

function sendData() {
    const data = getData();
    console.log(data);
    if (!checkInput(data)) return;
    
    fetch('/solve/simplex', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(answer => {
        console.log('Результат:', answer);
        printInput(data);
        if (data.method == 'BASIC') {
            parseBasicSimplexAnswer(answer);
        } else {
            parseBigMSimplexAnswer(answer);
        }
        
    })
    .catch(error => {
        console.error('Ошибка:', error);
    });
}

function getData() {
    let objectiveFuncCoefficients = [];
    let objectiveVars = document.querySelectorAll('#objectiveVariables>.objVarDiv');
    for (let i = 0; i < objectiveVars.length; i++) {
        objectiveFuncCoefficients.push(parseFraction(document.getElementById(`obj${i}`).value));
    }

    const objective = {
        coefficients: objectiveFuncCoefficients,
        direction: document.getElementById('directionSelect').value
    };

    const constraints = [];
    const constraintDivs = document.querySelectorAll('#constraintsContainer > div');
    let variablesCount = document.getElementById("variableCount").value;

    constraintDivs.forEach((div, index) => {
        const coeffs = [];
        
        for(let i = 0; i < variablesCount; i++) {
            const input = document.getElementById(`v${index}${i}`);
            coeffs.push(parseFraction(input.value));
        }

        const constraint = {
            coefficients: coeffs,
            operator: document.querySelector(`#c${index} select`).value,
            rhs: parseFraction(document.getElementById(`rhs${index}`).value)
        };
        
        console.log(constraint);
        constraints.push(constraint);
    });

    let method = document.getElementById("method").value;

    const data = {
        objective,
        constraints,
        method
    };

    return data;
}

// При выборе файла читаем его с помощью FileReader
document.getElementById("fileInput").addEventListener("change", function (e) {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = function (event) {
        const content = event.target.result;
        // Выводим содержимое в консоль (можно убрать)
        console.log("Содержимое файла:\n" + content);
        // Парсим содержимое файла
        const lpProblem = parseLPFile(content);
        console.log(lpProblem);

        document.getElementById("constraintCount").value = lpProblem.numConstraints;
        const changeEvent = new Event("change", {
            bubbles: false,    // событие всплывающее, если это необходимо
            cancelable: false // событие нельзя отменить
        });   
        document.getElementById("constraintCount").dispatchEvent(changeEvent);

        document.getElementById("variableCount").value = lpProblem.numVars;

        document.getElementById("variableCount").dispatchEvent(changeEvent);

        for (let i = 0; i < lpProblem.numConstraints; i++) {
            for (let j = 0; j < lpProblem.numVars; j++) {
                document.getElementById(`v${i}${j}`).value = fractionToNumber(lpProblem.constraints[i].coefficients[j]);
            }

            if (lpProblem.constraints[i].comparator == ">=") {
                    document.querySelector(`#c${i} select`).value = 'GEQ';
                } else if (lpProblem.constraints[i].comparator == "<=") {
                    document.querySelector(`#c${i} select`).value = 'LEQ';
                } else {
                    document.querySelector(`#c${i} select`).value = 'EQ';
                }

            document.getElementById(`rhs${i}`).value = fractionToNumber(lpProblem.constraints[i].rhs);
        }

        let objectiveVars = document.querySelectorAll('#objectiveVariables>.objVarDiv');
        for (let i = 0; i < objectiveVars.length; i++) {
            document.getElementById(`obj${i}`).value = fractionToNumber(lpProblem.objective.coefficients[i]);
        }
        document.getElementById('directionSelect').value = lpProblem.objective.type;
    };
    reader.readAsText(file);
    });
  
/**
 * Функция для парсинга текста файла в объект с ограничениями и целевой функцией.
 * @param {string} content - содержимое файла
 * @return {Object} объект с полями:
 *         numConstraints - количество ограничений,
 *         constraints - массив ограничений { coefficients, comparator, rhs },
 *         objective - целевая функция { type, coefficients }
 */
function parseLPFile(content) {
    // Разбиваем содержимое на строки, удаляя пустые строки
    const lines = content.split(/\r?\n/).filter((line) => line.trim() !== "");
    let index = 0;

    // Первая строка содержит число ограничений и количество переменных
    const [numConstraints, numVars] = lines[index].trim().split(/\s+/).map(Number);
    index++;

    const constraints = [];

    // Читаем ограничения
    for (let i = 0; i < numConstraints; i++, index++) {
        // Например строка "1 1 <= 7"
        // Разбиваем строку на токены по пробелам
        const tokens = lines[index].trim().split(/\s+/);
        
        // Берем только первые numVars токенов как коэффициенты
        const coefficients = tokens.slice(0, numVars).map(input => parseFraction(input));
        
        // Знак неравенства - это токен после коэффициентов
        const comparator = tokens[numVars];
        
        // Правая часть - это последний токен
        const rhs = parseFraction(tokens[numVars + 1]);

        constraints.push({
            coefficients,
            comparator,
            rhs,
        });
    }

    // Следующая строка – тип целевой функции ("MAX" или "MIN")
    const objectiveType = lines[index].trim().toUpperCase();
    index++;

    // Следующая строка – коэффициенты целевой функции
    const objectiveCoefficients = lines[index].trim().split(/\s+/).map(input => parseFraction(input));
    index++;

    return {
        numConstraints,
        numVars,
        constraints,
        objective: {
        type: objectiveType,
        coefficients: objectiveCoefficients,
        },
    };
}