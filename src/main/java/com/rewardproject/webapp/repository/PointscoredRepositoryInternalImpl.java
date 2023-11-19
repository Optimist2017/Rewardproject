package com.rewardproject.webapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.rewardproject.webapp.domain.Pointscored;
import com.rewardproject.webapp.repository.rowmapper.PointscoredRowMapper;
import com.rewardproject.webapp.repository.rowmapper.TasksRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Pointscored entity.
 */
@SuppressWarnings("unused")
class PointscoredRepositoryInternalImpl extends SimpleR2dbcRepository<Pointscored, Long> implements PointscoredRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TasksRowMapper tasksMapper;
    private final PointscoredRowMapper pointscoredMapper;

    private static final Table entityTable = Table.aliased("pointscored", EntityManager.ENTITY_ALIAS);
    private static final Table nameTable = Table.aliased("tasks", "name");

    public PointscoredRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TasksRowMapper tasksMapper,
        PointscoredRowMapper pointscoredMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Pointscored.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.tasksMapper = tasksMapper;
        this.pointscoredMapper = pointscoredMapper;
    }

    @Override
    public Flux<Pointscored> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Pointscored> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PointscoredSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TasksSqlHelper.getColumns(nameTable, "name"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(nameTable)
            .on(Column.create("name_id", entityTable))
            .equals(Column.create("id", nameTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Pointscored.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Pointscored> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Pointscored> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Pointscored> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Pointscored> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Pointscored> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Pointscored process(Row row, RowMetadata metadata) {
        Pointscored entity = pointscoredMapper.apply(row, "e");
        entity.setName(tasksMapper.apply(row, "name"));
        return entity;
    }

    @Override
    public <S extends Pointscored> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
