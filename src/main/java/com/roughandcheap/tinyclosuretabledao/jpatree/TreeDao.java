package com.roughandcheap.tinyclosuretabledao.jpatree;

import java.io.Serializable;
import java.util.List;

import com.roughandcheap.tinyclosuretabledao.jpatree.closuretable.TreePath;

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
     * Returns whether the specified {@code entity} is persistent or not.
     * @param entity  &lt; N extends TreeNode &gt;
     * @return true:persistent, false:not persistent
     */
    boolean isPersistent(N entity);

    /**
     * Returns whether the specified {@code entity} is the root or not.
     * <p>Determine if {@code entity} is the root if both {@code ancestor} and {@code descendant} of the path are the same and {@code depth} is 0.
     * @param entity &lt; N extends TreeNode &gt;
     * @return true:is root, false:is not root.
     */
    boolean isRoot(N entity);

    /**
     * Returns whether the given {@code parent} has any children
     * @param parent &lt; N extends TreeNode &gt;
     * @return true:has child(ren), false:has no child
     */
    boolean hasChild(N parent);

    /**
     * Returns whether or not the specified {@code node} path exists.
     * @param node &lt; N extends TreeNode &gt;
     * @return true:exists, false: not exists.
     */
    boolean isPathExists(N node);

    /**
     * Returns whether {@code descendant} exists for {@code parent}.
     * <p>
     * It means that {@code descendant} belongs to {@code parent}.
     * @param parent &lt; N extends TreeNode &gt;
     * @param descendant &lt; N extends TreeNode &gt;
     * @return true:exists, false:not exists
     */
    boolean parentContains(N parent, N descendant);

    /**
     * Returns whether {@code child} belongs to {@code ancestor
     * 
     * @param child &lt; N extends TreeNode &gt;
     * @param ancestor &lt; N extends TreeNode &gt;
     * @return true:belongs, false:not belongs
     */
    boolean childBelongsTo(N child, N ancestor);

    /**
     * insert {@code entity}
     * @param entity &lt; N extends TreeNode &gt;
     * @return &lt; N extends TreeNode &gt;
     */
    N insert(N entity);

    /**
     * update {@code entity}
     * @param entity &lt; N extends TreeNode &gt;
     * @return &lt; N extends TreeNode &gt;
     */
    N update(N entity);

    /**
     * insert or update{@code entity}
     * @param entity &lt; N extends TreeNode &gt;
     * @return &lt; N extends TreeNode &gt;
     */
    N insertOrUpdate(N entity);

    /**
     * Return element N with {@code id}.
     * @param id ID
     * @return &lt; N extends TreeNode &gt;
     */
    N find(Serializable id);

    /**
     * Return a collection of nodes matching {@code nodeName}. 
     * @param nodeName String
     * @return &lt; N extends TreeNode &gt;
     */
    List<N> findByNodeName(String nodeName);

    /**
     * Return a collection of nodes starting with {@code startingWith}. 
     * @param startingWith String
     * @return &lt; N extends TreeNode &gt;
     */
    List<N> findByNodeNameStartingWith(String startingWith);

    /**
     * Return a collection of nodes containing {@code contains}.
     * @param contains String
     * @return &lt; N extends TreeNode &gt;
     */
    List<N> findByNodeNameContains(String contains);

    /**
     * Return a collection of nodes ending with {@code endsWith}. 
     * @param endsWith String
     * @return &lt; N extends TreeNode &gt;
     */
    List<N> findByNodeNameEndsWith(String endsWith);

    /**
     * Create a tree node with {@code entity}
     * @param entity &lt; N extends TreeNode &gt;
     * @return &lt; N extends TreeNode &gt;
     */
    N createRoot(N entity);

    /**
     * Add {@code child} node as a child element of {@code parent} node
     * 
     * @param parent &lt; N extends TreeNode &gt;
     * @param child &lt; N extends TreeNode &gt;
     * @return &lt; N extends TreeNode &gt;
     * 
     */
    N addChild(N parent, N child);

    /**
     * Add {@code child} node as a child element of {@code parent} node
     * @param parent &lt; N extends TreeNode &gt;
     * @param child &lt; N extends TreeNode &gt;
     * @param orderIndex int
     * @return &lt; N extends TreeNode &gt;
     */
    N addChild(N parent, N child, int orderIndex);

    /**
     * Returns the number of child nodes directly under the specified {@code parent}.
     * @param parent &lt; N extends TreeNode &gt;
     * @return number of child
     */
    long getChildrenCount(N parent);

    /**
     * Returns a collection of child nodes immediately below the specified node, sorted by {@code orderIndex}.
     * @param parent &lt; N extends TreeNode &gt;
     * @return List<N>
     */
    List<N> getChildren(N parent);

    /**
     * Returns the depth on the path of the specified {@code node}.
     * <p>
     * Depth is calculated by setting the depth of the root node as 1.
     * @param node &lt; N extends TreeNode &gt;
     * @return int depth
     */
    int getLevel(N node);

    /**
     * Returns a collection of tree nodes under {@code parent}.
     * @param parent &lt; N extends TreeNode &gt;
     * @return List<N>
     */
    List<N> getTree(N parent);

    /**
     * Returns a collection of paths up to {@code node}.
     * @param node &lt; N extends TreeNode &gt;
     * @return List<N>
     */
    List<N> getPath(N node);

    /**
     * Returns a tree of child nodes containing {@code parent}.
     * @param parent &lt; N extends TreeNode &gt;
     * @return List<N>
     */
    List<P> getTreePaths(N parent);

    /**
     * Return a collection of paths from the root path to {@code parent}.
     * @param node &lt; N extends TreeNode &gt;
     * @return List<N>
     */
    List<P> getDescendantPaths(N node);

    /**
     * Returns the parent node of the specified {@code node}.
     * <p>
     * Returns null if the specified node has no parent (it is the root path).
     * @param node &lt; N extends TreeNode &gt;
     * @return &lt; N extends TreeNode &gt;
     */
    N getParent(N node);

    /**
     * Returns a collection of sibling nodes of the specified {@code node}.
     * @param node &lt; N extends TreeNode &gt;
     * @return List<N>
     */
    List<N> getSiblings(N node);

    /**
     * Get root nodes as a collection
     * @return List<N>
     */
    List<N> getRootNodes();

    /**
     * Move the tree of child nodes containing {@code parent} directly under {@code moveTo
     * <p>
     * If destination is specified as {@code null}, move to root. 
     * If there are no child nodes, the {@code parent} node is moved to the specified destination.
     * @param parent &lt; N extends TreeNode &gt; source
     * @param moveTo &lt; N extends TreeNode &gt; destination
     */
    void moveTo(N parent, N moveTo);

    /**
     * {@code TreePath.descendant} が {@code descendant} であるパスを削除する
     * @param descendant N extends TreeNode
     */
    void deletePath(N descendant);

    /**
     * Search for TreePath matching {@code P}.
     * <p>
     * If no matching record exists, return {@code null}.
     * @param treePath P extends TreePath
     * @return P extends TreePath
     */
    P findTreePath(P treePath);

    /**
     * Search for TreePath matching {@code ancestor} {@code descendang}.
     * @param ancestor P extends TreePath ancestor
     * @param descendant P extends TreePath descendant
     * @return P extends TreePath
     */
    P findTreePath(N ancestor, N descendant);

    /**
     * {@code node} 
     * @param node
     * @return
     */
    long countPaths(N node);
}
