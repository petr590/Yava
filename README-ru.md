# JDecompiler-java
Это декомпилятор Java, который я делаю с начала 2022 года (с перерывами).
Писать его на C++ Оказалось ошибкой, поэтому я перенёс его на Java.
Возможно, я закончу его когда-нибудь, но надо добавить ещё много функционала и исправить баги.

## Не реализовано:
- for(E e : arr)
- Поддержка jar

## Частично реализовано:
- finally
- switch
- break/continue
- Чтение ассемблерного кода Java

## Реализовано:
- Система примитивных типов (тип переменной определяется по её использованию)
- Вызов через `super`
- Аннотации
- if/else
- for(;;)
- while
- Тернарный оператор
- Операторы && и ||
- Отдельные переменные
- Дженерики
- Лямбды
- try/catch
- sealed классы
- records

## Реализованные фичи:
- Подбор имён переменных
- Возмодность опустить `this` и текущий класс
- Объявление полей через запятую
- Имена переменных из LocalVariableTable
- Распознавание констант в текущем классе, а также стандартных констант (Math.PI, Double.NaN и др.)
- Аннотация @Override

## Используемые библиотеки:
- ArgParser - https://github.com/petr590/ArgParser
- x590.util - https://github.com/petr590/x590.util
- fastutil
- junit (для тестирования)

