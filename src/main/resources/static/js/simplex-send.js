function sendData() {
    const data = getData();
    console.log(data);

    fetch('http://localhost:8080/solve/simplex', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        console.log('Результат:', data);
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
            console.log("input: " + input.value);
            coeffs.push(parseFraction(input.value));
        }

        const constraint = {
            coefficients: coeffs,
            operator: document.querySelector(`#c${index} select`).value,
            rhs: parseFraction(document.getElementById(`rhs${index}`).value)
        };
        
        constraints.push(constraint);
    });

    let method = document.getElementById("method").value;

    const data = {
        objective,
        constraints,
        method
    };
    console.log(data);

    return data;
}
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