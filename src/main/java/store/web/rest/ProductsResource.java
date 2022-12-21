package store.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import store.domain.Products;
import store.repository.ProductsRepository;
import store.service.ProductsQueryService;
import store.service.ProductsService;
import store.service.criteria.ProductsCriteria;
import store.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link store.domain.Products}.
 */
@RestController
@RequestMapping("/api")
public class ProductsResource {

    private final Logger log = LoggerFactory.getLogger(ProductsResource.class);

    private static final String ENTITY_NAME = "products";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductsService productsService;

    private final ProductsRepository productsRepository;

    private final ProductsQueryService productsQueryService;

    public ProductsResource(
        ProductsService productsService,
        ProductsRepository productsRepository,
        ProductsQueryService productsQueryService
    ) {
        this.productsService = productsService;
        this.productsRepository = productsRepository;
        this.productsQueryService = productsQueryService;
    }

    /**
     * {@code POST  /products} : Create a new products.
     *
     * @param products the products to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new products, or with status {@code 400 (Bad Request)} if the products has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/products")
    public ResponseEntity<Products> createProducts(@Valid @RequestBody Products products) throws URISyntaxException {
        log.debug("REST request to save Products : {}", products);
        if (products.getId() != null) {
            throw new BadRequestAlertException("A new products cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Products result = productsService.save(products);
        return ResponseEntity
            .created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /products/:id} : Updates an existing products.
     *
     * @param id the id of the products to save.
     * @param products the products to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated products,
     * or with status {@code 400 (Bad Request)} if the products is not valid,
     * or with status {@code 500 (Internal Server Error)} if the products couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<Products> updateProducts(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Products products
    ) throws URISyntaxException {
        log.debug("REST request to update Products : {}, {}", id, products);
        if (products.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, products.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Products result = productsService.update(products);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, products.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /products/:id} : Partial updates given fields of an existing products, field will ignore if it is null
     *
     * @param id the id of the products to save.
     * @param products the products to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated products,
     * or with status {@code 400 (Bad Request)} if the products is not valid,
     * or with status {@code 404 (Not Found)} if the products is not found,
     * or with status {@code 500 (Internal Server Error)} if the products couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/products/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Products> partialUpdateProducts(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Products products
    ) throws URISyntaxException {
        log.debug("REST request to partial update Products partially : {}, {}", id, products);
        if (products.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, products.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Products> result = productsService.partialUpdate(products);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, products.getId().toString())
        );
    }

    /**
     * {@code GET  /products} : get all the products.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/products")
    public ResponseEntity<List<Products>> getAllProducts(
        ProductsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Products by criteria: {}", criteria);
        Page<Products> page = productsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /products/count} : count all the products.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/products/count")
    public ResponseEntity<Long> countProducts(ProductsCriteria criteria) {
        log.debug("REST request to count Products by criteria: {}", criteria);
        return ResponseEntity.ok().body(productsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /products/:id} : get the "id" products.
     *
     * @param id the id of the products to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the products, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Products> getProducts(@PathVariable Long id) {
        log.debug("REST request to get Products : {}", id);
        Optional<Products> products = productsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(products);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" products.
     *
     * @param id the id of the products to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProducts(@PathVariable Long id) {
        log.debug("REST request to delete Products : {}", id);
        productsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
