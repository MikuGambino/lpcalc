document.addEventListener('DOMContentLoaded', function() {
    // Получаем элементы
    const dialog = document.getElementById('myDialog');
    const openButton = document.getElementById('openDialog');
    const closeButton = document.getElementById('closeDialog');

    // Функция для открытия диалога
    function openDialog() {
        dialog.showModal(); // Показываем как модальное окно
    }

    // Функция для закрытия диалога
    function closeDialog() {
        dialog.close();
    }

    // Добавляем обработчики событий
    openButton.addEventListener('click', openDialog);
    closeButton.addEventListener('click', closeDialog);
        
    // Дополнительно: закрываем при клике на фон (backdrop)
    dialog.addEventListener('click', function(event) {
        const rect = dialog.getBoundingClientRect();
        const isInDialog = (
            rect.top <= event.clientY &&
            event.clientY <= rect.top + rect.height &&
            rect.left <= event.clientX &&
            event.clientX <= rect.left + rect.width
        );
        
        if (!isInDialog) {
            dialog.close();
        }
    });
});