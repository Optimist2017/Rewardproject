package com.rewardproject.webapp.repository.rowmapper;

import com.rewardproject.webapp.domain.Tasks;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tasks}, with proper type conversions.
 */
@Service
public class TasksRowMapper implements BiFunction<Row, String, Tasks> {

    private final ColumnConverter converter;

    public TasksRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tasks} stored in the database.
     */
    @Override
    public Tasks apply(Row row, String prefix) {
        Tasks entity = new Tasks();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPoint(converter.fromRow(row, prefix + "_point", Integer.class));
        return entity;
    }
}
