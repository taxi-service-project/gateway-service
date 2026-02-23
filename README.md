# 🚪 Gateway Service (API Gateway)

> **전체 MSA 시스템의 단일 진입점(Single Point of Entry)으로서, 중앙 집중형 라우팅, 로드 밸런싱, 그리고 모든 서비스의 공통 인증/인가(Auth)를 책임집니다.**

## 🛠 Tech Stack
| Category | Technology                               |
| :--- |:-----------------------------------------|
| **Language** | **Java 17** |
| **Framework** | Spring Cloud Gateway (WebFlux)           |
| **Security** | Spring Security (JWT)                     |
| **Discovery** | Netflix Eureka Client                    |

## 📡 API Specification (Routing & Security)
*`application.yml`에 정의된 15개의 라우팅 규칙 및 보안 필터 적용 현황입니다.*

| Target Service | 라우팅 URI | Path & Method (Predicates) | 적용된 필터 (Filters) | 비고 |
| :--- | :--- | :--- | :--- | :--- |
| **User Service** | `lb://user-service` | `Path=/login, /reissue` | ❌ (Public) | 로그인, 토큰 재발급 |
| **User Service** | `lb://user-service` | `Path=/api/users`, `Method=POST` | ❌ (Public) | 일반 사용자 회원가입 |
| **User Service** | `lb://user-service` | `Path=/api/users/**` | `AuthorizationHeaderFilter` | 사용자 정보 관리 |
| **Trip Service** | `lb://trip-service` | `Path=/api/trips/admin/failed-events/**`, `Method=POST` | **`AdminOnlyFilter`** | **[어드민]** 여정 DLT 복구 |
| **Trip Service** | `lb://trip-service` | `Path=/api/trips/**` | `AuthorizationHeaderFilter` | 여정 관리 API |
| **Driver Service** | `lb://driver-service` | `Path=/api/drivers`, `Method=POST` | ❌ (Public) | 기사 회원가입 |
| **Driver Service** | `lb://driver-service` | `Path=/api/drivers/**` | `AuthorizationHeaderFilter` | 기사 정보 및 상태 관리 |
| **Payment Service** | `lb://payment-service` | `Path=/api/payments/admin/failed-events/**`, `Method=POST` | **`AdminOnlyFilter`** | **[어드민]** 결제 DLT 복구 |
| **Payment Service** | `lb://payment-service` | `Path=/api/payments/**` | `AuthorizationHeaderFilter` | 결제 조회 API |
| **Matching Service**| `lb://matching-service` | `Path=/api/matches/**` | `AuthorizationHeaderFilter` | 배차 매칭 API |
| **Geo Service** | `lb://geospatial-service`| `Path=/api/locations/**` | `AuthorizationHeaderFilter` | 위치 정보 관련 API |
| **Notification** | `lb://notification-service`| `Path=/api/notifications/**` | `AuthorizationHeaderFilter` | 알림 관련 API |
| **Pricing Service** | `lb://pricing-service` | `Path=/api/prices/**` | `AuthorizationHeaderFilter` | 요금 계산 관련 API |
| **Trip Service (WS)**| `lb:ws://trip-service` | `Path=/ws/trips/tracking/**` | **`WebSocketJwtFilter`** | **[WS]** 승객용 실시간 추적 |
| **Geo Service (WS)** | `lb:ws://geospatial-service`| `Path=/ws/location/**` | **`WebSocketJwtFilter`** | **[WS]** 기사 위치 수집 |

## 🚀 Key Improvements (핵심 기술적 개선)

### 1. Auth Offloading & Trust Boundary (인증 책임 중앙화)
* **문제점:** 개별 마이크로서비스마다 JWT 파싱 및 만료 검증 로직을 구현하면 결합도가 높아지고 보안 홀이 발생할 위험이 있었습니다.
* **개선안:** `AuthorizationHeaderFilter`를 통해 게이트웨이 진입점에서 JWT 유효성을 단 1회만 일괄 검증합니다. 검증 완료 후 토큰의 Payload를 추출하여 `X-User-Id`, `X-Role` 형태의 HTTP Header로 변환해 뒷단으로 넘겨주는 패턴을 적용했습니다.


----------

## 아키텍쳐
<img width="2324" height="1686" alt="Image" src="https://github.com/user-attachments/assets/81a25ff9-ee02-4996-80d3-f9217c3b7750" />
