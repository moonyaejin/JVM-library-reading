# JVM 라이브러리 코드 리딩
> 16주간 JVM 생태계의 핵심 라이브러리를 분석하는 학습

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Guava](https://img.shields.io/badge/Guava-32.1.3-blue.svg)](https://github.com/google/guava)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

블로그: [JVM 라이브러리 코드 리딩](https://velog.io/@xaexix/series/JVM-라이브러리-코드-리딩)

## 학습 목표
- 주요 라이브러리의 내부 구조와 설계 원리 이해
- 소스코드 분석을 통한 실전 패턴 학습
- 테스트 코드로 동작 검증 및 학습 내용 체득

--- 

## 🗓️ 16주 커리큘럼

### Phase 1: Guava 기초 (Week 1-4)

| 주차 | 월요일 | 수요일 | 금요일 | 학습 목표 |
|------|--------|--------|--------|-----------|
| **Week 1** | Strings 클래스 | Joiner 클래스 | Splitter 클래스 | 문자열 처리와 null 안전성 |
| **Week 2** | Preconditions | Optional | CharMatcher | 방어적 프로그래밍과 null 처리 |
| **Week 3** | ImmutableList | ImmutableSet/Map | ImmutableMultimap | 불변 컬렉션의 설계 |
| **Week 4** | Multiset | BiMap | Table | 특수 컬렉션 자료구조 |

### Phase 2: 직렬화 & 로깅 (Week 5-8)

| 주차 | 월요일 | 수요일 | 금요일 | 학습 목표 |
|------|--------|--------|--------|-----------|
| **Week 5** | CollectionUtils | Bag & BidiMap | Predicate & Transformer | Commons Collections 패턴 |
| **Week 6** | SLF4J 아키텍처 | Logback Core | MDC & Marker | 로깅 추상화와 구현 분리 |
| **Week 7** | JsonParser | JsonGenerator | ObjectMapper | JSON 직렬화 최적화 |
| **Week 8** | 애노테이션 처리 | 다형성 처리 | 커스텀 직렬라이저 | 복잡한 객체 매핑 전략 |

### Phase 3: 동시성 프로그래밍 (Week 9-12)

| 주차 | 월요일 | 수요일 | 금요일 | 학습 목표 |
|------|--------|--------|--------|-----------|
| **Week 9** | ReentrantLock | CountDownLatch | Atomic 클래스 | 저수준 동시성 API |
| **Week 10** | ThreadPoolExecutor | ScheduledExecutor | ForkJoinPool | 스레드 풀 설계와 튜닝 |
| **Week 11** | 기본 조합 연산 | 병렬 조합 | 커스텀 Executor | 비동기 프로그래밍 패턴 |
| **Week 12** | ListenableFuture | Service 프레임워크 | RateLimiter | Guava 고수준 동시성 |

### Phase 4: 네트워크 라이브러리 (Week 13-16)

| 주차 | 월요일 | 수요일 | 금요일 | 학습 목표 |
|------|--------|--------|--------|-----------|
| **Week 13** | EventLoop & Channel | ByteBuf | ChannelPipeline | 이벤트 기반 네트워킹 |
| **Week 14** | 코덱 시스템 | SSL/TLS | 백프레셔 | 프로덕션급 서버 구현 |
| **Week 15** | 연결 관리 | 인터셉터 | 캐시 시스템 | HTTP 클라이언트 설계 |
| **Week 16** | 미니 프레임워크 | 성능 종합 분석 | 최종 회고 | 전체 학습 내용 통합 |

---

### 📊 진행 상황

| Phase | 상태 | 완료율 | 비고 |
|-------|------|--------|------|
| **Phase 1** (Guava) | 🟢 진행중 | 25% | Week 1 완료 |
| **Phase 2** (직렬화/로깅) | ⚪ 예정 | 0% | Week 5 시작 예정 |
| **Phase 3** (동시성) | ⚪ 예정 | 0% | Week 9 시작 예정 |
| **Phase 4** (네트워크) | ⚪ 예정 | 0% | Week 13 시작 예정 |
