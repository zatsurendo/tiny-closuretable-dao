package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.roughandcheap.tinyclosuretabledao.commons.DbSession;
import com.roughandcheap.tinyclosuretabledao.jpatree.JpaTreeException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClosureTableTreeDaoTest {
    
    private static final Logger log = LoggerFactory.getLogger(ClosureTableTreeDaoTest.class);

    @Autowired
    DbSession dbSession;
    @PersistenceContext
    EntityManager entityManager;

    @BeforeAll
    public void setup() {
        createInitialData();
    }

    @Test
    public void testRemoveNode() {
        //
        PersonCtt n1 = getDao().find(25L);
        getDao().deletePath(n1);
        getDao().removeNode(n1, true);
        PersonCtt n2 = getDao().find(25L);
        assertNull(n2);
    }

    @Test
    public void testIsRoot() {

        PersonCtt n1 = getDao().find(1L);
        PersonCtt n2 = getDao().find(2L);
        assertTrue(getDao().isRoot(n1));
        assertFalse(getDao().isRoot(n2));
    }

    @Test
    public void testHasChild() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(1L);
        PersonCtt n2 = dao.find(9L);
        assertTrue(dao.hasChild(n1));
        assertFalse(dao.hasChild(n2));
    }

    @Test
    public void testIsPathExists() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(1L);
        assertTrue(dao.isPathExists(n1));

        PersonCtt n2 = new PersonCtt("path not registered node");
        entityManager.persist(n2);
        entityManager.flush();
        log.debug("Pwrsisted {}", n2.toString());
        assertFalse(dao.isPathExists(n2));
    }

    @Test
    public void testInsert() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = new PersonCtt("New Person");
        PersonCtt n2 = dao.find(2L);
        PersonCtt n3 = new PersonCtt(null);
        assertDoesNotThrow(() -> dao.insert(n1));
        assertThrows(JpaTreeException.class, () -> dao.insert(n2));
        assertThrows(PropertyValueException.class, () -> dao.insert(n3));
    }

    @Test
    public void testUpdate() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = new PersonCtt("New Person");
        PersonCtt n2 = dao.find(2L);
        PersonCtt n3 = dao.find(3L);
        assertDoesNotThrow(() -> dao.update(n2));
        assertThrows(JpaTreeException.class, () -> dao.update(n1));
        n3.setNodeName(null);
        assertThrows(PropertyValueException.class, () -> dao.update(n3));
    }

    @Test
    public void testInsertOrUpdate() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = new PersonCtt("New Person");
        PersonCtt n2 = dao.find(2L);
        PersonCtt n3 = dao.find(3L);
        assertDoesNotThrow(() -> dao.insertOrUpdate(n2));
        assertDoesNotThrow(() -> dao.insertOrUpdate(n1));
        n3.setNodeName(null);
        assertThrows(PropertyValueException.class, () -> dao.update(n3));
    }

    @Test
    public void testFind() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        assertDoesNotThrow(() -> dao.find(1));
        assertNull(dao.find(-1));
    }

    public void testCreateRoot() {
        // createInitialData();
    }

    public void tesetAddChild() {
        // createInitialData();
    }

    @Test
    public void testNewTreePathInstance() throws Exception {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonTreePath path = dao.newTreePathInstance();
        assertTrue(path instanceof PersonTreePath);
    }

    @Test
    public void testGetChildrenCount() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(9L);
        PersonCtt n2 = dao.find(6L);
        assertEquals(0, dao.getChildrenCount(n1));
        assertEquals(2, dao.getChildrenCount(n2));
    }

    @Test
    public void testGetChildren() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(9L);
        PersonCtt n2 = dao.find(6L);
        assertEquals(0, dao.getChildren(n1).size());
        assertEquals(2, dao.getChildren(n2).size());
    }

    @Test
    public void testGetLevel() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(6L);
        PersonCtt n2 = new PersonCtt("New Person");
        dao.insert(n2);

        assertEquals(4, dao.getLevel(n1));
        assertThrows(JpaTreeException.class, () -> dao.getLevel(n2));
    }

    @Test
    public void testGetTree() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(17L);
        PersonCtt n2 = dao.find(5L);
        PersonCtt n3 = new PersonCtt("New Person");
        dao.insert(n3);
        assertEquals(1, dao.getTree(n1).size());
        assertEquals(7, dao.getTree(n2).size());
        assertTrue(dao.getTree(n3).isEmpty());
    }

    @Test
    public void testGetPath() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(17L);
        PersonCtt n2 = dao.find(5L);
        PersonCtt n3 = new PersonCtt("New Person");
        dao.insert(n3);
        assertEquals(5, dao.getPath(n1).size());
        assertEquals(3, dao.getPath(n2).size());
        assertTrue(dao.getTree(n3).isEmpty());
    }

    @Test
    public void testGetTreePaths() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(17L);
        PersonCtt n2 = dao.find(5L);
        PersonCtt n3 = new PersonCtt("New Person");
        dao.insert(n3);
        assertEquals(1, dao.getTreePaths(n1).size());
        assertEquals(7, dao.getTreePaths(n2).size());
        assertTrue(dao.getTreePaths(n3).isEmpty());
    }

    @Test
    public void testGetDescendantPaths() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(17L);
        PersonCtt n2 = dao.find(5L);
        PersonCtt n3 = new PersonCtt("New Person");
        dao.insert(n3);
        assertEquals(5, dao.getDescendantPaths(n1).size());
        assertEquals(3, dao.getDescendantPaths(n2).size());
        assertTrue(dao.getDescendantPaths(n3).isEmpty());
    }

    @Test
    public void testGetParent() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(3L);
        PersonCtt n2 = dao.find(1L);
        PersonCtt n3 = new PersonCtt("new person");
        dao.save(n3);
        assertEquals(2L, dao.getParent(n1).getId());
        assertNull(dao.getParent(n2));
        assertThrows(JpaTreeException.class, () -> dao.getParent(n3));
    }

    @Test
    public void testGetSiblings() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(3L);
        PersonCtt n2 = dao.find(1L);
        PersonCtt n3 = new PersonCtt("new person");
        dao.save(n3);
        assertEquals(3, dao.getSiblings(n1).size());
        assertEquals(1, dao.getSiblings(n2).size());
        assertThrows(JpaTreeException.class, () -> dao.getSiblings(n3));
    }

    @Test
    public void testGetRootNodes() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(1L);
        assertEquals(1, dao.getRootNodes().size());
        assertEquals(n1, dao.getRootNodes().get(0));
    }

    @Test
    public void testMoveTo() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(21L);
        PersonCtt n2 = dao.find(25L);

        assertEquals(11L, dao.getParent(n1).getId());
        dao.moveTo(n1, n2);
        PersonCtt n3 = dao.find(21L);
        assertEquals(25L, dao.getParent(n3).getId());
        dao.printNodeInfo(dao.getClosureTableTreeNodeInfo());
        PersonCtt n4 = dao.find(23L);
        PersonCtt n5 = dao.find(22L);
        dao.printPaths(dao.getDescendantPaths(n5), "path");
        dao.printPaths(dao.getDescendantPaths(n4), "path");
        dao.printPaths(dao.getDescendantPaths(n1), "path");
    }

    @Test
    public void testDeletePath() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(12L);
        assertThrows(JpaTreeException.class, () -> dao.deletePath(n1));
        PersonCtt n2 = dao.find(15L);
        PersonCtt n3 = dao.find(16L);
        assertDoesNotThrow(() -> {
            dao.deletePath(n2);
            dao.deletePath(n3);
            dao.deletePath(n1);
        });
    }

    @Test
    public void testFindTreePath() {
        
        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();
        PersonCtt n1 = dao.find(12L);
        PersonTreePath p1 = dao.findTreePath(n1, n1);
        assertTrue(n1.equals(p1.getAncestor()) && n1.equals(p1.getDescendant()));
    }

    private void createInitialData() {

        ClosureTableTreeDao<PersonCtt, PersonTreePath> dao = getDao();

        PersonCtt root = new PersonCtt("カテゴリ");
        PersonCtt camera = new PersonCtt("カメラ");
        PersonCtt dslr = new PersonCtt("デジタル一眼レフ");
        PersonCtt mlsl = new PersonCtt("ミラーレス一眼");
        PersonCtt lens = new PersonCtt("交換レンズ");
        PersonCtt af = new PersonCtt("オートフォーカスレンズ");
        PersonCtt mf = new PersonCtt("マニュアルフォーカスレンズ");
        PersonCtt pc = new PersonCtt("PC");
        PersonCtt notePC = new PersonCtt("ノートPC");
        PersonCtt desktop = new PersonCtt("デスクトップPC");
        PersonCtt pcparts = new PersonCtt("パソコンパーツ");
        PersonCtt cpu = new PersonCtt("CPU");
        PersonCtt memory = new PersonCtt("メモリ");
        PersonCtt chasis = new PersonCtt("PCケース");
        PersonCtt intel = new PersonCtt("Intel");
        PersonCtt amd = new PersonCtt("AMD");
        PersonCtt tan = new PersonCtt("単焦点レンズ");
        PersonCtt zoom = new PersonCtt("ズームレンズ");
        PersonCtt ddr4 = new PersonCtt("DDR4メモリ");
        PersonCtt ddr5 = new PersonCtt("DDR5メモリ");
        PersonCtt storage = new PersonCtt("ストレージ");
        PersonCtt sd = new PersonCtt("SDカード");
        PersonCtt ssd = new PersonCtt("SSD");
        PersonCtt mftan = new PersonCtt("MF単焦点");
        PersonCtt mfzoom = new PersonCtt("MFズーム");

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
        dao.addChild(pcparts, storage, 50);
        dao.addChild(storage, sd, 2);
        dao.addChild(storage, ssd, 1);
        dao.addChild(mf, mftan);
        dao.addChild(mf, mfzoom);

        log.info("PRINT");
        dao.printNodeInfo(dao.getClosureTableTreeNodeInfo());
        log.info("PRINT");
    }

    private ClosureTableTreeDao<PersonCtt, PersonTreePath> getDao() {
        return new ClosureTableTreeDao<>(PersonCtt.class, PersonTreePath.class, dbSession);
    }
}
