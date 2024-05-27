from dotenv import load_dotenv
from sklearn.linear_model import LinearRegression
import joblib
import numpy as np
import os
from sqlalchemy import create_engine, Column, BigInteger, Integer, Double, select, String
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

    with engine.connect() as conn:
        # select target table
        stmt = select(TaskAssignment.active_tasks, TaskAssignment.average_completion, TaskAssignment.time_taken).where(TaskAssignment.dataset == 'train')
        for row in conn.execute(stmt):
            active_tasks_avg_completion.append([row[0],row[1]])
            actual_completion.append(row[2])
    x = np.array(active_tasks_avg_completion)
    y = np.array(actual_completion)    


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
    else:
        print("not saved")
    
    


def determine_accuracy():
    pass



def predict():
    # Load the trained model
    model = joblib.load("task_completion_model.pkl")
    input_features = [[2, 50]]  # Active tasks and average completion time
    # Make predictions
    predicted_completion_times = model.predict(input_features)

    for predicted_completion_time in enumerate(predicted_completion_times):
        print(f"Predicted completion time is {round(predicted_completion_time[1], 2)} minutes")
    

if __name__ == '__main__':
    main()
    #predict()



# train dataset, test dataset.


