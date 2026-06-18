import unittest
import time
from base_test import BaseTest

class TestNavigation(BaseTest):
    def test_full_end_to_end_flow(self):
        """TC: Login -> Dashboard -> Sub-page -> Back -> Logout"""

        # 1. Login
        email = self.wait_for_tag("email_field")
        email.clear()
        email.send_keys("parent@sunriseacademy.edu")
        
        pwd = self.find_by_tag("password_field")
        pwd.clear()
        pwd.send_keys("password123")
        
        self.find_by_tag("login_button").click()

        # 2. Verify Dashboard
        time.sleep(2)
        self.wait_for_tag("welcome_banner")

        # 3. Go to Attendance
        self.find_by_tag("quick_access_attendance").click()
        time.sleep(1)
        self.wait_for_tag("attendance_list")

        # 4. Back to Dashboard
        self.find_by_tag("back_button").click()
        time.sleep(1)
        self.wait_for_tag("welcome_banner")

        # 5. Go to Tests
        self.find_by_tag("quick_access_tests").click()
        time.sleep(1)
        self.wait_for_tag("test_results_list")

        # 6. Android Back Key to return
        self.driver.back()
        time.sleep(1)
        self.wait_for_tag("welcome_banner")

        # 7. Logout
        self.find_by_tag("signout_button").click()
        time.sleep(1)
        self.wait_for_tag("login_card")

if __name__ == "__main__":
    unittest.main()
