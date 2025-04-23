function parseGraphicalSolution(solution) {
    const container = createSvgContainer(solution, 'div', { 
        class: 'svg-wrapper', 
        id: 'my-svg-container'
    });

    document.getElementById('solution-container').innerHTML = '';
    document.getElementById('solution-container').appendChild(container);
}

function createSvgContainer(svgText, containerTagName = 'div', containerAttributes = {}) {
    // Создаем контейнер
    const container = document.createElement(containerTagName);
    
    // Устанавливаем атрибуты для контейнера
    for (const [attr, value] of Object.entries(containerAttributes)) {
        container.setAttribute(attr, value);
    }
    
    try {
        // Валидируем SVG, проверяя можем ли мы разобрать его как XML
        const parser = new DOMParser();
        const svgDoc = parser.parseFromString(svgText, 'image/svg+xml');
        
        // Проверяем на ошибки парсинга
        const parserError = svgDoc.querySelector('parsererror');
        if (parserError) {
            throw new Error('Некорректный SVG: ' + parserError.textContent);
        }
        
        // Вставляем SVG в контейнер
        container.innerHTML = svgText;
        
        // Получаем SVG элемент, который теперь находится в контейнере
        const svgElement = container.querySelector('svg');
        
        if (!svgElement) {
            throw new Error('В предоставленном тексте не найден SVG элемент');
        }
        
        return container;
    } catch (error) {
        console.error('Ошибка при вставке SVG:', error);
        throw error;
    }
}