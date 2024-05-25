from dotenv import load_dotenv
import os


def main():
    load_dotenv()
    test = os.getenv("Test")
    print(f"{test}")


if __name__ == '__main__':
    main()


