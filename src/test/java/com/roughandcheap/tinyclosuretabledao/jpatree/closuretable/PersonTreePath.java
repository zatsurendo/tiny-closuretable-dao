package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@IdClass(PersonTreePathId.class)
@Entity
public class PersonTreePath extends AbstractTreePath<PersonCtt> {
    
    @Id
    @ManyToOne(targetEntity = PersonCtt.class)
    @JoinColumn(name = "ancestor", nullable = false)
    @EqualsAndHashCode.Include
    private PersonCtt ancestor;
    @Id
    @ManyToOne(targetEntity = PersonCtt.class)
    @JoinColumn(name = "descendant", nullable = false)
    @EqualsAndHashCode.Include
    private PersonCtt descendant;

    public PersonTreePath(PersonCtt ancestor, PersonCtt descendant, int depth) {
        super(depth);
        this.ancestor = ancestor;
        this.descendant = descendant;
    }

    public PersonTreePath(PersonCtt ancestor, PersonCtt descendant, int depth, int orderIndex) {
        super(depth, orderIndex);
        this.ancestor = ancestor;
        this.descendant = descendant;
    }

    public PersonTreePath clone() {
        return new PersonTreePath(ancestor, descendant, depth, orderIndex);
    }
}
