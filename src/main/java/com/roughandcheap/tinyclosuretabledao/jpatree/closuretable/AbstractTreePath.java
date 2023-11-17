package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@MappedSuperclass
public abstract class AbstractTreePath<N extends ClosureTableTreeNode> implements TreePath<N> {
    
    @Column(nullable = false)
    protected int depth;
    @Column(columnDefinition = "integer default 0")
    protected int orderIndex;

    /**
     * Constructor
     */
    public AbstractTreePath() {
        this(0, 0);
    }

    /**
     * Constructor
     * @param depth
     */
    public AbstractTreePath(int depth) {
        this(depth, 0);
    }

    /**
     * Constructor
     * @param depth
     * @param orderIndex
     */
    public AbstractTreePath(int depth, int orderIndex) {
        this.depth = depth;
        this.orderIndex = orderIndex;
    }
}
