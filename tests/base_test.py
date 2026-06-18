import unittest
import os
from appium import webdriver
from appium.options.android import UiAutomator2Options
from selenium.webdriver.support.ui import WebDriverWait

class BaseTest(unittest.TestCase):
    def setUp(self):
        # 1. Force absolute clean path strings inside Python
        sdk_path = r"C:\Users\srira\AppData\Local\Android\Sdk"
        os.environ["ANDROID_HOME"] = sdk_path
        os.environ["ANDROID_SDK_ROOT"] = sdk_path

        options = UiAutomator2Options()
        options.platform_name = "Android"
        options.automation_name = "UiAutomator2"
        options.device_name = "RZCW51FDQLY"
        options.udid = "RZCW51FDQLY"
        options.app_package = "com.example.progressviewapp"
        options.app_activity = ".MainActivity"
        options.no_reset = True
        
        # The new clean path with absolutely zero spaces
        sdk_apk_path = r"C:\Users\srira\StudioProjects\ProgressViewApp\app-debug.apk"
        options.set_capability("appium:app", sdk_apk_path)
        options.set_capability("app", sdk_apk_path)

        # 2. Inject both W3C prefixed and legacy capabilities to completely bypass system paths
        options.set_capability("appium:androidHome", sdk_path)
        options.set_capability("appium:androidSdkRoot", sdk_path)
        options.set_capability("androidHome", sdk_path)
        options.set_capability("androidSdkRoot", sdk_path)

        options.ensure_webviews_have_pages = True
        options.native_web_screenshot = True
        options.new_command_timeout = 3600

        # 3. Initialize the driver session
        self.driver = webdriver.Remote("http://127.0.0.1:4723", options=options)
        self.wait = WebDriverWait(self.driver, 20)

    def tearDown(self):
        if hasattr(self, 'driver') and self.driver:
            self.driver.quit()

    def find_by_tag(self, tag):
        """Helper: find element by Compose testTag (exposed as content-desc or resource-id).
        Compose testTag is exposed via UiAutomator2 as resource-id with package prefix
        or as content-description depending on the Compose/UiAutomator2 version.
        We try exact resource-id, then with package prefix, then accessibility_id."""
        from appium.webdriver.common.appiumby import AppiumBy
        try:
            # Compose 1.5+ sets resource-id directly to the tag name
            return self.driver.find_element(
                AppiumBy.ANDROID_UIAUTOMATOR,
                f'new UiSelector().resourceId("{tag}")'
            )
        except:
            try:
                return self.driver.find_element(
                    AppiumBy.ANDROID_UIAUTOMATOR,
                    f'new UiSelector().resourceId("com.example.progressviewapp:id/{tag}")'
                )
            except:
                try:
                    return self.driver.find_element(AppiumBy.ACCESSIBILITY_ID, tag)
                except:
                    return self.driver.find_element(
                        AppiumBy.ANDROID_UIAUTOMATOR,
                        f'new UiSelector().description("{tag}")'
                    )

    def wait_for_tag(self, tag, timeout=20):
        """Helper: wait for a Compose testTag element to be present."""
        from appium.webdriver.common.appiumby import AppiumBy
        from selenium.webdriver.support import expected_conditions as EC
        import time
        
        end_time = time.time() + timeout
        while time.time() < end_time:
            try:
                el = self.find_by_tag(tag)
                if el and el.is_displayed():
                    return el
            except:
                pass
            time.sleep(0.5)
        
        # Final attempt - raise if not found
        return self.find_by_tag(tag)