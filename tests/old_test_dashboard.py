import unittest
import time
from base_test import BaseTest

class TestDashboard(BaseTest):
    def ensure_logged_in(self):
        try:
            self.wait_for_tag("welcome_banner", timeout=5)
        except:
            # Not on dashboard, try logging in
            try:
                email = self.wait_for_tag("email_field", timeout=5)
                email.clear()
                email.send_keys("parent@sunriseacademy.edu")
                
                pwd = self.find_by_tag("password_field")
                pwd.clear()
                pwd.send_keys("password123")
                
                self.find_by_tag("login_button").click()
                self.wait_for_tag("welcome_banner", timeout=10)
            except Exception as e:
                print(f"Could not log in: {e}")

    def test_ui_elements_visibility(self):
        """TC_010 to TC_015: Verify all Dashboard UI elements are visible"""
        self.ensure_logged_in()
        
        elements = [
            "welcome_banner",
            "quick_access_attendance",
            "quick_access_tests",
            "menu_button",
            "signout_button"
        ]
        
        for tag in elements:
            el = self.wait_for_tag(tag)
            self.assertTrue(el.is_displayed(), f"Element {tag} should be visible")

    def test_navigation_to_subpages(self):
        """TC_019 to TC_021: Verify quick access buttons lead to correct screens"""
        self.ensure_logged_in()

        # Attendance
        self.find_by_tag("quick_access_attendance").click()
        time.sleep(1)
        self.wait_for_tag("back_button").click()
        time.sleep(1)
        self.wait_for_tag("welcome_banner") # wait for dashboard to reappear

        # Tests
        self.find_by_tag("quick_access_tests").click()
        time.sleep(1)
        self.wait_for_tag("back_button").click()
        time.sleep(1)
        self.wait_for_tag("welcome_banner")

    def test_sidebar_open_close(self):
        """TC_023 to TC_034: Sidebar navigation flow"""
        self.ensure_logged_in()
        
        # Open sidebar
        self.find_by_tag("menu_button").click()
        time.sleep(1)

        # Verify sidebar contents
        self.assertTrue(self.wait_for_tag("sidebar_student_name").is_displayed())
        self.assertTrue(self.find_by_tag("sidebar_item_attendance").is_displayed())

        # Click on Dashboard in sidebar to close it
        self.find_by_tag("sidebar_item_dashboard").click()
        time.sleep(1)
        self.assertTrue(self.wait_for_tag("welcome_banner").is_displayed())

if __name__ == "__main__":
    unittest.main()
