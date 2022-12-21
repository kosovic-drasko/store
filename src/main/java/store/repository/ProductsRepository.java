package store.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import store.domain.Products;

/**
 * Spring Data JPA repository for the Products entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductsRepository extends JpaRepository<Products, Long>, JpaSpecificationExecutor<Products> {}
