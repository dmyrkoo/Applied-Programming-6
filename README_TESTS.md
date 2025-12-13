Інструкції по запуску тестів та виправленню помилки Mockito/Byte Buddy (JDK)

Коротко
- Проблема: при запуску тестів у IDE під JDK 25 Mockito (через Byte Buddy) може кидати помилку:
  "Java 25 (69) is not supported by the current version of Byte Buddy..." або
  "Mockito cannot mock this class: class services.SkladService."
- Причина: Byte Buddy, яку використовує Mockito для inline-мокінгу, не підтримує ще нову версію JVM.

Рішення (рівні пріоритету)
1) Рекомендуване (просте і надійне): запускати тести під JDK 17
   - Maven (термінал): переконайтеся, що `mvn -v` показує Java 17. Якщо ні — встановіть JAVA_HOME на JDK 17 або вкажіть JDK при запуску Maven.
   - IntelliJ IDEA: у Run Configuration для JUnit або у глобальних налаштуваннях Maven вкажіть JRE = Java 17.
     Шляхи в IDEA:
       File → Settings → Build, Execution, Deployment → Build Tools → Maven → Runner → "JRE"
       або Edit Configurations → відповідна конфігурація JUnit → поле "JRE" → виберіть Java 17.

2) Тимчасовий обхід (коли неможливо змінити JDK): включити експериментальну опцію Byte Buddy
   - Додайте VM опцію при запуску тестів у IDE (Run Configuration → VM options):
       -Dnet.bytebuddy.experimental=true
   - АБО для запуску через Maven ця опція вже включена в `pom.xml` для Surefire (argLine включає `-Dnet.bytebuddy.experimental=true`).
   - Попередження: це тимчасовий обхід, не гарантує стовідсоткової сумісності.

3) Довгострокове (оновлення залежностей)
   - Оновити Byte Buddy / Mockito до версій, що офіційно підтримують вашу JVM (якщо такі версії доступні в Maven Central).
   - Це означає: знайти сумісні версії `net.bytebuddy:byte-buddy` і `org.mockito:mockito-core`/`mockito-inline` і зафіксувати їх у `pom.xml`.
   - Якщо артефакти недоступні у центральному репозиторії — треба або змінити джерела репозиторію, або використовувати інший підхід (JDK 17).

Діагностика — корисні команди
- Перевірити версію Maven/Java (термінал):
```cmd
mvn -v
```
- Запустити конкретні тести з Windows cmd (обов'язково в кавичках):
```cmd
mvn -Dtest="commands.AddVagonCommandTest,commands.DeleteVagonCommandTest" test
```

Що робити зараз (швидко і безболісно)
- Якщо ви працюєте в IDEA — відкрийте конфігурацію тестів і поміняйте JRE на Java 17 (або додайте VM option -Dnet.bytebuddy.experimental=true).
- Якщо ви використовуєте mvn у терміналі — переконайтеся, що mvn працює під JDK 17 (як у мене під час перевірки — тести пройшли).

---

Додатково: Постійна (XML/Maven) конфігурація для безпечної відправки пошти

Це кроки, щоб правильно (постійно) налаштувати відправку пошти з `log4j2` через SMTP без збереження паролів у репозиторії.

1) Структура
- `src/main/resources/log4j2.xml` використовує змінні середовища: `${env:MAIL_USER}`, `${env:MAIL_PASS}`, `${env:MAIL_TO}` і т.д. (вже застосовано у проекті).
- `pom.xml` має профіль `enable-mail`, який встановлює `<mail.enabled>true</mail.enabled>` і дозволяє включати відправку в продакшні/стейджі без коміту секретів.

2) Як отримати Gmail app‑password (якщо ваш акаунт має 2FA)
- Увійдіть в акаунт Google → Security → Signing in to Google → App passwords
- Створіть новий app password (виберіть Custom name, наприклад `railway-management`) і збережіть згенерований пароль один раз.
- Якщо ви випадково опублікували пароль, негайно відкличте його (Revoke) і створіть новий.

3) Задати змінні оточення у Windows (permanent — через setx)
У командному рядку (cmd.exe) виконайте (замість NEW_APP_PASSWORD поставте реально створений app password):
```cmd
setx MAIL_USER "samaratop259@gmail.com"
setx MAIL_PASS "NEW_APP_PASSWORD"
setx MAIL_TO "admin@admin.com"
setx MAIL_FROM "samaratop259@gmail.com"
setx MAIL_HOST "smtp.gmail.com"
setx MAIL_PORT "587"
```
Після виконання команд закрийте і заново відкрийте термінал/IDE щоб змінні підхопилися.

Перевірка в новій сесії:
```cmd
echo %MAIL_USER%
echo %MAIL_TO%
```

4) Запуск Maven з включеною відправкою пошти (не зберігайте паролі у параметрах!)
- У production/stage, де встановлені env vars (MAIL_USER/MAIL_PASS), запустіть з профілем `enable-mail`:
```cmd
mvn -Penable-mail clean verify
```
- CI (GitHub Actions, GitLab CI): збережіть `MAIL_USER` і `MAIL_PASS` у секрете (Secrets) і передайте їх як env в job; активуйте `-Penable-mail` тільки у job, де дозволено реальне відправлення.

5) Тести і локальна розробка
- За замовчуванням `mail.enabled` = false, тому тести не будуть відправляти листи.
- Для unit‑тестів використовуйте `MyLogger` (симуляція відправки) або налаштуйте окремий `log4j2-test.xml` без SMTP appender.

6) CI: приклад для GitHub Actions (фрагмент)
```yaml
env:
  MAIL_TO: admin@admin.com
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Build and test
        env:
          MAIL_USER: ${{ secrets.MAIL_USER }}
          MAIL_PASS: ${{ secrets.MAIL_PASS }}
        run: mvn -Penable-mail clean verify
```

7) Резюме безпечного потоку
- Ніколи не комітіть паролі у репозиторій.
- Використовуйте змінні оточення та CI secrets.
- Активуйте відправку пошти через Maven профіль `enable-mail` лише там, де це безпечно.

---

Быстрые шаги (что я уже сделал / рекомендую сделать дальше)

- Я внес небольшие, безопасные изменения, чтобы тесты не отправляли реальные письма по умолчанию:
  - Добавлен `src/test/resources/log4j2-test.xml`, который используется при `mvn test` и не содержит SMTP‑appender (теперь он пишет только в консоль и в файл внутри `target/test-logs` по умолчанию).
  - Обновлён `src/test/java/services/LoggerCriticalTest.java`: тест теперь использует `@TempDir` и передає
