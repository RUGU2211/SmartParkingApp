package com.smartparking.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smartparking.fragments.ListViewFragment;
import com.smartparking.fragments.MapViewFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DashboardPagerAdapterTest {

    private DashboardPagerAdapter adapter;

    @Mock
    private FragmentActivity mockActivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        adapter = new DashboardPagerAdapter(mockActivity);
    }

    @Test
    public void testAdapterInitialization() {
        assertNotNull(adapter);
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testCreateListViewFragment() {
        Fragment fragment = adapter.createFragment(0);
        assertNotNull(fragment);
        assertTrue(fragment instanceof ListViewFragment);
    }

    @Test
    public void testCreateMapViewFragment() {
        Fragment fragment = adapter.createFragment(1);
        assertNotNull(fragment);
        assertTrue(fragment instanceof MapViewFragment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPosition() {
        adapter.createFragment(2); // Should throw exception for invalid position
    }

    @Test
    public void testFragmentConsistency() {
        // Test that the same position always returns the same type of fragment
        Fragment firstListFragment = adapter.createFragment(0);
        Fragment secondListFragment = adapter.createFragment(0);
        assertTrue(firstListFragment.getClass() == secondListFragment.getClass());

        Fragment firstMapFragment = adapter.createFragment(1);
        Fragment secondMapFragment = adapter.createFragment(1);
        assertTrue(firstMapFragment.getClass() == secondMapFragment.getClass());
    }

    @Test
    public void testItemCount() {
        // ViewPager should always have exactly 2 pages
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testFragmentTypes() {
        // Test that fragments are of correct type and in correct order
        Fragment[] fragments = new Fragment[adapter.getItemCount()];
        for (int i = 0; i < adapter.getItemCount(); i++) {
            fragments[i] = adapter.createFragment(i);
        }

        assertTrue(fragments[0] instanceof ListViewFragment);
        assertTrue(fragments[1] instanceof MapViewFragment);
    }

    @Test
    public void testFragmentInstantiation() {
        // Test that fragments can be instantiated without errors
        Fragment listFragment = adapter.createFragment(0);
        assertNotNull(listFragment);
        assertTrue(listFragment.isStateSaved() == false);

        Fragment mapFragment = adapter.createFragment(1);
        assertNotNull(mapFragment);
        assertTrue(mapFragment.isStateSaved() == false);
    }

    @Test
    public void testAdapterBehavior() {
        // Test adapter behavior with multiple calls
        for (int i = 0; i < 5; i++) {
            Fragment listFragment = adapter.createFragment(0);
            assertNotNull(listFragment);
            assertTrue(listFragment instanceof ListViewFragment);

            Fragment mapFragment = adapter.createFragment(1);
            assertNotNull(mapFragment);
            assertTrue(mapFragment instanceof MapViewFragment);
        }
    }

    @Test
    public void testFragmentLifecycle() {
        // Create fragments
        Fragment listFragment = adapter.createFragment(0);
        Fragment mapFragment = adapter.createFragment(1);

        // Verify initial state
        assertNotNull(listFragment);
        assertNotNull(mapFragment);
        assertTrue(listFragment.isStateSaved() == false);
        assertTrue(mapFragment.isStateSaved() == false);

        // Verify fragment manager interaction
        assertTrue(listFragment.getFragmentManager() == null); // Not yet attached
        assertTrue(mapFragment.getFragmentManager() == null); // Not yet attached
    }
}
