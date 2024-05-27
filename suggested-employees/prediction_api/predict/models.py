# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class TaskAssignmentModel(models.Model):
    id = models.BigIntegerField(primary_key=True)
    path = models.CharField(max_length=255)
    score = models.FloatField()

    class Meta:
        managed = False
        db_table = 'task_assignment_model'




