package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 閉包テーブルツリーノードの抽象クラス
 * <p>
 * 閉包テーブルツリーノードの実装クラスに共通のプロパティの実装をこのクラスを継承することで強制します。
 * 
 * @param <N> リンク可ノード
 * @author Zatsurendo
 * @since 2023.11.15
 */
@Setter
@Getter
@ToString
@MappedSuperclass
public abstract class AbstractClosureTableTreeNode<N extends AbstractClosureTableTreeNode<N>>
        implements ClosureTableTreeNode {

    /** Node name */
    @Column(name = "node_name", nullable = false, length = 100)
    protected String nodeName;

    /**
     * Constructor
     */
    public AbstractClosureTableTreeNode() {
        this("");
    }

    /**
     * Constructor
     * 
     * @param nodeName
     */
    public AbstractClosureTableTreeNode(String nodeName) {
        this.nodeName = nodeName;
    }

    /** {@inheritDoc} */
    @Override
    public N clone() {
        throw new UnsupportedOperationException();
    }
}
