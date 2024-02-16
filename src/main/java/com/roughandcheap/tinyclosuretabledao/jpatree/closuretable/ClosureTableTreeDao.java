package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import com.roughandcheap.tinyclosuretabledao.commons.DbSession;
import com.roughandcheap.tinyclosuretabledao.jpatree.AbstractTreeDao;
import com.roughandcheap.tinyclosuretabledao.jpatree.ClosureTableTreeNodeInfo;
import com.roughandcheap.tinyclosuretabledao.jpatree.JpaTreeException;

import jakarta.persistence.Query;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClosureTableTreeDao<N extends ClosureTableTreeNode, P extends TreePath<N>> extends AbstractTreeDao<N, P> {

    private static final Logger log = LoggerFactory.getLogger(ClosureTableTreeDao.class);
    /** {@code ClosureTableTreeNode} のサブクラス */
    protected final Class<N> treeNodeEntityClass;
    /** {@code TreePath} のサブクラス */
    protected final Class<P> treePathEntityClass;
    /** */
    private boolean removeReferencedNodes = false;

    /**
     * コンストラクタ
     * 
     * @param treeNodeEntityClass {@code ClosureTableTreeNode} のサブクラス
     * @param treePathEntityClass {@code TreePath} のサブクラス
     * @param dbSession           {@code DbSession} の実装クラス
     */
    public ClosureTableTreeDao(
            Class<N> treeNodeEntityClass,
            Class<P> treePathEntityClass,
            DbSession dbSession) {
        super(treeNodeEntityClass.getSimpleName(), treePathEntityClass.getSimpleName(), dbSession);
        this.treeNodeEntityClass = treeNodeEntityClass;
        this.treePathEntityClass = treePathEntityClass;
    }

    public boolean isRemoveReferenceNodes() {
        return removeReferencedNodes;
    }

    public void setRemoveReferenceNodes(boolean removeReferencedNodes) {
        this.removeReferencedNodes = removeReferencedNodes;
    }

    /**
     * 指定された {@ncode node} を削除します
     * <p>
     * {@code removeReferencedNodes} が TRUE の場合に削除します。
     * <p>
     * {@code removeReferencedNodes} に TRUE
     * をセットするか、{@link com.ranc.demotree.common.jpatree.ClosureTableTreeDao#removeNode(ClosureTableTreeNode, boolean)}
     * を使用してください。
     * 
     * @param nodeToRemove node to remove
     */
    public void removeNode(N nodeToRemove) {

        removeNode(nodeToRemove, false);
    }

    /**
     * 指定された {@ncode node} を削除します
     * <p>
     * {@code removeReferencedNodes} が TRUE の場合か、{@code force == true} の場合に削除します。
     * 
     * @param nodeToRemove
     * @param force
     * @exception JpaTreeException if TreePath exists.
     */
    public void removeNode(N nodeToRemove, boolean force) {
        if (isRemoveReferenceNodes() || force) {
            if (!isPersistent(nodeToRemove)) {
                throw new JpaTreeException("parent must be persist.");
            }
            if (isPathExists(nodeToRemove)) {
                throw new JpaTreeException("specified node is TreePath member. delete TreePath first.");
            }
            if (force) {
                log.info("force to remove {}", nodeToRemove.toString());
            }
            session.delete(nodeToRemove);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRoot(N entity) {

        if (!isPersistent(entity) || !isPathExists(entity)) {
            return false;
        }
        return 1 == getDescendantPaths(entity).size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChild(N parent) {

        if (!isPersistent(parent) || !isPathExists(parent)) {
            throw new JpaTreeException("parent must be persist.");
        }

        // TODO kesu
        printPaths(getTreePaths(parent), "hasChild:");

        return 1 < getTreePaths(parent).size();
    }

    /**
     * {@code node} のパスが存在するかを返す
     * <p>
     * {@code TreePath.descendant == node} に等しいレコードを検索して、1件以上のレコードが存在するか
     * どうかを確認する。
     * 
     * @param node
     * @return
     */
    @Override
    public boolean isPathExists(N node) {

        return 0 < getDescendantPaths(node).size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean parentContains(N parent, N descendant) {
        return getTree(parent).stream().filter(p -> p.equals(descendant)).findFirst().isPresent();
    }

    /** {@inheritDoc} */
    @Override
    public boolean childBelongsTo(N child, N parent) {
        return getPath(child).stream().filter(p -> p.equals(parent)).findFirst().isPresent();
    }

    /** {@inheritDoc} */
    @Override
    public N insert(N entity) {
        if (isPersistent(entity)) {
            throw new JpaTreeException("Specified entity is already persist");
        }
        return treeNodeEntityClass.cast(save(entity));
    }

    /** {@inheritDoc} */
    @Override
    public N update(N entity) {

        if (isPersistent(entity)) {
            return treeNodeEntityClass.cast(save(entity));
        }
        throw new JpaTreeException("Specified entity is not exist");
    }

    /** {@inheritDoc} */
    @Override
    public N insertOrUpdate(N entity) {
        Object o = session.save(entity);
        return treeNodeEntityClass.cast(o);
    }

    /** {@inheritDoc} */
    @Override
    public N find(Serializable id) {

        return treeNodeEntityClass.cast(session.get(treeNodeEntityClass, id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<N> findByNodeName(String nodeName) {

        String sqlString = "select n from " + nodeEntityName() + " n where nodeName = ?1";
        return (List<N>) session.queryList(sqlString, Arrays.asList(nodeName).toArray());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<N> findByNodeNameStartingWith(String startingWith) {

        startingWith = startingWith + '%';
        String sqlString = "select n from " + nodeEntityName() + " n where n.nodeName like ?1";
        return (List<N>) session.queryList(sqlString, Arrays.asList(startingWith).toArray());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<N> findByNodeNameContains(String contains) {

        contains = '%' + contains + '%';
        String sqlString = "select n from " + nodeEntityName() + " n where n.nodeName like ?1";
        return (List<N>) session.queryList(sqlString, Arrays.asList(contains).toArray());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<N> findByNodeNameEndsWith(String endsWith) {

        endsWith = '%' + endsWith;
        String sqlString = "select n from " + nodeEntityName() + " n where n.nodeName like ?1";
        return (List<N>) session.queryList(sqlString, Arrays.asList(endsWith).toArray());
    }

    /** {@inheritDoc} */
    @Override
    public N createRoot(N entity) {

        return addChild(null, entity);
    }

    /**
     * {@code parent} ノードの子要素として {@code child} ノードを追加する
     * 
     * @param parent N extends TreeNode
     * @param child  N extends TreeNode
     * @return N extends TreeNode
     * @throws JpaTreeException child が Null の場合
     * @throws JpaTreeException パスが未登録の場合
     */
    @Override
    public N addChild(N parent, N child) {

        return addChild(parent, child, 0);
    }

    @Override
    public N addChild(N parent, N child, int orderIndex) {

        if (child == null) {
            throw new JpaTreeException("Child node must not be null.");
        }
        if (!isPersistent(child)) {
            persist(child);
            flush();
        }
        if (isPathExists(child)) {
            throw new JpaTreeException("Child node already part of tree.");
        }

        List<P> parentPaths = new ArrayList<>();

        if (parent != null) {
            if (isPersistent(parent) && isPathExists(parent)) {
                parentPaths = getDescendantPaths(parent);
                clonePaths(child, parentPaths);
            } else {
                throw new JpaTreeException("parent should be persist");
            }
        }

        insertSelfReference(child, 0, orderIndex);
        return child;
    }

    /**
     * {@code parentPaths}の各要素のdescendantを{@code child}にした上でDBを更新する
     * 
     * @param child       ClosureTableTreeNode
     * @param parentPaths TreePath のコレクション
     */
    private void clonePaths(N child, List<P> parentPaths) {

        for (P path : parentPaths) {
            P treePath = newTreePathInstance();
            treePath.setAncestor(path.getAncestor());
            treePath.setDescendant(child);
            treePath.setDepth(path.getDepth() + 1);
            treePath = treePathEntityClass.cast(session.save(treePath));
        }
    }

    /**
     * {@code node} の自己参照パスをDBに保存する
     * 
     * @param node  ClosureTableTreeNode
     * @param depth int
     */
    private void insertSelfReference(N node, int depth, int orderIndex) {

        P path = newTreePathInstance();
        path.setAncestor(node);
        path.setDescendant(node);
        path.setDepth(depth);
        path.setOrderIndex(orderIndex);
        path = treePathEntityClass.cast(session.save(path));
        log.info("self referenced treePath: {}", path.toString());
    }

    /**
     * {@code treePathEntityClass} の新しいインスタンスを返します
     * 
     * @return TreePath
     */
    public P newTreePathInstance() {

        try {
            Constructor<?> constructor = treePathEntityClass.getConstructor();
            return treePathEntityClass.cast(constructor.newInstance());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new JpaTreeException(e.getMessage(), e.getCause());
        }
    }

    /** {@inheritDoc} */
    @Override
    public long getChildrenCount(N parent) {

        return getChildren(parent).size();
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<N> getChildren(N parent) {

        String sqlString = "select p.descendant from "
                + pathEntityName() + " p where p.ancestor = ?1 and p.depth = 1 "
                + "order by p.orderIndex";
        return (List<N>) session.queryList(sqlString, Arrays.asList(parent).toArray());
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<N> getChildrenWithParent(N parent) {

        String sqlString = "select p.descendant from "
                + pathEntityName() + " p where p.ancestor = ?1 and p.depth = 1 or p.depth = 0 "
                + "order by p.depth, p.orderIndex";
        return (List<N>) session.queryList(sqlString, Arrays.asList(parent).toArray());
    }

    /** {@inheritDoc} */
    @Override
    public int getLevel(N node) {

        if (!isPersistent(node) || !isPathExists(node)) {
            throw new JpaTreeException("specified path not registered. " + node.toString());
        }
        return getDescendantPaths(node).size();
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<N> getTree(N parent) {
        String queryString = "select p.descendant from " + pathEntityName() + " p "
                + "where p.ancestor = ?1 "
                + "order by p.depth asc";
        List<Object> params = List.of(parent);
        List<N> result = (List<N>) session.queryList(queryString, params.toArray());
        return ((result == null || result.isEmpty()) ? new ArrayList<>() : result);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<N> getPath(N node) {

        String queryString = "select p.ancestor from " + pathEntityName() + " p "
                + "where p.descendant = ?1 "
                + "order by p.depth desc";
        List<Object> params = List.of(node);
        List<N> result = (List<N>) session.queryList(queryString, params.toArray());
        return ((result == null || result.isEmpty()) ? new ArrayList<>() : result);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<P> getTreePaths(N parent) {

        String queryString = "select p from " + pathEntityName() + " p "
                + "where p.ancestor = ?1 "
                + "order by p.depth asc";
        List<Object> params = List.of(parent);
        return (List<P>) session.queryList(queryString, params.toArray());
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<P> getDescendantPaths(N node) {

        String queryString = "select p from " + pathEntityName() + " p "
                + "where p.descendant = ?1 "
                + "order by p.depth desc";
        List<Object> params = List.of(node);

        return (List<P>) session.queryList(queryString, params.toArray());
    }

    /** {@inheritDoc} */
    @Override
    public N getParent(N node) {

        if (!isPersistent(node) || !isPathExists(node)) {
            throw new JpaTreeException("specified path not registered.");
        }
        if (isRoot(node)) {
            return null;
        }
        String sqlString = "select p from " + pathEntityName() + " p "
                + "where p.descendant = ?1 and  p.depth = 1";
        @SuppressWarnings("unchecked")
        List<P> resultList = (List<P>) session.queryList(sqlString, Arrays.asList(node).toArray());
        return resultList.get(0).getAncestor();
    }

    /** {@inheritDoc} */
    @Override
    public List<N> getSiblings(N node) {

        if (!isPersistent(node) || !isPathExists(node)) {
            throw new JpaTreeException("specified path not registered.");
        }
        if (!isRoot(node)) {
            return getChildren(getParent(node));
        }
        return getRootNodes();
    }

    /** {@inheritDoc} */
    @Override
    public List<N> getRootNodes() {

        String queryString = "select p.descendant, count(p.ancestor) from " + pathEntityName() + " p "
                + "group by p.descendant having count(p.ancestor) = 1";
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = (List<Object[]>) session.queryList(queryString, null);
        List<N> result = new ArrayList<>();
        resultList.forEach(p -> {
            result.add(treeNodeEntityClass.cast(p[0]));
        });
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void moveTo(N parent, N moveTo) {

        if (parent == null || !isPersistent(parent) || !isPathExists(parent)) {
            // 移動元ノードがNullあるいは永続化されていない場合例外をスロー
            throw new JpaTreeException("parent should be persist.");
        }
        if (moveTo != null && (!isPersistent(moveTo) || !isPathExists(moveTo))) {
            // 移動先がNull出ない場合、永続化されていない場合例外をスロー
            throw new JpaTreeException("moveTo should be persist.");
        }
        if (moveTo != null && parent.equals(moveTo)) {
            // 移動元と移動先が同じ場合は例外をスロー
            throw new IllegalArgumentException("specified nodes are same node.");
        }
        if (isRoot(parent) && moveTo == null) {
            // 移動元がルートで、移動先が指定されていない場合、例外をスロー
            throw new JpaTreeException("parent is already root node.");
        }

        // 移動元の子ノード（移動元ノード含む）コレクション
        List<N> targetNodes = getTree(parent);

        // 移動元の子ノードツリーに moveTo が含まれている場合は例外をスロー
        if (moveTo != null
                && !targetNodes.isEmpty()
                && targetNodes.stream().anyMatch(n -> n.equals(moveTo))) {

            throw new JpaTreeException(
                    "The destination node must not be included in the source tree node. node = " + moveTo.toString());
        }

        // 移動先のノードパス（null を許容）
        List<N> moveToPath = moveTo == null ? List.of() : getPath(moveTo);
        // 移動元のノードパス（移動元ノード含む）コレクション
        List<N> parentPath = getPath(parent);
        // 移動元のノードパスから 移動元ノードを削除
        parentPath.remove(parent);

        // 新パスマップ
        Map<N, List<P>> newPathsMap = new HashMap<>();
        // 移動先のパスを生成
        for (N key : targetNodes) {
            // 各移動元子ノードのパス（parentPath）から、移動元パスを除いた部分を tail に保存
            List<N> tail = getPath(key).stream().filter(n -> {
                if (parentPath.stream().anyMatch(m -> m.equals(n))) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            // 移動先ノードパスとtailを一つにしたコレクション
            List<N> newOrder = Stream.concat(moveToPath.stream(), tail.stream()).collect(Collectors.toList());
            // 新しいパスを保持するコレクション
            List<P> newPaths = new ArrayList<>();
            // newOrder のサイズ（depth計算用）
            int count = newOrder.size();
            // パス生成ループ
            for (N node : newOrder) {
                // 新しいインスタンスを取得
                P path = newTreePathInstance();
                path.setAncestor(node);
                path.setDescendant(key);
                path.setDepth(--count);
                // key と node が同一（自己参照）の場合、オーダーインデックスを取得しセット
                path.setOrderIndex(node.equals(key) ? findTreePath(node, node).getOrderIndex() : 0);
                newPaths.add(path);
            }
            newPathsMap.put(key, newPaths);
        }

        int num = targetNodes.size() - 1;
        // targetNodes を逆順に並べ替えて、パスを削除
        IntStream.rangeClosed(0, num).mapToObj(i -> targetNodes.get(num - i)).forEach(nodeToDelete -> {
            deletePath(nodeToDelete);
        });

        // 新しいパスを保存
        newPathsMap.forEach((k, v) -> {
            v.forEach(session::save);
        });
        session.flush();
    }

    /** {@inheritDoc} */
    @Override
    public void deletePath(N descendant) {

        if (hasChild(descendant)) {
            throw new JpaTreeException("specified node has child(ren)");
        }
        List<P> paths = getDescendantPaths(descendant);
        paths.forEach(p -> session.delete(p));
        removeNode(descendant);
    }

    /** {@inheritDoc} */
    @Override
    public P findTreePath(P treePath) {

        return findTreePath(treePath.getAncestor(), treePath.getDescendant());
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public P findTreePath(N ancestor, N descendant) {

        String queryString = "select p from " + pathEntityName() + " p "
                + "where p.ancestor = ?1 "
                + "and   p.descendant = ?2";
        Object[] params = { ancestor, descendant };

        List<P> paths = (List<P>) session.queryList(queryString, params);
        if (paths == null || paths.isEmpty()) {
            return null;
        }
        return paths.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public long countPaths(N node) {

        List<P> paths = getDescendantPaths(node);
        return paths != null ? paths.size() : 0;
    }

    public List<ClosureTableTreeNodeInfo> getClosureTableTreeNodeInfo(int level) {

        if (level < 1) {
            throw new IllegalArgumentException("level should be lager than 0");
        }
        return getClosureTableTreeNodeInfo().stream().filter(info -> info.getDepth() >= level)
                .collect(Collectors.toList());
    }

    /**
     * 
     * @return
     */
    public List<ClosureTableTreeNodeInfo> getClosureTableTreeNodeInfo() {

        String queryString = "select p_1.descendant, "
                + "listagg( substr( concat( '000000', str( p_1.ancestor ) ), length( concat( '000000', str( p_1.ancestor ) ) ) - 5 ), '-' ) within  group ( order by p_1.depth desc ), "
                + "listagg( str(p_1.ancestor), ',' ) within  group ( order by p_1.depth desc ), "
                + "count(p_1) depth, "
                + "max(p_1.orderIndex) orderIndex "
                + "from " + pathEntityName() + " p_1 "
                + "group by p_1.descendant";
        return getTransFomrmed(session.getEntityManager().createQuery(queryString));
    }

    /**
     * 複数のノードのそれぞれを親とした場合の子ノードツリーを取得する
     * 
     * @param havingNodes
     * @return
     */
    public List<ClosureTableTreeNodeInfo> getClosureTableTreeNodeInfo(List<N> havingNodes) {

        if (havingNodes == null || havingNodes.isEmpty()) {
            return null;
        }
        List<N> treeNodes = havingNodes.stream().map(this::getTree).flatMap(Collection::stream)
                .collect(Collectors.toList());
        String queryString = "select p_1.descendant, "
                + "listagg( substr( concat( '000000', str( p_1.ancestor ) ), length( concat( '000000', str( p_1.ancestor ) ) ) - 5 ), '-' ) within  group ( order by p_1.depth desc ), "
                + "listagg( str(p_1.ancestor), ',' ) within  group ( order by p_1.depth desc ), "
                + "count(p_1) depth, "
                + "max(p_1.orderIndex) orderIndex "
                + "from " + pathEntityName() + " p_1 "
                + "where p_1.descendant in(?1) "
                + "group by p_1.descendant";
        List<ClosureTableTreeNodeInfo> resultList = getTransFomrmed(
                session.getEntityManager().createQuery(queryString).setParameter(1, treeNodes));
        long d = resultList.get(0).getDepth() - 1L;
        return resultList.stream().map(n -> {
            n.setDepth(n.getDepth() - d);
            return n;
        }).collect(Collectors.toList());
    }

    /**
     * 
     * @param query
     * @return
     */
    private List<ClosureTableTreeNodeInfo> getTransFomrmed(Query query) {

        @SuppressWarnings("unchecked")
        List<ClosureTableTreeNodeInfo> pathDepths = query.unwrap(org.hibernate.query.Query.class)
                .setTupleTransformer((tuple, aliases) -> {
                    return new ClosureTableTreeNodeInfo(
                            (ClosureTableTreeNode) tuple[0],
                            (String) tuple[1],
                            (String) tuple[2],
                            (long) tuple[3],
                            (int) tuple[4]);
                }).getResultList();
        return pathDepths.stream().sorted(Comparator.comparing(ClosureTableTreeNodeInfo::getTreePaths))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<N> findAllTreeNode() {

        String queryString = "select n from " + nodeEntityName() + " n";
        return (List<N>) session.queryList(queryString, null);
    }

    @SuppressWarnings("unchecked")
    public List<P> findAllTreePath() {

        String queryString = "select p from " + pathEntityName() + " p";
        return (List<P>) session.queryList(queryString, null);
    }

    public void printNodeInfo(List<ClosureTableTreeNodeInfo> nodeInfo) {

        String top = "+- id -+- node_name ---------------------------------+- path -------------------------------------------------+ depth +";
        String bot = "+------+---------------------------------------------+--------------------------------------------------------+-------+";
        log.debug(top);
        nodeInfo.forEach(n -> {
            String line = "| " + format(n.getDescendant().getId(), 4) + " | "
                    + format(tabSpace((int) n.getDepth()) + "- " + n.getDescendant().getNodeName(), 43) + " | "
                    + format(n.getTreePaths(), 54) + " | "
                    + format(n.getDepth(), 5) + " |";
            log.debug(line);
        });
        log.debug(bot);
    }

    private String tabSpace(int times) {
        return String.join("", Collections.nCopies(times, "  "));
    }

    public void printPaths(List<P> paths, String title) {

        String top = "+ ancestor +-node_name -------------------------------+ descendant + node_name -------------------------------+ depth + index +";
        String bot = "+----------+------------------------------------------+------------+------------------------------------------+-------+-------+";
        log.debug("====");
        log.debug(title);
        log.debug("====");
        log.debug(top);
        paths.forEach(p -> {
            String line = "| " + format(p.getAncestor().getId(), 8) + " | "
                    + format(p.getAncestor().getNodeName(), 41) + " | "
                    + format(p.getDescendant().getId(), 10) + " | "
                    + format(p.getDescendant().getNodeName(), 41) + " | "
                    + format(p.getDepth(), 5) + " | "
                    + format(p.getOrderIndex(), 5) + " |";
            log.debug(line);
        });
        log.debug(bot);
    }

    private String format(Serializable target, int length) {

        if (target instanceof String) {
            return format((String) target, length);
        } else if (target instanceof Number) {
            return format((Number) target, length);
        } else {
            return format(target.toString(), length);
        }
    }

    private String format(String target, int length) {
        int byteDiff = (getByLength(target, Charset.forName("UTF-8")) - target.length()) / 2;
        return String.format("%-" + (length - byteDiff) + "s", target);
    }

    private String format(Number target, int length) {
        String str = String.valueOf(target);
        int byteDiff = (getByLength(str, Charset.forName("UTF-8")) - str.length()) / 2;
        return String.format("%" + (length - byteDiff) + "d", target);
    }

    private int getByLength(String string, Charset charset) {
        return string.getBytes(charset).length;
    }

}
