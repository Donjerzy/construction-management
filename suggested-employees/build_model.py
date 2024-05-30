from dotenv import load_dotenv
from sklearn.linear_model import LinearRegression
import joblib
import numpy as np
import os
from sqlalchemy import create_engine, Column, BigInteger, Integer, Double, select, String, update, insert
from sqlalchemy.orm import declarative_base
import psycopg2
from datetime import date




Base = declarative_base()

class TaskAssignment(Base):
    __tablename__ = "task_assignment"

    id = Column(BigInteger, primary_key=True)
    active_tasks = Column(Integer)
    average_completion = Column(Double)
    employee_id = Column(BigInteger)
    task_id = Column(BigInteger)
    average_completion = Column(Double)
    time_taken = Column(Double)
    dataset = Column(String)

class BestModel(Base):
    __tablename__ = "task_assignment_model"

    id = Column(BigInteger, primary_key=True)
    path = Column(String)
    score = Column(Double)


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
        return postgres_db_conn_str
    except Exception as e:
        print("Could not connect to construction management db")
        quit()

def main():
    load_dotenv()
    connection_string = connect_to_db()
    active_tasks_avg_completion = []
    actual_completion = []
    engine = create_engine(connection_string)

    count = 0

    with engine.connect() as conn:
        # select target table
        stmt = select(TaskAssignment.active_tasks, TaskAssignment.average_completion, TaskAssignment.time_taken).where(TaskAssignment.dataset == 'train')
        for row in conn.execute(stmt):
            count += 1
            active_tasks_avg_completion.append([row[0],row[1]])
            actual_completion.append(row[2])
    x = np.array(active_tasks_avg_completion)
    y = np.array(actual_completion)    

    if count < 1:
        print("Inadequate training data")
        quit()

    # Train the model
    model = LinearRegression()
    model.fit(x, y)

    directory = './models'

    if not os.path.exists(directory):
        os.makedirs(directory)
    
    
    today = date.today()
    today_formatted = today.strftime("%b-%d-%Y")
    
    model_path = f'{directory}/{today_formatted}'

    if not os.path.exists(model_path):
        os.makedirs(model_path)
        joblib.dump(model, f'{model_path}/model.pkl')
        print("saved")
        determine_accuracy(os.path.abspath(f'{model_path}/model.pkl'), connection_string)
    else:
        print("not saved")
    
    


def determine_accuracy(path:str, connection_string:str):
    """
        Have a total.
        Get the training set count used for average.
        Fetch test set (active, avg_completion, actual)
        predict 1. Score = actual - predicted. 
        add score the percentage total. Get average.
        The higher the value the worse it is.
        Fetch the current best from db.
        Compare if lower.
        If lower, update path to new path, update score as well.
        else: save to history -> get max id + 1, date, score.
    """
    total_score = 0
    model = joblib.load(path)
    engine = create_engine(connection_string)
    count = 0
    with engine.connect() as conn:
        # select target table
        stmt = select(TaskAssignment.active_tasks, TaskAssignment.average_completion, TaskAssignment.time_taken).where(TaskAssignment.dataset == 'test')
        test_data_count = 0
        for row in conn.execute(stmt):
            test_data_count += 1 
        if test_data_count == 0:
            print("No test data")
            quit()
        for row in conn.execute(stmt):
            input_features = [[row[0],row[1]]]
            actual = row[2]
            predicted_completion_times = model.predict(input_features)
            for predicted_completion_time in enumerate(predicted_completion_times):
                current_score = abs(float(actual) - float(round(predicted_completion_time[1], 2)))
                total_score += current_score
            count += 1
    score = total_score/count  
    fetched_score = score - 1
    pk = 0
    with engine.connect() as conn:
        rows = 0
        stmt = select(BestModel.id,BestModel.score).filter_by().limit(1)
        for row in conn.execute(stmt):
            rows += 1
        if rows > 1:
            print("Issue with best model table")
            quit()
        elif rows < 1:
            insert_stmt = insert(BestModel).values(id=1, path=path, score=score)
            conn.execute(insert_stmt)
            conn.commit()
            print('Inserted successfully')
            updateHistory(score)
            quit()
        for row in conn.execute(stmt):
            fetched_score = row[1]
            pk = row[0]
        
    if fetched_score > score:
        update(BestModel).where(BestModel.id == pk).values(path=path, score=score)

    updateHistory(score)


def updateHistory(score):
    today = date.today()
    today_formatted = today.strftime("%b-%d-%Y")
    
    with open('./model-history/history.txt', 'a') as f:
        f.write(f'\n{today_formatted} - {score}')

    

if __name__ == '__main__':
    main()



