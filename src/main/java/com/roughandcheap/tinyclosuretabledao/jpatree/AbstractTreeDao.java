package com.roughandcheap.tinyclosuretabledao.jpatree;



import com.roughandcheap.tinyclosuretabledao.commons.DbSession;
import com.roughandcheap.tinyclosuretabledao.jpatree.closuretable.TreePath;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Common functionalities for a TreeDao implementation.
 * <p/>
 * Mind that concurrency is not handled in any way.
 * The caller is expected to provide a transaction around every DAO write-method.
 * DAO write-methods execute more than one JPQL statement when called!
 * 
 * @author Fritz Ritzberger, 27.10.2012
 * 
 * @param <N> the tree node type handled by this DAO.
 */
@Setter
@Getter
@ToString
public abstract class AbstractTreeDao<N extends TreeNode, P extends TreePath<N>> implements TreeDao<N, P> {

    /** DbSession インスタンス */
    protected final DbSession session;
    /** ノードエンティティ名 */
    private final String nodeEntityName;
    /** パスエンティティ名 */
    private final String pathEntityName;

    /**
     * コンストラクタ
     * @param nodeEntityName ノードエンティティ名
     * @param pathEntityName パスエンティティ名
     * @param session DbSession インスタンス
     */
    public AbstractTreeDao(String nodeEntityName, String pathEntityName, DbSession session) {
        this.nodeEntityName = nodeEntityName;
        this.pathEntityName = pathEntityName;
        this.session = session;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPersistent(N entity) {
        return entity.getId() != null;
    }

    /**
     * ノードを保存
     * @param node
     * @return
     */
    public Object save(N node) {
        Object o = session.save(node);
        flush();
        return o;
    }

    /**
     * ノードを永続化コンテキストに保存
     * @param node
     */
    public void persist(N node) {
        session.persist(node);
    }

    /**
     * 永続化コンテキストをフラッシュ
     */
    public void flush() {
        session.flush();
    }

    /**
     * ノードエンティティ名を返す
     * @return ノードエンティティ名
     */
    protected String nodeEntityName() {
        return nodeEntityName;
    }

    /**
     * パスエンティティ名を返す
     * @return パスエンティティ名
     */
    protected String pathEntityName() {
        return pathEntityName;
    }
}
