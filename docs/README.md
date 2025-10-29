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

## Phase 1: Guava 기초 (Week 1-4)
| 주차 | 월요일 | 수요일 | 금요일 | 학습 목표 |
|------|--------|--------|--------|-----------|
| **Week 1** | Strings 클래스 | Joiner 클래스 | Splitter 클래스 | 문자열 처리와 null 안전성 |
| **Week 2** | Preconditions | Optional | CharMatcher | 방어적 프로그래밍과 null 처리 |
| **Week 3** | ImmutableList | ImmutableSet/Map | ImmutableMultimap | 불변 컬렉션의 설계 |
| **Week 4** | Multiset | BiMap | Table | 특수 컬렉션 자료구조 |

## Phase 2: Jackson & 로깅 (Week 5-8)
| 주차 | 월요일 | 수요일 | 금요일 | 학습 목표 |
|------|--------|--------|--------|-----------|
| **Week 5** | JsonParser 내부 | JsonGenerator | 스트리밍 파싱 | JSON 직렬화 기초 |
| **Week 6** | ObjectMapper | @JsonProperty 동작 | Custom Serializer | 복잡한 객체 매핑 |
| **Week 7** | @JsonTypeInfo | 다형성 처리 | 성능 최적화 | 고급 Jackson 패턴 |
| **Week 8** | SLF4J 아키텍처 | Logback Appender | MDC 활용 | 로깅 프레임워크 완전 정복 |

## Phase 3: Spring Core & JPA (Week 9-12)
| 주차 | 월요일 | 수요일 | 금요일 | 학습 목표 |
|------|--------|--------|--------|-----------|
| **Week 9** | BeanFactory 구조 | ApplicationContext | Bean 생명주기 | Spring IoC 컨테이너 내부 |
| **Week 10** | @Transactional 동작 | AOP Proxy 생성 | TransactionManager | 트랜잭션 메커니즘 |
| **Week 11** | EntityManager | Persistence Context | 1차 캐시 | JPA 영속성 관리 |
| **Week 12** | LazyLoading 내부 | Proxy 객체 | N+1 해결 전략 | JPA 성능 최적화 |

## Phase 4: 실무 필수 라이브러리 (Week 13-16)
| 주차 | 월요일 | 수요일 | 금요일 | 학습 목표 |
|------|--------|--------|--------|-----------|
| **Week 13** | QueryDSL Q타입 생성 | JPAQuery 내부 | 동적 쿼리 빌더 | 타입 안전한 쿼리 작성 |
| **Week 14** | Caffeine Cache 구조 | 캐시 전략(LRU/LFU) | 비동기 로딩 | 로컬 캐싱 완전 정복 |
| **Week 15** | Resilience4j CircuitBreaker | Retry/Bulkhead | 메트릭 수집 | 장애 대응 패턴 |
| **Week 16** | MapStruct 매핑 생성 | 성능 벤치마크 | 전체 회고 | DTO 변환 & 총정리 |
