from django.shortcuts import render
from django.http import HttpResponse, HttpResponseServerError, JsonResponse
from predict.models import TaskAssignmentModel
import json
import joblib
from django.core.serializers import serialize

# Create your views here.


def predict(request):
    length = len(TaskAssignmentModel.objects.all())
    if length != 1:
        return HttpResponseServerError("Error fetching mode")
    body_unicode = request.body.decode('utf-8')
    body = json.loads(body_unicode)
    features = body["features"]

    model_path = TaskAssignmentModel.objects.first().path
    predicted_minutes = predict_minutes_taken(model_path, features)

    response = dict()
    response["httpStatus"] = 200
    response["message"] = "Estimated task time predicted successfully"
    response["minutes"] = predicted_minutes
    
    
    return JsonResponse(response)



def predict_minutes_taken(path, input):
    # Load the trained model
    model = joblib.load(path)

    predicted_completion_times = model.predict(input)
    minutes = 0

    for predicted_completion_time in enumerate(predicted_completion_times):
        minutes = round(predicted_completion_time[1], 2)
        break
    return minutes