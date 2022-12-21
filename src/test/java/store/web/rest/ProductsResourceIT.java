package store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import store.IntegrationTest;
import store.domain.Products;
import store.repository.ProductsRepository;
import store.service.criteria.ProductsCriteria;

/**
 * Integration tests for the {@link ProductsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductsResourceIT {

    private static final String DEFAULT_ARTICAL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ARTICAL_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_ARTICAL_PRICE = 1D;
    private static final Double UPDATED_ARTICAL_PRICE = 2D;
    private static final Double SMALLER_ARTICAL_PRICE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductsMockMvc;

    private Products products;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createEntity(EntityManager em) {
        Products products = new Products().articalName(DEFAULT_ARTICAL_NAME).articalPrice(DEFAULT_ARTICAL_PRICE);
        return products;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createUpdatedEntity(EntityManager em) {
        Products products = new Products().articalName(UPDATED_ARTICAL_NAME).articalPrice(UPDATED_ARTICAL_PRICE);
        return products;
    }

    @BeforeEach
    public void initTest() {
        products = createEntity(em);
    }

    @Test
    @Transactional
    void createProducts() throws Exception {
        int databaseSizeBeforeCreate = productsRepository.findAll().size();
        // Create the Products
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isCreated());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate + 1);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getArticalName()).isEqualTo(DEFAULT_ARTICAL_NAME);
        assertThat(testProducts.getArticalPrice()).isEqualTo(DEFAULT_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void createProductsWithExistingId() throws Exception {
        // Create the Products with an existing ID
        products.setId(1L);

        int databaseSizeBeforeCreate = productsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkArticalNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().size();
        // set the field null
        products.setArticalName(null);

        // Create the Products, which fails.

        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isBadRequest());

        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkArticalPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().size();
        // set the field null
        products.setArticalPrice(null);

        // Create the Products, which fails.

        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isBadRequest());

        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].articalName").value(hasItem(DEFAULT_ARTICAL_NAME)))
            .andExpect(jsonPath("$.[*].articalPrice").value(hasItem(DEFAULT_ARTICAL_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    void getProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get the products
        restProductsMockMvc
            .perform(get(ENTITY_API_URL_ID, products.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(products.getId().intValue()))
            .andExpect(jsonPath("$.articalName").value(DEFAULT_ARTICAL_NAME))
            .andExpect(jsonPath("$.articalPrice").value(DEFAULT_ARTICAL_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        Long id = products.getId();

        defaultProductsShouldBeFound("id.equals=" + id);
        defaultProductsShouldNotBeFound("id.notEquals=" + id);

        defaultProductsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductsShouldNotBeFound("id.greaterThan=" + id);

        defaultProductsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByArticalNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalName equals to DEFAULT_ARTICAL_NAME
        defaultProductsShouldBeFound("articalName.equals=" + DEFAULT_ARTICAL_NAME);

        // Get all the productsList where articalName equals to UPDATED_ARTICAL_NAME
        defaultProductsShouldNotBeFound("articalName.equals=" + UPDATED_ARTICAL_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByArticalNameIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalName in DEFAULT_ARTICAL_NAME or UPDATED_ARTICAL_NAME
        defaultProductsShouldBeFound("articalName.in=" + DEFAULT_ARTICAL_NAME + "," + UPDATED_ARTICAL_NAME);

        // Get all the productsList where articalName equals to UPDATED_ARTICAL_NAME
        defaultProductsShouldNotBeFound("articalName.in=" + UPDATED_ARTICAL_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByArticalNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalName is not null
        defaultProductsShouldBeFound("articalName.specified=true");

        // Get all the productsList where articalName is null
        defaultProductsShouldNotBeFound("articalName.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByArticalNameContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalName contains DEFAULT_ARTICAL_NAME
        defaultProductsShouldBeFound("articalName.contains=" + DEFAULT_ARTICAL_NAME);

        // Get all the productsList where articalName contains UPDATED_ARTICAL_NAME
        defaultProductsShouldNotBeFound("articalName.contains=" + UPDATED_ARTICAL_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByArticalNameNotContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalName does not contain DEFAULT_ARTICAL_NAME
        defaultProductsShouldNotBeFound("articalName.doesNotContain=" + DEFAULT_ARTICAL_NAME);

        // Get all the productsList where articalName does not contain UPDATED_ARTICAL_NAME
        defaultProductsShouldBeFound("articalName.doesNotContain=" + UPDATED_ARTICAL_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByArticalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalPrice equals to DEFAULT_ARTICAL_PRICE
        defaultProductsShouldBeFound("articalPrice.equals=" + DEFAULT_ARTICAL_PRICE);

        // Get all the productsList where articalPrice equals to UPDATED_ARTICAL_PRICE
        defaultProductsShouldNotBeFound("articalPrice.equals=" + UPDATED_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByArticalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalPrice in DEFAULT_ARTICAL_PRICE or UPDATED_ARTICAL_PRICE
        defaultProductsShouldBeFound("articalPrice.in=" + DEFAULT_ARTICAL_PRICE + "," + UPDATED_ARTICAL_PRICE);

        // Get all the productsList where articalPrice equals to UPDATED_ARTICAL_PRICE
        defaultProductsShouldNotBeFound("articalPrice.in=" + UPDATED_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByArticalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalPrice is not null
        defaultProductsShouldBeFound("articalPrice.specified=true");

        // Get all the productsList where articalPrice is null
        defaultProductsShouldNotBeFound("articalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByArticalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalPrice is greater than or equal to DEFAULT_ARTICAL_PRICE
        defaultProductsShouldBeFound("articalPrice.greaterThanOrEqual=" + DEFAULT_ARTICAL_PRICE);

        // Get all the productsList where articalPrice is greater than or equal to UPDATED_ARTICAL_PRICE
        defaultProductsShouldNotBeFound("articalPrice.greaterThanOrEqual=" + UPDATED_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByArticalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalPrice is less than or equal to DEFAULT_ARTICAL_PRICE
        defaultProductsShouldBeFound("articalPrice.lessThanOrEqual=" + DEFAULT_ARTICAL_PRICE);

        // Get all the productsList where articalPrice is less than or equal to SMALLER_ARTICAL_PRICE
        defaultProductsShouldNotBeFound("articalPrice.lessThanOrEqual=" + SMALLER_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByArticalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalPrice is less than DEFAULT_ARTICAL_PRICE
        defaultProductsShouldNotBeFound("articalPrice.lessThan=" + DEFAULT_ARTICAL_PRICE);

        // Get all the productsList where articalPrice is less than UPDATED_ARTICAL_PRICE
        defaultProductsShouldBeFound("articalPrice.lessThan=" + UPDATED_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByArticalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where articalPrice is greater than DEFAULT_ARTICAL_PRICE
        defaultProductsShouldNotBeFound("articalPrice.greaterThan=" + DEFAULT_ARTICAL_PRICE);

        // Get all the productsList where articalPrice is greater than SMALLER_ARTICAL_PRICE
        defaultProductsShouldBeFound("articalPrice.greaterThan=" + SMALLER_ARTICAL_PRICE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductsShouldBeFound(String filter) throws Exception {
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].articalName").value(hasItem(DEFAULT_ARTICAL_NAME)))
            .andExpect(jsonPath("$.[*].articalPrice").value(hasItem(DEFAULT_ARTICAL_PRICE.doubleValue())));

        // Check, that the count call also returns 1
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductsShouldNotBeFound(String filter) throws Exception {
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProducts() throws Exception {
        // Get the products
        restProductsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products
        Products updatedProducts = productsRepository.findById(products.getId()).get();
        // Disconnect from session so that the updates on updatedProducts are not directly saved in db
        em.detach(updatedProducts);
        updatedProducts.articalName(UPDATED_ARTICAL_NAME).articalPrice(UPDATED_ARTICAL_PRICE);

        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProducts.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getArticalName()).isEqualTo(UPDATED_ARTICAL_NAME);
        assertThat(testProducts.getArticalPrice()).isEqualTo(UPDATED_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, products.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(products))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(products))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts.articalName(UPDATED_ARTICAL_NAME);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getArticalName()).isEqualTo(UPDATED_ARTICAL_NAME);
        assertThat(testProducts.getArticalPrice()).isEqualTo(DEFAULT_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts.articalName(UPDATED_ARTICAL_NAME).articalPrice(UPDATED_ARTICAL_PRICE);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getArticalName()).isEqualTo(UPDATED_ARTICAL_NAME);
        assertThat(testProducts.getArticalPrice()).isEqualTo(UPDATED_ARTICAL_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, products.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(products))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(products))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeDelete = productsRepository.findAll().size();

        // Delete the products
        restProductsMockMvc
            .perform(delete(ENTITY_API_URL_ID, products.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
