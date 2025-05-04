function parseGraphicalSolution(solution) {
    let solutionContainer = document.getElementById('solution-container');
    solutionContainer.innerHTML = '';

    let algoTitle = document.createElement('h2');
    algoTitle.innerText = 'Решение ЗЛП графическим методом';
    solutionContainer.appendChild(algoTitle);
    
    let addConstraintsSteps = parseAddConstraintSteps(solution.addConstraintSteps);
    solutionContainer.appendChild(addConstraintsSteps);

    if (solution.graphicalAnswer.graphicalSolutionType != 'FEASIBLE_REGION_EMPTY') {
        let addObjectiveFuncStep = parseAddObjectiveFunc(solution.addObjectiveFunc);
        solutionContainer.appendChild(createP('Добавление вектора-градиента целевой функции.', 'subtitle'));
        solutionContainer.appendChild(addObjectiveFuncStep);
    }

    let answerDescriptionStep = parseAnswerDescription(solution.graphicalAnswer, solution.finalGraphSVG);
    solutionContainer.appendChild(createP('Поиск оптимальных точек.', 'subtitle'));
    solutionContainer.appendChild(answerDescriptionStep);

    renderKatexElement('solution-container');
    renderKatexElement('input-block');
    document.querySelector(".solve-container").hidden = false;
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
        stepsContainer.appendChild(createP(`Добавление прямой ограничения ${i + 1}.`, 'subtitle'))

        let stepContainer = document.createElement('div');
        stepContainer.id = 'addConstraintContainer-' + (i + 1);
        stepContainer.className = 'stepContainer';
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

function parseAddObjectiveFunc(step) {
    const { objectiveFunc, inScale, graph } = step;

    let objectiveFuncStepContainer = document.createElement('div');
    objectiveFuncStepContainer.className = 'stepContainer';

    let descriptionContainer = document.createElement('div');
    descriptionContainer.appendChild(createP(`Строим вектор, координатами которого являются коэффициенты целевой функции. Координаты: $(${objectiveFunc.a}, ${objectiveFunc.b})$.`));
    if (!inScale) {
        descriptionContainer.appendChild(createP(`Вектор нарисован не в масштабе, так как он не помещается на графике.`));
    }

    objectiveFuncStepContainer.appendChild(descriptionContainer);
    objectiveFuncStepContainer.appendChild(createSvgContainer(graph));

    return objectiveFuncStepContainer;
}

function parseAnswerDescription(answer, graph) {
    let container = document.createElement('div');
    container.className = 'stepContainer';
    if (answer.graphicalSolutionType == 'SUCCESS_POINT') {
        let stepContainer = parseOnePointSuccessSolution(answer.pointSolution, 'A');
        stepContainer.prepend(createP('Функция $F$ достигает оптимального значения в точке $A$.'));
        stepContainer.appendChild(createP('Ответ найден.'));
        container.appendChild(stepContainer);
    } else if (answer.graphicalSolutionType == 'SUCCESS_SEGMENT') {
        container.appendChild(parseSegmentSolution(answer.segmentSolution));
    } else if (answer.graphicalSolutionType == 'SUCCESS_RAY') {
        container.appendChild(parseRaySolution(answer.raySolution));
    } else if (answer.graphicalSolutionType == 'UNBOUNDED_MAX' || answer.graphicalSolutionType == 'UNBOUNDED_MIN') {
        container.appendChild(parseUnboundedSolution(answer.graphicalSolutionType));
    } else if (answer.graphicalSolutionType == 'FEASIBLE_REGION_POINT') {
        container.appendChild(parseFeasibleRegionPointSolution(answer.pointSolution));
    } else if (answer.graphicalSolutionType == 'FEASIBLE_REGION_EMPTY') {
        container.appendChild(parseFeasibleRegionEmptySolution());
    }

    container.appendChild(createSvgContainer(graph));
    return container;
}

function parseOnePointSuccessSolution(pointSolutionStep, pointLabel) {
    let container = document.createElement('div');

    if (!pointSolutionStep.findPointCoordinates.pointOnAxis) {
        container.appendChild(parsePointIsIntersection(pointSolutionStep.findPointCoordinates, pointLabel));
    } else {
        container.appendChild(parsePointOnAxis(pointSolutionStep.findPointCoordinates, pointLabel));
    }

    container.appendChild(parseCalcObjValue(pointSolutionStep, pointLabel));
    
    return container;
}

function parsePointIsIntersection(findPointCoordinatesStep, pointLabel) {
    const { constraintNumber1, constraintNumber2, systemOfEquationLatex } = findPointCoordinatesStep;
    let container = document.createElement('div');

    container.appendChild(createP(`Найдём координаты точки $${pointLabel}$.`));
    container.appendChild(createP(`Точка ${pointLabel} одновременно принадлежит прямым $(${constraintNumber1})$ и $(${constraintNumber2})$.`));
    container.appendChild(createP(systemOfEquationLatex, 'latex'));

    return container;
}

function parsePointOnAxis(findPointCoordinatesStep, pointLabel) {
    const { point } = findPointCoordinatesStep;

    let container = document.createElement('div');
    container.appendChild(createP(`Точка $${pointLabel}$ лежит на оси, её координаты известны: $(${formatDoubleNumber(point.x)}, ${formatDoubleNumber(point.y)})$.`));

    return container;
}

function parseCalcObjValue(step, pointLabel) {
    const { point, objectiveFuncValue } = step;
    let container = document.createElement('div');
    
    container.appendChild(createP(`Вычислим значение функции $F$ в точке $${pointLabel} (${formatDoubleNumber(point.x)}, ${formatDoubleNumber(point.y)})$.`));
    container.appendChild(createP(objectiveFuncValue, 'latex'));

    return container;
}

function parseSegmentSolution(step) {
    let container = document.createElement('div');

    container.appendChild(createP('Функция $F$ достигает оптимального значения в точках $A$ и $B$ одновременно.'));
    container.appendChild(parseOnePointSuccessSolution(step.pointSolution1, 'A'));
    container.appendChild(parseOnePointSuccessSolution(step.pointSolution2, 'B'));
    container.appendChild(createP('Функция $F$ достигает своего наибольшего значения в любой точке отрезка $AB$.'));
    container.appendChild(createP('Ответ найден.'));

    return container;
}

function parseRaySolution(step) {
    const {constraintLatex, constraintNumber, paramAnswerSystem, pointSolution} = step;
    let container = document.createElement('div');

    container.appendChild(createP('Функция $F$ достигает оптимального значения на луче, который имеет своё начало в точке $A$.'));
    container.appendChild(parseOnePointSuccessSolution(pointSolution, 'A'));
    container.appendChild(createP(`Луч находится на прямой $${constraintNumber}$.`));
    container.appendChild(createP(`$${constraintLatex}$`));
    container.appendChild(createP(`На основании данного уравнения прямой, можно записать параметрическое уравнение луча:`));
    container.appendChild(createP(`$${paramAnswerSystem}$`, 'latex'));
    container.appendChild(createP(`Где $t \\geq 0$`));
    container.appendChild(createP('Ответ найден.'));

    return container;
}

function parseUnboundedSolution(type) {
    let container = document.createElement('div');
    
    if (type == 'UNBOUNDED_MAX') {
        container.appendChild(createP('Из рисунка видно, что невозможно определить последнее пересечение линии уровня области допустимых решений, т.е. функция $F$ неограниченно возрастает.'));
    } else if (type == 'UNBOUNDED_MIN') {
        container.appendChild(createP('Из рисунка видно, что невозможно определить первое пересечение линии уровня области допустимых решений, т.е. функция $F$ неограниченно убывает.'));
    }

    return container;
}

function parseFeasibleRegionPointSolution(pointSolution) {
    let container = document.createElement('div');
    container.appendChild(createP('Область допустимого решения - точка.'));
    container.appendChild(parseOnePointSuccessSolution(pointSolution, 'A'));
    container.appendChild(createP('Ответ найден.'));
    return container;
}

function parseFeasibleRegionEmptySolution() {
    let container = document.createElement('div');
    container.appendChild(createP('Ограничения несовместны.'));
    container.appendChild(createP('Область допустимого решения - пустое множество.'));
    return container;
}