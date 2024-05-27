from django.shortcuts import render
from django.http import HttpResponse, HttpResponseServerError
from predict.models import TaskAssignmentModel

# Create your views here.


def predict(request):
    length = len(TaskAssignmentModel.objects.all())
    if length == 1:
        return HttpResponseServerError("Error fetching model")
    return HttpResponse(length)
