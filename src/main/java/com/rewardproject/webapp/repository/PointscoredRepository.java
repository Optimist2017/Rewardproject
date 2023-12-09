package com.rewardproject.webapp.repository;

import com.rewardproject.webapp.domain.Pointscored;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Pointscored entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PointscoredRepository extends ReactiveCrudRepository<Pointscored, Long>, PointscoredRepositoryInternal {
    Flux<Pointscored> findAllBy(Pageable pageable);

    @Override
    Mono<Pointscored> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Pointscored> findAllWithEagerRelationships();

    @Override
    Flux<Pointscored> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM pointscored entity WHERE entity.name_id = :id")
    Flux<Pointscored> findByName(Long id);

    @Query("SELECT * FROM pointscored entity WHERE entity.name_id IS NULL")
    Flux<Pointscored> findAllWhereNameIsNull();

    @Override
    <S extends Pointscored> Mono<S> save(S entity);

    @Override
    Flux<Pointscored> findAll();

    @Override
    Mono<Pointscored> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PointscoredRepositoryInternal {
    <S extends Pointscored> Mono<S> save(S entity);

    Flux<Pointscored> findAllBy(Pageable pageable);

    Flux<Pointscored> findAll();

    Mono<Pointscored> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Pointscored> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Pointscored> findOneWithEagerRelationships(Long id);

    Flux<Pointscored> findAllWithEagerRelationships();

    Flux<Pointscored> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
