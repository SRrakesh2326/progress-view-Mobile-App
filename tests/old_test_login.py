import unittest
import time
from base_test import BaseTest

class TestLogin(BaseTest):
    def setUp(self):
        super().setUp()

    def test_valid_login(self):
        """TC_004: Valid credentials login -> assert Dashboard loads"""
        # Wait for the login screen to be ready
        email_field = self.wait_for_tag("email_field")
        email_field.clear()
        email_field.send_keys("parent@sunriseacademy.edu")

        pass_field = self.find_by_tag("password_field")
        pass_field.clear()
        pass_field.send_keys("password123")

        self.find_by_tag("login_button").click()

        # Check if Dashboard loads (Wait for welcome banner)
        time.sleep(2)
        banner = self.wait_for_tag("welcome_banner")
        self.assertTrue(banner.is_displayed(), "Dashboard welcome banner should be visible")

    def test_invalid_login(self):
        """TC_005: Invalid credentials -> assert error message shown"""
        email_field = self.wait_for_tag("email_field")
        email_field.clear()
        email_field.send_keys("wrong@email.com")

        pass_field = self.find_by_tag("password_field")
        pass_field.clear()
        pass_field.send_keys("wrongpass")

        self.find_by_tag("login_button").click()
        time.sleep(1)

        error = self.wait_for_tag("login_error")
        self.assertTrue(error.is_displayed(), "Error message should be visible")

    def test_empty_fields_login(self):
        """TC_007: Empty fields -> assert error shown"""
        email_field = self.wait_for_tag("email_field")
        email_field.clear()

        pass_field = self.find_by_tag("password_field")
        pass_field.clear()

        self.find_by_tag("login_button").click()
        time.sleep(1)

        error = self.wait_for_tag("login_error")
        self.assertTrue(error.is_displayed(), "Error message should be visible for empty fields")

    def test_logout_relogin_flow(self):
        """TC_008/TC_009: Logout and re-login flow"""
        # First login
        email_field = self.wait_for_tag("email_field")
        email_field.clear()
        email_field.send_keys("parent@sunriseacademy.edu")

        pass_field = self.find_by_tag("password_field")
        pass_field.clear()
        pass_field.send_keys("password123")

        self.find_by_tag("login_button").click()
        time.sleep(2)

        # Verify dashboard loaded
        banner = self.wait_for_tag("welcome_banner")
        self.assertTrue(banner.is_displayed())

        # Logout
        signout = self.find_by_tag("signout_button")
        signout.click()
        time.sleep(1)

        # Verify back at Login
        login_card = self.wait_for_tag("login_card")
        self.assertTrue(login_card.is_displayed(), "Should return to login screen after sign out")

        # Login again
        email_field = self.wait_for_tag("email_field")
        email_field.clear()
        email_field.send_keys("parent@sunriseacademy.edu")

        pass_field = self.find_by_tag("password_field")
        pass_field.clear()
        pass_field.send_keys("password123")

        self.find_by_tag("login_button").click()
        time.sleep(2)

        banner = self.wait_for_tag("welcome_banner")
        self.assertTrue(banner.is_displayed(), "Should be back on dashboard after re-login")

if __name__ == "__main__":
    unittest.main()
