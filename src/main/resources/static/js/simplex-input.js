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
variableCountTextbox.addEventListener('change', variablesList);
window.addEventListener('load', () => handleVariableCountChange({target: variableCountTextbox}));
window.addEventListener('load', () => variablesList());
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

function variablesList() {
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
function clearInputs() {
    // Находим все input элементы на странице и очищаем их
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