package com.roughandcheap.tinyclosuretabledao.jpatree;

import java.io.Serializable;
import java.util.List;

import com.roughandcheap.tinyclosuretabledao.jpatree.closuretable.TreePath;

import jakarta.annotation.Nullable;

/**
 * Tree DAO provides hierarchical access to records in a JPA database layer.
 * There can be more than one tree in a database table.
 * A record can not be in several trees at the same time.
 * A record can have one parent or no parent (root), but not several parents.
 * Normally there is no record that is not in a tree, except it is a root without children.
 * <p/>
 * This interface abstracts the functionality of a tree DAO so that the underlying
 * implementation can be replaced. In this library two such DAOs are implemented, 
 * one for a nested-sets-tree, one for a closure-table-tree.
 * <p/>
 * For more information see
 * <a href="http://de.slideshare.net/billkarwin/models-for-hierarchical-data">Bill Karwin slides "Models for Hierarchical Data"</a>, page 40, and
 * <a href="http://de.slideshare.net/billkarwin/sql-antipatterns-strike-back">Bill Karwin slides "SQL Antipatterns strike back"</a>, page 68.
 * 
 * @author Fritz Ritzberger, 19.10.2012
 * @author Zatsurendo 11.15.2023
 * 
 * @param N the tree node type managed by this DAO.
 * @param P the tree path type managed by this DAO.
 */
public interface TreeDao<N extends TreeNode, P extends TreePath<N>> {
    
    /**
     * 指定された {@code entity} が永続化されているかどうかを返す
     * @param entity  &lt; N extends TreeNode &gt;
     * @return true:永続化されている, false:永続化されたいない
     */
    boolean isPersistent(N entity);

    /**
     * 指定された {@code entity} がルートであるかどうかを返す
     * <p>{@code entity} がルートであるかどうかは、パスの祖先{@code ancestor}
     * と{@code descendant} が共に同じで、{@code depth}が 0 であるかどうか。
     * @param entity N extends TreeNode
     * @return true:ルート要素, false:ルート要素ではない
     */
    boolean isRoot(N entity);

    /**
     * 指定された {@code parent} が子要素を持っているかどうかを返す
     * @param parent N extends TreeNode
     * @return true:もっている, false:もっていない
     */
    boolean hasChild(N parent);

    /**
     * 指定されたノードのパスが存在するかどうかを返す
     * @param node
     * @return true:存在する, false: 存在しない
     */
    boolean isPathExists(N node);

    /**
     * parent に descendant が存在するかどうかを返す
     * <p>
     * descendant が parent に属しているのかどうかを返します。
     * @param parent
     * @param descendant
     * @return true:属している, false:属していない
     */
    boolean parentContains(N parent, N descendant);

    /**
     * child が ancestor に属しているかを返す
     * 
     * @param child
     * @param ancestor
     * @return
     */
    boolean childBelongsTo(N child, N ancestor);

    /**
     * {@code entity}を挿入する
     * @param entity N extends TreeNode
     * @retur N extends TreeNode
     */
    N insert(N entity);

    /**
     * {@code entity}を更新する
     * @param entity N extends TreeNode
     * @return N extends TreeNode
     */
    N update(N entity);

    
    /**
     * パスを保存
     * @param path
     * @return
     */
    P save(P path);
    
    /**
     * {@code entity} を挿入、あるいは更新する
     * @param entity N extends TreeNode
     * @return N extends TreeNode
     */
    N insertOrUpdate(N entity);

    /**
     * {@code id} を持つ要素 N を返す
     * @param id ID
     * @return N extends TreeNode
     */
    N find(Serializable id);

    /**
     * 
     * @param nodeName
     * @return
     */
    List<N> findByNodeName(String nodeName);

    /**
     * 
     * @param startingWith
     * @return
     */
    List<N> findByNodeNameStartingWith(String startingWith);

    /**
     * 
     * @param contains
     * @return
     */
    List<N> findByNodeNameContains(String contains);

    /**
     * 
     * @param endsWith
     * @return
     */
    List<N> findByNodeNameEndsWith(String endsWith);

    /**
     * {@code entity} ルート要素とした TreePath を作成する
     * @param entity N extends TreeNode
     * @return N extends TreeNode
     */
    N createRoot(N entity);

    /**
     * {@code parent} ノードの子要素として {@code child} ノードを追加する
     * 
     * @param parent N extends TreeNode
     * @param child N extends TreeNode
     * @return N extends TreeNode
     * 
     */
    N addChild(N parent, N child);

    /**
     * 
     * @param parent
     * @param child
     * @param orderIndex
     * @return
     */
    N addChild(N parent, N child, int orderIndex);

    /**
     * 指定されたノードの直下の子ノードの数を返します。
     * @param parent
     * @return
     */
    long getChildrenCount(N parent);

    /**
     * 指定されたノードの直下の子ノードを、{@code orderIndex} の順で並べ替えたコレクションを返します
     * @param parent
     * @return
     */
    List<N> getChildren(N parent);

