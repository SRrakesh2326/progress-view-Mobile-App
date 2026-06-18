import unittest
import sys
import os

# Add the 'tests' directory to the path so that test modules can find each other
tests_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'tests')
if tests_dir not in sys.path:
    sys.path.insert(0, tests_dir)

def run_suite():
    print(f"Searching for tests in: {tests_dir}")

    # Discover all tests in the 'tests' directory
    # We set top_level_dir to tests_dir so that the loader treats it as the root for imports
    loader = unittest.TestLoader()
    suite = loader.discover(start_dir=tests_dir, pattern='test_*.py', top_level_dir=tests_dir)

    print(f"Number of tests discovered: {suite.countTestCases()}")

    if suite.countTestCases() == 0:
        print("No tests found. Check the directory path and pattern.")
        return

    # Run the tests
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)

    # Return exit code based on success
    if result.wasSuccessful():
        print("\n[SUCCESS] ALL TESTS PASSED SUCCESSFULLY!")
        sys.exit(0)
    else:
        print("\n[FAILED] SOME TESTS FAILED.")
        sys.exit(1)

if __name__ == "__main__":
    print("========================================")
    print("   PROGRESS VIEW APP - APPIUM TESTS")
    print("========================================")
    print("NOTE: If you get SDK path errors, please RESTART your Appium Server.")
    print("========================================")
    run_suite()
