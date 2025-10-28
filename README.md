# MSA 기반 Taxi 호출 플랫폼 - API Gateway Service

Taxi 호출 플랫폼의 **API Gateway** 역할을 수행하는 마이크로서비스입니다. 외부 클라이언트(앱, 웹)로부터 들어오는 모든 요청을 받아 적절한 내부 마이크로서비스로 라우팅하고, 로드 밸런싱을 수행합니다.

## 주요 기능

* **라우팅 (Routing):** Path Predicate(`Path=/driver/**`, `/user/**` 등)를 기반으로 요청을 해당 마이크로서비스로 전달합니다.
* **로드 밸런싱 (Load Balancing):** Eureka Server로부터 서비스 인스턴스 목록을 받아 `lb://` 프로토콜을 사용하여 클라이언트 사이드 로드 밸런싱을 수행합니다.
* **서비스 디스커버리 (Service Discovery):** Eureka Client로서 Discovery Service(Eureka Server)에 등록됩니다.

## 라우팅 규칙 (Routing Rules)

| Path Prefix          | Target Service         |
| :------------------- | :--------------------- |
| `/driver/**`         | `driver-service`       |
| `/geospatial/**`     | `geospatial-service`   |
| `/matching/**`       | `matching-service`     |
| `/notification/**`   | `notification-service` |
| `/payment/**`        | `payment-service`      |
| `/pricing/**`        | `pricing-service`      |
| `/trip/**`           | `trip-service`         |
| `/user/**`           | `user-service`         |

## 기술 스택 (Technology Stack)

* **Language & Framework:** Java, Spring Boot
* **API Gateway:** Spring Cloud Gateway
* **Service Discovery:** Spring Cloud Netflix Eureka Client
