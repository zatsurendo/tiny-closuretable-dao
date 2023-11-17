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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@IdClass(MenuItemPathId.class)
@Entity
public class MenuItemPath extends AbstractTreePath<MenuItemNode> {
    
    @Id
    @ManyToOne(targetEntity = MenuItemNode.class)
    @JoinColumn(name = "ancestor", nullable = false)
    @EqualsAndHashCode.Include
    private MenuItemNode ancestor;

    @Id
    @ManyToOne(targetEntity = MenuItemNode.class)
    @JoinColumn(name = "descendant", nullable = false)
    private MenuItemNode descendant;

    /**
     * Constructor
     * @param ancestor
     * @param descendant
     * @param depth
     */
    public MenuItemPath(MenuItemNode ancestor, MenuItemNode descendant, int depth) {
        super(depth);
        this.ancestor = ancestor;
        this.descendant = descendant;
    }

    /**
     * Constructor
     * @param ancestor
     * @param descendant
     * @param depth
     * @param orderIndex
     */
    public MenuItemPath(MenuItemNode ancestor, MenuItemNode descendant, int depth, int orderIndex) {
        super(depth, orderIndex);
        this.ancestor = ancestor;
        this.descendant = descendant;
    }

    /** {@inheritDoc} */
    @Override
    public MenuItemPath clone() {
        return new MenuItemPath(ancestor, descendant, depth, orderIndex);
    }
}
