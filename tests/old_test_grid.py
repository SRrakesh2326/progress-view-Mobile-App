import unittest
import time
from base_test import BaseTest

class TestGrid(BaseTest):
    def navigate_to_tests(self):
        try:
            self.wait_for_tag("welcome_banner", timeout=5)
        except:
            # Login if needed
            email = self.wait_for_tag("email_field", timeout=5)
            email.clear()
            email.send_keys("parent@sunriseacademy.edu")
            
            pwd = self.find_by_tag("password_field")
            pwd.clear()
            pwd.send_keys("password123")
            
            self.find_by_tag("login_button").click()
            self.wait_for_tag("welcome_banner", timeout=10)
            
        self.find_by_tag("quick_access_tests").click()
        time.sleep(1)
        self.wait_for_tag("test_results_list")

    def test_read_only_list_display(self):
        """TC: Verify the read-only test results list is displayed"""
        self.navigate_to_tests()
        
        # Verify the list container is present
        list_container = self.wait_for_tag("test_results_list")
        self.assertTrue(list_container.is_displayed(), "Test results list should be visible")
        
        # In a real scenario we'd check for specific list items, but Compose 
        # LazyColumn items without individual testTags are hard to identify by ID.
        # We at least verify the screen loads without crashing.
        
        # Go back
        self.find_by_tag("back_button").click()
        time.sleep(1)
        self.wait_for_tag("welcome_banner")

if __name__ == "__main__":
    unittest.main()
