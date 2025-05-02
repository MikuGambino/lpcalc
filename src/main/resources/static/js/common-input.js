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