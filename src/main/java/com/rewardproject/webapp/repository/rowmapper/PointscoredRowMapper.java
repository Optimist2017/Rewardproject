package com.rewardproject.webapp.repository.rowmapper;

import com.rewardproject.webapp.domain.Pointscored;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pointscored}, with proper type conversions.
 */
@Service
public class PointscoredRowMapper implements BiFunction<Row, String, Pointscored> {

    private final ColumnConverter converter;

    public PointscoredRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pointscored} stored in the database.
     */
    @Override
    public Pointscored apply(Row row, String prefix) {
        Pointscored entity = new Pointscored();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNameId(converter.fromRow(row, prefix + "_name_id", Long.class));
        return entity;
    }
}
