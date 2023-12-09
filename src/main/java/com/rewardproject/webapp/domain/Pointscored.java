package com.rewardproject.webapp.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Pointscored.
 */
@Table("pointscored")
public class Pointscored implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    private Tasks name;

    @Column("name_id")
    private Long nameId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pointscored id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tasks getName() {
        return this.name;
    }

    public void setName(Tasks tasks) {
        this.name = tasks;
        this.nameId = tasks != null ? tasks.getId() : null;
    }

    public Pointscored name(Tasks tasks) {
        this.setName(tasks);
        return this;
    }

    public Long getNameId() {
        return this.nameId;
    }

    public void setNameId(Long tasks) {
        this.nameId = tasks;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pointscored)) {
            return false;
        }
        return id != null && id.equals(((Pointscored) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pointscored{" +
            "id=" + getId() +
            "}";
    }
}
