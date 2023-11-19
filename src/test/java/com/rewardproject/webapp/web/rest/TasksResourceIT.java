package com.rewardproject.webapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.rewardproject.webapp.IntegrationTest;
import com.rewardproject.webapp.domain.Tasks;
import com.rewardproject.webapp.repository.EntityManager;
import com.rewardproject.webapp.repository.TasksRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TasksResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TasksResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_POINT = 1;
    private static final Integer UPDATED_POINT = 2;

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Tasks tasks;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tasks createEntity(EntityManager em) {
        Tasks tasks = new Tasks().name(DEFAULT_NAME).url(DEFAULT_URL).description(DEFAULT_DESCRIPTION).point(DEFAULT_POINT);
        return tasks;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tasks createUpdatedEntity(EntityManager em) {
        Tasks tasks = new Tasks().name(UPDATED_NAME).url(UPDATED_URL).description(UPDATED_DESCRIPTION).point(UPDATED_POINT);
        return tasks;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Tasks.class).block();
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
        tasks = createEntity(em);
    }

    @Test
    void createTasks() throws Exception {
        int databaseSizeBeforeCreate = tasksRepository.findAll().collectList().block().size();
        // Create the Tasks
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tasks))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeCreate + 1);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTasks.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testTasks.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTasks.getPoint()).isEqualTo(DEFAULT_POINT);
    }

    @Test
    void createTasksWithExistingId() throws Exception {
        // Create the Tasks with an existing ID
        tasks.setId(1L);

        int databaseSizeBeforeCreate = tasksRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tasks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTasks() {
        // Initialize the database
        tasksRepository.save(tasks).block();

        // Get all the tasksList
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
            .value(hasItem(tasks.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].point")
            .value(hasItem(DEFAULT_POINT));
    }

    @Test
    void getTasks() {
        // Initialize the database
        tasksRepository.save(tasks).block();

        // Get the tasks
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tasks.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tasks.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.point")
            .value(is(DEFAULT_POINT));
    }

    @Test
    void getNonExistingTasks() {
        // Get the tasks
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTasks() throws Exception {
        // Initialize the database
        tasksRepository.save(tasks).block();

        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();

        // Update the tasks
        Tasks updatedTasks = tasksRepository.findById(tasks.getId()).block();
        updatedTasks.name(UPDATED_NAME).url(UPDATED_URL).description(UPDATED_DESCRIPTION).point(UPDATED_POINT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTasks.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTasks))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTasks.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testTasks.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTasks.getPoint()).isEqualTo(UPDATED_POINT);
    }

    @Test
    void putNonExistingTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();
        tasks.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tasks.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tasks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();
        tasks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tasks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();
        tasks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tasks))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTasksWithPatch() throws Exception {
        // Initialize the database
        tasksRepository.save(tasks).block();

        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();

        // Update the tasks using partial update
        Tasks partialUpdatedTasks = new Tasks();
        partialUpdatedTasks.setId(tasks.getId());

        partialUpdatedTasks.url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTasks.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTasks))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTasks.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testTasks.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTasks.getPoint()).isEqualTo(DEFAULT_POINT);
    }

    @Test
    void fullUpdateTasksWithPatch() throws Exception {
        // Initialize the database
        tasksRepository.save(tasks).block();

        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();

        // Update the tasks using partial update
        Tasks partialUpdatedTasks = new Tasks();
        partialUpdatedTasks.setId(tasks.getId());

        partialUpdatedTasks.name(UPDATED_NAME).url(UPDATED_URL).description(UPDATED_DESCRIPTION).point(UPDATED_POINT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTasks.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTasks))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTasks.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testTasks.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTasks.getPoint()).isEqualTo(UPDATED_POINT);
    }

    @Test
    void patchNonExistingTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();
        tasks.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tasks.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tasks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();
        tasks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tasks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().collectList().block().size();
        tasks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tasks))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTasks() {
        // Initialize the database
        tasksRepository.save(tasks).block();

        int databaseSizeBeforeDelete = tasksRepository.findAll().collectList().block().size();

        // Delete the tasks
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tasks.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Tasks> tasksList = tasksRepository.findAll().collectList().block();
        assertThat(tasksList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
