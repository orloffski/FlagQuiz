<b>Приложение Flag Quiz из книги "Android для разработчиков. 3-е издание" П. Дейтел, Х. Дейтел, А. Уолд</b>

Приложение Flag Quiz проверяет знание пользователем 10 флагов различных стран. 
После запуска приложения на экране появляется изображение флага и четыре кнопки с вариантами возможного ответа. 
Один из вариантов правильный, а остальные (неправильные) выбираются случайным образом без повторений. 
Приложение выводит информацию о ходе игры, отображая номер вопроса (из десяти возможных).

Формат отображения вариантов зависит от устройства, на котором выполняется приложение, и ориентации экрана — 
приложение поддерживает портретную ориентацию на любом устройстве, но альбомная ориентация поддерживается только на планшетах.

## [Rev 1.010] - 2016-10-01
### Changed
Исправлены некритичные ошибки в изначальной логике
- редко проявляющаяся ошибка в альбомной ориентации при смене количества вариантов ответов на большее чем имеющееся
- сообщение о перезапуске игры при смене настроек количества ответов вызывалось 8 раз подряд, перенесено вверх, вызываться стало однократно
- ошибка при первом запуске приложения при пустых настройках (автоматически устанавливается 4-й регион "Северная Америка")
- добавлена дополнительная проверка соответствия загружаемых данных (при смене ориентации либо возврата на экран игры из настроек) на валидность - если не валидны (смена настроек не привела к сбросу игры) производится принудительный сброс
- исправлена ошибка вывода корректного ответа без замены спецсимволов на пробел и дефис
- подчищены логи

## [Rev 1.006] - 2016-09-30
### Changed
Приложение изменено для упрощения локализации, изменена логика формирования данных, удалены множественные обращения к AssetManager и множественного чтения имен файлов для построения структур данных
- удалена привязка названий стран к изображений флагов
- добавлена возможность добавлять региона и страны без изменений в коде
- добавлена возможность добавлять локализации через файлы ресурсов

## [Rev 1.005] - 2016-09-28
### Changed
- Исправлена некорректная анимация в альбомном режиме

## [Rev 1.004] - 2016-09-27
### Changed
- Исправлено сохранение вариантов ответов при повороте устройства. 
- Исправлена ошибка перезапуска игры при переходе из планшетной ориентации после смены настроек

## [Rev 1.003] - 2016-09-27
### Changed
- Исправлена ошибка диалогового окна при завершении игры

## [Rev 1.002] - 2016-09-27
### Changed
- Исправлена некорректная логика работы приложения - ранее при повороте устройства игра сбрасывалась на начало.

## [Rev 1.001] - 2016-09-27
### Added
Приложение аналогично исследуемое в 4-й главе книги.

