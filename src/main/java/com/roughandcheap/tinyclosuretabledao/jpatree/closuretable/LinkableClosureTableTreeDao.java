package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.roughandcheap.tinyclosuretabledao.commons.DbSession;
import com.roughandcheap.tinyclosuretabledao.jpatree.JpaTreeException;
import com.roughandcheap.tinyclosuretabledao.jpatree.LinkableTreeDao;
import com.roughandcheap.tinyclosuretabledao.jpatree.LinkableTreeNode;

public class LinkableClosureTableTreeDao<N extends AbstractLinkableClosureTableTreeNode<N>, P extends TreePath<N>>
        extends ClosureTableTreeDao<N, P> implements LinkableTreeDao<N, P> {

    private static final Logger log = LoggerFactory.getLogger(LinkableClosureTableTreeDao.class);

    /**
     * コンストラクタ
     * 
     * @param treeNodeClass ノードクラス
     * @param treePathClass パスクラス
     * @param session       DBセッション
     */
    public LinkableClosureTableTreeDao(
            Class<N> treeNodeEntityClass,
            Class<P> treePathEntityClass,
            DbSession dbSession) {
        super(treeNodeEntityClass, treePathEntityClass, dbSession);
    }

    
    /**
     * {@code parent} ノードの子要素として {@code child} ノードを追加する
     * <p>
     * {@inheritDoc}
     * @param parent N extends TreeNode
     * @param child N extends TreeNode
     * @return N extends TreeNode
     * @throws LinkableJpaTreeException parent がリンクの場合
     * @throws JpaTreeException child が Null の場合
     * @throws JpaTreeException パスが未登録の場合
     */
    @Override
    public N addChild(N parent, N child) {

        if (parent != null && isLink(parent)) {
            throw new LinkableJpaTreeException("Type.LINK node cannot add child.");
        }
        return super.addChild(parent, child);
    }

    /**
     * {@code parent} がリンクかどうかを返します
     * <p>
     * このメソッドは {@code node.type} が {@code LinkableTreeNode.Type.LINK} であるかどうかを返します。
     * <p>
     * 実際にリンク先ノードが設定されているかどうかは返しません。
     * {@link com.ranc.demotree.common.jpatree.LinkableTreeNode#isDeadLink} を使用してください。
     * @param node
     * @return true if type = Link, false if not.
     */
    @Override
    public boolean isLink(N node) {

        return node.getType() == LinkableTreeNode.Type.LINK;
    }

    /** {@inheritDoc} */
    @Override
    public void removeNode(N nodeToRemove) {

        removeNode(nodeToRemove, false);
    }

    /** {@inheritDoc} */
    @Override
    public void removeNode(N nodeToRemove, boolean force) {

        if (isRemoveReferenceNodes() || force) {
            if (!findLinkTo(nodeToRemove).isEmpty()) {
                throw new LinkableJpaTreeException("specified node is whome linkTo of other nodes. " + nodeToRemove.toString());
            }
            super.removeNode(nodeToRemove, force);
        }
    }

    /** {@inheritDoc} */
    @Override
    public N addLinkTo(N node, N linkTo) {
        
        return setLinkTo(node, linkTo);
    }

    /** {@inheritDoc} */
    @Override
    public N removeLinkTo(N node) {
        
        return setLinkTo(node, null);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    private N setLinkTo(N node, N linkTo) {

        if (linkTo != null && linkTo.getType() == LinkableTreeNode.Type.LINK) {
            throw new LinkableJpaTreeException("Linking with another link is not allowed.");
        }
        if (linkTo != null && !isPersistent(linkTo)) {
            throw new LinkableJpaTreeException("linkTo must be persist.");
        }
        if (node == null) {
            throw new LinkableJpaTreeException("node must not be null.");
        }
        if (isPersistent(node) && hasChild(node)) {
            throw new LinkableJpaTreeException("node has child.");
        }
        if (node.equals(linkTo)) {
            throw new LinkableJpaTreeException("same node cannot make link.");
        }
        node.setLinkTo(linkTo);
        node.setType(linkTo == null ? LinkableTreeNode.Type.NORMAL : LinkableTreeNode.Type.LINK);
        N result = (N) session.save(node);
        log.info("{}" , node.toString());
        return result;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<N> findLinkTo(N linkTo) {

        String queryString = "select n from " + nodeEntityName() + " n where n.linkTo = ?1";
        return (List<N>) session.queryList(queryString, Arrays.asList(linkTo).toArray());
    }

    /** {@inheritDoc} */
    @Override
    public void removeLinkToFromNode(N linkTo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeLinkToFromNode'");
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<N> findDeadLinks() {
        
        String queryString = "select n from " + nodeEntityName() + " n where n.linkTo = null and n.type = LINK";
        return (List<N>) session.queryList(queryString, null);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<N> findLinked() {
        
        String queryString = "select n from " + nodeEntityName() + " n "
                + "join " + nodeEntityName() + " l on l.linkTo.id = n.id ";
        return (List<N>) session.queryList(queryString, null);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<N> findLinking() {

        String queryString = "select n from " + nodeEntityName() + " n "
                + "join linkTo l ";
        return (List<N>) session.queryList(queryString, null);
    }

}
