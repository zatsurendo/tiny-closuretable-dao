package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import com.roughandcheap.tinyclosuretabledao.jpatree.LinkableTreeNode;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * リンク可ノード Entity の抽象クラス
 * <p>
 * リンク可ノード Entity の共通フィールドを実装した抽象クラス。リンク可ノードの実装クラスはこのクラスを継承してださい。
 * 
 * @param <N> リンク可ノード
 * @author Zatsurendo
 * @since 2023.11.15
 */
@ToString
@MappedSuperclass
public abstract class AbstractLinkableClosureTableTreeNode<N extends AbstractLinkableClosureTableTreeNode<N>>
        extends AbstractClosureTableTreeNode<N>
        implements LinkableClosureTableTreeNode<N> {

    /** */
    @Setter
    @Getter
    @Column(nullable = false)
    protected LinkableTreeNode.Type type;

    /** */
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "link_to", nullable = true)
    protected N linkTo;

    /**
     * Constructor
     */
    public AbstractLinkableClosureTableTreeNode() {
        this(LinkableTreeNode.Type.NORMAL, null, "");
    }

    /**
     * Constructor
     * 
     * @param type
     * @param linkTo
     */
    public AbstractLinkableClosureTableTreeNode(LinkableTreeNode.Type type, N linkTo) {
        this(type, linkTo, "");
    }

    /**
     * constructor 
     * 
     * @param nodeName
     */
    public AbstractLinkableClosureTableTreeNode(String nodeName) {
        this(LinkableTreeNode.Type.NORMAL, null, nodeName);
    }

    /**
     * Constructor
     * 
     * @param type
     * @param linkTo
     * @param nodeName
     */
    public AbstractLinkableClosureTableTreeNode(LinkableTreeNode.Type type, N linkTo, String nodeName) {
        super(nodeName);
        this.type = type;
        this.linkTo = linkTo;
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeName() {
        if (this.getType() == LinkableTreeNode.Type.LINK) {
            return "@" + this.nodeName;
        }
        return this.nodeName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDeadLink() {
        if (type == LinkableTreeNode.Type.LINK && linkTo == null)
            return true;
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public N clone() {
        throw new UnsupportedOperationException();
    }
}
