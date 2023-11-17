package com.roughandcheap.tinyclosuretabledao.jpatree;

import java.util.List;

import com.roughandcheap.tinyclosuretabledao.jpatree.closuretable.LinkableClosureTableTreeNode;
import com.roughandcheap.tinyclosuretabledao.jpatree.closuretable.TreePath;

/**
 * リンク可能なツリーを扱うDAOインターフェース
 * <p>
 * LinkableTreeNode を実装した Entity を扱う場合は、この DAO を実装した DAO で管理する必要があります。
 * <p>
 * リンクの付与や、リンクノードの作成削除、リンクし、あるいはリンクされているノードの検索などに便利化メソッドが用意されています。
 * 
 * @param N このDAOで管理するリンク可能ノードのサブクラス
 * @param P このDAOで管理するパス
 * @author Zatsurendo
 * @since 2923.11.15
 */
public interface LinkableTreeDao<N extends LinkableClosureTableTreeNode<N>, P extends TreePath<N>> {
    
    /**
     * {@code node} のリンクを設定します
     * @param node 対象ノード
     * @param linkTo リンク先
     * @return 永続化後のノード
     */
    N addLinkTo(N node, N linkTo);

    boolean isLink(N node);

    /**
     * {@code node} のリンクを削除します
     * @param node 対象ノード
     * @return 永続化後のノード
     */
    N removeLinkTo(N node);

    /**
     * リンク先に {@code linkTo} を保持しているノードの一覧を取得します
     * @param linkTo
     * @return
     */
    List<N> findLinkTo(N linkTo);
    
    /**
     * {@code linkTo} をリンク先としているノードからリンク先を削除します
     * @param linkTo 削除したいリンク先ノード
     */
    void removeLinkToFromNode(N linkTo);

    /**
     * デッドリンクを取得する
     * <d>デッドリンクとは、{@code type == Type.LINK && linkTo == null}
     * であるノードを指します。
     * @return
     */
    List<N> findDeadLinks();

    /**
     * リンクされているノードを抽出する
     * <p>
     * 被リンクノード、すなわち、あるノードの{@code linkTo}にリンク先として保持されている
     * ノードを返します。
     * @return
     */
    List<N> findLinked();

    /**
     * リンクしているノードを抽出する
     * <p>
     * {@code linkTo} に値を保持しているノードを抽出します
     * @return
     */
    List<N> findLinking();
}
