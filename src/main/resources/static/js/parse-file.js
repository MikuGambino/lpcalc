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
                } else {
                    document.querySelector(`#c${i} select`).value = 'LEQ';
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

function fractionToNumber(input) {
    if(input.denominator == 1) {
        return input.numerator;
    } else {
        return input.numerator + "/" + input.denominator;
    }
}