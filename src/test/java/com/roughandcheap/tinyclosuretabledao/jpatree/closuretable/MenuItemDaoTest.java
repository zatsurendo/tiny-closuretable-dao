package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.roughandcheap.tinyclosuretabledao.commons.DbSession;
import com.roughandcheap.tinyclosuretabledao.jpatree.JpaTreeException;
import com.roughandcheap.tinyclosuretabledao.jpatree.LinkableTreeNode;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MenuItemDaoTest {

    private static final Logger log = LoggerFactory.getLogger(MenuItemDaoTest.class);
    
    @Autowired
    DbSession dbSession;

    @BeforeAll
    public void setup() {
        createInitialData();
    }

    @Test
    public void testAddChild() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        MenuItemNode n1 = dao.find(26L);
        MenuItemNode n2 = new MenuItemNode("あるノード", "somenode");
        assertThrows(LinkableJpaTreeException.class, () -> dao.addChild(n1, n2));
    }

    @Test
    public void testIsLink() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        MenuItemNode n1 = dao.find(26L);
        MenuItemNode n2 = dao.find(21L);
        assertTrue(dao.isLink(n1));
        assertFalse(dao.isLink(n2));
    }

    @Test
    public void testRemoveNode() {
        
        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        dao.setRemoveReferenceNodes(true);
        MenuItemNode n1 = dao.find(21L);
        MenuItemNode n2 = dao.find(22L);
        MenuItemNode n3 = dao.find(23L);
        MenuItemNode n4 = dao.find(26L);
        System.out.println(n1.toString());
        assertThrows(JpaTreeException.class,() -> dao.deletePath(n1));
        dao.deletePath(n2);
        dao.deletePath(n3);
        assertThrows(LinkableJpaTreeException.class, () -> dao.deletePath(n1));
        assertThrows(JpaTreeException.class, () -> dao.removeNode(n4));
        dao.deletePath(n4);
        dao.printNodeInfo(dao.getClosureTableTreeNodeInfo());
    }

    public void testAddLinkTo() {
        //
    }

    public void testRemoveLinkTo() {
        //
    }

    public void testSetLinkTo() {
        //
    }

    public void testRemoveLinkToFromNode() {
        //
    }

    @Test
    public void testFindDeadLinks() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        assertEquals(0, dao.findDeadLinks().size());
        layDeadLinks(dao);
        assertEquals(2, dao.findDeadLinks().size());
    }

    private void layDeadLinks(LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao) {
        MenuItemNode n1 = dao.find(24L);
        MenuItemNode n2 = dao.find(25L);
        n1.setType(LinkableTreeNode.Type.LINK);
        n2.setType(LinkableTreeNode.Type.LINK);
        dbSession.save(n1);
        dbSession.save(n2);
    }

    @Test
    public void testFindLinkTo() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        MenuItemNode n2 = dao.find(21L);
        assertEquals(1, dao.findLinkTo(n2).size());
    }

    @Test
    public void testFindLinkToAlt() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        MenuItemNode n2 = dao.find(21L);
        assertEquals(1, dao.findLinkTo(n2).size());
    }
    
    @Test
    public void testFindLinked() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        dao.findLinked().forEach(System.out::println);
    }

    @Test
    public void testFindLinking() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        dao.findLinking().forEach(System.out::println);
    }

    @Test
    public void testMoveTo() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();
        MenuItemNode n1 = dao.find(21L);
        MenuItemNode n2 = dao.find(25L);
        
        assertEquals(11L, dao.getParent(n1).getId());
        dao.moveTo(n1, n2);
        MenuItemNode n3 = dao.find(21L);
        assertEquals(25L, dao.getParent(n3).getId());
        dao.printNodeInfo(dao.getClosureTableTreeNodeInfo());
    }

    private void createInitialData() {

        LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> dao = getDao();

        MenuItemNode root = new MenuItemNode("カテゴリ", "cat");
        MenuItemNode camera = new MenuItemNode("カメラ", "camera");
        MenuItemNode dslr = new MenuItemNode("デジタル一眼レフ", "dslr");
        MenuItemNode mlsl = new MenuItemNode("ミラーレス一眼", "mlsl");
        MenuItemNode lens = new MenuItemNode("交換レンズ", "lens");
        MenuItemNode af = new MenuItemNode("オートフォーカスレンズ", "aflens");
        MenuItemNode mf = new MenuItemNode("マニュアルフォーカスレンズ", "mflens");
        MenuItemNode pc = new MenuItemNode("PC", "pc");
        MenuItemNode notePC = new MenuItemNode("ノートPC", "notepc");
        MenuItemNode desktop = new MenuItemNode("デスクトップPC", "desktoppc");
        MenuItemNode pcparts = new MenuItemNode("パソコンパーツ", "pcparts");
        MenuItemNode cpu = new MenuItemNode("CPU", "cpu");
        MenuItemNode memory = new MenuItemNode("メモリ", "memory");
        MenuItemNode chasis = new MenuItemNode("PCケース", "chasis");
        MenuItemNode intel = new MenuItemNode("Intel", "intel");
        MenuItemNode amd = new MenuItemNode("AMD", "amd");
        MenuItemNode tan = new MenuItemNode("単焦点レンズ", "singlefl");
        MenuItemNode zoom = new MenuItemNode("ズームレンズ", "zoomfl");
        MenuItemNode ddr4 = new MenuItemNode("DDR4メモリ", "ddr4");
        MenuItemNode ddr5 = new MenuItemNode("DDR5メモリ", "ddr5");
        MenuItemNode storage = new MenuItemNode("ストレージ", "storage");
        MenuItemNode sd = new MenuItemNode("SDカード", "sd");
        MenuItemNode ssd = new MenuItemNode("SSD", "ssd");
        MenuItemNode mftan = new MenuItemNode("MF単焦点", "singleflm");
        MenuItemNode mfzoom = new MenuItemNode("MFズーム", "zoomflm");

        dao.createRoot(root);
        dao.addChild(root, camera);
        dao.addChild(camera, dslr);
        dao.addChild(camera, mlsl);

        dao.addChild(camera, lens);
        dao.addChild(lens, af);
        dao.addChild(lens, mf);
        dao.addChild(root, pc);
        dao.addChild(pc, notePC);
        dao.addChild(pc, desktop);
        dao.addChild(pc, pcparts);
        dao.addChild(pcparts, cpu);
        dao.addChild(pcparts, memory);
        dao.addChild(pcparts, chasis);
        dao.addChild(cpu, intel);
        dao.addChild(cpu, amd);
        dao.addChild(af, tan);
        dao.addChild(af, zoom);
        dao.addChild(memory, ddr4);
        dao.addChild(memory, ddr5);
        dao.addChild(pcparts, storage);
        dao.addChild(storage, sd);
        dao.addChild(storage, ssd);
        dao.addChild(mf, mftan);
        dao.addChild(mf, mfzoom);

        MenuItemNode linktoStorage = new MenuItemNode("ストレージ", "cam_storage", LinkableTreeNode.Type.LINK, storage);
        dao.addLinkTo(linktoStorage, storage);
        dao.addChild(camera, linktoStorage);

        log.info("PRINT");
        dao.printNodeInfo(dao.getClosureTableTreeNodeInfo());
        log.info("PRINT");
    }

    private LinkableClosureTableTreeDao<MenuItemNode, MenuItemPath> getDao() {
        return new LinkableClosureTableTreeDao<>(MenuItemNode.class, MenuItemPath.class, dbSession);
    }
}
