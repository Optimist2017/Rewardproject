package com.rewardproject.webapp.repository;

import com.rewardproject.webapp.domain.Tasks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Tasks entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TasksRepository extends ReactiveCrudRepository<Tasks, Long>, TasksRepositoryInternal {
    Flux<Tasks> findAllBy(Pageable pageable);

    @Override
    <S extends Tasks> Mono<S> save(S entity);

    @Override
    Flux<Tasks> findAll();

    @Override
    Mono<Tasks> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TasksRepositoryInternal {
    <S extends Tasks> Mono<S> save(S entity);

    Flux<Tasks> findAllBy(Pageable pageable);

    Flux<Tasks> findAll();

    Mono<Tasks> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tasks> findAllBy(Pageable pageable, Criteria criteria);

}