    /**
     * 指定されたノードと直下の子ノードを、{@code orderIndex} の順で並べ替えたコレクションを返します
     * @param parent
     * @return
     */
    List<N> getChildrenWithParent(N parent);

    /**
     * 指定されたノードのパス上の深さを返します
     * <p>
     * 深さは、ルートノードの深さを 1 として算出します。
     * @param node
     * @return
     */
    int getLevel(N node);

    /**
     * 
     * @param parent
     * @return
     */
    List<N> getTree(N parent);

    /**
     * 
     * @param node
     * @return
     */
    List<N> getPath(N node);

    /**
     * {@code parent}を含む子ノードのツリーを返す
     * @param parent N extends TreeNode
     * @return Collection of P extends TreePath
     */
    List<P> getTreePaths(N parent);

    /**
     * ルートパスから {@code parent} までのパスをコレクションで返す
     * @param node N extends TreeNode
     * @return Collection of P extends TreePath
     */
    List<P> getDescendantPaths(N node);

    /**
     * 指定されたノードの親のノードを返します
     * <p>指定されたノードが親が存在しない（ルートパスである）場合は null を返します。
     * @param node
     * @return
     */
    N getParent(N node);

    /**
     * 指定されたノードの兄弟ノードをコレクションで返します
     * @param node
     * @return
     */
    List<N> getSiblings(N node);

    /**
     * ルートノードを取得します
     * @return
     */
    List<N> getRootNodes();

    /**
     * {@code parent} を含む子ノードのツリーを {@code moveTo} の直下に移動する
     * <p>移動先を{@code null} に指定した場合、ルートに移動します。子ノードが存在しない場合は、
     * {@code parent} ノードを指定先に移動します。
     * 
     * @param parent N extends TreeNode 移動元
     * @param moveTo N extends TreeNode 移動先
     * @param orderIndex TreePath(parent, parent) 時の純
     */
    void moveTo(N parent, N moveTo, int orderIndex);

    /**
     * {@code parent} を含む子ノードのツリーを {@code moveTo} の直下に移動する
     * <p>移動先を{@code null} に指定した場合、ルートに移動します。子ノードが存在しない場合は、
     * {@code parent} ノードを指定先に移動します。
     * @param parent N extends TreeNode 移動元
     * @param moveTo N extends TreeNode 移動先
     */
    void moveTo(N parent, N moveTo);

    /**
     * {@code sourceId} で与えられたノードを {@code parentId} ノードの直下に移動する
     * <p>
     * souceId のノードがパスに存在する場合は、実際の親ノードと与えられた parentId のノードが
     * 同一の場合は、単純に ソースノードの orderIndex を更新するのみで終わる。
     * <p>
     * 異なる場合は、ソースノードとその下に続くツリーを移動先のノードの下に移動する。
     * <p>
     * ソースノードのパスが存在していない場合は、ソースノードを親ノードの直下に addChild する。
     * <p>
     * いずれの場合でも、ソースノードのパスの orderIndex は更新される。
     * @param sourceId
     * @param parentId
     * @param orderIndex
     * @throws  JpaTreeException    移動時に例外が発生した場合
     */
    void procParent(Serializable sourceId, @Nullable Serializable parentId, int orderIndex);

    /**
     * {@code TreePath.descendant} が {@code descendant} であるパスを削除する
     * @param descendant N extends TreeNode
     */
    void deletePath(N descendant);

    /**
     * {@code TreePath.descendant} が {@code descendant} であるパスを削除する
     * @param descendant N extends TreeNode
     * @param force true:強制削除、false:強制削除しない
     */
    void deletePath(N descendant, boolean force);

    /**
     * {@code P} に一致する TreePath を検索する
     * <p>一致するレコードが存在しない場合は、{@code null} を返す。
     * @param treePath P extends TreePath
     * @return P extends TreePath
     */
    P findTreePath(P treePath);

    /**
     * {@code ancestor} {@code descendang} に一致する TreePath を検索する
     * @param ancestor P extends TreePath 祖先
     * @param descendant P extends TreePath 仕損
     * @return P extends TreePath
     */
    P findTreePath(N ancestor, N descendant);

    /**
     * {@code node} 
     * @param node
     * @return
     */
    long countPaths(N node);

    /**
     * TreeNodeのすべてのノードをNestedTreeNodeで返す
     * @return
     */
    List<NestedTreeNode<N>> getNestedTreeNodeList();

    /**
     * {@code nodes} の子ノード全てを NestedTreeNode で返す
     * @param nodes
     * @return
     */
    List<NestedTreeNode<N>> getNestedTreeNodeList(List<N> nodes);

    /**
     * {@code node} を親とする子ノード全てを NestedNodeList で返す
     * @param node
     * @return
     */
    NestedTreeNode<N> getNestedTreeNodeList(N node);

}
