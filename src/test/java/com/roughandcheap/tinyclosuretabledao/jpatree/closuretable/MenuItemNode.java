package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.roughandcheap.tinyclosuretabledao.jpatree.LinkableTreeNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Table(uniqueConstraints = @UniqueConstraint(name = "ux_menu_item_node_slug", columnNames = {"slug"}))
@Entity
public class MenuItemNode extends AbstractLinkableClosureTableTreeNode<MenuItemNode> {

    @Id
    @GeneratedValue(generator = "menu_item_node_seq_generator")
    @GenericGenerator(
        name = "menu_item_node_seq_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value="menu_item_node_seq"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        }
    )
    private Long id;
    @Column(nullable = false, length = 80)
    @EqualsAndHashCode.Include
    private String slug;
    @Column(nullable = true, length = 1000)
    private String description;

    /**
     * Constructor
     */
    public MenuItemNode() {
        super();
    }

    /**
     * Constructor
     * @param nodeName
     * @param slug
     */
    public MenuItemNode(String nodeName, String slug) {
        super(nodeName);
        this.slug = slug;
    }

    /**
     * Constructor
     * @param nodeName
     * @param slug
     * @param type
     * @param linkTo
     */
    public MenuItemNode(String nodeName, String slug, LinkableTreeNode.Type type, MenuItemNode linkTo) {
        super(type, linkTo, nodeName);
        this.slug = slug;
    }

    /**
     * Constructor
     * @param id
     * @param nodeName
     * @param slug
     * @param type
     * @param linkTo
     */
    public MenuItemNode(Long id, String nodeName, String slug, LinkableTreeNode.Type type, MenuItemNode linkTo) {
        super(type, linkTo, nodeName);
        this.slug = slug;
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override
    public MenuItemNode clone() {
        return new MenuItemNode(id, nodeName, slug, type, (MenuItemNode) getLinkTo());
    }
}
