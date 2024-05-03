<script>
    export let projectId;
    export let employeeId;
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'; 
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Employee"
    let employee = {
        "id": 204,
        "name": "Jane Doe",
        "employeeType": "Painter",
        "email": "jane.doe@gmail.com",
        "tasksCompleted": 0,
        "tasksCompletedOnTime": 0,
        "tasksCompletedPastTime": 0,
        "tasksOngoing": 0,
        "wage": "20000",
        "wageType": "Monthly",
        "archetype": "n/a",
        "totalTasks": 0
    }
</script>


<svelte:head>
    <title>Employee</title>
</svelte:head>


<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>
    <div class="mt-4 min-h-4 rounded-md flex gap-8 items-center">
        <Button 
            height=10 width=36 label="Edit" fontSize="sm" padding="8px"
        />
        <Button 
            height=10 width=36 label="Reset Password" fontSize="sm" padding="8px"
        />
        <Button 
            height=10 width=36 label="Pay Wages" fontSize="sm" padding="8px"
        />
    </div>
    <div class="border border-black mt-4 min-h-72 rounded-md">

    </div>
</AdminComponent>