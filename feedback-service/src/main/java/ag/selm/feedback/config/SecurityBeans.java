package ag.selm.feedback.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityBeans {

    /*  Аннотация @Bean говорит Spring создать бин — экземпляр SecurityWebFilterChain,
 основной объект фильтрации безопасности в Spring WebFlux (в отличие от SecurityFilterChain в обычном Spring MVC).*/
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){
        return http
                .authorizeExchange(configurer -> configurer.anyExchange().authenticated())
                /*🔹 Здесь задаётся авторизация:
    authorizeExchange(...) — метод, который позволяет задать правила для URL-путей.
    anyExchange().authenticated() — все запросы (любые пути, любой метод) требуют аутентификации.
        Это означает, что без токена (или без успешной авторизации) доступ к API запрещён.*/
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                /*🔹 Отключает CSRF-защиту (межсайтовую подделку запроса):
    CSRF чаще нужен в формах HTML, для защиты от подделки запросов в браузере.
    В API-приложениях с токенами он обычно не нужен, особенно в stateless-приложениях.*/
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                /*Указывает, что не нужно сохранять SecurityContext (например, в сессии):=
    NoOpServerSecurityContextRepository — это реализация, которая игнорирует сохранение контекста.
    Это означает, что каждый запрос будет обрабатываться независимо (stateless).
    Такой подход — стандарт для REST API.*/
                .oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()))
                /*🔹 Это важная часть: включает поддержку OAuth2 Resource Server с использованием JWT-токенов:
    oauth2ResourceServer(...) — активирует функциональность ресурс-сервера.
    .jwt(...) — говорит, что приложение ожидает JWT-токены в заголовке Authorization: Bearer ....
    Customizer.withDefaults() — говорит Spring использовать стандартные настройки:
        он будет искать spring.security.oauth2.resourceserver.jwt.issuer-uri или jwks-uri в application.yml.
📌 Важно: Это означает, что приложение не логинит пользователей, а проверяет токены, присланные в запросах.*/
                .build(); //  Собирает SecurityWebFilterChain — готовую цепочку фильтров безопасности.

        /*🔄 Результат конфигурации
Приложение работает как OAuth2 Resource Server, которое:
    требует JWT-токен в каждом запросе (в Authorization: Bearer <token>),
    не использует сессии,
    не сохраняет SecurityContext,
    не поддерживает логин через браузер (вход через Keycloak не инициируется),
    полностью stateless.*/
    }
}
