function parseGraphicalSolution(solution) {
    let solutionContainer = document.getElementById('solution-container');
    solutionContainer.innerHTML = '';

    let algoTitle = document.createElement('h2');
    algoTitle.innerText = 'Решение ЗЛП графическим методом';
    solutionContainer.appendChild(algoTitle);
    
    let addConstraintsSteps = parseAddConstraintSteps(solution.addConstraintSteps);
    solutionContainer.appendChild(addConstraintsSteps);

    renderKatexElement('solution-container');
    renderKatexElement('input-block');
}

function createSvgContainer(svgText) {
    const container = document.createElement('div');
    container.className = 'graphContainer';
    
    const parser = new DOMParser();
    const svgDoc = parser.parseFromString(svgText, 'image/svg+xml');
    
    const parserError = svgDoc.querySelector('parsererror');
    if (parserError) {
        throw new Error('Некорректный SVG: ' + parserError.textContent);
    }
    
    container.innerHTML = svgText;
    // const svgElement = container.querySelector('svg');
    
    return container;
}

function parseAddConstraintSteps(steps) {
    let stepsContainer = document.createElement('div');

    for (let i = 0; i < steps.length; i++) {
        stepsContainer.appendChild(createP(`Шаг ${i + 1}. Добавление прямой ограничения.`, 'subtitle'))

        let stepContainer = document.createElement('div');
        stepContainer.id = 'addConstraintContainer-' + (i + 1);
        stepContainer.className = 'addConstraintContainer';
        let step = steps[i];

        stepContainer.appendChild(parseAddContraintStep(step, i + 1));
        stepContainer.appendChild(createSvgContainer(step.graph));
        
        stepsContainer.appendChild(stepContainer);
    }

    return stepsContainer;
}

function parseAddContraintStep(step, number) {
    const { inequalityLatex, equalityLatex, lineParallelToAxis, findX1, findX2, findInequalityRegion, lineParallelToAxisStep, graph } = step;

    let container = document.createElement('div');
    
    container.appendChild(createP(`Рассмотрим ${number} неравенство системы ограничений.`));
    container.appendChild(createP(`$${inequalityLatex}$`, 'latex'));
    container.appendChild(createP(`Построим прямую: $${equalityLatex}$`));

    if (!lineParallelToAxis) {
        let findXContainer = document.createElement('div');
        findXContainer.className = 'findXContainer';

        let findX1Container = document.createElement('div');
        let findX2Container = document.createElement('div');
        findX2Container.className = 'findX2Container';

        findX2Container.appendChild(createP(`Пусть $x_1 = 0$`));
        for (let i = 0; i < findX2.interceptSteps.length; i++) {
            findX2Container.appendChild(createP(findX2.interceptSteps[i], 'latex'));
        }

        findX1Container.appendChild(createP(`Пусть $x_2 = 0$`));
        for (let i = 0; i < findX1.interceptSteps.length; i++) {
            findX1Container.appendChild(createP(findX1.interceptSteps[i], 'latex'));
        }

        findXContainer.appendChild(findX1Container);
        findXContainer.appendChild(findX2Container);
        container.appendChild(findXContainer);

        container.appendChild(createP(`Найдены координаты двух точек $(0,  ${findX2.x})$ и $(${findX1.x}, 0)$.`));
        container.appendChild(createP(`Соединяем точки и получаем прямую $(${number})$.`));
    } else {
        for (let i = 0; i < lineParallelToAxisStep.findXStep.interceptSteps.length; i++) {
            container.appendChild(createP(lineParallelToAxisStep.findXStep.interceptSteps[i], 'latex'));
        }

        if (!lineParallelToAxisStep.horizontal) {
            container.appendChild(createP(`Данная прямая параллельна оси $X_2$ и проходит через точку $(${lineParallelToAxisStep.findXStep.x}, 0)$.`));
        } else {
            container.appendChild(createP(`Данная прямая параллельна оси $X_1$ и проходит через точку $(0, ${lineParallelToAxisStep.findXStep.x})$.`));
        }
    }

    container.appendChild(createP(`Нужно определить какие точки нас интересуют: выше или ниже прямой?`));
    if (!lineParallelToAxis || lineParallelToAxis && lineParallelToAxisStep.horizontal) { 
        container.appendChild(createP(`Преобразуем неравенство, оставив в левой части только $x_2$.`));
    } else {
        container.appendChild(createP(`Преобразуем неравенство, оставив в левой части только $x_1$.`));
    }
    

    for (let i = 0; i < findInequalityRegion.steps.length; i++) {
        container.appendChild(createP(`$${findInequalityRegion.steps[i]}$`, 'latex'));
    }

    if (!lineParallelToAxis || lineParallelToAxis && lineParallelToAxisStep.horizontal) {
        if (findInequalityRegion.region == 'LEQ') {
            container.appendChild(createP(`Знак неравенства $\\leq$. Следовательно, нас интересуют точки расположенные ниже построенной прямой.`));
        } else {
            container.appendChild(createP(`Знак неравенства $\\geq$. Следовательно, нас интересуют точки расположенные выше построенной прямой.`));
        }
    } else {
        if (findInequalityRegion.region == 'LEQ') {
            container.appendChild(createP(`Знак неравенства $\\leq$. Следовательно, нас интересуют точки расположенные левее построенной прямой.`));
        } else {
            container.appendChild(createP(`Знак неравенства $\\geq$. Следовательно, нас интересуют точки расположенные правее построенной прямой.`));
        }
    }

    return container;
}