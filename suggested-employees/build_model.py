from dotenv import load_dotenv
import os
from sqlalchemy import create_engine
import psycopg2


def connect_to_db():
    user = os.getenv('USER')
    password = os.getenv('PASS')
    host = os.getenv('HOST')
    port = os.getenv('PORT')
    db_name = os.getenv('DB_NAME')
    postgres_db_conn_str = f'postgresql+psycopg2://{user}:{password}@{host}:{port}/{db_name}'
    engine = create_engine(postgres_db_conn_str)
    try:
        connection = engine.connect()
        connection.close()
        return engine
    except Exception as e:
        print("Could not connect to construction management db")
        quit()

def main():
    load_dotenv()
    engine = connect_to_db()

if __name__ == '__main__':
    main()


