package com.rewardproject.webapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.rewardproject.webapp.IntegrationTest;
import com.rewardproject.webapp.domain.Pointscored;
import com.rewardproject.webapp.repository.EntityManager;
import com.rewardproject.webapp.repository.PointscoredRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link PointscoredResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PointscoredResourceIT {

    private static final String ENTITY_API_URL = "/api/pointscoreds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PointscoredRepository pointscoredRepository;

    @Mock
    private PointscoredRepository pointscoredRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pointscored pointscored;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pointscored createEntity(EntityManager em) {
        Pointscored pointscored = new Pointscored();
        return pointscored;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pointscored createUpdatedEntity(EntityManager em) {
        Pointscored pointscored = new Pointscored();
        return pointscored;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Pointscored.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        pointscored = createEntity(em);
    }

    @Test
    void createPointscored() throws Exception {
        int databaseSizeBeforeCreate = pointscoredRepository.findAll().collectList().block().size();
        // Create the Pointscored
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pointscored))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeCreate + 1);
        Pointscored testPointscored = pointscoredList.get(pointscoredList.size() - 1);
    }

    @Test
    void createPointscoredWithExistingId() throws Exception {
        // Create the Pointscored with an existing ID
        pointscored.setId(1L);

        int databaseSizeBeforeCreate = pointscoredRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pointscored))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPointscoreds() {
        // Initialize the database
        pointscoredRepository.save(pointscored).block();

        // Get all the pointscoredList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(pointscored.getId().intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPointscoredsWithEagerRelationshipsIsEnabled() {
        when(pointscoredRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(pointscoredRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPointscoredsWithEagerRelationshipsIsNotEnabled() {
        when(pointscoredRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(pointscoredRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPointscored() {
        // Initialize the database
        pointscoredRepository.save(pointscored).block();

        // Get the pointscored
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pointscored.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pointscored.getId().intValue()));
    }

    @Test
    void getNonExistingPointscored() {
        // Get the pointscored
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPointscored() throws Exception {
        // Initialize the database
        pointscoredRepository.save(pointscored).block();

        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();

        // Update the pointscored
        Pointscored updatedPointscored = pointscoredRepository.findById(pointscored.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPointscored.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPointscored))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
        Pointscored testPointscored = pointscoredList.get(pointscoredList.size() - 1);
    }

    @Test
    void putNonExistingPointscored() throws Exception {
        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();
        pointscored.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pointscored.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pointscored))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPointscored() throws Exception {
        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();
        pointscored.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pointscored))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPointscored() throws Exception {
        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();
        pointscored.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pointscored))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePointscoredWithPatch() throws Exception {
        // Initialize the database
        pointscoredRepository.save(pointscored).block();

        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();

        // Update the pointscored using partial update
        Pointscored partialUpdatedPointscored = new Pointscored();
        partialUpdatedPointscored.setId(pointscored.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPointscored.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPointscored))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
        Pointscored testPointscored = pointscoredList.get(pointscoredList.size() - 1);
    }

    @Test
    void fullUpdatePointscoredWithPatch() throws Exception {
        // Initialize the database
        pointscoredRepository.save(pointscored).block();

        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();

        // Update the pointscored using partial update
        Pointscored partialUpdatedPointscored = new Pointscored();
        partialUpdatedPointscored.setId(pointscored.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPointscored.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPointscored))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
        Pointscored testPointscored = pointscoredList.get(pointscoredList.size() - 1);
    }

    @Test
    void patchNonExistingPointscored() throws Exception {
        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();
        pointscored.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pointscored.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pointscored))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPointscored() throws Exception {
        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();
        pointscored.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pointscored))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPointscored() throws Exception {
        int databaseSizeBeforeUpdate = pointscoredRepository.findAll().collectList().block().size();
        pointscored.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pointscored))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pointscored in the database
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePointscored() {
        // Initialize the database
        pointscoredRepository.save(pointscored).block();

        int databaseSizeBeforeDelete = pointscoredRepository.findAll().collectList().block().size();

        // Delete the pointscored
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pointscored.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Pointscored> pointscoredList = pointscoredRepository.findAll().collectList().block();
        assertThat(pointscoredList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
