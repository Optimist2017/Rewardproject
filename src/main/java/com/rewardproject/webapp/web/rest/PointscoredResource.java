package com.rewardproject.webapp.web.rest;

import com.rewardproject.webapp.domain.Pointscored;
import com.rewardproject.webapp.repository.PointscoredRepository;
import com.rewardproject.webapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.rewardproject.webapp.domain.Pointscored}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PointscoredResource {

    private final Logger log = LoggerFactory.getLogger(PointscoredResource.class);

    private static final String ENTITY_NAME = "pointscored";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PointscoredRepository pointscoredRepository;

    public PointscoredResource(PointscoredRepository pointscoredRepository) {
        this.pointscoredRepository = pointscoredRepository;
    }

    /**
     * {@code POST  /pointscoreds} : Create a new pointscored.
     *
     * @param pointscored the pointscored to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pointscored, or with status {@code 400 (Bad Request)} if the pointscored has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pointscoreds")
    public Mono<ResponseEntity<Pointscored>> createPointscored(@RequestBody Pointscored pointscored) throws URISyntaxException {
        log.debug("REST request to save Pointscored : {}", pointscored);
        if (pointscored.getId() != null) {
            throw new BadRequestAlertException("A new pointscored cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pointscoredRepository
            .save(pointscored)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/pointscoreds/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /pointscoreds/:id} : Updates an existing pointscored.
     *
     * @param id the id of the pointscored to save.
     * @param pointscored the pointscored to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pointscored,
     * or with status {@code 400 (Bad Request)} if the pointscored is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pointscored couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pointscoreds/{id}")
    public Mono<ResponseEntity<Pointscored>> updatePointscored(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Pointscored pointscored
    ) throws URISyntaxException {
        log.debug("REST request to update Pointscored : {}, {}", id, pointscored);
        if (pointscored.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pointscored.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pointscoredRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return pointscoredRepository
                    .save(pointscored)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /pointscoreds/:id} : Partial updates given fields of an existing pointscored, field will ignore if it is null
     *
     * @param id the id of the pointscored to save.
     * @param pointscored the pointscored to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pointscored,
     * or with status {@code 400 (Bad Request)} if the pointscored is not valid,
     * or with status {@code 404 (Not Found)} if the pointscored is not found,
     * or with status {@code 500 (Internal Server Error)} if the pointscored couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pointscoreds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Pointscored>> partialUpdatePointscored(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Pointscored pointscored
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pointscored partially : {}, {}", id, pointscored);
        if (pointscored.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pointscored.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pointscoredRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Pointscored> result = pointscoredRepository
                    .findById(pointscored.getId())
                    .map(existingPointscored -> {
                        return existingPointscored;
                    })
                    .flatMap(pointscoredRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /pointscoreds} : get all the pointscoreds.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pointscoreds in body.
     */
    @GetMapping("/pointscoreds")
    public Mono<ResponseEntity<List<Pointscored>>> getAllPointscoreds(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Pointscoreds");
        return pointscoredRepository
            .count()
            .zipWith(pointscoredRepository.findAllBy(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /pointscoreds/:id} : get the "id" pointscored.
     *
     * @param id the id of the pointscored to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pointscored, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pointscoreds/{id}")
    public Mono<ResponseEntity<Pointscored>> getPointscored(@PathVariable Long id) {
        log.debug("REST request to get Pointscored : {}", id);
        Mono<Pointscored> pointscored = pointscoredRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(pointscored);
    }

    /**
     * {@code DELETE  /pointscoreds/:id} : delete the "id" pointscored.
     *
     * @param id the id of the pointscored to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pointscoreds/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePointscored(@PathVariable Long id) {
        log.debug("REST request to delete Pointscored : {}", id);
        return pointscoredRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
