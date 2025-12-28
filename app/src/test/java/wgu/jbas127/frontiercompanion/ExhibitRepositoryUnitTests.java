package wgu.jbas127.frontiercompanion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import wgu.jbas127.frontiercompanion.data.dao.ArticleDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitPanelDao;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.models.SearchResult;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;

@RunWith(MockitoJUnitRunner.class)
public class ExhibitRepositoryUnitTests {
    @Mock
    private ExhibitDao mockExhibitDao;
    @Mock
    private ExhibitPanelDao mockExhibitPanelDao;
    @Mock
    private ArticleDao mockArticleDao;
    @InjectMocks
    private ExhibitRepository repository;

    private Exhibit fakeExhibit;

    @Before
    public void setUp() {
        fakeExhibit = new Exhibit("Mock Exhibit", 38.0, -79.0, "Desc 1", "1700s", "Location 1");
        fakeExhibit.setId(1L);
    }

    @Test
    public void getExhibitSync_whenDaoReturnsExhibit_repositoryReturnsExhibit() {
        when(mockExhibitDao.getExhibitByIdSync(1L)).thenReturn(fakeExhibit);

        Exhibit result = repository.getExhibitSync(1L);

        assertNotNull("Result should not be null", result);
        assertEquals("The returned exhibit ID should be 1", 1L, result.getId());
        assertEquals("The exhibit name should match the fake data", "Mock Exhibit", result.getName());

        verify(mockExhibitDao, times(1)).getExhibitByIdSync(1L);
    }

    @Test
    public void getExhibitSync_whenDaoReturnsNull_repositoryReturnsNull() {
        long invalidId = 99L;
        when(mockExhibitDao.getExhibitByIdSync(invalidId)).thenReturn(null);

        Exhibit result = repository.getExhibitSync(invalidId);

        assertNull("Result should be null for an invalid ID", result);
        verify(mockExhibitDao, times(1)).getExhibitByIdSync(invalidId);
    }

    @Test
    public void getAllExhibitsSync_whenDaoReturnsList_repositoryReturnsList() {
        List<Exhibit> fakeList = Arrays.asList(fakeExhibit, new Exhibit("Exhibit 2", 0,0,"","",""));
        when(mockExhibitDao.getAllExhibitsSync()).thenReturn(fakeList);

        List<Exhibit> result = repository.getAllExhibitsSync();

        assertNotNull(result);
        assertEquals("List size should be 2", 2, result.size());
        assertEquals("First item's name should match", "Mock Exhibit", result.get(0).getName());
        verify(mockExhibitDao).getAllExhibitsSync();
    }

    @Test
    public void getAllExhibitsSync_whenDaoReturnsEmpty_repositoryReturnsEmpty() {
        // Arrange
        when(mockExhibitDao.getAllExhibitsSync()).thenReturn(Collections.emptyList());

        // Act
        List<Exhibit> result = repository.getAllExhibitsSync();

        // Assert
        assertNotNull(result);
        assertTrue("Result list should be empty", result.isEmpty());
        verify(mockExhibitDao).getAllExhibitsSync();
    }

    @Test
    public void insert_withValidExhibit_callsDaoInsert() {
        repository.insert(fakeExhibit);

        verify(mockExhibitDao, times(1)).insert(fakeExhibit);
    }
}
