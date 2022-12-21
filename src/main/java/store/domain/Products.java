package store.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Products.
 */
@Entity
@Table(name = "products")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "artical_name", nullable = false, unique = true)
    private String articalName;

    @NotNull
    @Column(name = "artical_price", nullable = false)
    private Double articalPrice;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Products id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArticalName() {
        return this.articalName;
    }

    public Products articalName(String articalName) {
        this.setArticalName(articalName);
        return this;
    }

    public void setArticalName(String articalName) {
        this.articalName = articalName;
    }

    public Double getArticalPrice() {
        return this.articalPrice;
    }

    public Products articalPrice(Double articalPrice) {
        this.setArticalPrice(articalPrice);
        return this;
    }

    public void setArticalPrice(Double articalPrice) {
        this.articalPrice = articalPrice;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Products)) {
            return false;
        }
        return id != null && id.equals(((Products) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Products{" +
            "id=" + getId() +
            ", articalName='" + getArticalName() + "'" +
            ", articalPrice=" + getArticalPrice() +
            "}";
    }
}
