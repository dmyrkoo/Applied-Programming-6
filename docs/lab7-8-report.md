# Лабораторні роботи 7 та 8 — короткий звіт

Коротко: додано тести для покриття (LR7) та конфігурацію логування з File + SMTP (LR8). За замовчуванням відправка пошти відключена (без паролів у репозиторії).

Змінені/додані файли

- pom.xml — додано передачу системних властивостей для тестів та JaCoCo з перевіркою COVEREDRATIO >= 90% на рівні bundle.
- src/main/resources/log4j2.xml — нова конфігурація: FileAppender + SMTPAppender (значення для SMTP читаються з системних властивостей / змінних оточення).
- src/test/java/services/SkladServiceTest.java — перероблені тести для інжекції Scanner.
- src/test/java/services/LoggerCriticalTest.java — тест, що логгувує FATAL і перевіряє, що файл логів містить повідомлення (mail.disabled за замовчуванням).
- src/test/java/services/LoggerSmtpManualTest.java — manual@Disabled тест для ручної перевірки SMTP (відправляє лист лише коли запускається з mail.enabled=true та валідними обліковими даними).
- src/test/java/utils/FileManagerTest.java — тест з тимчасовим файлом для перевірки збереження/завантаження.
- docs/lab7-8-report.md — цей короткий звіт та інструкції.

Як це безпечно (секрети)

- Паролі/емейли не зберігаються у репозиторії.
- Для тестів і CI за замовчуванням `mail.enabled=false`.
- Для ручної перевірки SMTP використовуйте Google App Password і задайте змінні оточення `MAIL_USER` і `MAIL_PASS` (або передавайте їх в mvn як -Dmail.user / -Dmail.pass).

Необхідна дія — помилка, що я отримав

Під час запуску `mvn clean verify -Dmail.enabled=false` я отримав помилку: "The JAVA_HOME environment variable is not defined correctly" — це означає, що на вашій системі не задано `JAVA_HOME`, через це Maven не може запуститись.

Кроки для виправлення (Windows, cmd.exe)

1) Визначте шлях до вашої JDK (наприклад: `C:\Program Files\Java\jdk-17`).
2) Відкрийте командний рядок (cmd.exe) з правами користувача і виконайте (замініть шлях на ваш):

```
setx JAVA_HOME "C:\Program Files\Java\jdk-17"
setx PATH "%PATH%;%JAVA_HOME%\bin"
```

3) Перезапустіть термінал та IntelliJ IDEA (щоб змінні оточення подіяли).
4) Перевірте:

```
echo %JAVA_HOME%
mvn -v
```

Після цього можна виконати збірку:

```
mvn clean verify -Dmail.enabled=false
```

(цей виклик запустить тести та JaCoCo; відправка пошти буде вимкнена)

Як перевірити SMTP вручну (після налаштування Gmail App Password)

1) Увімкніть 2FA у Google-акаунті та створіть App Password (Mail).
2) В PowerShell/ cmd встановіть змінні:

```
setx MAIL_USER "your@gmail.com"
setx MAIL_PASS "your_app_password"
```

3) Перезапустіть IDE/термінал.
4) Запустіть ручний тест (після того як переконались, що `mvn -v` працює):

```
mvn -Dmail.enabled=true -Dmail.user=%MAIL_USER% -Dmail.pass=%MAIL_PASS% -Dtest=LoggerSmtpManualTest#sendFatalEmailManual test
```

Якщо потрібно вказати одержувача, додайте `-Dmail.to=recipient@example.com` у виклик Maven, або змініть `log4j2.xml`.

Наступні кроки від мене

- Якщо ви дозволите (після встановлення `JAVA_HOME`), я можу повторно виконати `mvn clean verify -Dmail.enabled=false` тут та повернути повний вивід, включно з відсотком покриття JaCoCo і інформацією про неохоплені класи, якщо покриття <90%.

Якщо хочете, допоможу з GUI-налаштуванням `JAVA_HOME` у Windows або з іншими командами. Якщо згодні — встановіть `JAVA_HOME` і скажіть, коли готові, або дайте дозвіл, і я повторю запуск тут.

