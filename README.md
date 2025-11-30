# 🚪 Gateway Service (API Gateway)

> **시스템의 단일 진입점으로서 보안(Auth), 라우팅(Routing), 부하 분산(Load Balancing)을 담당하는 마이크로서비스입니다.**

## 🛠 Tech Stack
| Category | Technology                              |
| :--- |:----------------------------------------|
| **Framework** | Spring Cloud Gateway  |
| **Security** | JWT , Spring Security                   |
| **Discovery** | Netflix Eureka Client                   |

## 📡 API Specification (Routing & Security)

| Service | Path Pattern | Auth Required | Description |
| :--- | :--- | :---: | :--- |
| **User** | `POST /login`, `/reissue` | ❌ | 로그인 및 인증 토큰 관련 (Public) |
| **User** | `POST /api/users` | ❌ | 회원가입 관련 (Public) |
| **User** | `/api/users/**` | 🔐 | 회원 정보 조회 및 수정 관련 |
| **Trip** | `/api/trips/**` | 🔐 | 여정 및 배차 관리 관련 |
| **Driver** | `/api/drivers/**` | 🔐 | 기사 정보 및 상태 관리 관련 |
| **Matching** | `/api/matches/**` | 🔐 | 실시간 기사 매칭 관련 |
| **Geo** | `/api/locations/**` | 🔐 | 실시간 위치 및 관제 관련 |
| **Payment** | `/api/payments/**` | 🔐 | 결제 승인 및 내역 관련 |
| **Pricing** | `/api/prices/**` | 🔐 | 요금 계산 및 정책 관련 |
| **Notification**| `/api/notifications/**`| 🔐 | 알림 발송 및 설정 관련 |
| **Recommend** | `/api/recommendations/**`| 🔐 | AI 수요 예측 및 추천 관련 |

## 🚀 Key Improvements (핵심 기술적 개선)

### 1. Auth Offloading (인증 책임 분리)
* **문제:** 개별 마이크로서비스마다 JWT 검증 로직을 중복 구현하여 관리 포인트가 분산되는 문제 발생.
* **해결:** `AuthorizationHeaderFilter`를 구현하여 게이트웨이 진입점에서 **JWT 유효성을 일괄 검증**합니다. 검증에 성공하면 사용자 정보를 **HTTP Header(`X-User-Id`)**에 주입하여 뒷단 서비스로 전파하는 **Trust Boundary** 모델을 구축했습니다.




----------

## 아키텍쳐
<img width="2324" height="1686" alt="Image" src="https://github.com/user-attachments/assets/81a25ff9-ee02-4996-80d3-f9217c3b7750" />
