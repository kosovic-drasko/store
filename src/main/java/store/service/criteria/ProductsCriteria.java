package store.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link store.domain.Products} entity. This class is used
 * in {@link store.web.rest.ProductsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter articalName;

    private DoubleFilter articalPrice;

    private Boolean distinct;

    public ProductsCriteria() {}

    public ProductsCriteria(ProductsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.articalName = other.articalName == null ? null : other.articalName.copy();
        this.articalPrice = other.articalPrice == null ? null : other.articalPrice.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProductsCriteria copy() {
        return new ProductsCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getArticalName() {
        return articalName;
    }

    public StringFilter articalName() {
        if (articalName == null) {
            articalName = new StringFilter();
        }
        return articalName;
    }

    public void setArticalName(StringFilter articalName) {
        this.articalName = articalName;
    }

    public DoubleFilter getArticalPrice() {
        return articalPrice;
    }

    public DoubleFilter articalPrice() {
        if (articalPrice == null) {
            articalPrice = new DoubleFilter();
        }
        return articalPrice;
    }

    public void setArticalPrice(DoubleFilter articalPrice) {
        this.articalPrice = articalPrice;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductsCriteria that = (ProductsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(articalName, that.articalName) &&
            Objects.equals(articalPrice, that.articalPrice) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, articalName, articalPrice, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (articalName != null ? "articalName=" + articalName + ", " : "") +
            (articalPrice != null ? "articalPrice=" + articalPrice + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
