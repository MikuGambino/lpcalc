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
            { value: 'GEQ', symbol: '≥' }
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

// При выборе файла читаем его с помощью FileReader
document.getElementById("fileInput").addEventListener("change", function (e) {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = function (event) {
      const content = event.target.result;
      // Парсим содержимое файла
      const lpProblem = parseLPFile(content);

      document.getElementById("constraintCount").value = lpProblem.numConstraints;
      const changeEvent = new Event("change", {
          bubbles: false,    // событие всплывающее, если это необходимо
          cancelable: false // событие нельзя отменить
      });   
      document.getElementById("constraintCount").dispatchEvent(changeEvent);
      for (let i = 0; i < lpProblem.numConstraints; i++) {
          document.getElementById(`v${i}0`).value = fractionToNumber(lpProblem.constraints[i].coefficients[0]);
          document.getElementById(`v${i}1`).value = fractionToNumber(lpProblem.constraints[i].coefficients[1]);

          if (lpProblem.constraints[i].comparator == ">=") {
              document.querySelector(`#c${i} select`).value = 'GEQ';
          } else {
              document.querySelector(`#c${i} select`).value = 'LEQ';
          }

          document.getElementById(`rhs${i}`).value = fractionToNumber(lpProblem.constraints[i].rhs);
      }

      document.getElementById('obj0').value = fractionToNumber(lpProblem.objective.coefficients[0]);
      document.getElementById('obj1').value = fractionToNumber(lpProblem.objective.coefficients[1]);
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

    // Первая строка – число ограничений
    const numConstraints = parseInt(lines[index].trim(), 10);
    index++;

    const constraints = [];

    // Читаем ограничения
    for (let i = 0; i < numConstraints; i++, index++) {
      // Например строка "1 -2 >= 3"
      // Разбиваем строку на токены по пробелам
      const tokens = lines[index].trim().split(/\s+/);
      // Предполагаем, что последние два токена – это знак неравенства и значение правой части,
      // а остальные токены – коэффициенты
      const coefficients = tokens.slice(0, tokens.length - 2).map(input => parseFraction(input));
      const comparator = tokens[tokens.length - 2]; // например, ">=" или "<="
      const rhs = parseFraction(tokens[tokens.length - 1]);

      constraints.push({
        coefficients,
        comparator,
        rhs,
      });
    }

    // Следующая строка – тип целевой функции ("max" или "min")
    const objectiveType = lines[index].trim().toUpperCase();
    index++;

    // Следующая строка – коэффициенты целевой функции
    const objectiveCoefficients = lines[index].trim().split(/\s+/).map(input => parseFraction(input));
    index++;

    return {
      numConstraints,
      constraints,
      objective: {
        type: objectiveType,
        coefficients: objectiveCoefficients,
      },
    };
  }

function sendData() {
    const data = getData();
    console.log(data);

    fetch('/solve/graph', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(solution => {
        console.log('Результат:', solution);
        parseGraphicalSolution(solution.finalGraphSVG);
    })
    .catch(error => {
        console.error('Ошибка:', error);
    });
}

function getData() {
    const objective = {
        coefficients: [
            parseFraction(document.getElementById('obj0').value) || 0,
            parseFraction(document.getElementById('obj1').value) || 0
        ],
        direction: document.getElementById('directionSelect').value
    };

    const constraints = [];
    const constraintDivs = document.querySelectorAll('#constraintsContainer > div');

    constraintDivs.forEach((div, index) => {
        const coeffs = [];
        
        for(let i = 0; i < 2; i++) {
            const input = document.getElementById(`v${index}${i}`);
            coeffs.push(parseFraction(input.value) || 0);
        }

        const constraint = {
            coefficients: coeffs,
            operator: document.querySelector(`#c${index} select`).value,
            rhs: parseFraction(document.getElementById(`rhs${index}`).value) || 0
        };
        
        constraints.push(constraint);
    });

    const data = {
        objective,
        constraints
    };

    return data;
}