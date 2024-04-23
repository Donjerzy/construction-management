<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'; 
    import { get } from "svelte/store";
    import {onMount} from 'svelte';
    export let projectId;

    let active = 'overview';
    let appName = 'Mjengo Bora Construction'; //  overview | 
    let projectOverview = {
        projectName : "-",
        numberOfClients: 0,
        numberOfEmployees: 0,
        numberOfTasksDone: 0,
        numberOfTasksOngoing: 0,
        budgetAvailable: 0.0,
        budgetSpent: 0.0
    }


    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/project/overview?project=${projectId}`, {
            headers: {
                'Authorization': `Bearer ${get(accessToken)}`
            }
        })
        .then(response => {
            if(!response.ok) {
                errorFetch = true;
               firstName.set("");
               accessToken.set("");
               loggedIn.set("false");
               window.location.replace('/'); 
            } else {
                return response.json();
            }
        }).then((result)=> {
            if(!errorFetch) {
                projectOverview = result.overview;
            }
        })
    });


    function navigate(to) {
        active = to;
    }


</script>




<AdminComponent appName={appName} userFirstName={get(firstName)} contentTitle={projectOverview.projectName}>
    {#if active === 'overview'}
    <div class="container">
        <div class="project-nav">
            <p id="active-link">Overview</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("clients")}>Clients</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("employees")}>Employees</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("tasks")}>Tasks</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("expenses")}>Expenses</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("actions")}>Actions</p>
        </div>
        <div class="overview-table">
            <table>
                <thead>
                    <tr class="table-head">
                        <th>Clients</th>
                        <th>Employees</th>
                        <th colspan="2">Tasks</th>
                        <th colspan="2">Budget</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="table-attributes">
                        <td>No</td>
                        <td>No</td>
                        <td>Done</td>
                        <td>Open</td>
                        <td>Available</td>
                        <td>Spent</td>
                    </tr>
                    <tr>
                        <td>{projectOverview.numberOfClients}</td>
                        <td>{projectOverview.numberOfEmployees}</td>
                        <td>{projectOverview.numberOfTasksDone}</td>
                        <td>{projectOverview.numberOfTasksOngoing}</td>
                        <td>{projectOverview.budgetAvailable}</td>
                        <td>{projectOverview.budgetSpent}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    {/if}
</AdminComponent>


<style>
    .container {
        margin-top: 40px;
    }
    .project-nav {
        display: flex;
        justify-content: space-between;
        height: 20px;
        padding-right: 20px;
        align-items: center;
    }
    .project-nav p {
        text-decoration: underline;
        font-size: 1rem;
    }
    .project-nav p:hover {
        cursor: pointer;
        color: var(--tertiary-clr);
    }
    #active-link {
        color: var(--tertiary-clr);
        font-size: 1.2rem;
    }
    .overview-table {
        margin-top: 40px;
    }
    table {
       border-collapse: collapse;
       width: 100%;
    }
    th, td {
        border: 1px black solid;
        width: 120px;
        height: 80px; 
        text-align: center;
    }
    .table-head,
    .table-attributes {
        height: 80px;
    }
</style>