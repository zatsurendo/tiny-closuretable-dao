package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
public class PersonCtt extends AbstractClosureTableTreeNode<PersonCtt> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    public PersonCtt(String name) {
        super(name);
    }

    public PersonCtt(Long id, String name) {
        super(name);
        this.id = id;

    }
    /** {@inheritDoc} */
    @Override
    public PersonCtt clone() {
        return new PersonCtt(id, nodeName);
    }
}
